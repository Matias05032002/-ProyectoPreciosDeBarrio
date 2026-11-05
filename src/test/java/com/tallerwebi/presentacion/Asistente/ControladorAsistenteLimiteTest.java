package com.tallerwebi.presentacion.Asistente;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.tallerwebi.dominio.Asistente.ServicioAsistente;
import com.tallerwebi.dominio.Asistente.ServicioLimiteConsultas;
import com.tallerwebi.dominio.excepcion.LimiteDeConsultasExcedido;
import com.tallerwebi.presentacion.ControladorAsistente;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

class ControladorAsistenteLimiteTest {

  private ServicioAsistente servicioAsistente;
  private ServicioLimiteConsultas servicioLimite;
  private ControladorAsistente controlador;

  @BeforeEach
  void preparar() {
    servicioAsistente = mock(ServicioAsistente.class);
    servicioLimite = mock(ServicioLimiteConsultas.class);
    controlador = new ControladorAsistente(servicioAsistente, servicioLimite);
  }

  @Test
  void siSeExcedeElLimiteRespondeSinLlamarAlAsistente() throws Exception {
    doThrow(new LimiteDeConsultasExcedido()).when(servicioLimite).registrarConsulta(anyString());

    ResponseEntity<?> respuesta = controlador.consultar(
      Map.of("pregunta", "leche"),
      new MockHttpSession()
    );

    assertThat(respuesta.getStatusCode().value(), equalTo(429));
    verify(servicioAsistente, never()).consultar(any(), any(), any(), any(), anyList());
  }

  @Test
  void conSesionIniciadaElCupoEsDelUsuario() throws Exception {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute("ID", 7L);

    controlador.consultar(Map.of("pregunta", "leche"), session);

    verify(servicioLimite).registrarConsulta("usuario-7");
  }

  @Test
  void unaPreguntaInvalidaNoCuentaParaElLimite() throws Exception {
    ResponseEntity<?> respuesta = controlador.consultar(
      Map.of("pregunta", ""),
      new MockHttpSession()
    );

    assertThat(respuesta.getStatusCode().value(), equalTo(400));
    verify(servicioLimite, never()).registrarConsulta(anyString());
  }
}
