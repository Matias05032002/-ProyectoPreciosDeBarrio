package com.tallerwebi.presentacion.Asistente;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Asistente.ServicioAsistente;
import com.tallerwebi.presentacion.ControladorAsistente;
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
    when(servicioAsistenteMock.consultar("leche", null, null)).thenReturn("La leche está a $100.");

    Map<String, Object> body = Map.of("pregunta", "leche");
    ResponseEntity<?> response = controladorAsistente.consultar(body);

    assertThat(response.getStatusCode().value(), equalTo(200));
    Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
    assertThat(responseBody.get("respuesta"), equalTo("La leche está a $100."));
  }

  @Test
  public void consultarConCoordenadasLasPasaAlServicio() throws Exception {
    when(servicioAsistenteMock.consultar("leche", -34.6037, -58.3816))
      .thenReturn("Respuesta con ubicación.");

    Map<String, Object> body = Map.of(
      "pregunta",
      "leche",
      "latitud",
      -34.6037,
      "longitud",
      -58.3816
    );
    ResponseEntity<?> response = controladorAsistente.consultar(body);

    assertThat(response.getStatusCode().value(), equalTo(200));
    verify(servicioAsistenteMock).consultar("leche", -34.6037, -58.3816);
  }

  @Test
  public void consultarCuandoServicioFallaDevuelve500() throws Exception {
    when(servicioAsistenteMock.consultar(anyString(), any(), any()))
      .thenThrow(new Exception("Error de Groq"));

    Map<String, Object> body = Map.of("pregunta", "leche");
    ResponseEntity<?> response = controladorAsistente.consultar(body);

    assertThat(response.getStatusCode().value(), equalTo(500));
    Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
    assertThat(responseBody.get("error"), equalTo("Error de Groq"));
  }

  @Test
  public void consultarSinLatitudLaPasaComoNull() throws Exception {
    when(servicioAsistenteMock.consultar("aceite", null, null))
      .thenReturn("Respuesta sin ubicación.");

    Map<String, Object> body = Map.of("pregunta", "aceite");
    controladorAsistente.consultar(body);

    verify(servicioAsistenteMock).consultar("aceite", null, null);
  }
}
