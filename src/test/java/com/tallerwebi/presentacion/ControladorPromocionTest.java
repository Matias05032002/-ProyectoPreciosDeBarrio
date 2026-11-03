package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.Promocion;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Comercio.ServicioPromocion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

public class ControladorPromocionTest {

  private static final Long USUARIO_ID = 7L;
  private static final Long COMERCIO_ID = 3L;
  private static final Long PROMOCION_ID = 10L;

  private ServicioPromocion servicioPromocionMock;
  private ServicioComercio servicioComercioMock;
  private HttpServletRequest requestMock;
  private HttpSession sesionMock;
  private ControladorPromocion controlador;
  private Comercio comercio;

  @BeforeEach
  public void init() {
    servicioPromocionMock = mock(ServicioPromocion.class);
    servicioComercioMock = mock(ServicioComercio.class);
    requestMock = mock(HttpServletRequest.class);
    sesionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sesionMock);

    controlador = new ControladorPromocion(servicioPromocionMock, servicioComercioMock);

    comercio = new Comercio();
    comercio.setId(COMERCIO_ID);
    when(servicioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(comercio);
  }

  private void iniciarSesionComo(String rol) {
    when(sesionMock.getAttribute("ID")).thenReturn(USUARIO_ID);
    when(sesionMock.getAttribute("ROL")).thenReturn(rol);
  }

  private BindingResult sinErrores(Promocion promocion) {
    return new BeanPropertyBindingResult(promocion, "promocion");
  }

  @Test
  public void listarActivasEsPublicoYMuestraLaVistaDeVecinos() {
    when(servicioPromocionMock.listarPromocionesActivas()).thenReturn(Collections.emptyList());

    ModelAndView mav = controlador.listarActivas();

    assertThat(mav.getViewName(), equalTo("comercio/promociones"));
    assertThat(mav.getModel().get("promociones"), equalTo(Collections.emptyList()));
  }

  @Test
  public void misPromocionesSinSesionRedirigeAlLogin() {
    ModelAndView mav = controlador.misPromociones(requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void misPromocionesComoVecinoRedirigeAlInicio() {
    iniciarSesionComo("USER");

    ModelAndView mav = controlador.misPromociones(requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/home"));
    verify(servicioPromocionMock, never()).listarPorComercio(any());
  }

  @Test
  public void misPromocionesComoAdminRedirigeAlInicio() {
    iniciarSesionComo("ADMIN");

    assertThat(controlador.misPromociones(requestMock).getViewName(), equalTo("redirect:/home"));
  }

  @Test
  public void misPromocionesComoComercioMuestraSusPromociones() {
    iniciarSesionComo("COMERCIO");
    when(servicioPromocionMock.listarPorComercio(COMERCIO_ID)).thenReturn(Collections.emptyList());

    ModelAndView mav = controlador.misPromociones(requestMock);

    assertThat(mav.getViewName(), equalTo("comercio/mis-promociones"));
    assertThat(mav.getModel().get("promocion") instanceof Promocion, equalTo(true));
  }

  @Test
  public void misPromocionesSinComercioRedirigeAlPerfil() {
    iniciarSesionComo("COMERCIO");
    when(servicioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(null);

    assertThat(
      controlador.misPromociones(requestMock).getViewName(),
      equalTo("redirect:/comercio/perfil")
    );
  }

  @Test
  public void crearComoVecinoNoCreaNadaYRedirigeAlInicio() {
    iniciarSesionComo("USER");
    Promocion promocion = new Promocion();

    ModelAndView mav = controlador.crear(promocion, sinErrores(promocion), requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/home"));
    verify(servicioPromocionMock, never()).crearPromocion(any(), any());
  }

  @Test
  public void crearSinSesionRedirigeAlLogin() {
    Promocion promocion = new Promocion();

    ModelAndView mav = controlador.crear(promocion, sinErrores(promocion), requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
    verify(servicioPromocionMock, never()).crearPromocion(any(), any());
  }

  @Test
  public void crearComoComercioCreaYRedirigeAMisPromociones() {
    iniciarSesionComo("COMERCIO");
    Promocion promocion = new Promocion();

    ModelAndView mav = controlador.crear(promocion, sinErrores(promocion), requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/promociones/mis-promociones"));
    verify(servicioPromocionMock).crearPromocion(promocion, USUARIO_ID);
  }

  @Test
  public void crearConDatosInvalidosVuelveAlFormularioConElMensajeYLoCargado() {
    iniciarSesionComo("COMERCIO");
    Promocion promocion = new Promocion();
    promocion.setTitulo("Oferta");
    when(servicioPromocionMock.crearPromocion(promocion, USUARIO_ID))
      .thenThrow(new IllegalArgumentException("El título es obligatorio."));

    ModelAndView mav = controlador.crear(promocion, sinErrores(promocion), requestMock);

    assertThat(mav.getViewName(), equalTo("comercio/mis-promociones"));
    assertThat(mav.getModel().get("error"), equalTo("El título es obligatorio."));
    assertThat(mav.getModel().get("promocion"), equalTo(promocion));
  }

  @Test
  public void crearConErroresDeConversionNoLlegaAlServicio() {
    iniciarSesionComo("COMERCIO");
    Promocion promocion = new Promocion();
    BindingResult resultado = sinErrores(promocion);
    resultado.rejectValue("fechaInicio", "typeMismatch");

    ModelAndView mav = controlador.crear(promocion, resultado, requestMock);

    assertThat(mav.getViewName(), equalTo("comercio/mis-promociones"));
    assertThat(mav.getModel().get("error").toString(), containsString("precio y las fechas"));
    verify(servicioPromocionMock, never()).crearPromocion(any(), any());
  }

  @Test
  public void desactivarComoVecinoNoDesactivaNada() {
    iniciarSesionComo("USER");

    ModelAndView mav = controlador.desactivar(PROMOCION_ID, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/home"));
    verify(servicioPromocionMock, never()).desactivarPromocion(any(), any());
  }

  @Test
  public void desactivarPromocionPropiaRedirigeAMisPromociones() {
    iniciarSesionComo("COMERCIO");

    ModelAndView mav = controlador.desactivar(PROMOCION_ID, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/promociones/mis-promociones"));
    verify(servicioPromocionMock).desactivarPromocion(PROMOCION_ID, USUARIO_ID);
  }

  @Test
  public void desactivarPromocionAjenaMuestraElErrorSinPerderLaLista() {
    iniciarSesionComo("COMERCIO");
    doThrow(new IllegalArgumentException("No tenés permiso para modificar esta promoción."))
      .when(servicioPromocionMock)
      .desactivarPromocion(PROMOCION_ID, USUARIO_ID);

    ModelAndView mav = controlador.desactivar(PROMOCION_ID, requestMock);

    assertThat(mav.getViewName(), equalTo("comercio/mis-promociones"));
    assertThat(mav.getModel().get("error").toString(), containsString("No tenés permiso"));
  }

  @Test
  public void elFormularioLeeYEscribeLasFechasEnFormatoDeDatetimeLocal() {
    Promocion promocion = new Promocion();
    WebDataBinder binder = new WebDataBinder(promocion, "promocion");
    controlador.configurarFechas(binder);

    binder.bind(
      new org.springframework.beans.MutablePropertyValues(
        java.util.Map.of("fechaInicio", "2030-11-03T10:30", "fechaFin", "")
      )
    );

    assertThat(binder.getBindingResult().hasErrors(), equalTo(false));
    assertThat(promocion.getFechaInicio(), equalTo(LocalDateTime.of(2030, 11, 3, 10, 30)));
    assertThat(promocion.getFechaFin() == null, equalTo(true));
  }

  @Test
  public void unaFechaConFormatoInvalidoQuedaComoErrorDeBinding() {
    Promocion promocion = new Promocion();
    WebDataBinder binder = new WebDataBinder(promocion, "promocion");
    controlador.configurarFechas(binder);

    binder.bind(
      new org.springframework.beans.MutablePropertyValues(
        java.util.Map.of("fechaInicio", "mañana a la tarde")
      )
    );

    assertThat(binder.getBindingResult().hasFieldErrors("fechaInicio"), equalTo(true));
  }
}
