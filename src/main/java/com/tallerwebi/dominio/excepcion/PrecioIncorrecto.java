package com.tallerwebi.dominio.excepcion;

public class PrecioIncorrecto extends Exception {

  private static final long serialVersionUID = 1L;

  public PrecioIncorrecto(String mensaje) {
    super(mensaje);
  }
}
