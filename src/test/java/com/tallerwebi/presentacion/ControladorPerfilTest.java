package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Reporte.ServicioReporte;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorPerfilTest {

  private ControladorPerfil controladorPerfil;
  private ServicioLogin servicioLoginMock;
  private ServicioReporte servicioReporteMock;
  private HttpServletRequest requestMock;
  private HttpSession sesionMock;

  @BeforeEach
  public void init() {
    servicioLoginMock = mock(ServicioLogin.class);
    servicioReporteMock = mock(ServicioReporte.class);
    requestMock = mock(HttpServletRequest.class);
    sesionMock = mock(HttpSession.class);

    controladorPerfil = new ControladorPerfil(servicioLoginMock, servicioReporteMock);
  }

  @Test
  public void verPerfilSinSesionDeberiaRedirigirAlLogin() {
    when(requestMock.getSession(false)).thenReturn(null);

    ModelAndView resultado = controladorPerfil.verPerfil(requestMock);

    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
    verifyNoInteractions(servicioLoginMock, servicioReporteMock);
  }

  @Test
  public void verPerfilSinIdDeberiaRedirigirAlLogin() {
    when(requestMock.getSession(false)).thenReturn(sesionMock);
    when(sesionMock.getAttribute("ID")).thenReturn(null);

    ModelAndView resultado = controladorPerfil.verPerfil(requestMock);

    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
    verifyNoInteractions(servicioLoginMock, servicioReporteMock);
  }

  @Test
  public void verPerfilConUsuarioInexistenteDeberiaInvalidarSesion() {
    when(requestMock.getSession(false)).thenReturn(sesionMock);
    when(sesionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioLoginMock.buscarPorId(1L)).thenReturn(null);

    ModelAndView resultado = controladorPerfil.verPerfil(requestMock);

    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
    verify(sesionMock).invalidate();
    verifyNoInteractions(servicioReporteMock);
  }

  @Test
  public void verPerfilAutenticadoDeberiaMostrarDatosYReportes() {
    Usuario usuario = new Usuario();
    usuario.setEmail("test@test.com");

    when(requestMock.getSession(false)).thenReturn(sesionMock);
    when(sesionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioLoginMock.buscarPorId(1L)).thenReturn(usuario);
    when(servicioReporteMock.buscarPorUsuarioId(1L)).thenReturn(new ArrayList<>());

    ModelAndView resultado = controladorPerfil.verPerfil(requestMock);

    assertThat(resultado.getViewName(), equalTo("perfil"));
    assertThat(resultado.getModel().get("usuario"), equalTo(usuario));
    assertThat(resultado.getModel().get("cantidadReportes"), equalTo(0));
    assertThat(resultado.getModel().get("reportes"), equalTo(new ArrayList<>()));
    verify(servicioReporteMock).buscarPorUsuarioId(1L);
  }

  @Test
  public void actualizarEmailSinSesionDeberiaRedirigirAlLogin() {
    when(requestMock.getSession(false)).thenReturn(null);

    ModelAndView resultado = controladorPerfil.actualizarEmail("nuevo@test.com", requestMock);

    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
    verifyNoInteractions(servicioLoginMock);
  }

  @Test
  public void actualizarEmailExitosoDeberiaActualizarSesion() {
    when(requestMock.getSession(false)).thenReturn(sesionMock);
    when(sesionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioLoginMock.actualizarEmail(1L, "nuevo@test.com")).thenReturn(true);

    ModelAndView resultado = controladorPerfil.actualizarEmail("nuevo@test.com", requestMock);

    assertThat(resultado.getViewName(), equalTo("redirect:/perfil?emailActualizado"));
    verify(sesionMock).setAttribute("EMAIL", "nuevo@test.com");
  }

  @Test
  public void actualizarEmailFallidoDeberiaRedirigirConError() {
    when(requestMock.getSession(false)).thenReturn(sesionMock);
    when(sesionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioLoginMock.actualizarEmail(1L, "ocupado@test.com")).thenReturn(false);

    ModelAndView resultado = controladorPerfil.actualizarEmail("ocupado@test.com", requestMock);

    assertThat(resultado.getViewName(), equalTo("redirect:/perfil?errorEmail"));
    verify(sesionMock, never()).setAttribute(eq("EMAIL"), anyString());
  }

  @Test
  public void cambiarContrasenaSinSesionDeberiaRedirigirAlLogin() {
    when(requestMock.getSession(false)).thenReturn(null);

    ModelAndView resultado = controladorPerfil.cambiarContrasena(
      "Actual123",
      "Nueva1234",
      requestMock
    );

    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
    verifyNoInteractions(servicioLoginMock);
  }

  @Test
  public void cambiarContrasenaExitosoDeberiaRedirigirConConfirmacion() {
    when(requestMock.getSession(false)).thenReturn(sesionMock);
    when(sesionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioLoginMock.cambiarContrasena(1L, "Actual123", "Nueva1234")).thenReturn(true);

    ModelAndView resultado = controladorPerfil.cambiarContrasena(
      "Actual123",
      "Nueva1234",
      requestMock
    );

    assertThat(resultado.getViewName(), equalTo("redirect:/perfil?contrasenaActualizada"));
  }

  @Test
  public void cambiarContrasenaFallidoDeberiaRedirigirConError() {
    when(requestMock.getSession(false)).thenReturn(sesionMock);
    when(sesionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioLoginMock.cambiarContrasena(1L, "Actual123", "Nueva1234")).thenReturn(false);

    ModelAndView resultado = controladorPerfil.cambiarContrasena(
      "Actual123",
      "Nueva1234",
      requestMock
    );

    assertThat(resultado.getViewName(), equalTo("redirect:/perfil?errorContrasena"));
  }

  @Test
  public void actualizarDatosSinSesionDeberiaRedirigirAlLogin() {
    when(requestMock.getSession(false)).thenReturn(null);

    ModelAndView resultado = controladorPerfil.actualizarDatos(
      "Matias",
      "Llanos",
      null,
      requestMock
    );

    assertThat(resultado.getViewName(), equalTo("redirect:/login"));
    verifyNoInteractions(servicioLoginMock);
  }

  @Test
  public void actualizarDatosConSesionValidaDeberiaLlamarAlServicioYRedirigir() {
    when(requestMock.getSession(false)).thenReturn(sesionMock);
    when(sesionMock.getAttribute("ID")).thenReturn(1L);

    ModelAndView resultado = controladorPerfil.actualizarDatos(
      "Matias",
      "Llanos",
      "2002-03-05",
      requestMock
    );

    assertThat(resultado.getViewName(), equalTo("redirect:/perfil?datosActualizados"));
    verify(servicioLoginMock)
      .actualizarDatosPersonales(
        eq(1L),
        eq("Matias"),
        eq("Llanos"),
        any(java.time.LocalDate.class)
      );
  }

  @Test
  public void actualizarDatosSinFechaDeberiaLlamarAlServicioConFechaNull() {
    when(requestMock.getSession(false)).thenReturn(sesionMock);
    when(sesionMock.getAttribute("ID")).thenReturn(1L);

    ModelAndView resultado = controladorPerfil.actualizarDatos(
      "Matias",
      "Llanos",
      null,
      requestMock
    );

    assertThat(resultado.getViewName(), equalTo("redirect:/perfil?datosActualizados"));
    verify(servicioLoginMock)
      .actualizarDatosPersonales(eq(1L), eq("Matias"), eq("Llanos"), eq(null));
  }
}
