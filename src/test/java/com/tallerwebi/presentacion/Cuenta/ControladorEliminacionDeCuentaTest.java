package com.tallerwebi.presentacion.Cuenta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Cuenta.ServicioEliminacionDeCuenta;
import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import com.tallerwebi.presentacion.ControladorEliminacionDeCuenta;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorEliminacionDeCuentaTest {

  private ServicioEliminacionDeCuenta servicio;
  private HttpServletRequest request;
  private HttpSession sesion;
  private ControladorEliminacionDeCuenta controlador;

  @BeforeEach
  void init() {
    servicio = mock(ServicioEliminacionDeCuenta.class);
    request = mock(HttpServletRequest.class);
    sesion = mock(HttpSession.class);
    controlador = new ControladorEliminacionDeCuenta(servicio);
  }

  @Test
  void sinSesionDeberiaRedirigirAlLoginSinEliminarNada() {
    when(request.getSession(false)).thenReturn(null);

    ModelAndView mav = controlador.eliminar("Clave1234", request);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
    verify(servicio, never())
      .eliminarCuenta(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
  }

  @Test
  void conSesionSinIdDeberiaRedirigirAlLogin() {
    when(request.getSession(false)).thenReturn(sesion);
    when(sesion.getAttribute("ID")).thenReturn(null);

    assertThat(
      controlador.eliminar("Clave1234", request).getViewName(),
      equalTo("redirect:/login")
    );
  }

  @Test
  void siSeEliminoDeberiaCerrarLaSesionYVolverAlLogin() {
    when(request.getSession(false)).thenReturn(sesion);
    when(sesion.getAttribute("ID")).thenReturn(7L);
    when(servicio.eliminarCuenta(7L, "Clave1234")).thenReturn(true);

    ModelAndView mav = controlador.eliminar("Clave1234", request);

    verify(sesion).invalidate();
    assertThat(mav.getViewName(), equalTo("redirect:/login?cuentaEliminada"));
  }

  @Test
  void conUnaContrasenaIncorrectaDeberiaVolverAlPerfilConElError() {
    when(request.getSession(false)).thenReturn(sesion);
    when(sesion.getAttribute("ID")).thenReturn(7L);
    when(servicio.eliminarCuenta(7L, "mala")).thenReturn(false);

    ModelAndView mav = controlador.eliminar("mala", request);

    verify(sesion, never()).invalidate();
    assertThat(mav.getViewName(), equalTo("redirect:/perfil?errorEliminarCuenta"));
  }

  @Test
  void unaCuentaQueNoPuedeEliminarseDeberiaVolverAlPerfilConElError() {
    when(request.getSession(false)).thenReturn(sesion);
    when(sesion.getAttribute("ID")).thenReturn(1L);
    when(servicio.eliminarCuenta(1L, "Clave1234")).thenThrow(new AccesoDenegado("admin"));

    ModelAndView mav = controlador.eliminar("Clave1234", request);

    verify(sesion, never()).invalidate();
    assertThat(mav.getViewName(), equalTo("redirect:/perfil?errorEliminarCuenta"));
  }
}
