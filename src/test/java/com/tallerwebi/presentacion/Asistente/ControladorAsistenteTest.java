package com.tallerwebi.presentacion.Asistente;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Asistente.ServicioAsistente;
import com.tallerwebi.presentacion.ControladorAsistente;
import jakarta.servlet.http.HttpSession;
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
    assertThat(responseBody.get("error"), equalTo("Error de Groq"));
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
