package com.tallerwebi.presentacion.Comercio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.presentacion.ControladorComercio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorComercioTest {

  private ControladorComercio controladorComercio;
  private ServicioComercio servicioComercioMock;
  private ServicioReporte servicioReporteMock;
  private ServicioLogin servicioLoginMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    this.servicioComercioMock = mock(ServicioComercio.class);
    this.servicioReporteMock = mock(ServicioReporte.class);
    this.servicioLoginMock = mock(ServicioLogin.class);
    this.controladorComercio =
      new ControladorComercio(servicioComercioMock, servicioReporteMock, servicioLoginMock);
    this.requestMock = mock(HttpServletRequest.class);
    this.sessionMock = mock(HttpSession.class);
    when(this.requestMock.getSession()).thenReturn(sessionMock);
  }

  // ── tests existentes ──────────────────────────────────────────────────────

  @Test
  public void guardarUnNuevoComercio() {
    Comercio comercio = new Comercio();
    when(this.servicioComercioMock.guardarComercio(comercio)).thenReturn(comercio);

    ModelAndView modelAndView = this.controladorComercio.guardarComercio(comercio);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/comercio"));
    verify(this.servicioComercioMock, times(1)).guardarComercio(comercio);
  }

  @Test
  public void listarTodosLosComercios() {
    Comercio comercio = new Comercio();
    List<Comercio> listaDeComercios = Arrays.asList(comercio);
    when(this.servicioComercioMock.listarTodos()).thenReturn(listaDeComercios);

    ModelAndView modelAndView = this.controladorComercio.listarTodos();

    assertThat(modelAndView.getModel().get("comercios"), is(listaDeComercios));
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("comercio/lista-comercios"));
  }

  @Test
  public void buscarComercioPorId() {
    Comercio comercio = new Comercio();
    when(this.servicioComercioMock.buscarComercio(1L)).thenReturn(comercio);

    ModelAndView modelAndView = this.controladorComercio.buscarPorId(1L);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("comercio/comercios"));
    assertThat(modelAndView.getModel().get("comercio"), is(comercio));
    verify(this.servicioComercioMock, times(1)).buscarComercio(1L);
  }

  @Test
  public void buscarComercioPorNombre() {
    Comercio comercio = new Comercio();
    List<Comercio> comercios = Arrays.asList(comercio);
    when(this.servicioComercioMock.buscarComercioPorNombre("Almacen de Matias"))
      .thenReturn(comercios);

    ModelAndView modelAndView =
      this.controladorComercio.buscarComercioPorNombre("Almacen de Matias");

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("comercio/lista-comercios"));
    assertThat(modelAndView.getModel().get("comercios"), is(comercios));
    verify(this.servicioComercioMock, times(1)).buscarComercioPorNombre("Almacen de Matias");
  }

  // ── verPerfil ─────────────────────────────────────────────────────────────

  @Test
  public void verPerfilSinSesionRedireccionaALogin() {
    when(sessionMock.getAttribute("ID")).thenReturn(null);

    ModelAndView mav = controladorComercio.verPerfil(requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void verPerfilConSesionMuestraVista() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    Comercio comercio = new Comercio();
    comercio.setId(1L);
    Usuario usuario = new Usuario();
    when(servicioComercioMock.buscarComercioPorUsuarioId(1L)).thenReturn(comercio);
    when(servicioLoginMock.buscarPorId(1L)).thenReturn(usuario);
    when(servicioReporteMock.buscarPorComercio(1L)).thenReturn(Collections.emptyList());

    ModelAndView mav = controladorComercio.verPerfil(requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("comercio/perfil-comercio"));
    assertThat(mav.getModel().get("comercio"), is(comercio));
    assertThat(mav.getModel().get("usuario"), is(usuario));
  }

  @Test
  public void verPerfilSinComercioMuestraCantidadCero() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioComercioMock.buscarComercioPorUsuarioId(1L)).thenReturn(null);
    when(servicioLoginMock.buscarPorId(1L)).thenReturn(new Usuario());

    ModelAndView mav = controladorComercio.verPerfil(requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("comercio/perfil-comercio"));
    assertThat((Long) mav.getModel().get("cantidadProductos"), is(0L));
  }

  // ── guardarPerfil ─────────────────────────────────────────────────────────

  @Test
  public void guardarPerfilSinSesionRedireccionaALogin() {
    when(sessionMock.getAttribute("ID")).thenReturn(null);

    ModelAndView mav = controladorComercio.guardarPerfil(new Comercio(), requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void guardarPerfilConSesionVinculaYRedirige() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    Comercio comercio = new Comercio();

    ModelAndView mav = controladorComercio.guardarPerfil(comercio, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/comercio/perfil"));
    verify(servicioComercioMock, times(1)).vincularComercioAUsuario(comercio, 1L);
  }

  // ── editarPerfil ──────────────────────────────────────────────────────────

  @Test
  public void editarPerfilSinSesionRedireccionaALogin() {
    when(sessionMock.getAttribute("ID")).thenReturn(null);

    ModelAndView mav = controladorComercio.editarPerfil(new Comercio(), requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void editarPerfilSinComercioExistenteRedirigePerfil() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioComercioMock.buscarComercioPorUsuarioId(1L)).thenReturn(null);

    ModelAndView mav = controladorComercio.editarPerfil(new Comercio(), requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/comercio/perfil"));
  }

  @Test
  public void editarPerfilActualizaYRedirige() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    Comercio existente = new Comercio();
    existente.setId(1L);
    when(servicioComercioMock.buscarComercioPorUsuarioId(1L)).thenReturn(existente);

    Comercio datos = new Comercio();
    datos.setNombre("Nuevo nombre");
    datos.setTipo("Almacen");
    datos.setDireccion("Calle 1");
    datos.setLocalidad("La Plata");
    datos.setDescripcion("Desc");

    ModelAndView mav = controladorComercio.editarPerfil(datos, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/comercio/perfil?editado=true"));
    verify(servicioComercioMock, times(1)).actualizarComercio(existente);
  }

  // ── cambiarContrasena ─────────────────────────────────────────────────────

  @Test
  public void cambiarContrasenaSinSesionRedireccionaALogin() {
    when(sessionMock.getAttribute("ID")).thenReturn(null);

    ModelAndView mav = controladorComercio.cambiarContrasena(
      "vieja",
      "nueva",
      "nueva",
      requestMock
    );

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void cambiarContrasenaConConfirmacionDistintaDevuelveError() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);

    ModelAndView mav = controladorComercio.cambiarContrasena(
      "vieja",
      "nueva1",
      "nueva2",
      requestMock
    );

    assertThat(mav.getViewName(), containsString("errorContrasena=noCoinciden"));
  }

  @Test
  public void cambiarContrasenaIncorrectaDevuelveError() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioLoginMock.cambiarContrasena(1L, "vieja", "nueva")).thenReturn(false);

    ModelAndView mav = controladorComercio.cambiarContrasena(
      "vieja",
      "nueva",
      "nueva",
      requestMock
    );

    assertThat(mav.getViewName(), containsString("errorContrasena=incorrecta"));
  }

  @Test
  public void cambiarContrasenaExitosaRedirige() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioLoginMock.cambiarContrasena(1L, "vieja", "nueva")).thenReturn(true);

    ModelAndView mav = controladorComercio.cambiarContrasena(
      "vieja",
      "nueva",
      "nueva",
      requestMock
    );

    assertThat(mav.getViewName(), containsString("contrasenaCambiada=true"));
  }

  // ── verDashboard ──────────────────────────────────────────────────────────

  @Test
  public void verDashboardConRolIncorrectoRedireccionaAHome() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");

    ModelAndView mav = controladorComercio.verDashboard(requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/home"));
  }

  @Test
  public void verDashboardSinComercioRedirigePerfil() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(sessionMock.getAttribute("ROL")).thenReturn("COMERCIO");
    when(servicioComercioMock.buscarComercioPorUsuarioId(1L)).thenReturn(null);

    ModelAndView mav = controladorComercio.verDashboard(requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/comercio/perfil"));
  }

  @Test
  public void verDashboardConComercioMuestraVista() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(sessionMock.getAttribute("ROL")).thenReturn("COMERCIO");
    Comercio comercio = new Comercio();
    comercio.setId(1L);
    when(servicioComercioMock.buscarComercioPorUsuarioId(1L)).thenReturn(comercio);
    when(servicioReporteMock.buscarPorComercio(1L)).thenReturn(Collections.emptyList());
    when(servicioReporteMock.contarUsuariosDistintosPorComercio(1L))
      .thenReturn(Collections.emptyList());
    when(servicioReporteMock.contarReportesDudososPorComercio(1L)).thenReturn(0L);
    when(servicioReporteMock.rangoEtariosVisitantes(1L)).thenReturn(Collections.emptyList());

    ModelAndView mav = controladorComercio.verDashboard(requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("comercio/dashboard-comercio"));
    assertThat(mav.getModel().get("comercio"), is(comercio));
    assertThat(mav.getModel().get("totalPrecios"), is(0));
  }

  @Test
  public void verDashboardCalculaPrecioPromedio() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(sessionMock.getAttribute("ROL")).thenReturn("COMERCIO");
    Comercio comercio = new Comercio();
    comercio.setId(1L);
    when(servicioComercioMock.buscarComercioPorUsuarioId(1L)).thenReturn(comercio);

    Reporte r1 = new Reporte();
    r1.setPrecio(100.0);
    Reporte r2 = new Reporte();
    r2.setPrecio(200.0);
    when(servicioReporteMock.buscarPorComercio(1L)).thenReturn(Arrays.asList(r1, r2));
    when(servicioReporteMock.contarUsuariosDistintosPorComercio(1L))
      .thenReturn(Collections.emptyList());
    when(servicioReporteMock.contarReportesDudososPorComercio(1L)).thenReturn(0L);
    when(servicioReporteMock.rangoEtariosVisitantes(1L)).thenReturn(Collections.emptyList());

    ModelAndView mav = controladorComercio.verDashboard(requestMock);

    assertThat((Double) mav.getModel().get("precioPromedio"), is(150.0));
  }

  // --- verDashboard (Task 4) ---

  @Test
  public void verDashboardSinSesionRedireccionaALogin() {
    when(sessionMock.getAttribute("ID")).thenReturn(null);
    when(requestMock.getSession()).thenReturn(sessionMock);

    ModelAndView mav = controladorComercio.verDashboard(requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void verDashboardConRolInvalidoRedireccionaAHome() {
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(requestMock.getSession()).thenReturn(sessionMock);

    ModelAndView mav = controladorComercio.verDashboard(requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/home"));
  }

  @Test
  public void verDashboardDeberiaAgregarProductosMasBuscadosAlModelo() {
    Comercio comercio = new Comercio();
    comercio.setId(1L);
    comercio.setLocalidad("Matanza");

    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(sessionMock.getAttribute("ROL")).thenReturn("COMERCIO");
    when(requestMock.getSession()).thenReturn(sessionMock);
    when(servicioComercioMock.buscarComercioPorUsuarioId(1L)).thenReturn(comercio);
    when(servicioReporteMock.buscarPorComercio(1L)).thenReturn(Collections.emptyList());
    when(servicioReporteMock.contarUsuariosDistintosPorComercio(1L))
      .thenReturn(Collections.emptyList());
    when(servicioReporteMock.contarReportesDudososPorComercio(1L)).thenReturn(0L);
    when(servicioReporteMock.rangoEtariosVisitantes(1L)).thenReturn(Collections.emptyList());

    List<Object[]> productosMasBuscados = new java.util.ArrayList<>();
    productosMasBuscados.add(new Object[] { "Leche", "Lácteos", 3L });
    when(servicioReporteMock.productosMasBuscadosPorLocalidad("Matanza", 5))
      .thenReturn(productosMasBuscados);

    ModelAndView mav = controladorComercio.verDashboard(requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("comercio/dashboard-comercio"));
    assertThat(mav.getModel().get("productosMasBuscados"), equalTo(productosMasBuscados));
    verify(servicioReporteMock, times(1)).productosMasBuscadosPorLocalidad("Matanza", 5);
  }

  // --- subirFoto (Task 2) ---

  @Test
  public void subirFotoConTipoInvalidoRedireccionaConError() throws Exception {
    when(sessionMock.getAttribute("ROL")).thenReturn("COMERCIO");
    when(sessionMock.getAttribute("ID")).thenReturn(1L);

    Comercio comercio = new Comercio();
    comercio.setId(1L);
    when(servicioComercioMock.buscarComercioPorUsuarioId(1L)).thenReturn(comercio);

    org.springframework.mock.web.MockMultipartFile archivo =
      new org.springframework.mock.web.MockMultipartFile(
        "foto",
        "foto.gif",
        "image/gif",
        new byte[100]
      );

    ModelAndView mav = controladorComercio.subirFoto(archivo, sessionMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/comercio/perfil?errorFoto=true"));
    verify(servicioComercioMock, never()).guardarFoto(any(), any(), any());
  }

  @Test
  public void subirFotoConArchivoDemsiadoGrandeRedireccionaConError() throws Exception {
    when(sessionMock.getAttribute("ROL")).thenReturn("COMERCIO");
    when(sessionMock.getAttribute("ID")).thenReturn(1L);

    Comercio comercio = new Comercio();
    comercio.setId(1L);
    when(servicioComercioMock.buscarComercioPorUsuarioId(1L)).thenReturn(comercio);

    // 6 MB > 5 MB límite
    byte[] bytes = new byte[6 * 1024 * 1024];
    org.springframework.mock.web.MockMultipartFile archivo =
      new org.springframework.mock.web.MockMultipartFile("foto", "foto.jpg", "image/jpeg", bytes);

    ModelAndView mav = controladorComercio.subirFoto(archivo, sessionMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/comercio/perfil?errorFoto=true"));
    verify(servicioComercioMock, never()).guardarFoto(any(), any(), any());
  }

  @Test
  public void subirFotoValidaDeberiaGuardarYRedireccionarConExito() throws Exception {
    when(sessionMock.getAttribute("ROL")).thenReturn("COMERCIO");
    when(sessionMock.getAttribute("ID")).thenReturn(1L);

    Comercio comercio = new Comercio();
    comercio.setId(1L);
    when(servicioComercioMock.buscarComercioPorUsuarioId(1L)).thenReturn(comercio);

    byte[] bytes = new byte[100];
    org.springframework.mock.web.MockMultipartFile archivo =
      new org.springframework.mock.web.MockMultipartFile("foto", "foto.jpg", "image/jpeg", bytes);

    ModelAndView mav = controladorComercio.subirFoto(archivo, sessionMock);

    assertThat(
      mav.getViewName(),
      equalToIgnoringCase("redirect:/comercio/perfil?fotoActualizada=true")
    );
    verify(servicioComercioMock, times(1)).guardarFoto(1L, bytes, "image/jpeg");
  }
}
