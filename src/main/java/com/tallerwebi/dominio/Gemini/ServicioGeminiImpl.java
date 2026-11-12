package com.tallerwebi.dominio.Gemini;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ServicioGeminiImpl implements ServicioGemini {

  private static final String GROQ_API_KEY = resolverApiKey("GROQ_API_KEY");
  private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
  private static final String GEMINI_API_KEY = resolverApiKey("GEMINI_API_KEY");
  private static final String GEMINI_URL_BASE =
    "https://generativelanguage.googleapis.com/v1beta/models/";
  private static final String OPENROUTER_API_KEY = resolverApiKey("OPENROUTER_API_KEY");
  private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
  private static final String GROQ_MODELO_VISION = "qwen/qwen3.8-27b";
  private static final String OPENROUTER_MODELO_VISION = "openrouter/free";
  // Si el primero está saturado o no existe, se prueba el siguiente
  private static final List<String> MODELOS_GEMINI = List.of(
    "gemini-3.8-flash",
    "gemini-3.6-flash",
    "gemini-3.5-flash-lite",
    "gemini-flash-latest"
  );
  private static final int INTENTOS_POR_MODELO = 2;
  private static final long ESPERA_ENTRE_INTENTOS_MS = 2000;
  // Tiempo máximo que se le da a Gemini (todos sus modelos juntos) antes de pasar al respaldo,
  // y espera máxima por respuesta: así el vecino no espera minutos con el spinner girando
  private static final long PRESUPUESTO_GEMINI_MS = 30000;
  private static final int LECTURA_GEMINI_MS = 25000;
  private static final int HTTP_TIMEOUT = 504;
  private static final int HTTP_OK = 200;
  private static final int HTTP_NOT_FOUND = 404;
  private static final String UTF_8 = "UTF-8";

  private final String geminiUrlBase;
  private final long esperaEntreIntentosMs;
  private final RespaldoDeVision respaldo;
  private final long presupuestoMs;
  private final int lecturaMs;

  public ServicioGeminiImpl() {
    this(GEMINI_URL_BASE, ESPERA_ENTRE_INTENTOS_MS, proveedoresDeRespaldo());
  }

  ServicioGeminiImpl(String geminiUrlBase, long esperaEntreIntentosMs) {
    this(geminiUrlBase, esperaEntreIntentosMs, RespaldoDeVision.ninguno());
  }

  ServicioGeminiImpl(String geminiUrlBase, long esperaEntreIntentosMs, RespaldoDeVision respaldo) {
    this(geminiUrlBase, esperaEntreIntentosMs, respaldo, PRESUPUESTO_GEMINI_MS, LECTURA_GEMINI_MS);
  }

  ServicioGeminiImpl(
    String geminiUrlBase,
    long esperaEntreIntentosMs,
    RespaldoDeVision respaldo,
    long presupuestoMs,
    int lecturaMs
  ) {
    this.geminiUrlBase = geminiUrlBase;
    this.esperaEntreIntentosMs = esperaEntreIntentosMs;
    this.respaldo = respaldo;
    this.presupuestoMs = presupuestoMs;
    this.lecturaMs = lecturaMs;
  }

  private static RespaldoDeVision proveedoresDeRespaldo() {
    return new RespaldoDeVision(
      List.of(
        new RespaldoDeVision.Proveedor("Groq", GROQ_URL, GROQ_API_KEY, GROQ_MODELO_VISION, true),
        new RespaldoDeVision.Proveedor(
          "OpenRouter",
          OPENROUTER_URL,
          OPENROUTER_API_KEY,
          OPENROUTER_MODELO_VISION,
          false
        )
      )
    );
  }

  @Override
  public String llamar(String prompt) throws Exception {
    String requestBody = construirBody(prompt);
    return llamarGroq(requestBody);
  }

  @Override
  public String llamar(String systemPrompt, String userPrompt) throws Exception {
    String requestBody =
      "{\"model\":\"openai/gpt-oss-20b\"," +
      "\"messages\":[" +
      "{\"role\":\"system\",\"content\":" +
      escapeJson(systemPrompt) +
      "}," +
      "{\"role\":\"user\",\"content\":" +
      escapeJson(userPrompt) +
      "}" +
      "]}";
    return llamarGroq(requestBody);
  }

  @Override
  public String llamarGemini(String requestBody) throws Exception {
    long limite = System.currentTimeMillis() + presupuestoMs;
    RespuestaHttp ultima = null;
    String ultimoModelo = "";
    for (String modelo : MODELOS_GEMINI) {
      ultimoModelo = modelo;
      ultima = intentarConModelo(modelo, requestBody, limite);
      if (ultima.codigo() == HTTP_OK) {
        return ultima.cuerpo();
      }
      if (!puedeProbarOtroModelo(ultima.codigo()) || System.currentTimeMillis() >= limite) {
        break;
      }
    }
    // Ningún modelo de Gemini pudo: se intenta con los proveedores de respaldo
    return respaldo.leer(
      requestBody,
      "Error Gemini API (" + ultimoModelo + "): " + ultima.codigo() + " - " + ultima.cuerpo()
    );
  }

  private RespuestaHttp intentarConModelo(String modelo, String requestBody, long limite)
    throws IOException {
    RespuestaHttp respuesta = enviarAGemini(modelo, requestBody);
    int intento = 1;
    while (debeReintentar(respuesta, intento, limite)) {
      esperar(intento);
      intento++;
      respuesta = enviarAGemini(modelo, requestBody);
    }
    return respuesta;
  }

  // Un modelo que ya tardó de más no se reintenta: se pasa al siguiente. Y si se agotó el tiempo
  // dado a Gemini, tampoco.
  private boolean debeReintentar(RespuestaHttp respuesta, int intento, long limite) {
    return (
      esErrorTemporal(respuesta.codigo()) &&
      respuesta.codigo() != HTTP_TIMEOUT &&
      intento < INTENTOS_POR_MODELO &&
      System.currentTimeMillis() < limite
    );
  }

  private RespuestaHttp enviarAGemini(String modelo, String requestBody) throws IOException {
    URL url = URI.create(geminiUrlBase + modelo + ":generateContent?key=" + GEMINI_API_KEY).toURL();
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    conn.setDoOutput(true);
    conn.setConnectTimeout(10000);
    conn.setReadTimeout(lecturaMs);
    try {
      try (OutputStream os = conn.getOutputStream()) {
        os.write(requestBody.getBytes(UTF_8));
      }
      int code = conn.getResponseCode();
      return new RespuestaHttp(code, leerRespuesta(conn, code));
    } catch (SocketTimeoutException e) {
      return new RespuestaHttp(HTTP_TIMEOUT, "El modelo tardo demasiado en responder");
    }
  }

  private boolean esErrorTemporal(int codigo) {
    return codigo == 429 || codigo >= 500;
  }

  private boolean puedeProbarOtroModelo(int codigo) {
    return codigo == HTTP_NOT_FOUND || esErrorTemporal(codigo);
  }

  private void esperar(int intento) {
    try {
      Thread.sleep(esperaEntreIntentosMs * intento);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private String construirBody(String prompt) {
    return (
      "{\"model\":\"openai/gpt-oss-20b\",\"messages\":[{\"role\":\"user\",\"content\":" +
      escapeJson(prompt) +
      "}]}"
    );
  }

  private String escapeJson(String text) {
    return (
      "\"" +
      text
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t") +
      "\""
    );
  }

  private String llamarGroq(String requestBody) throws Exception {
    URL url = URI.create(GROQ_URL).toURL();
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    conn.setRequestProperty("Authorization", "Bearer " + GROQ_API_KEY);
    conn.setDoOutput(true);
    conn.setConnectTimeout(10000);
    conn.setReadTimeout(30000);
    try (OutputStream os = conn.getOutputStream()) {
      os.write(requestBody.getBytes(UTF_8));
    }
    int code = conn.getResponseCode();
    String respuesta = leerRespuesta(conn, code);
    if (code != HTTP_OK) {
      throw new Exception("Error Groq API: " + code + " - " + respuesta);
    }
    return respuesta;
  }

  private String leerRespuesta(HttpURLConnection conn, int code) throws IOException {
    StringBuilder sb = new StringBuilder();
    try (InputStream is = (code == HTTP_OK) ? conn.getInputStream() : conn.getErrorStream()) {
      if (is == null) {
        return "";
      }
      try (BufferedReader br = new BufferedReader(new InputStreamReader(is, UTF_8))) {
        String line = br.readLine();
        while (line != null) {
          sb.append(line);
          line = br.readLine();
        }
      }
    }
    return sb.toString();
  }

  private static String resolverApiKey(String nombre) {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    String clave = dotenv.get(nombre);
    if (clave == null || clave.isEmpty()) {
      clave = System.getenv(nombre);
    }
    return clave;
  }

  private record RespuestaHttp(int codigo, String cuerpo) {}
}
