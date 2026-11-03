package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

public class ManejadorDeErroresTest {

  private ManejadorDeErrores manejador;
  private HttpServletRequest requestMock;
  private HttpServletResponse responseMock;

  @BeforeEach
  public void init() {
    this.manejador = new ManejadorDeErrores();
    this.requestMock = mock(HttpServletRequest.class);
    this.responseMock = mock(HttpServletResponse.class);
    when(this.requestMock.getRequestURI()).thenReturn("/spring/reporte");
  }

  @Test
  public void unErrorInesperadoDeberiaMostrarLaPaginaDeErrorConStatus500() throws Exception {
    Exception error = new IllegalStateException(
      "could not execute statement [insert into reporte]"
    );

    ModelAndView modelAndView = manejador.manejarErrorInesperado(error, requestMock, responseMock);

    assertThat(modelAndView.getViewName(), equalTo("error"));
    verify(responseMock).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  }

  @Test
  public void laPaginaDeErrorNoDeberiaExponerDetallesDeLaExcepcion() throws Exception {
    Exception error = new IllegalStateException(
      "could not execute statement [insert into reporte]"
    );

    ModelAndView modelAndView = manejador.manejarErrorInesperado(error, requestMock, responseMock);

    assertThat(modelAndView.getModel().isEmpty(), is(true));
  }

  @Test
  public void unMetodoHttpNoPermitidoDeberiaSeguirSiendoUnErrorDelCliente() {
    Exception error = new HttpRequestMethodNotSupportedException("POST");

    assertThrows(
      HttpRequestMethodNotSupportedException.class,
      () -> manejador.manejarErrorInesperado(error, requestMock, responseMock)
    );
    verify(responseMock, never()).setStatus(anyInt());
  }

  @Test
  public void unParametroConTipoIncorrectoDeberiaMostrarUnErrorAmableConStatus400()
    throws Exception {
    Exception error = new TypeMismatchException("abc", Double.class);

    ModelAndView mav = manejador.manejarErrorInesperado(error, requestMock, responseMock);

    assertThat(mav.getViewName(), equalTo("error"));
    verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    assertThat(mav.getModel().get("titulo"), equalTo("Los datos enviados no son válidos"));
  }

  @Test
  public void unParametroQueFaltaDeberiaMostrarUnErrorAmableConStatus400() throws Exception {
    Exception error = new MissingServletRequestParameterException("precio", "double");

    ModelAndView mav = manejador.manejarErrorInesperado(error, requestMock, responseMock);

    assertThat(mav.getViewName(), equalTo("error"));
    verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void unJsonMalFormadoDeberiaMostrarUnErrorAmableConStatus400() throws Exception {
    Exception error = new HttpMessageNotReadableException("JSON roto", (HttpInputMessage) null);

    ModelAndView mav = manejador.manejarErrorInesperado(error, requestMock, responseMock);

    assertThat(mav.getViewName(), equalTo("error"));
    verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void unErrorDeBindingDeberiaMostrarUnErrorAmableConStatus400() throws Exception {
    Exception error = new BindException(new Object(), "objeto");

    ModelAndView mav = manejador.manejarErrorInesperado(error, requestMock, responseMock);

    assertThat(mav.getViewName(), equalTo("error"));
    verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void elMensajeDelErrorAmableNoDeberiaExponerDetallesTecnicos() throws Exception {
    Exception error = new TypeMismatchException("abc", Double.class);

    ModelAndView mav = manejador.manejarErrorInesperado(error, requestMock, responseMock);

    assertThat(mav.getModel().toString(), not(containsString("abc")));
    assertThat(mav.getModel().toString(), not(containsString("Double")));
  }

  @Test
  public void unArchivoDemasiadoGrandeDeberiaSeguirSiendoUnErrorDelCliente() {
    Exception error = new MaxUploadSizeExceededException(1L);

    assertThrows(
      MaxUploadSizeExceededException.class,
      () -> manejador.manejarErrorInesperado(error, requestMock, responseMock)
    );
  }
}
