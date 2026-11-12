package com.tallerwebi.presentacion.Notificacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.notificacion.Notificacion;
import com.tallerwebi.dominio.notificacion.ServicioNotificacion;
import com.tallerwebi.presentacion.ControladorNotificacion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorNotificacionTest {

  private ServicioNotificacion servicio;
  private HttpServletRequest request;
  private HttpSession sesion;
  private ControladorNotificacion controlador;

  @BeforeEach
  void init() {
    servicio = mock(ServicioNotificacion.class);
    request = mock(HttpServletRequest.class);
    sesion = mock(HttpSession.class);
    when(request.getSession()).thenReturn(sesion);
    controlador = new ControladorNotificacion(servicio);
  }

  private void conSesion(Long id) {
    when(sesion.getAttribute("ID")).thenReturn(id);
  }

  @Test
  void verNotificacionesSinSesionDeberiaRedirigirAlLogin() {
    ModelAndView mav = controlador.ver(request);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  void verNotificacionesDeberiaMostrarLasDelUsuarioDeLaSesion() {
    conSesion(7L);
    Notificacion n = new Notificacion();
    when(servicio.listar(7L)).thenReturn(List.of(n));

    ModelAndView mav = controlador.ver(request);

    assertThat(mav.getViewName(), equalTo("notificacion/notificaciones"));
    assertThat(mav.getModel().get("notificaciones"), equalTo(List.of(n)));
  }

  @Test
  void contadorDeberiaDevolverLasNoLeidasDelUsuarioDeLaSesion() {
    conSesion(7L);
    when(servicio.contarNoLeidas(7L)).thenReturn(4L);

    Map<String, Object> respuesta = controlador.contador(request);

    assertThat(respuesta.get("noLeidas"), equalTo(4L));
  }

  @Test
  void contadorSinSesionDeberiaDevolverCero() {
    Map<String, Object> respuesta = controlador.contador(request);

    assertThat(respuesta.get("noLeidas"), equalTo(0L));
  }

  @Test
  void leerDeberiaMarcarComoLeidaYRedirigirAlEnlaceDeLaNotificacion() {
    conSesion(7L);
    when(servicio.enlaceDe(5L, 7L)).thenReturn("/comercio/dashboard");

    ModelAndView mav = controlador.leer(5L, request);

    verify(servicio).marcarComoLeida(5L, 7L);
    assertThat(mav.getViewName(), equalTo("redirect:/comercio/dashboard"));
  }

  @Test
  void leerUnaNotificacionSinEnlaceDeberiaVolverALaLista() {
    conSesion(7L);
    when(servicio.enlaceDe(5L, 7L)).thenReturn(null);

    ModelAndView mav = controlador.leer(5L, request);

    verify(servicio).marcarComoLeida(5L, 7L);
    assertThat(mav.getViewName(), equalTo("redirect:/notificaciones"));
  }

  @Test
  void leerSinSesionNoDeberiaTocarNada() {
    ModelAndView mav = controlador.leer(5L, request);

    verify(servicio, never()).marcarComoLeida(5L, 7L);
    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  void leerTodasDeberiaMarcarLasDelUsuarioYVolverALaLista() {
    conSesion(7L);

    ModelAndView mav = controlador.leerTodas(request);

    verify(servicio).marcarTodasComoLeidas(7L);
    assertThat(mav.getViewName(), equalTo("redirect:/notificaciones"));
  }
}
