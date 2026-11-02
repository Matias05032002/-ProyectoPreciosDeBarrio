package com.tallerwebi.dominio.excepcion;

public class AccesoDenegado extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public AccesoDenegado(String mensaje) {
    super(mensaje);
  }
}
