package com.tallerwebi.dominio.Asistente;

import com.google.gson.*;
import com.tallerwebi.dominio.Gemini.ServicioGemini;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import java.util.List;
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
  public String consultar(String pregunta, Double latitud, Double longitud) throws Exception {
    List<Reporte> reportes = repositorioReporte.listarTodos();
    String contexto = construirContexto(reportes, latitud, longitud);

    String prompt =
      "Sos Don Peso, un asistente amigable del Mapa de Precios del Barrio. " +
      "Ayudás a los vecinos a encontrar los mejores precios cerca suyo. " +
      "Respondé en español, de forma breve y amigable. " +
      "Usá la siguiente información de la app para responder:\n\n" +
      contexto +
      "\nPregunta del vecino: " +
      pregunta;

    return llamarGemini(prompt);
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

  private String llamarGemini(String prompt) throws Exception {
    String response = servicioGemini.llamar(prompt);
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
}
