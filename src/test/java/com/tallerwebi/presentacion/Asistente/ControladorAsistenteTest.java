package com.tallerwebi.presentacion.Asistente;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Asistente.ServicioAsistente;
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
    this.controladorAsistente = new ControladorAsistente(servicioAsistenteMock);
  }

  @Test
  public void consultarDevuelveRespuestaExitosa() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);
    when(sessionMock.getAttribute("donPesoHistorial")).thenReturn(null);
    when(servicioAsistenteMock.consultar(eq("leche"), isNull(), isNull(), anyList()))
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
    when(servicioAsistenteMock.consultar(eq("leche"), eq(-34.6037), eq(-58.3816), anyList()))
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
    verify(servicioAsistenteMock).consultar(eq("leche"), eq(-34.6037), eq(-58.3816), anyList());
  }

  @Test
  public void consultarCuandoServicioFallaDevuelve500() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);
    when(sessionMock.getAttribute("donPesoHistorial")).thenReturn(null);
    when(servicioAsistenteMock.consultar(anyString(), any(), any(), anyList()))
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
    when(servicioAsistenteMock.consultar(anyString(), any(), any(), anyList()))
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
    verify(servicioAsistenteMock, never()).consultar(any(), any(), any(), any());
  }

  @Test
  public void consultarConPreguntaEnBlancoDevuelve400() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);

    ResponseEntity<?> response = controladorAsistente.consultar(
      Map.of("pregunta", "   "),
      sessionMock
    );

    assertThat(response.getStatusCode().value(), equalTo(400));
    verify(servicioAsistenteMock, never()).consultar(any(), any(), any(), any());
  }

  @Test
  public void consultarConPreguntaQueNoEsTextoDevuelve400() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);

    ResponseEntity<?> response = controladorAsistente.consultar(
      Map.of("pregunta", 12345),
      sessionMock
    );

    assertThat(response.getStatusCode().value(), equalTo(400));
    verify(servicioAsistenteMock, never()).consultar(any(), any(), any(), any());
  }

  @Test
  public void consultarConPreguntaDemasiadoLargaDevuelve400() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);

    ResponseEntity<?> response = controladorAsistente.consultar(
      Map.of("pregunta", "a".repeat(301)),
      sessionMock
    );

    assertThat(response.getStatusCode().value(), equalTo(400));
    verify(servicioAsistenteMock, never()).consultar(any(), any(), any(), any());
  }

  @Test
  public void consultarConCoordenadasQueNoSonNumerosLasIgnora() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);
    when(servicioAsistenteMock.consultar(eq("leche"), isNull(), isNull(), anyList()))
      .thenReturn("Respuesta sin ubicación.");

    Map<String, Object> body = Map.of("pregunta", "leche", "latitud", "abc", "longitud", "xyz");
    ResponseEntity<?> response = controladorAsistente.consultar(body, sessionMock);

    assertThat(response.getStatusCode().value(), equalTo(200));
    verify(servicioAsistenteMock).consultar(eq("leche"), isNull(), isNull(), anyList());
  }

  @Test
  public void consultarConCoordenadasFueraDeRangoLasIgnora() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);
    when(servicioAsistenteMock.consultar(eq("leche"), isNull(), isNull(), anyList()))
      .thenReturn("Respuesta sin ubicación.");

    Map<String, Object> body = Map.of("pregunta", "leche", "latitud", 500.0, "longitud", -999.0);
    controladorAsistente.consultar(body, sessionMock);

    verify(servicioAsistenteMock).consultar(eq("leche"), isNull(), isNull(), anyList());
  }

  @Test
  public void consultarSinLatitudLaPasaComoNull() throws Exception {
    HttpSession sessionMock = mock(HttpSession.class);
    when(sessionMock.getAttribute("donPesoHistorial")).thenReturn(null);
    when(servicioAsistenteMock.consultar(eq("aceite"), isNull(), isNull(), anyList()))
      .thenReturn("Respuesta sin ubicación.");

    Map<String, Object> body = Map.of("pregunta", "aceite");
    controladorAsistente.consultar(body, sessionMock);

    verify(servicioAsistenteMock).consultar(eq("aceite"), isNull(), isNull(), anyList());
  }
}
