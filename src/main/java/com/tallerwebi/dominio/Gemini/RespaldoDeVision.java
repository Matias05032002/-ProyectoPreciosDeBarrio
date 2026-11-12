package com.tallerwebi.dominio.Gemini;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Cuando Gemini no responde, la misma foto se manda a otros proveedores con visión
// (Groq y OpenRouter, que hablan el formato de OpenAI). La respuesta se devuelve con la forma
// de Gemini para que ServicioOcr no tenga que enterarse de quién la leyó.
final class RespaldoDeVision {

  record Proveedor(String nombre, String url, String clave, String modelo, boolean modoJson) {
    boolean estaConfigurado() {
      return clave != null && !clave.isBlank();
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(RespaldoDeVision.class);
  private static final int HTTP_OK = 200;
  private static final int TIMEOUT_CONEXION_MS = 10000;
  private static final int TIMEOUT_LECTURA_MS = 30000;
  private static final String CAMPO_TEXTO = "text";
  private static final String CAMPO_PARTS = "parts";

  private final List<Proveedor> proveedores;

  RespaldoDeVision(List<Proveedor> proveedores) {
    this.proveedores = proveedores.stream().filter(Proveedor::estaConfigurado).toList();
  }

  static RespaldoDeVision ninguno() {
    return new RespaldoDeVision(List.of());
  }

  // Devuelve la respuesta con forma de Gemini, o lanza una excepción que incluye el error de Gemini
  // y el de cada proveedor de respaldo, para que el log diga qué pasó en cada uno.
  String leer(String cuerpoGemini, String errorDeGemini) throws Exception {
    List<String> fallos = new ArrayList<>();
    for (Proveedor proveedor : proveedores) {
      try {
        String respuesta = consultar(proveedor, cuerpoGemini);
        String nombre = proveedor.nombre();
        LOGGER.warn("Gemini no respondió; la imagen se leyó con {}", nombre);
        return respuesta;
      } catch (IOException | RuntimeException e) {
        fallos.add(proveedor.nombre() + ": " + e.getMessage());
      }
    }
    String detalle = fallos.isEmpty() ? "" : " | Respaldo: " + String.join(" | ", fallos);
    throw new Exception(errorDeGemini + detalle);
  }

  private String consultar(Proveedor proveedor, String cuerpoGemini) throws IOException {
    String cuerpo = aFormatoOpenAi(cuerpoGemini, proveedor);
    HttpURLConnection conn = (HttpURLConnection) URI
      .create(proveedor.url())
      .toURL()
      .openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    conn.setRequestProperty("Authorization", "Bearer " + proveedor.clave());
    conn.setDoOutput(true);
    conn.setConnectTimeout(TIMEOUT_CONEXION_MS);
    conn.setReadTimeout(TIMEOUT_LECTURA_MS);
    try (OutputStream os = conn.getOutputStream()) {
      os.write(cuerpo.getBytes(StandardCharsets.UTF_8));
    }
    int codigo = conn.getResponseCode();
    String respuesta = leer(conn, codigo);
    if (codigo != HTTP_OK) {
      throw new IOException(codigo + " - " + respuesta);
    }
    return aFormatoGemini(respuesta);
  }

  // Del cuerpo de Gemini (texto + imagen en base64) al mensaje de chat con imagen de OpenAI
  String aFormatoOpenAi(String cuerpoGemini, Proveedor proveedor) {
    JsonArray partes = JsonParser
      .parseString(cuerpoGemini)
      .getAsJsonObject()
      .getAsJsonArray("contents")
      .get(0)
      .getAsJsonObject()
      .getAsJsonArray(CAMPO_PARTS);
    JsonArray contenido = new JsonArray();
    for (JsonElement parte : partes) {
      contenido.add(convertirParte(parte.getAsJsonObject()));
    }
    JsonObject mensaje = new JsonObject();
    mensaje.addProperty("role", "user");
    mensaje.add("content", contenido);
    JsonArray mensajes = new JsonArray();
    mensajes.add(mensaje);

    JsonObject cuerpo = new JsonObject();
    cuerpo.addProperty("model", proveedor.modelo());
    cuerpo.add("messages", mensajes);
    cuerpo.addProperty("temperature", 0);
    if (proveedor.modoJson()) {
      JsonObject formato = new JsonObject();
      formato.addProperty("type", "json_object");
      cuerpo.add("response_format", formato);
    }
    return cuerpo.toString();
  }

  private JsonObject convertirParte(JsonObject parte) {
    JsonObject convertida = new JsonObject();
    if (parte.has(CAMPO_TEXTO)) {
      convertida.addProperty("type", CAMPO_TEXTO);
      convertida.addProperty(CAMPO_TEXTO, parte.get(CAMPO_TEXTO).getAsString());
      return convertida;
    }
    JsonObject imagen = parte.getAsJsonObject("inline_data");
    JsonObject url = new JsonObject();
    url.addProperty(
      "url",
      "data:" +
      imagen.get("mime_type").getAsString() +
      ";base64," +
      imagen.get("data").getAsString()
    );
    convertida.addProperty("type", "image_url");
    convertida.add("image_url", url);
    return convertida;
  }

  // De la respuesta de chat de OpenAI a {"candidates":[{"content":{"parts":[{"text":...}]}}]}
  String aFormatoGemini(String respuestaOpenAi) throws IOException {
    JsonArray opciones = JsonParser
      .parseString(respuestaOpenAi)
      .getAsJsonObject()
      .getAsJsonArray("choices");
    if (opciones == null || opciones.isEmpty()) {
      throw new IOException("respuesta sin resultados: " + respuestaOpenAi);
    }
    JsonObject mensaje = opciones.get(0).getAsJsonObject().getAsJsonObject("message");
    JsonElement contenido = mensaje == null ? null : mensaje.get("content");
    if (contenido == null || contenido.isJsonNull() || contenido.getAsString().isBlank()) {
      throw new IOException("respuesta vacía: " + respuestaOpenAi);
    }
    String texto = contenido.getAsString().replaceAll("(?s)<think>.*?</think>", "").trim();

    JsonObject parteTexto = new JsonObject();
    parteTexto.addProperty(CAMPO_TEXTO, texto);
    JsonArray partes = new JsonArray();
    partes.add(parteTexto);
    JsonObject contenidoGemini = new JsonObject();
    contenidoGemini.add(CAMPO_PARTS, partes);
    JsonObject candidato = new JsonObject();
    candidato.add("content", contenidoGemini);
    JsonArray candidatos = new JsonArray();
    candidatos.add(candidato);
    JsonObject raiz = new JsonObject();
    raiz.add("candidates", candidatos);
    return raiz.toString();
  }

  private String leer(HttpURLConnection conn, int codigo) throws IOException {
    StringBuilder sb = new StringBuilder();
    try (InputStream is = codigo == HTTP_OK ? conn.getInputStream() : conn.getErrorStream()) {
      if (is == null) {
        return "";
      }
      try (
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
      ) {
        String linea = br.readLine();
        while (linea != null) {
          sb.append(linea);
          linea = br.readLine();
        }
      }
    }
    return sb.toString();
  }
}
