package com.tallerwebi.dominio.Asistente;

import com.google.gson.*;
import com.tallerwebi.dominio.Gemini.ServicioGemini;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioAsistenteImpl implements ServicioAsistente {

  private static final int MAX_REPORTES = 20;
  private static final double RADIO_KM = 2.0;

  private final RepositorioReporte repositorioReporte;
  private final ServicioGemini servicioGemini;

  public ServicioAsistenteImpl(
    RepositorioReporte repositorioReporte,
    ServicioGemini servicioGemini
  ) {
    this.repositorioReporte = repositorioReporte;
    this.servicioGemini = servicioGemini;
  }

  @Override
  public String consultar(
    String pregunta,
    Double latitud,
    Double longitud,
    List<Map<String, String>> historial
  ) throws Exception {
    List<Reporte> reportes = repositorioReporte.listarRecientes(7);
    List<Reporte> reportesFiltrados = filtrarPorKeyword(reportes, pregunta);
    String contexto = construirContexto(reportesFiltrados, latitud, longitud);

    String systemPrompt =
      "Sos Don Peso, el asistente del Mapa de Precios del Barrio.\n" +
      "Tu único trabajo es ayudar a vecinos a comprar más barato usando datos reales de la app.\n" +
      "Reglas:\n" +
      "- Respondé en español rioplatense, breve y directo.\n" +
      "- Recomendá siempre el precio más bajo. Mencioná el comercio y la dirección.\n" +
      "- Si los datos tienen más de 5 días, avisalo.\n" +
      "- NO inventes precios ni comercios. Solo usá los datos que te dan.\n" +
      "- Si la pregunta no es sobre precios o compras, decí: 'Solo puedo ayudarte con precios del barrio.'";

    String userPrompt = construirUserPrompt(contexto, pregunta, historial);

    return llamarGemini(systemPrompt, userPrompt);
  }

  private String construirUserPrompt(
    String contexto,
    String pregunta,
    List<Map<String, String>> historial
  ) {
    StringBuilder sb = new StringBuilder();
    sb.append("Datos disponibles:\n").append(contexto).append("\n");

    if (historial != null && !historial.isEmpty()) {
      sb.append("Conversación anterior:\n");
      int desde = Math.max(0, historial.size() - 6); // últimos 3 turnos (user + assistant)
      for (int i = desde; i < historial.size(); i++) {
        Map<String, String> turno = historial.get(i);
        String rol = "user".equals(turno.get("role")) ? "Vecino" : "Don Peso";
        sb.append(rol).append(": ").append(turno.get("content")).append("\n");
      }
      sb.append("\n");
    }

    sb.append("Pregunta actual: ").append(pregunta);
    return sb.toString();
  }

  private String construirContexto(List<Reporte> reportes, Double latitud, Double longitud) {
    String contexto = construirContextoConFiltro(reportes, latitud, longitud, true);
    if (contexto == null) {
      contexto = construirContextoConFiltro(reportes, latitud, longitud, false);
    }
    return contexto != null ? contexto : "No hay reportes disponibles.\n";
  }

  private String construirContextoConFiltro(
    List<Reporte> reportes,
    Double latitud,
    Double longitud,
    boolean filtrarPorDistancia
  ) {
    StringBuilder sb = new StringBuilder();
    sb.append(
      filtrarPorDistancia
        ? "Productos disponibles en la app (nombre, precio, comercio, dirección, distancia):\n"
        : "Productos disponibles en la app (sin filtro de distancia):\n"
    );

    int count = 0;
    for (Reporte reporte : reportes) {
      if (count >= MAX_REPORTES) break;
      if (!esReporteValido(reporte)) continue;
      if (debeExcluir(reporte, latitud, longitud, filtrarPorDistancia)) continue;
      sb.append(formatearReporte(reporte, latitud, longitud));
      count++;
    }
    return count > 0 ? sb.toString() : null;
  }

  private boolean esReporteValido(Reporte reporte) {
    return reporte.getComercio() != null && reporte.getProducto() != null;
  }

  private boolean debeExcluir(
    Reporte reporte,
    Double latitud,
    Double longitud,
    boolean filtrarPorDistancia
  ) {
    if (!filtrarPorDistancia || latitud == null || longitud == null) return false;
    double distancia = calcularDistanciaKm(
      latitud,
      longitud,
      reporte.getComercio().getLatitud(),
      reporte.getComercio().getLongitud()
    );
    return distancia > RADIO_KM;
  }

  private String formatearReporte(Reporte reporte, Double latitud, Double longitud) {
    double distancia = calcularDistanciaKm(
      latitud,
      longitud,
      reporte.getComercio().getLatitud(),
      reporte.getComercio().getLongitud()
    );
    return String.format(
      "- %s: $%.2f en %s (%s) a %.1f km%n",
      reporte.getProducto().getNombre(),
      reporte.getPrecio(),
      reporte.getComercio().getNombre(),
      reporte.getComercio().getDireccion(),
      distancia
    );
  }

  private double calcularDistanciaKm(Double lat1, Double lon1, Double lat2, Double lon2) {
    if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) return 0.0;
    final int radioTierra = 6371;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double haversine =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(Math.toRadians(lat1)) *
        Math.cos(Math.toRadians(lat2)) *
        Math.sin(dLon / 2) *
        Math.sin(dLon / 2);
    double angulo = 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
    return radioTierra * angulo;
  }

  private String llamarGemini(String systemPrompt, String userPrompt) throws Exception {
    String response = servicioGemini.llamar(systemPrompt, userPrompt);
    JsonObject root = JsonParser.parseString(response).getAsJsonObject();
    return root
      .getAsJsonArray("choices")
      .get(0)
      .getAsJsonObject()
      .getAsJsonObject("message")
      .get("content")
      .getAsString()
      .trim();
  }

  private List<Reporte> filtrarPorKeyword(List<Reporte> reportes, String pregunta) {
    String preguntaLower = pregunta.toLowerCase(Locale.ROOT);
    List<Reporte> filtrados = reportes
      .stream()
      .filter(reporte ->
        reporte.getProducto() != null &&
        reporte.getProducto().getNombre() != null &&
        preguntaLower.contains(reporte.getProducto().getNombre().toLowerCase(Locale.ROOT))
      )
      .collect(Collectors.toList());
    return filtrados.isEmpty() ? reportes : filtrados;
  }
}
