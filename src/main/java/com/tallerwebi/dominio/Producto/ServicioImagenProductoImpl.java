package com.tallerwebi.dominio.Producto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.config.VariablesDeEntorno;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioImagenProducto")
public class ServicioImagenProductoImpl implements ServicioImagenProducto {

  interface ClienteHttp {
    String pedirToken(String idCliente, String secreto) throws IOException, InterruptedException;

    String buscar(String url, String token) throws IOException, InterruptedException;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(ServicioImagenProductoImpl.class);
  private static final String URL_TOKEN = "https://api.mercadolibre.com/oauth/token";
  private static final String URL_BUSQUEDA = "https://api.mercadolibre.com/products/search";
  private static final String ATRIBUTO_MARCA = "BRAND";
  private static final String DOMINIO_DE_IMAGENES = "mlstatic.com";
  private static final long MARGEN_DE_VENCIMIENTO_MILIS = 60_000;
  private static final int HTTP_OK = 200;
  private static final HttpClient HTTP = HttpClient
    .newBuilder()
    .connectTimeout(Duration.ofSeconds(3))
    .build();

  private final String idCliente;
  private final String secreto;
  private final ClienteHttp cliente;
  private final ObjectMapper lectorJson = new ObjectMapper();
  private String token = "";
  private long tokenVenceEn;

  @Autowired
  public ServicioImagenProductoImpl() {
    this(
      VariablesDeEntorno.leer("MERCADOLIBRE_CLIENT_ID"),
      VariablesDeEntorno.leer("MERCADOLIBRE_CLIENT_SECRET"),
      clienteReal()
    );
  }

  ServicioImagenProductoImpl(String idCliente, String secreto, ClienteHttp cliente) {
    this.idCliente = idCliente;
    this.secreto = secreto;
    this.cliente = cliente;
  }

  @Override
  public String buscarImagen(String nombre, String marca, String unidad) {
    if (nombre == null || nombre.isBlank() || !hayCredenciales()) {
      return null;
    }
    try {
      String respuesta = cliente.buscar(urlDeBusqueda(nombre, marca), tokenVigente());
      JsonNode catalogo = lectorJson.readTree(respuesta);
      String imagen = primeraImagenValida(catalogo, nombre, marca, unidad);
      // Si la marca es real pero no existe ese tamaño, sirve la foto del mismo producto en otro
      if (imagen == null && CoincidenciaDeProducto.tieneMarca(marca)) {
        imagen = primeraImagenValida(catalogo, nombre, marca, null);
      }
      if (imagen == null) {
        LOGGER.info("Mercado Libre no tiene foto para '{} {} {}'", nombre, marca, unidad);
      }
      return imagen;
    } catch (IOException excepcion) {
      olvidarToken();
      LOGGER.warn("No se pudo buscar la imagen de '{}' en Mercado Libre", nombre, excepcion);
    } catch (InterruptedException excepcion) {
      Thread.currentThread().interrupt();
    }
    return null;
  }

  private boolean hayCredenciales() {
    return idCliente != null && !idCliente.isBlank() && secreto != null && !secreto.isBlank();
  }

  private synchronized String tokenVigente() throws IOException, InterruptedException {
    if (System.currentTimeMillis() >= tokenVenceEn) {
      JsonNode respuesta = lectorJson.readTree(cliente.pedirToken(idCliente, secreto));
      if (!respuesta.path("access_token").isTextual()) {
        throw new IOException("Mercado Libre no entregó un token");
      }
      token = respuesta.get("access_token").asText();
      tokenVenceEn =
        System.currentTimeMillis() +
        respuesta.path("expires_in").asLong(0) * 1000 -
        MARGEN_DE_VENCIMIENTO_MILIS;
    }
    return token;
  }

  private synchronized void olvidarToken() {
    tokenVenceEn = 0;
  }

  private static String urlDeBusqueda(String nombre, String marca) {
    String texto = marca == null || marca.isBlank()
      ? nombre.trim()
      : nombre.trim() + " " + marca.trim();
    return URL_BUSQUEDA + "?status=active&site_id=MLA&limit=10&q=" + codificar(texto);
  }

  // Del catálogo se toma la primera foto válida del primer resultado que sea el mismo producto:
  // sin esa comprobación una verdura a granel terminaba con la foto de unos auriculares.
  private static String primeraImagenValida(
    JsonNode respuesta,
    String nombre,
    String marca,
    String unidad
  ) {
    for (JsonNode resultado : respuesta.path("results")) {
      if (
        CoincidenciaDeProducto.coincide(
          nombre,
          marca,
          unidad,
          resultado.path("name").asText(""),
          marcaDelCatalogo(resultado)
        )
      ) {
        for (JsonNode foto : resultado.path("pictures")) {
          String imagen = imagenValida(foto.path("url"));
          if (imagen != null) {
            return imagen;
          }
        }
      }
    }
    return null;
  }

  private static String marcaDelCatalogo(JsonNode resultado) {
    for (JsonNode atributo : resultado.path("attributes")) {
      if (ATRIBUTO_MARCA.equals(atributo.path("id").asText())) {
        return atributo.path("value_name").asText("");
      }
    }
    return "";
  }

  // La URL termina en un <img src>: solo se aceptan las de las imágenes de Mercado Libre, por
  // https. Las fotos del catálogo ya vienen en tamaño grande (-F).
  private static String imagenValida(JsonNode nodo) {
    if (!nodo.isTextual()) {
      return null;
    }
    try {
      URI uri = URI.create(nodo.asText().replaceFirst("^http://", "https://"));
      String host = uri.getHost();
      boolean esDeMercadoLibre =
        "https".equals(uri.getScheme()) &&
        host != null &&
        (DOMINIO_DE_IMAGENES.equals(host) || host.endsWith("." + DOMINIO_DE_IMAGENES));
      return esDeMercadoLibre ? uri.toString() : null;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private static String codificar(String texto) {
    return URLEncoder.encode(texto, StandardCharsets.UTF_8).replace("+", "%20");
  }

  private static ClienteHttp clienteReal() {
    return new ClienteHttp() {
      @Override
      public String pedirToken(String idCliente, String secreto)
        throws IOException, InterruptedException {
        String formulario =
          "grant_type=client_credentials&client_id=" +
          codificar(idCliente) +
          "&client_secret=" +
          codificar(secreto);
        HttpRequest pedido = HttpRequest
          .newBuilder(URI.create(URL_TOKEN))
          .timeout(Duration.ofSeconds(4))
          .header("Content-Type", "application/x-www-form-urlencoded")
          .header("Accept", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(formulario))
          .build();
        return enviar(pedido);
      }

      @Override
      public String buscar(String url, String token) throws IOException, InterruptedException {
        HttpRequest pedido = HttpRequest
          .newBuilder(URI.create(url))
          .timeout(Duration.ofSeconds(4))
          .header("Authorization", "Bearer " + token)
          .header("Accept", "application/json")
          .GET()
          .build();
        return enviar(pedido);
      }
    };
  }

  private static String enviar(HttpRequest pedido) throws IOException, InterruptedException {
    HttpResponse<String> respuesta = HTTP.send(
      pedido,
      HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
    );
    if (respuesta.statusCode() != HTTP_OK) {
      throw new IOException("Respuesta " + respuesta.statusCode() + " de Mercado Libre");
    }
    return respuesta.body();
  }
}
