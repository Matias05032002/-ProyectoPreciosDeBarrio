package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Consulta.Consulta;
import com.tallerwebi.dominio.Consulta.ServicioConsulta;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorConsultasTest {

  private ControladorConsultas controladorConsultas;
  private ServicioConsulta servicioConsultaMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    servicioConsultaMock = mock(ServicioConsulta.class);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
    controladorConsultas = new ControladorConsultas(servicioConsultaMock);
  }

  @Test
  public void listarConsultasSinRolDeberiaRedirigirAlHome() {
    when(sessionMock.getAttribute("ROL")).thenReturn(null);

    ModelAndView mav = controladorConsultas.listarConsultas(requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/home"));
    verifyNoInteractions(servicioConsultaMock);
  }

  @Test
  public void listarConsultasConRolUserDeberiaRedirigirAlHome() {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");

    ModelAndView mav = controladorConsultas.listarConsultas(requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/home"));
    verifyNoInteractions(servicioConsultaMock);
  }

  @Test
  public void listarConsultasConRolComercioDeberiaRedirigirAlHome() {
    when(sessionMock.getAttribute("ROL")).thenReturn("COMERCIO");

    ModelAndView mav = controladorConsultas.listarConsultas(requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/home"));
    verifyNoInteractions(servicioConsultaMock);
  }

  @Test
  public void listarConsultasConRolAdminDeberiaMostrarVistaConConsultas() {
    when(sessionMock.getAttribute("ROL")).thenReturn("ADMIN");
    Consulta consulta = new Consulta();
    consulta.setNombre("Juan");
    List<Consulta> consultas = Arrays.asList(consulta);
    when(servicioConsultaMock.listarTodas()).thenReturn(consultas);

    ModelAndView mav = controladorConsultas.listarConsultas(requestMock);

    assertThat(mav.getViewName(), equalTo("admin/consultas"));
    @SuppressWarnings("unchecked")
    List<Consulta> resultado = (List<Consulta>) mav.getModel().get("consultas");
    assertThat(resultado, hasSize(1));
  }

  @Test
  public void listarConsultasConRolAdminDeberiaLlamarAlServicio() {
    when(sessionMock.getAttribute("ROL")).thenReturn("ADMIN");
    when(servicioConsultaMock.listarTodas()).thenReturn(Collections.emptyList());

    controladorConsultas.listarConsultas(requestMock);

    verify(servicioConsultaMock, times(1)).listarTodas();
  }

  @Test
  public void listarConsultasConRolAdminSinConsultasDeberiaMostrarListaVacia() {
    when(sessionMock.getAttribute("ROL")).thenReturn("ADMIN");
    when(servicioConsultaMock.listarTodas()).thenReturn(Collections.emptyList());

    ModelAndView mav = controladorConsultas.listarConsultas(requestMock);

    assertThat(mav.getViewName(), equalTo("admin/consultas"));
    @SuppressWarnings("unchecked")
    List<Consulta> resultado = (List<Consulta>) mav.getModel().get("consultas");
    assertThat(resultado, hasSize(0));
  }
}
