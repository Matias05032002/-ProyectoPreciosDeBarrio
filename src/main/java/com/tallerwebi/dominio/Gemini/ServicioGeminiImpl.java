package com.tallerwebi.dominio.Gemini;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.stereotype.Component;

@Component
public class ServicioGeminiImpl implements ServicioGemini {

  private static final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY");
  private static final String GEMINI_URL =
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent?key=" +
    GEMINI_API_KEY;
  private static final int HTTP_OK = 200;

  @Override
  public String llamar(String requestBody) throws Exception {
    URL url = new URL(GEMINI_URL);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setDoOutput(true);
    conn.setConnectTimeout(30000);
    conn.setReadTimeout(60000);

    try (OutputStream os = conn.getOutputStream()) {
      os.write(requestBody.getBytes("UTF-8"));
    }

    int code = conn.getResponseCode();
    InputStream is = (code >= HTTP_OK && code < 300)
      ? conn.getInputStream()
      : conn.getErrorStream();

    StringBuilder sb = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
      String line = br.readLine();
      while (line != null) {
        sb.append(line);
        line = br.readLine();
      }
    }

    if (code != HTTP_OK) {
      throw new Exception("Error Gemini API: " + code + " - " + sb);
    }
    return sb.toString();
  }
}
