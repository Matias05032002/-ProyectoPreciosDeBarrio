package com.tallerwebi.presentacion;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Atrapa los errores de todos los controladores y muestra una página propia.
 * Así el usuario nunca ve el stack trace, el SQL ni los nombres de clases internas.

 * Un id que no es un número en la ruta (/comercio/abc) da un 404: ese enlace no existe.
 * Los datos mal formados (un precio con texto, un parámetro que falta) dan un 400 con un
 * mensaje claro; el resto de los errores inesperados dan un 500 genérico.
 */
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
    if (esIdDeRutaInvalido(excepcion)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      ModelAndView mav = new ModelAndView(VISTA_ERROR);
      mav.addObject("titulo", "No encontramos lo que buscás");
      mav.addObject("mensaje", "El enlace no existe o ya no está disponible.");
      return mav;
    }
    if (sonDatosMalFormados(excepcion)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      ModelAndView mav = new ModelAndView(VISTA_ERROR);
      mav.addObject("titulo", "Los datos enviados no son válidos");
      mav.addObject("mensaje", "Revisá lo que cargaste e intentá de nuevo.");
      return mav;
    }
    // Los otros errores del cliente (405, archivo muy grande...) conservan su código HTTP
    if (esErrorDelCliente(excepcion)) {
      throw excepcion;
    }
    if (LOGGER.isErrorEnabled()) {
      LOGGER.error("Error no controlado en {}", request.getRequestURI(), excepcion);
    }
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    return new ModelAndView(VISTA_ERROR);
  }

  // Solo cuando el valor mal escrito viaja en la ruta (@PathVariable); en un parámetro de la
  // consulta (?pagina=abc) sigue siendo un dato mal formado
  private boolean esIdDeRutaInvalido(Exception excepcion) {
    return (
      excepcion instanceof MethodArgumentTypeMismatchException error &&
      error.getParameter().hasParameterAnnotation(PathVariable.class)
    );
  }

  private boolean sonDatosMalFormados(Exception excepcion) {
    return (
      excepcion instanceof TypeMismatchException ||
      excepcion instanceof MissingServletRequestParameterException ||
      excepcion instanceof HttpMessageNotReadableException ||
      excepcion instanceof BindException
    );
  }

  private boolean esErrorDelCliente(Exception excepcion) {
    return (
      excepcion instanceof ErrorResponse ||
      excepcion instanceof ServletException ||
      excepcion instanceof MultipartException
    );
  }
}
