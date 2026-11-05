package com.tallerwebi.presentacion.Asistente;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Asistente.ServicioAsistente;
import com.tallerwebi.dominio.Asistente.ServicioLimiteConsultas;
import com.tallerwebi.presentacion.ControladorAsistente;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class ControladorAsistenteTest {

  private ServicioAsistente servicioAsistenteMock;
  private ControladorAsistente controladorAsistente;

  @BeforeEach
  public void init() {
    this.servicioAsistenteMock = mock(ServicioAsistente.class);
    this.controladorAsistente =
      new ControladorAsistente(servicioAsistenteMock, mock(ServicioLimiteConsultas.class));
  }

  @Test
  public void consultarDevuelveRespuestaExitosa() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);
    when(sessionMock.getAttribute("donPesoHistorial")).thenReturn(null);
    when(servicioAsistenteMock.consultar(eq("leche"), isNull(), isNull(), any(), anyList()))
      .thenReturn("La leche está a $100.");

    Map<String, Object> body = Map.of("pregunta", "leche");
    ResponseEntity<?> response = controladorAsistente.consultar(body, sessionMock);

    assertThat(response.getStatusCode().value(), equalTo(200));
    Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
    assertThat(responseBody.get("respuesta"), equalTo("La leche está a $100."));
  }

  @Test
  public void consultarConCoordenadasLasPasaAlServicio() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);
    when(sessionMock.getAttribute("donPesoHistorial")).thenReturn(null);
    when(servicioAsistenteMock.consultar(eq("leche"), eq(-34.6037), eq(-58.3816), any(), anyList()))
      .thenReturn("Respuesta con ubicación.");

    Map<String, Object> body = Map.of(
      "pregunta",
      "leche",
      "latitud",
      -34.6037,
      "longitud",
      -58.3816
    );
    ResponseEntity<?> response = controladorAsistente.consultar(body, sessionMock);

    assertThat(response.getStatusCode().value(), equalTo(200));
    verify(servicioAsistenteMock)
      .consultar(eq("leche"), eq(-34.6037), eq(-58.3816), any(), anyList());
  }

  @Test
  public void consultarCuandoServicioFallaDevuelve500() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);
    when(sessionMock.getAttribute("donPesoHistorial")).thenReturn(null);
    when(servicioAsistenteMock.consultar(anyString(), any(), any(), any(), anyList()))
      .thenThrow(new Exception("Error de Groq"));

    Map<String, Object> body = Map.of("pregunta", "leche");
    ResponseEntity<?> response = controladorAsistente.consultar(body, sessionMock);

    assertThat(response.getStatusCode().value(), equalTo(500));
    Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
    assertThat(
      responseBody.get("error"),
      equalTo("Don Peso no pudo responder en este momento. Probá de nuevo en un rato.")
    );
  }

  @Test
  public void consultarCuandoServicioFallaNoDeberiaExponerElMensajeDeLaExcepcion()
    throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);
    when(servicioAsistenteMock.consultar(anyString(), any(), any(), any(), anyList()))
      .thenThrow(new Exception("could not execute statement [select * from reporte]"));

    ResponseEntity<?> response = controladorAsistente.consultar(
      Map.of("pregunta", "leche"),
      sessionMock
    );

    Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
    assertThat(responseBody.get("error").toString(), not(containsString("select")));
  }

  @Test
  public void consultarSinPreguntaDevuelve400SinLlamarAlServicio() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);

    ResponseEntity<?> response = controladorAsistente.consultar(new HashMap<>(), sessionMock);

    assertThat(response.getStatusCode().value(), equalTo(400));
    verify(servicioAsistenteMock, never()).consultar(any(), any(), any(), any(), any());
  }

  @Test
  public void consultarConPreguntaEnBlancoDevuelve400() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);

    ResponseEntity<?> response = controladorAsistente.consultar(
      Map.of("pregunta", "   "),
      sessionMock
    );

    assertThat(response.getStatusCode().value(), equalTo(400));
    verify(servicioAsistenteMock, never()).consultar(any(), any(), any(), any(), any());
  }

  @Test
  public void consultarConPreguntaQueNoEsTextoDevuelve400() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);

    ResponseEntity<?> response = controladorAsistente.consultar(
      Map.of("pregunta", 12345),
      sessionMock
    );

    assertThat(response.getStatusCode().value(), equalTo(400));
    verify(servicioAsistenteMock, never()).consultar(any(), any(), any(), any(), any());
  }

  @Test
  public void consultarConPreguntaDemasiadoLargaDevuelve400() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);

    ResponseEntity<?> response = controladorAsistente.consultar(
      Map.of("pregunta", "a".repeat(301)),
      sessionMock
    );

    assertThat(response.getStatusCode().value(), equalTo(400));
    verify(servicioAsistenteMock, never()).consultar(any(), any(), any(), any(), any());
  }

  @Test
  public void consultarConCoordenadasQueNoSonNumerosLasIgnora() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);
    when(servicioAsistenteMock.consultar(eq("leche"), isNull(), isNull(), any(), anyList()))
      .thenReturn("Respuesta sin ubicación.");

    Map<String, Object> body = Map.of("pregunta", "leche", "latitud", "abc", "longitud", "xyz");
    ResponseEntity<?> response = controladorAsistente.consultar(body, sessionMock);

    assertThat(response.getStatusCode().value(), equalTo(200));
    verify(servicioAsistenteMock).consultar(eq("leche"), isNull(), isNull(), any(), anyList());
  }

  @Test
  public void consultarConCoordenadasFueraDeRangoLasIgnora() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);
    when(servicioAsistenteMock.consultar(eq("leche"), isNull(), isNull(), any(), anyList()))
      .thenReturn("Respuesta sin ubicación.");

    Map<String, Object> body = Map.of("pregunta", "leche", "latitud", 500.0, "longitud", -999.0);
    controladorAsistente.consultar(body, sessionMock);

    verify(servicioAsistenteMock).consultar(eq("leche"), isNull(), isNull(), any(), anyList());
  }

  @Test
  public void consultarSinLatitudLaPasaComoNull() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);
    when(sessionMock.getAttribute("donPesoHistorial")).thenReturn(null);
    when(servicioAsistenteMock.consultar(eq("aceite"), isNull(), isNull(), any(), anyList()))
      .thenReturn("Respuesta sin ubicación.");

    Map<String, Object> body = Map.of("pregunta", "aceite");
    controladorAsistente.consultar(body, sessionMock);

    verify(servicioAsistenteMock).consultar(eq("aceite"), isNull(), isNull(), any(), anyList());
  }

  @Test
  public void historialSinConversacionDevuelveListaVacia() {
    HttpSession sessionMock = mock(HttpSession.class);
    when(sessionMock.getAttribute("donPesoHistorial")).thenReturn(null);

    ResponseEntity<java.util.List<Map<String, String>>> response = controladorAsistente.historial(
      sessionMock
    );

    assertThat(response.getStatusCode().value(), equalTo(200));
    assertThat(response.getBody(), empty());
  }

  @Test
  public void historialDevuelveLaConversacionGuardadaEnLaSesion() {
    HttpSession sessionMock = mock(HttpSession.class);
    java.util.List<Map<String, String>> guardado = java.util.List.of(
      Map.of("role", "user", "content", "leche"),
      Map.of("role", "assistant", "content", "La leche está a $100.")
    );
    when(sessionMock.getAttribute("donPesoHistorial")).thenReturn(guardado);

    assertThat(controladorAsistente.historial(sessionMock).getBody(), equalTo(guardado));
  }

  @Test
  public void reiniciarBorraLaConversacionDeLaSesion() {
    HttpSession sessionMock = mock(HttpSession.class);

    ResponseEntity<Void> response = controladorAsistente.reiniciar(sessionMock);

    assertThat(response.getStatusCode().value(), equalTo(204));
    verify(sessionMock).removeAttribute("donPesoHistorial");
  }

  @Test
  public void consultarLePasaAlServicioElUsuarioDeLaSesion() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);
    when(sessionMock.getAttribute("ID")).thenReturn(7L);
    when(servicioAsistenteMock.consultar(eq("leche"), isNull(), isNull(), eq(7L), anyList()))
      .thenReturn("ok");
    Map<String, Object> body = new HashMap<>();
    body.put("pregunta", "leche");

    controladorAsistente.consultar(body, sessionMock);

    verify(servicioAsistenteMock).consultar(eq("leche"), isNull(), isNull(), eq(7L), anyList());
  }

  @Test
  public void consultarSinSesionIniciadaPasaUsuarioNulo() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);
    when(sessionMock.getAttribute("ID")).thenReturn(null);
    when(servicioAsistenteMock.consultar(eq("leche"), isNull(), isNull(), isNull(), anyList()))
      .thenReturn("ok");
    Map<String, Object> body = new HashMap<>();
    body.put("pregunta", "leche");

    controladorAsistente.consultar(body, sessionMock);

    verify(servicioAsistenteMock).consultar(eq("leche"), isNull(), isNull(), isNull(), anyList());
  }
}
