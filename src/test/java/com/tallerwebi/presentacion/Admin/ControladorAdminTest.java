package com.tallerwebi.presentacion.Admin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Admin.ServicioAdmin;
import com.tallerwebi.presentacion.ControladorAdmin;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorAdminTest {

  private ControladorAdmin controladorAdmin;
  private ServicioAdmin servicioAdminMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    servicioAdminMock = mock(ServicioAdmin.class);
    sessionMock = mock(HttpSession.class);
    controladorAdmin = new ControladorAdmin(servicioAdminMock);
  }

  @Test
  public void sinRolDeberiaRedirigirAlHome() {
    when(sessionMock.getAttribute("ROL")).thenReturn(null);

    ModelAndView mav = controladorAdmin.verEstadisticas(sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/home"));
    verifyNoInteractions(servicioAdminMock);
  }

  @Test
  public void conRolUserDeberiaRedirigirAlHome() {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");

    ModelAndView mav = controladorAdmin.verEstadisticas(sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/home"));
    verifyNoInteractions(servicioAdminMock);
  }

  @Test
  public void conRolComercioDeberiaRedirigirAlHome() {
    when(sessionMock.getAttribute("ROL")).thenReturn("COMERCIO");

    ModelAndView mav = controladorAdmin.verEstadisticas(sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/home"));
    verifyNoInteractions(servicioAdminMock);
  }

  @Test
  public void conRolAdminDeberiaMostrarVistaEstadisticas() {
    when(sessionMock.getAttribute("ROL")).thenReturn("ADMIN");
    when(servicioAdminMock.totalUsuarios()).thenReturn(10L);
    when(servicioAdminMock.totalReportes()).thenReturn(50L);
    when(servicioAdminMock.productosMasReportados()).thenReturn(new ArrayList<>());
    when(servicioAdminMock.comerciosMasReportados()).thenReturn(new ArrayList<>());
    when(servicioAdminMock.reportesPorMes()).thenReturn(new ArrayList<>());
    when(servicioAdminMock.productosMasDudosos()).thenReturn(new ArrayList<>());
    when(servicioAdminMock.usuariosMasActivos()).thenReturn(new ArrayList<>());
    when(servicioAdminMock.distribucionPorRangoEtario()).thenReturn(new ArrayList<>());

    ModelAndView mav = controladorAdmin.verEstadisticas(sessionMock);

    assertThat(mav.getViewName(), equalTo("admin/estadisticas"));
    assertThat(mav.getModel().get("totalUsuarios"), equalTo(10L));
    assertThat(mav.getModel().get("totalReportes"), equalTo(50L));
  }

  @Test
  public void conRolAdminDeberiaPedirTodosLosDatosAlServicio() {
    when(sessionMock.getAttribute("ROL")).thenReturn("ADMIN");
    when(servicioAdminMock.totalUsuarios()).thenReturn(5L);
    when(servicioAdminMock.totalReportes()).thenReturn(20L);
    when(servicioAdminMock.productosMasReportados()).thenReturn(new ArrayList<>());
    when(servicioAdminMock.comerciosMasReportados()).thenReturn(new ArrayList<>());
    when(servicioAdminMock.reportesPorMes()).thenReturn(new ArrayList<>());
    when(servicioAdminMock.productosMasDudosos()).thenReturn(new ArrayList<>());
    when(servicioAdminMock.usuariosMasActivos()).thenReturn(new ArrayList<>());
    when(servicioAdminMock.distribucionPorRangoEtario()).thenReturn(new ArrayList<>());

    controladorAdmin.verEstadisticas(sessionMock);

    verify(servicioAdminMock).totalUsuarios();
    verify(servicioAdminMock).totalReportes();
    verify(servicioAdminMock).productosMasReportados();
    verify(servicioAdminMock).comerciosMasReportados();
    verify(servicioAdminMock).reportesPorMes();
    verify(servicioAdminMock).productosMasDudosos();
    verify(servicioAdminMock).usuariosMasActivos();
    verify(servicioAdminMock).distribucionPorRangoEtario();
  }
}
