package com.tallerwebi.dominio.Gemini;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioGeminiTest {

  private ServicioGeminiImpl servicioGemini;

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
}
