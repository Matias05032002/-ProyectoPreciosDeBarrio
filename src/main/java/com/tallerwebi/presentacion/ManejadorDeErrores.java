package com.tallerwebi.presentacion;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ManejadorDeErrores {

  private static final String VISTA_ERROR = "error";
  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(
    ManejadorDeErrores.class
  );

  @ExceptionHandler(Exception.class)
  public ModelAndView manejarErrorInesperado(
    Exception excepcion,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws Exception {
    // Los errores del cliente (405, 400, archivo muy grande...) conservan su código HTTP
    if (esErrorDelCliente(excepcion)) {
      throw excepcion;
    }
    if (LOGGER.isErrorEnabled()) {
      LOGGER.error("Error no controlado en {}", request.getRequestURI(), excepcion);
    }
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    return new ModelAndView(VISTA_ERROR);
  }

  private boolean esErrorDelCliente(Exception excepcion) {
    return (
      excepcion instanceof ErrorResponse ||
      excepcion instanceof ServletException ||
      excepcion instanceof TypeMismatchException ||
      excepcion instanceof MultipartException ||
      excepcion instanceof HttpMessageNotReadableException ||
      excepcion instanceof BindException
    );
  }
}
