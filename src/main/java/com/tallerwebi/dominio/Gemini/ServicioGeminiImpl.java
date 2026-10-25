package com.tallerwebi.dominio.Gemini;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.stereotype.Component;

@Component
public class ServicioGeminiImpl implements ServicioGemini {

  private static final String GROQ_API_KEY = resolverApiKey();
  private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
  private static final int HTTP_OK = 200;

  @Override
  public String llamar(String prompt) throws Exception {
    String requestBody = construirBody(prompt);
    return llamarGroq(requestBody);
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
    URL url = new URL(GROQ_URL);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    conn.setRequestProperty("Authorization", "Bearer " + GROQ_API_KEY);
    conn.setDoOutput(true);
    conn.setConnectTimeout(10000);
    conn.setReadTimeout(30000);
    try (OutputStream os = conn.getOutputStream()) {
      os.write(requestBody.getBytes("UTF-8"));
    }
    int code = conn.getResponseCode();
    InputStream is = (code == HTTP_OK) ? conn.getInputStream() : conn.getErrorStream();
    StringBuilder sb = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
      String line = br.readLine();
      while (line != null) {
        sb.append(line);
        line = br.readLine();
      }
    }
    if (code != HTTP_OK) {
      throw new Exception("Error Groq API: " + code + " - " + sb);
    }
    return sb.toString();
  }

  private static String resolverApiKey() {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    String clave = dotenv.get("GROQ_API_KEY");
    if (clave == null || clave.isEmpty()) {
      clave = System.getenv("GROQ_API_KEY");
    }
    return clave;
  }
}
