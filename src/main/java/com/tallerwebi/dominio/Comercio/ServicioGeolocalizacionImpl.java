package com.tallerwebi.dominio.Comercio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Busca direcciones primero en Georef (API oficial de normalización de direcciones de Argentina,
 * que tolera errores de tipeo) y, si no encuentra nada con coordenadas, en Nominatim
 * (OpenStreetMap). Nominatim exige como máximo una consulta por segundo para toda la app, que la
 * app se identifique y que los resultados se guarden en caché: por eso el control de ritmo y la
 * caché.
 */
@Service("servicioGeolocalizacion")
public class ServicioGeolocalizacionImpl implements ServicioGeolocalizacion {

  /** Hace un GET y devuelve el cuerpo de la respuesta. Se reemplaza en los tests. */
  @FunctionalInterface
  interface ClienteHttp {
    String obtener(String url) throws IOException, InterruptedException;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(ServicioGeolocalizacionImpl.class);
  private static final String URL_GEOREF = "https://apis.datos.gob.ar/georef/api/v2.0/direcciones";
  private static final String URL_NOMINATIM = "https://nominatim.openstreetmap.org/search";
  private static final String AGENTE = "PreciosDeBarrio/1.0 (proyecto UNLaM Taller Web I)";
  private static final String CABA = "Buenos Aires";
  private static final String ID_PROVINCIA_CABA = "02";
  private static final String ID_PROVINCIA_BUENOS_AIRES = "06";
  private static final int MAXIMO_DE_SUGERENCIAS = 5;
  private static final int MAXIMO_EN_CACHE = 500;
  private static final long MILIS_ENTRE_CONSULTAS_A_NOMINATIM = 1100;
  private static final String FUENTE_GEOREF = "georef";
  private static final String FUENTE_NOMINATIM = "nominatim";
  private static final String UBICACION = "ubicacion";
  private static final String NOMBRE = "nombre";
  private static final int HTTP_OK = 200;
  // Un solo cliente para toda la app: reutiliza las conexiones y vive mientras viva la app
  private static final HttpClient HTTP = HttpClient
    .newBuilder()
    .connectTimeout(Duration.ofSeconds(3))
    .build();

  private final ClienteHttp cliente;
  private final long milisEntreConsultasANominatim;
  private final ObjectMapper lectorJson = new ObjectMapper();
  private final Map<String, List<UbicacionSugerida>> cache = new ConcurrentHashMap<>();
  private final Object turnoNominatim = new Object();
  private long ultimaConsultaANominatim;

  @Autowired
  public ServicioGeolocalizacionImpl() {
    this(clienteReal(), MILIS_ENTRE_CONSULTAS_A_NOMINATIM);
  }

  ServicioGeolocalizacionImpl(ClienteHttp cliente, long milisEntreConsultasANominatim) {
    this.cliente = cliente;
    this.milisEntreConsultasANominatim = milisEntreConsultasANominatim;
  }

  @Override
  public List<UbicacionSugerida> buscar(String direccion, String localidad) {
    if (direccion == null || direccion.isBlank()) return Collections.emptyList();
    String direccionLimpia = direccion.trim();
    String localidadLimpia = localidad == null ? "" : localidad.trim();
    String clave = (direccionLimpia + "|" + localidadLimpia).toLowerCase(Locale.ROOT);

    List<UbicacionSugerida> enCache = cache.get(clave);
    if (enCache != null) return enCache;

    Busqueda busqueda = new Busqueda();
    List<UbicacionSugerida> encontradas = buscarSinCache(
      direccionLimpia,
      localidadLimpia,
      busqueda
    );
    // Si algún servicio falló, el "no encontré nada" no es confiable: no se guarda
    if (!busqueda.huboFalla) {
      if (cache.size() >= MAXIMO_EN_CACHE) cache.clear();
      cache.put(clave, encontradas);
    }
    return encontradas;
  }

  /** Lo que pasó durante una búsqueda. */
  private static final class Busqueda {

    private boolean huboFalla;
  }

  private List<UbicacionSugerida> buscarSinCache(
    String direccion,
    String localidad,
    Busqueda busqueda
  ) {
    List<UbicacionSugerida> encontradas = consultarGeoref(
      urlGeoref(direccion, localidad, true),
      busqueda
    );
    // El catálogo mezcla partidos (La Matanza) con ciudades (Mar del Plata): si filtrar por partido
    // no da resultados, se prueba filtrando por localidad
    if (encontradas.isEmpty() && !CABA.equals(localidad) && !localidad.isEmpty()) {
      encontradas = consultarGeoref(urlGeoref(direccion, localidad, false), busqueda);
    }
    if (encontradas.isEmpty()) {
      encontradas = consultarNominatim(urlNominatim(direccion, localidad), busqueda);
    }
    return Collections.unmodifiableList(encontradas);
  }

  String urlGeoref(String direccion, String localidad, boolean comoPartido) {
    StringBuilder url = new StringBuilder(URL_GEOREF)
      .append("?max=")
      .append(MAXIMO_DE_SUGERENCIAS)
      .append("&direccion=")
      .append(codificar(direccion));
    if (CABA.equals(localidad)) {
      url.append("&provincia=").append(ID_PROVINCIA_CABA);
    } else {
      url.append("&provincia=").append(ID_PROVINCIA_BUENOS_AIRES);
      if (!localidad.isEmpty()) {
        url.append(comoPartido ? "&departamento=" : "&localidad=").append(codificar(localidad));
      }
    }
    return url.toString();
  }

  String urlNominatim(String direccion, String localidad) {
    String provincia = CABA.equals(localidad)
      ? "Ciudad Autónoma de Buenos Aires"
      : "Provincia de Buenos Aires";
    String consulta = localidad.isEmpty() || CABA.equals(localidad)
      ? direccion + ", " + provincia + ", Argentina"
      : direccion + ", " + localidad + ", " + provincia + ", Argentina";
    return (
      URL_NOMINATIM +
      "?format=jsonv2&countrycodes=ar&limit=" +
      MAXIMO_DE_SUGERENCIAS +
      "&q=" +
      codificar(consulta)
    );
  }

  private List<UbicacionSugerida> consultarGeoref(String url, Busqueda busqueda) {
    try {
      return leerGeoref(cliente.obtener(url));
    } catch (IOException e) {
      registrarFalla(FUENTE_GEOREF, e, busqueda);
    } catch (InterruptedException e) {
      busqueda.huboFalla = true;
      Thread.currentThread().interrupt();
    }
    return new ArrayList<>();
  }

  private List<UbicacionSugerida> consultarNominatim(String url, Busqueda busqueda) {
    try {
      esperarTurnoDeNominatim();
      return leerNominatim(cliente.obtener(url));
    } catch (IOException e) {
      registrarFalla(FUENTE_NOMINATIM, e, busqueda);
    } catch (InterruptedException e) {
      busqueda.huboFalla = true;
      Thread.currentThread().interrupt();
    }
    return new ArrayList<>();
  }

  // Una sola consulta por segundo a Nominatim para toda la app, como pide su política de uso
  private void esperarTurnoDeNominatim() throws InterruptedException {
    synchronized (turnoNominatim) {
      long espera =
        ultimaConsultaANominatim + milisEntreConsultasANominatim - System.currentTimeMillis();
      if (espera > 0) Thread.sleep(espera);
      ultimaConsultaANominatim = System.currentTimeMillis();
    }
  }

  List<UbicacionSugerida> leerGeoref(String json) throws IOException {
    Map<String, UbicacionSugerida> sinRepetir = new LinkedHashMap<>();
    for (JsonNode direccion : lectorJson.readTree(json).path("direcciones")) {
      JsonNode ubicacion = direccion.path(UBICACION);
      // Georef devuelve tramos de la calle donde esa altura no existe: vienen sin coordenadas
      if (!ubicacion.path("lat").isNumber() || !ubicacion.path("lon").isNumber()) continue;
      String nomenclatura = direccion.path("nomenclatura").asText();
      sinRepetir.putIfAbsent(
        nomenclatura,
        new UbicacionSugerida(
          nomenclatura,
          textoONull(direccion.path("localidad").path(NOMBRE)),
          ubicacion.path("lat").asDouble(),
          ubicacion.path("lon").asDouble(),
          FUENTE_GEOREF
        )
      );
    }
    return new ArrayList<>(sinRepetir.values());
  }

  List<UbicacionSugerida> leerNominatim(String json) throws IOException {
    Map<String, UbicacionSugerida> sinRepetir = new LinkedHashMap<>();
    for (JsonNode lugar : lectorJson.readTree(json)) {
      Double latitud = numeroONull(lugar.path("lat"));
      Double longitud = numeroONull(lugar.path("lon"));
      if (latitud == null || longitud == null) continue;
      String nombre = lugar.path("display_name").asText();
      sinRepetir.putIfAbsent(
        nombre,
        new UbicacionSugerida(nombre, null, latitud, longitud, FUENTE_NOMINATIM)
      );
    }
    return new ArrayList<>(sinRepetir.values());
  }

  // Nominatim manda las coordenadas como texto ("-34.68")
  private static Double numeroONull(JsonNode nodo) {
    try {
      return nodo.isMissingNode() || nodo.isNull() ? null : Double.valueOf(nodo.asText());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private static String textoONull(JsonNode nodo) {
    return nodo.isTextual() ? nodo.asText() : null;
  }

  private static String codificar(String texto) {
    return URLEncoder.encode(texto, StandardCharsets.UTF_8).replace("+", "%20");
  }

  private void registrarFalla(String fuente, Exception error, Busqueda busqueda) {
    busqueda.huboFalla = true;
    LOGGER.warn("No se pudo consultar {} para geolocalizar una dirección", fuente, error);
  }

  private static ClienteHttp clienteReal() {
    return url -> {
      HttpRequest pedido = HttpRequest
        .newBuilder(URI.create(url))
        .timeout(Duration.ofSeconds(5))
        .header("User-Agent", AGENTE)
        .header("Accept", "application/json")
        .GET()
        .build();
      HttpResponse<String> respuesta = HTTP.send(
        pedido,
        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
      );
      if (respuesta.statusCode() != HTTP_OK) {
        throw new IOException(
          "Respuesta " + respuesta.statusCode() + " de " + pedido.uri().getHost()
        );
      }
      return respuesta.body();
    };
  }
}
