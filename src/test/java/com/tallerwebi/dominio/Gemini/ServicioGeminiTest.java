package com.tallerwebi.dominio.Gemini;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioGeminiTest {

  private static final String RESPUESTA_OK = "{\"candidates\":[]}";

  private ServicioGeminiImpl servicioGemini;
  private HttpServer servidorFalso;
  private final List<String> modelosLlamados = Collections.synchronizedList(new ArrayList<>());

  @BeforeEach
  public void init() {
    servicioGemini = new ServicioGeminiImpl();
  }

  @Test
  public void escapeJsonDeberiaEscaparComillasDobles() throws Exception {
    Method method = ServicioGeminiImpl.class.getDeclaredMethod("escapeJson", String.class);
    method.setAccessible(true);
    String resultado = (String) method.invoke(servicioGemini, "hola \"mundo\"");
    assertThat(resultado, containsString("\\\"mundo\\\""));
  }

  @Test
  public void escapeJsonDeberiaEscaparBarrasInversas() throws Exception {
    Method method = ServicioGeminiImpl.class.getDeclaredMethod("escapeJson", String.class);
    method.setAccessible(true);
    String resultado = (String) method.invoke(servicioGemini, "C:\\ruta");
    assertThat(resultado, containsString("C:\\\\ruta"));
  }

  @Test
  public void escapeJsonDeberiaEscaparSaltoDeLinea() throws Exception {
    Method method = ServicioGeminiImpl.class.getDeclaredMethod("escapeJson", String.class);
    method.setAccessible(true);
    String resultado = (String) method.invoke(servicioGemini, "linea1\nlinea2");
    assertThat(resultado, containsString("\\n"));
    assertThat(resultado, not(containsString("\n")));
  }

  @Test
  public void escapeJsonDeberiaEscaparRetornoCarro() throws Exception {
    Method method = ServicioGeminiImpl.class.getDeclaredMethod("escapeJson", String.class);
    method.setAccessible(true);
    String resultado = (String) method.invoke(servicioGemini, "linea\r");
    assertThat(resultado, containsString("\\r"));
  }

  @Test
  public void escapeJsonDeberiaEscaparTabulacion() throws Exception {
    Method method = ServicioGeminiImpl.class.getDeclaredMethod("escapeJson", String.class);
    method.setAccessible(true);
    String resultado = (String) method.invoke(servicioGemini, "col1\tcol2");
    assertThat(resultado, containsString("\\t"));
  }

  @Test
  public void escapeJsonDeberiaEnvolverEnComillas() throws Exception {
    Method method = ServicioGeminiImpl.class.getDeclaredMethod("escapeJson", String.class);
    method.setAccessible(true);
    String resultado = (String) method.invoke(servicioGemini, "hola");
    assertThat(resultado, startsWith("\""));
    assertThat(resultado, endsWith("\""));
  }

  @Test
  public void construirBodyDeberiaContenerElPrompt() throws Exception {
    Method method = ServicioGeminiImpl.class.getDeclaredMethod("construirBody", String.class);
    method.setAccessible(true);
    String resultado = (String) method.invoke(servicioGemini, "precio del pan");
    assertThat(resultado, containsString("precio del pan"));
    assertThat(resultado, containsString("messages"));
  }

  // --- llamarGemini: reintentos y cambio de modelo ---

  @AfterEach
  public void apagarServidor() {
    if (servidorFalso != null) {
      servidorFalso.stop(0);
    }
  }

  @Test
  public void llamarGeminiSiElPrimerModeloEstaSaturadoUsaElSiguiente() throws Exception {
    ServicioGeminiImpl servicio = servicioContraServidorFalso(
      Map.of("gemini-3.8-flash", 503, "gemini-3.6-flash", 200)
    );

    String respuesta = servicio.llamarGemini("{}");

    assertThat(respuesta, equalTo(RESPUESTA_OK));
    assertThat(
      modelosLlamados,
      contains("gemini-3.8-flash", "gemini-3.8-flash", "gemini-3.6-flash")
    );
  }

  @Test
  public void llamarGeminiSiElModeloNoExisteMasPasaAlSiguienteSinReintentar() throws Exception {
    ServicioGeminiImpl servicio = servicioContraServidorFalso(
      Map.of("gemini-3.8-flash", 404, "gemini-3.6-flash", 200)
    );

    servicio.llamarGemini("{}");

    assertThat(modelosLlamados, contains("gemini-3.8-flash", "gemini-3.6-flash"));
  }

  @Test
  public void llamarGeminiConKeyInvalidaFallaSinProbarOtrosModelos() throws Exception {
    ServicioGeminiImpl servicio = servicioContraServidorFalso(Map.of("gemini-3.8-flash", 403));

    Exception error = assertThrows(Exception.class, () -> servicio.llamarGemini("{}"));

    assertThat(error.getMessage(), containsString("403"));
    assertThat(modelosLlamados, contains("gemini-3.8-flash"));
  }

  @Test
  public void llamarGeminiSiTodosLosModelosEstanSaturadosLanzaExcepcionConElUltimoError()
    throws Exception {
    ServicioGeminiImpl servicio = servicioContraServidorFalso(Map.of());

    Exception error = assertThrows(Exception.class, () -> servicio.llamarGemini("{}"));

    assertThat(error.getMessage(), containsString("503"));
    assertThat(modelosLlamados.size(), equalTo(8));
  }

  // Levanta un servidor HTTP local que responde con el código configurado para cada modelo.
  // Los modelos no configurados responden 503 (saturado).
  private ServicioGeminiImpl servicioContraServidorFalso(Map<String, Integer> codigoPorModelo)
    throws Exception {
    servidorFalso = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    servidorFalso.createContext(
      "/",
      intercambio -> {
        String ruta = intercambio.getRequestURI().getPath();
        String modelo = ruta.substring(ruta.lastIndexOf('/') + 1, ruta.indexOf(':'));
        modelosLlamados.add(modelo);
        intercambio.getRequestBody().readAllBytes();
        int codigo = codigoPorModelo.getOrDefault(modelo, 503);
        byte[] cuerpo =
          (codigo == 200 ? RESPUESTA_OK : "{\"error\":" + codigo + "}").getBytes(
              StandardCharsets.UTF_8
            );
        intercambio.sendResponseHeaders(codigo, cuerpo.length);
        try (OutputStream salida = intercambio.getResponseBody()) {
          salida.write(cuerpo);
        }
      }
    );
    servidorFalso.start();
    String url = "http://127.0.0.1:" + servidorFalso.getAddress().getPort() + "/models/";
    return new ServicioGeminiImpl(url, 0);
  }
}
