package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Consulta.Consulta;
import com.tallerwebi.dominio.Consulta.ServicioConsulta;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorOpcionesTest {

  private ControladorOpciones controladorOpciones;
  private ServicioConsulta servicioConsultaMock;
  private HttpServletRequest requestMock;

  @BeforeEach
  public void init() {
    servicioConsultaMock = mock(ServicioConsulta.class);
    requestMock = mock(HttpServletRequest.class);
    controladorOpciones = new ControladorOpciones(servicioConsultaMock);
  }

  @Test
  public void sobreLaAppDeberiaRetornarVistaSobreLaApp() {
    ModelAndView mav = controladorOpciones.sobreLaApp();

    assertThat(mav.getViewName(), equalTo("sobre-la-app"));
    verifyNoInteractions(servicioConsultaMock);
  }

  @Test
  public void darFeedbackDeberiaRetornarVistaFeedback() {
    ModelAndView mav = controladorOpciones.darFeedback();

    assertThat(mav.getViewName(), equalTo("feedback"));
    verifyNoInteractions(servicioConsultaMock);
  }

  @Test
  public void guardarFeedbackDeberiaLlamarAlServicioYMostrarMensajeExito() {
    Consulta consulta = new Consulta();
    consulta.setNombre("Ana");
    consulta.setEmail("ana@test.com");
    consulta.setAsunto("Sugerencia");
    consulta.setMensaje("Quiero que agreguen más comercios.");

    ModelAndView mav = controladorOpciones.guardarFeedback(consulta, requestMock);

    assertThat(mav.getViewName(), equalTo("feedback"));
    assertThat(
      mav.getModel().get("mensajeExito"),
      equalTo("Tu mensaje fue enviado correctamente.")
    );
    verify(servicioConsultaMock, times(1)).guardar(consulta);
  }

  @Test
  public void guardarFeedbackDeberiaLlamarGuardarConLaConsultaRecibida() {
    Consulta consulta = new Consulta();
    consulta.setNombre("Carlos");

    controladorOpciones.guardarFeedback(consulta, requestMock);

    verify(servicioConsultaMock).guardar(consulta);
  }
}
