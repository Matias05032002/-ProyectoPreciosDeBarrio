package com.tallerwebi.dominio.Producto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.config.VariablesDeEntorno;
import com.tallerwebi.dominio.Gemini.ServicioGemini;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Cuando no hay foto exacta del producto se usa la imagen principal del artículo de Wikipedia del
// tipo de producto ("avocado", "cotton wool"), buscado por título: una foto real, sin marca y sin necesidad de clave.
// Groq convierte el nombre abreviado del ticket en ese término; si falla o no hay foto, se prueba
// con el término de la categoría.
@Service("servicioFotoGenerica")
public class ServicioFotoGenericaImpl implements ServicioFotoGenerica {

  @FunctionalInterface
  interface ClienteWikipedia {
    String buscar(String url) throws IOException, InterruptedException;
  }

  @FunctionalInterface
  interface Pausa {
    void esperar(long milisegundos) throws InterruptedException;
  }

  // Wikipedia responde 429 cuando se le piden muchas consultas seguidas: dice cuánto esperar
  static class LimiteExcedido extends IOException {

    private static final long serialVersionUID = 1L;
    private final long espera;

    LimiteExcedido(long espera) {
      super("Wikipedia pide esperar " + espera + " ms");
      this.espera = espera;
    }

    long getEspera() {
      return espera;
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(ServicioFotoGenericaImpl.class);
  // Se pide el artículo por su título exacto (siguiendo redirecciones) y no se busca por relevancia:
  // buscar "milk" devolvía el retrato de una persona que se apellida así.
  private static final String URL_BUSQUEDA =
    "https://en.wikipedia.org/w/api.php?action=query&redirects=1&prop=pageimages" +
    "&piprop=thumbnail&pithumbsize=500&format=json&titles=";
  private static final String URL_PIXABAY =
    "https://pixabay.com/api/?image_type=photo&safesearch=true&per_page=30&lang=en";
  // Categorías de la app que son comida: se piden solo fotos de "food" (así "papa" no trae un bicho)
  private static final Set<String> CATEGORIAS_DE_COMIDA = Set.of(
    "almacen",
    "bebidas sin alcohol",
    "bebidas con alcohol",
    "carnes y aves",
    "cereales y legumbres",
    "condimentos y salsas",
    "congelados",
    "enlatados y conservas",
    "fiambres y embutidos",
    "lacteos",
    "panaderia y reposteria",
    "pescados y mariscos",
    "productos de dietetica",
    "snacks y golosinas",
    "verduras y frutas"
  );
  private static final String DOMINIO_DE_IMAGENES = "wikimedia.org";
  private static final String DOMINIO_DE_PIXABAY = "pixabay.com";
  private static final String USER_AGENT = "PreciosDeBarrio/1.0 (proyecto academico UNLaM)";
  private static final int MAXIMO_DE_PALABRAS = 3;
  private static final int MAXIMO_DE_LETRAS = 30;
  private static final int HTTP_OK = 200;
  private static final int HTTP_DEMASIADAS_CONSULTAS = 429;
  private static final int MAXIMO_DE_REINTENTOS = 3;
  private static final long ESPERA_POR_DEFECTO = 2000;
  private static final long ESPERA_MAXIMA = 10_000;
  private static final long SEGUNDO = 1000;
  // Entre consulta y consulta se deja un respiro para no llegar al límite
  private static final long SEPARACION_ENTRE_CONSULTAS = 250;
  private static long ultimaConsulta;
  private static final String PROMPT_DEL_SISTEMA =
    "Sos un asistente que normaliza nombres de productos de supermercado argentino leídos de un " +
    "ticket (abreviados, en mayúsculas, a veces con marca). Respondé SOLO con el nombre genérico " +
    "del producto en inglés, de 1 a 3 palabras, sin marcas, sin comillas ni explicación, tal como " +
    "se llamaría el artículo de una enciclopedia. Si la palabra sola es ambigua, aclarala " +
    "(kitchen sponge, chicken egg, white rice). Si solo se lee una marca, respondé el producto " +
    "por el que se la conoce. Ejemplos: PALTA -> avocado; HELLMANNS -> mayonnaise; " +
    "ALGODON FAMILI CLASI -> cotton wool; TOALL FEM NOCT ALAS -> sanitary napkin.";
  private static final HttpClient HTTP = HttpClient
    .newBuilder()
    .connectTimeout(Duration.ofSeconds(3))
    .build();

  private final ClienteWikipedia cliente;
  private final Pausa pausa;
  private final String clavePixabay;
  private final ServicioGemini servicioGemini;
  private final ObjectMapper lectorJson = new ObjectMapper();
  private final Map<String, String> fotosPorTermino = new ConcurrentHashMap<>();

  @Autowired
  public ServicioFotoGenericaImpl(ServicioGemini servicioGemini) {
    this(clienteReal(), servicioGemini, Thread::sleep, VariablesDeEntorno.leer("PIXABAY_API_KEY"));
  }

  ServicioFotoGenericaImpl(ClienteWikipedia cliente, ServicioGemini servicioGemini) {
    this(cliente, servicioGemini, milisegundos -> {});
  }

  ServicioFotoGenericaImpl(ClienteWikipedia cliente, ServicioGemini servicioGemini, Pausa pausa) {
    this(cliente, servicioGemini, pausa, null);
  }

  ServicioFotoGenericaImpl(
    ClienteWikipedia cliente,
    ServicioGemini servicioGemini,
    Pausa pausa,
    String clavePixabay
  ) {
    this.cliente = cliente;
    this.servicioGemini = servicioGemini;
    this.pausa = pausa;
    this.clavePixabay = clavePixabay;
  }

  // Solo se busca la foto del producto en sí. La de su categoría ("Vegetable", "Food") sale de un
  // artículo general, un collage o un puesto de feria, y parece la foto del producto: sin foto
  // propia es mejor dejar el ícono, y el relleno del próximo arranque lo vuelve a intentar.
  @Override
  public String buscarFoto(String nombre, String categoria) {
    String termino = terminoDelProducto(nombre, categoria);
    if (termino == null) {
      LOGGER.info("Sin foto genérica de '{}' ({}): Groq no dio un término", nombre, categoria);
      return null;
    }
    try {
      String foto = fotoDe(termino, categoria);
      if (foto != null) {
        LOGGER.info("Foto genérica de '{}' ({}): término '{}'", nombre, categoria, termino);
        return foto;
      }
    } catch (LimiteExcedido excepcion) {
      LOGGER.warn("Wikipedia sigue limitando las consultas: '{}' queda sin foto por ahora", nombre);
      return null;
    }
    LOGGER.info("Sin foto genérica de '{}' ({}): término probado '{}'", nombre, categoria, termino);
    return null;
  }

  private String terminoDelProducto(String nombre, String categoria) {
    if (nombre == null || nombre.isBlank()) {
      return null;
    }
    try {
      String respuesta = servicioGemini.llamar(
        PROMPT_DEL_SISTEMA,
        "Producto: " + nombre.trim() + "\nCategoría: " + (categoria == null ? "" : categoria)
      );
      return terminoLimpio(
        lectorJson.readTree(respuesta).path("choices").path(0).path("message").path("content")
      );
    } catch (Exception excepcion) {
      LOGGER.warn("No se pudo normalizar '{}' con Groq", nombre, excepcion);
      return null;
    }
  }

  // Solo letras y espacios, de 1 a 3 palabras: una frase o un texto de disculpa no es un término
  private static String terminoLimpio(JsonNode contenido) {
    if (!contenido.isTextual()) {
      return null;
    }
    String texto = CoincidenciaDeNombre
      .normalizar(contenido.asText())
      .replaceAll("[^a-z ]", " ")
      .trim();
    int palabras = texto.isEmpty() ? 0 : texto.split("\\s+").length;
    boolean valido =
      palabras >= 1 && palabras <= MAXIMO_DE_PALABRAS && texto.length() <= MAXIMO_DE_LETRAS;
    return valido ? texto.replaceAll("\\s+", " ") : null;
  }

  private String fotoDe(String termino, String categoria) throws LimiteExcedido {
    boolean esComida = CATEGORIAS_DE_COMIDA.contains(
      CoincidenciaDeNombre.normalizar(categoria).trim()
    );
    String clave = termino + (esComida ? "|food" : "");
    String guardada = fotosPorTermino.get(clave);
    if (guardada != null) {
      return guardada;
    }
    String foto = null;
    if (clavePixabay != null && !clavePixabay.isBlank()) {
      String categoriaPixabay = esComida ? "&category=food" : "";
      String consulta = codificar(termino + " isolated");
      foto =
        consultarFoto(
          URL_PIXABAY + categoriaPixabay + "&key=" + codificar(clavePixabay) + "&q=" + consulta,
          true,
          termino
        );
    }
    if (foto == null) {
      foto = consultarFoto(URL_BUSQUEDA + codificar(titulosDe(termino)), false, termino);
    }
    if (foto != null) {
      fotosPorTermino.put(clave, foto);
    }
    return foto;
  }

  private String consultarFoto(String url, boolean esPixabay, String termino)
    throws LimiteExcedido {
    try {
      JsonNode respuesta = lectorJson.readTree(consultar(url));
      return esPixabay ? fotoDePixabay(respuesta, termino) : primeraFotoValida(respuesta, termino);
    } catch (LimiteExcedido excepcion) {
      throw excepcion;
    } catch (IOException excepcion) {
      String fuente = esPixabay ? "Pixabay" : "Wikipedia";
      LOGGER.warn("No se pudo buscar la foto en {}", fuente, excepcion);
    } catch (InterruptedException excepcion) {
      Thread.currentThread().interrupt();
    }
    return null;
  }

  // Pixabay devuelve de todo (arte, escenas, bichos): solo sirve una foto cuyos tags nombran el
  // producto y dicen que está solo. Se prefiere la de fondo blanco, para que las tarjetas se vean
  // parejas; si no hay, una "isolated" que no sea sobre fondo oscuro.
  private static String fotoDePixabay(JsonNode respuesta, String termino) {
    List<String> palabras = List.of(termino.split("\\s+"));
    String conFondoClaro = null;
    for (JsonNode resultado : respuesta.path("hits")) {
      String tags = CoincidenciaDeNombre.normalizar(resultado.path("tags").asText(""));
      String foto = fotoDelDominio(resultado.path("webformatURL"), DOMINIO_DE_PIXABAY);
      if (foto == null || !palabras.stream().allMatch(tags::contains)) {
        continue;
      }
      if (tags.contains("white background")) {
        return foto;
      }
      boolean esOscura = tags.contains("black") || tags.contains("dark");
      if (conFondoClaro == null && tags.contains("isolated") && !esOscura) {
        conFondoClaro = foto;
      }
    }
    return conFondoClaro;
  }

  // Si Wikipedia limita, espera lo que pide y reintenta unas pocas veces antes de rendirse
  private String consultar(String url) throws IOException, InterruptedException {
    for (int intento = 1;; intento++) {
      try {
        return cliente.buscar(url);
      } catch (LimiteExcedido limite) {
        if (intento > MAXIMO_DE_REINTENTOS) {
          throw limite;
        }
        pausa.esperar(limite.getEspera());
      }
    }
  }

  // "Mustard" es una desambiguación sin foto: el artículo del condimento se llama "Mustard
  // (condiment)". Se piden las tres variantes juntas, en una sola consulta.
  private static String titulosDe(String termino) {
    return termino + "|" + termino + " (condiment)|" + termino + " (food)";
  }

  // Los artículos vienen en un mapa sin orden: se recorren por relevancia (index) y, entre los que
  // no la tienen, el de título exacto antes que sus variantes
  private static String primeraFotoValida(JsonNode respuesta, String termino) {
    List<JsonNode> paginas = new ArrayList<>();
    respuesta.path("query").path("pages").forEach(paginas::add);
    paginas.sort(
      Comparator
        .comparingInt((JsonNode pagina) -> pagina.path("index").asInt(Integer.MAX_VALUE))
        .thenComparing(pagina -> !termino.equalsIgnoreCase(pagina.path("title").asText("")))
    );
    for (JsonNode pagina : paginas) {
      String foto = fotoDelDominio(pagina.path("thumbnail").path("source"), DOMINIO_DE_IMAGENES);
      if (foto != null) {
        return foto;
      }
    }
    return null;
  }

  // La URL termina en un <img src>: solo se aceptan las del dominio esperado, por https
  private static String fotoDelDominio(JsonNode nodo, String dominio) {
    if (!nodo.isTextual()) {
      return null;
    }
    try {
      URI uri = URI.create(nodo.asText().replaceFirst("^http://", "https://"));
      boolean esDelDominio =
        "https".equals(uri.getScheme()) &&
        uri.getHost() != null &&
        (dominio.equals(uri.getHost()) || uri.getHost().endsWith("." + dominio));
      return esDelDominio ? uri.toString() : null;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private static String codificar(String texto) {
    return URLEncoder.encode(texto, StandardCharsets.UTF_8).replace("+", "%20");
  }

  private static ClienteWikipedia clienteReal() {
    return url -> {
      esperarTurno();
      HttpRequest pedido = HttpRequest
        .newBuilder(URI.create(url))
        .timeout(Duration.ofSeconds(4))
        .header("User-Agent", USER_AGENT)
        .header("Accept", "application/json")
        .GET()
        .build();
      HttpResponse<String> respuesta = HTTP.send(
        pedido,
        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
      );
      if (respuesta.statusCode() == HTTP_DEMASIADAS_CONSULTAS) {
        throw new LimiteExcedido(esperaIndicada(respuesta));
      }
      if (respuesta.statusCode() != HTTP_OK) {
        throw new IOException("Respuesta " + respuesta.statusCode() + " de Wikipedia");
      }
      return respuesta.body();
    };
  }

  private static long esperaIndicada(HttpResponse<String> respuesta) {
    long espera = respuesta
      .headers()
      .firstValue("Retry-After")
      .map(valor -> {
        try {
          return Long.parseLong(valor.trim()) * SEGUNDO;
        } catch (NumberFormatException e) {
          return ESPERA_POR_DEFECTO;
        }
      })
      .orElse(ESPERA_POR_DEFECTO);
    return Math.min(espera, ESPERA_MAXIMA);
  }

  private static synchronized void esperarTurno() throws InterruptedException {
    long falta = ultimaConsulta + SEPARACION_ENTRE_CONSULTAS - System.currentTimeMillis();
    if (falta > 0) {
      Thread.sleep(falta);
    }
    ultimaConsulta = System.currentTimeMillis();
  }
}
