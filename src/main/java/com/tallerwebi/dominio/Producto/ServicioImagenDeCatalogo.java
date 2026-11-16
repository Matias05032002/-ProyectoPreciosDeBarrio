package com.tallerwebi.dominio.Producto;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Lo que tienen en común las fuentes que buscan la foto de un producto en un catálogo por internet
// (supermercados, Open Food Facts): armar la consulta, leer la respuesta y quedarse con la foto del
// primer resultado que sea el mismo producto (nombre, marca y tamaño). Cada fuente dice a dónde
// consulta y cómo lee su respuesta.
abstract class ServicioImagenDeCatalogo implements ServicioImagenProducto {

  @FunctionalInterface
  interface ClienteHttp {
    String buscar(String url) throws IOException, InterruptedException;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(ServicioImagenDeCatalogo.class);
  private static final String USER_AGENT = "PreciosDeBarrio/1.0 (proyecto academico UNLaM)";
  private static final int CLASE_RESPUESTA_EXITOSA = 2;
  private static final int DIVISOR_DE_CLASE = 100;
  private static final HttpClient HTTP = HttpClient
    .newBuilder()
    .connectTimeout(Duration.ofSeconds(3))
    .build();

  private final ClienteHttp cliente;
  private final ObjectMapper lectorJson = new ObjectMapper();

  ServicioImagenDeCatalogo(ClienteHttp cliente) {
    this.cliente = cliente;
  }

  abstract String urlDeBusqueda(String nombre, String marca);

  abstract String primeraImagenValida(
    JsonNode catalogo,
    String nombre,
    String marca,
    String unidad
  );

  abstract String fuente();

  @Override
  public String buscarImagen(String nombre, String marca, String unidad) {
    if (nombre == null || nombre.isBlank()) {
      return null;
    }
    try {
      JsonNode catalogo = lectorJson.readTree(cliente.buscar(urlDeBusqueda(nombre, marca)));
      String imagen = primeraImagenValida(catalogo, nombre, marca, unidad);
      // Si la marca es real pero no existe ese tamaño, sirve la foto del mismo producto en otro
      if (imagen == null && CoincidenciaDeProducto.tieneMarca(marca)) {
        imagen = primeraImagenValida(catalogo, nombre, marca, null);
      }
      return imagen;
    } catch (IOException excepcion) {
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn("No se pudo buscar la imagen de '{}' en {}", nombre, fuente(), excepcion);
      }
    } catch (InterruptedException excepcion) {
      Thread.currentThread().interrupt();
    }
    return null;
  }

  // Lo que se pide al catálogo: el nombre y, si es una marca real, también la marca
  static String textoDeBusqueda(String nombre, String marca) {
    return CoincidenciaDeProducto.tieneMarca(marca)
      ? nombre.trim() + " " + marca.trim()
      : nombre.trim();
  }

  // La URL termina en un <img src>: solo se aceptan las del dominio esperado, por https
  static String imagenValida(JsonNode nodo, String dominio) {
    if (!nodo.isTextual()) {
      return null;
    }
    try {
      URI uri = URI.create(nodo.asText().replaceFirst("^http://", "https://"));
      String host = uri.getHost();
      boolean esDelDominio =
        "https".equals(uri.getScheme()) &&
        host != null &&
        (dominio.equals(host) || host.endsWith("." + dominio));
      return esDelDominio ? uri.toString() : null;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  static String codificar(String texto) {
    return URLEncoder.encode(texto, StandardCharsets.UTF_8).replace("+", "%20");
  }

  static ClienteHttp clienteReal(Duration espera) {
    return url -> {
      HttpRequest pedido = HttpRequest
        .newBuilder(URI.create(url))
        .timeout(espera)
        .header("User-Agent", USER_AGENT)
        .header("Accept", "application/json")
        .GET()
        .build();
      HttpResponse<String> respuesta = HTTP.send(
        pedido,
        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
      );
      if (respuesta.statusCode() / DIVISOR_DE_CLASE != CLASE_RESPUESTA_EXITOSA) {
        throw new IOException("Respuesta " + respuesta.statusCode() + " de " + url);
      }
      return respuesta.body();
    };
  }
}
