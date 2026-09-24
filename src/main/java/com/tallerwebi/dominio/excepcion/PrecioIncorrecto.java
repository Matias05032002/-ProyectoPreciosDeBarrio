package com.tallerwebi.dominio.excepcion;

public class PrecioIncorrecto extends Exception {

  private static final long serialVersionUID = 1L;

  public PrecioIncorrecto() {
    super("El precio debe ser mayor a cero");
  }
}
