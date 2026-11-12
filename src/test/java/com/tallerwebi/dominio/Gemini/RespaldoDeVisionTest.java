package com.tallerwebi.dominio.Gemini;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class RespaldoDeVisionTest {

  private static final String CUERPO_GEMINI =
    "{\"contents\":[{\"parts\":[{\"text\":\"leé el ticket\"}," +
    "{\"inline_data\":{\"mime_type\":\"image/jpeg\",\"data\":\"QUJD\"}}]}]," +
    "\"generationConfig\":{\"responseMimeType\":\"application/json\"}}";
  private static final String RESPUESTA_OPENAI =
    "{\"choices\":[{\"message\":{\"content\":\"{\\\"productos\\\":[]}\"}}]}";
  private static final String ERROR_GEMINI = "Error Gemini API (gemini-flash-latest): 503";

  private final List<HttpServer> servidores = new ArrayList<>();
  private final List<String> pedidos = Collections.synchronizedList(new ArrayList<>());

  @AfterEach
  public void apagarServidores() {
    servidores.forEach(servidor -> servidor.stop(0));
  }

  @Test
  public void aFormatoOpenAiLlevaElTextoYLaImagenComoDataUrl() {
    RespaldoDeVision respaldo = RespaldoDeVision.ninguno();

    String cuerpo = respaldo.aFormatoOpenAi(
      CUERPO_GEMINI,
      proveedor("Groq", "http://x", "k", true)
    );

    JsonObject json = JsonParser.parseString(cuerpo).getAsJsonObject();
    assertThat(json.get("model").getAsString(), equalTo("modelo-de-prueba"));
    assertThat(
      cuerpo,
      allOf(
        containsString("\"type\":\"text\""),
        containsString("leé el ticket"),
        containsString("\"type\":\"image_url\""),
        containsString("data:image/jpeg;base64,QUJD")
      )
    );
  }

  @Test
  public void aFormatoOpenAiPideJsonSoloSiElProveedorLoSoporta() {
    RespaldoDeVision respaldo = RespaldoDeVision.ninguno();

    String conJson = respaldo.aFormatoOpenAi(
      CUERPO_GEMINI,
      proveedor("Groq", "http://x", "k", true)
    );
    String sinJson = respaldo.aFormatoOpenAi(
      CUERPO_GEMINI,
      proveedor("OR", "http://x", "k", false)
    );

    assertThat(conJson, containsString("json_object"));
    assertThat(sinJson, not(containsString("json_object")));
  }

  @Test
  public void aFormatoGeminiDevuelveElTextoDentroDeCandidates() throws Exception {
    String respuesta = RespaldoDeVision.ninguno().aFormatoGemini(RESPUESTA_OPENAI);

    String texto = JsonParser
      .parseString(respuesta)
      .getAsJsonObject()
      .getAsJsonArray("candidates")
      .get(0)
      .getAsJsonObject()
      .getAsJsonObject("content")
      .getAsJsonArray("parts")
      .get(0)
      .getAsJsonObject()
      .get("text")
      .getAsString();
    assertThat(texto, equalTo("{\"productos\":[]}"));
  }

  @Test
  public void aFormatoGeminiDescartaElRazonamientoEntreEtiquetasThink() throws Exception {
    String conPensamiento =
      "{\"choices\":[{\"message\":{\"content\":\"<think>dudo\\nmucho</think>{\\\"a\\\":1}\"}}]}";

    String respuesta = RespaldoDeVision.ninguno().aFormatoGemini(conPensamiento);

    assertThat(respuesta, containsString("{\\\"a\\\":1}"));
    assertThat(respuesta, not(containsString("dudo")));
  }

  @Test
  public void aFormatoGeminiSinContenidoLanzaExcepcion() {
    assertThrows(
      Exception.class,
      () -> RespaldoDeVision.ninguno().aFormatoGemini("{\"choices\":[]}")
    );
    assertThrows(
      Exception.class,
      () ->
        RespaldoDeVision
          .ninguno()
          .aFormatoGemini("{\"choices\":[{\"message\":{\"content\":\" \"}}]}")
    );
  }

  @Test
  public void leerUsaElPrimerProveedorQueResponde() throws Exception {
    String urlA = servidor("/a", 200, RESPUESTA_OPENAI);
    RespaldoDeVision respaldo = new RespaldoDeVision(List.of(proveedor("A", urlA, "clave", true)));

    String respuesta = respaldo.leer(CUERPO_GEMINI, ERROR_GEMINI);

    assertThat(respuesta, containsString("candidates"));
    assertThat(pedidos, hasSize(1));
    assertThat(pedidos.get(0), containsString("data:image/jpeg;base64,QUJD"));
  }

  @Test
  public void leerSiElPrimerProveedorFallaPruebaConElSegundo() throws Exception {
    String urlA = servidor("/a", 500, "{\"error\":\"caido\"}");
    String urlB = servidor("/b", 200, RESPUESTA_OPENAI);
    RespaldoDeVision respaldo = new RespaldoDeVision(
      List.of(proveedor("A", urlA, "clave", true), proveedor("B", urlB, "clave", false))
    );

    String respuesta = respaldo.leer(CUERPO_GEMINI, ERROR_GEMINI);

    assertThat(respuesta, containsString("candidates"));
    assertThat(pedidos, hasSize(2));
  }

  @Test
  public void leerSiTodosFallanLanzaExcepcionConElErrorDeGeminiYElDeCadaProveedor()
    throws Exception {
    String urlA = servidor("/a", 429, "{\"error\":\"limite\"}");
    String urlB = servidor("/b", 200, "{\"choices\":[]}");
    RespaldoDeVision respaldo = new RespaldoDeVision(
      List.of(proveedor("Groq", urlA, "clave", true), proveedor("OpenRouter", urlB, "clave", false))
    );

    Exception error = assertThrows(
      Exception.class,
      () -> respaldo.leer(CUERPO_GEMINI, ERROR_GEMINI)
    );

    assertThat(
      error.getMessage(),
      allOf(
        startsWith(ERROR_GEMINI),
        containsString("Groq: 429"),
        containsString("OpenRouter: respuesta sin resultados")
      )
    );
  }

  @Test
  public void leerIgnoraLosProveedoresSinClave() {
    RespaldoDeVision respaldo = new RespaldoDeVision(
      List.of(
        proveedor("Groq", "http://no-se-llama", null, true),
        proveedor("OR", "http://x", " ", false)
      )
    );

    Exception error = assertThrows(
      Exception.class,
      () -> respaldo.leer(CUERPO_GEMINI, ERROR_GEMINI)
    );

    assertThat(error.getMessage(), equalTo(ERROR_GEMINI));
    assertThat(pedidos, empty());
  }

  @Test
  public void leerSiElCuerpoNoTieneLaFormaDeGeminiRegistraElFalloYSigue() throws Exception {
    String urlB = servidor("/b", 200, RESPUESTA_OPENAI);
    RespaldoDeVision respaldo = new RespaldoDeVision(List.of(proveedor("A", urlB, "clave", true)));

    Exception error = assertThrows(
      Exception.class,
      () -> respaldo.leer("no es json", ERROR_GEMINI)
    );

    assertThat(error.getMessage(), containsString("A:"));
  }

  @Test
  public void llamarGeminiSiGeminiEstaSaturadoLeeConElRespaldo() throws Exception {
    HttpServer gemini = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    gemini.createContext("/", i -> responder(i, 503, "{\"error\":503}"));
    gemini.start();
    servidores.add(gemini);
    String urlRespaldo = servidor("/chat", 200, RESPUESTA_OPENAI);
    ServicioGeminiImpl servicio = new ServicioGeminiImpl(
      "http://127.0.0.1:" + gemini.getAddress().getPort() + "/models/",
      0,
      new RespaldoDeVision(List.of(proveedor("Groq", urlRespaldo, "clave", true)))
    );

    String respuesta = servicio.llamarGemini(CUERPO_GEMINI);

    assertThat(respuesta, containsString("candidates"));
    assertThat(respuesta, containsString("productos"));
  }

  @Test
  public void llamarGeminiSiGeminiRespondeNoConsultaAlRespaldo() throws Exception {
    HttpServer gemini = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    gemini.createContext("/", i -> responder(i, 200, "{\"candidates\":[]}"));
    gemini.start();
    servidores.add(gemini);
    String urlRespaldo = servidor("/chat", 200, RESPUESTA_OPENAI);
    ServicioGeminiImpl servicio = new ServicioGeminiImpl(
      "http://127.0.0.1:" + gemini.getAddress().getPort() + "/models/",
      0,
      new RespaldoDeVision(List.of(proveedor("Groq", urlRespaldo, "clave", true)))
    );

    String respuesta = servicio.llamarGemini(CUERPO_GEMINI);

    assertThat(respuesta, equalTo("{\"candidates\":[]}"));
    assertThat(pedidos, empty());
  }

  @Test
  public void llamarGeminiSiFallanGeminiYElRespaldoLanzaExcepcionConAmbosErrores()
    throws Exception {
    HttpServer gemini = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    gemini.createContext("/", i -> responder(i, 503, "{\"error\":503}"));
    gemini.start();
    servidores.add(gemini);
    String urlRespaldo = servidor("/chat", 500, "{\"error\":\"caido\"}");
    ServicioGeminiImpl servicio = new ServicioGeminiImpl(
      "http://127.0.0.1:" + gemini.getAddress().getPort() + "/models/",
      0,
      new RespaldoDeVision(List.of(proveedor("Groq", urlRespaldo, "clave", true)))
    );

    Exception error = assertThrows(Exception.class, () -> servicio.llamarGemini(CUERPO_GEMINI));

    assertThat(
      error.getMessage(),
      allOf(containsString("Error Gemini API"), containsString("503"), containsString("Groq: 500"))
    );
  }

  @Test
  public void unModeloQueTardaDeMasNoSeReintentaYSePasaAlSiguiente() throws Exception {
    List<String> modelos = Collections.synchronizedList(new ArrayList<>());
    HttpServer gemini = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    gemini.setExecutor(Executors.newCachedThreadPool());
    gemini.createContext(
      "/",
      i -> {
        String ruta = i.getRequestURI().getPath();
        String modelo = ruta.substring(ruta.lastIndexOf('/') + 1, ruta.indexOf(':'));
        modelos.add(modelo);
        if (modelo.equals("gemini-3.8-flash")) {
          try {
            Thread.sleep(600);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }
        responder(i, 200, "{\"candidates\":[]}");
      }
    );
    gemini.start();
    servidores.add(gemini);
    ServicioGeminiImpl servicio = new ServicioGeminiImpl(
      "http://127.0.0.1:" + gemini.getAddress().getPort() + "/models/",
      0,
      RespaldoDeVision.ninguno(),
      30000,
      150
    );

    String respuesta = servicio.llamarGemini(CUERPO_GEMINI);

    assertThat(respuesta, equalTo("{\"candidates\":[]}"));
    assertThat(modelos, contains("gemini-3.8-flash", "gemini-3.6-flash"));
  }

  @Test
  public void sinTiempoRestanteParaGeminiPasaAlRespaldoSinProbarTodosLosModelos() throws Exception {
    List<String> modelos = Collections.synchronizedList(new ArrayList<>());
    HttpServer gemini = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    gemini.createContext(
      "/",
      i -> {
        modelos.add(i.getRequestURI().getPath());
        responder(i, 503, "{\"error\":503}");
      }
    );
    gemini.start();
    servidores.add(gemini);
    String urlRespaldo = servidor("/chat", 200, RESPUESTA_OPENAI);
    ServicioGeminiImpl servicio = new ServicioGeminiImpl(
      "http://127.0.0.1:" + gemini.getAddress().getPort() + "/models/",
      0,
      new RespaldoDeVision(List.of(proveedor("Groq", urlRespaldo, "clave", true))),
      0,
      25000
    );

    String respuesta = servicio.llamarGemini(CUERPO_GEMINI);

    assertThat(respuesta, containsString("productos"));
    assertThat(modelos, hasSize(1));
  }

  private RespaldoDeVision.Proveedor proveedor(
    String nombre,
    String url,
    String clave,
    boolean modoJson
  ) {
    return new RespaldoDeVision.Proveedor(nombre, url, clave, "modelo-de-prueba", modoJson);
  }

  // Levanta un servidor local que registra el cuerpo recibido y responde con el código indicado
  private String servidor(String ruta, int codigo, String cuerpo) throws Exception {
    HttpServer servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    servidor.createContext(
      ruta,
      intercambio -> {
        pedidos.add(
          new String(intercambio.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)
        );
        responder(intercambio, codigo, cuerpo);
      }
    );
    servidor.start();
    servidores.add(servidor);
    return "http://127.0.0.1:" + servidor.getAddress().getPort() + ruta;
  }

  private void responder(
    com.sun.net.httpserver.HttpExchange intercambio,
    int codigo,
    String cuerpo
  ) throws java.io.IOException {
    intercambio.getRequestBody().readAllBytes();
    byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
    intercambio.sendResponseHeaders(codigo, bytes.length);
    try (OutputStream salida = intercambio.getResponseBody()) {
      salida.write(bytes);
    }
  }
}
