package com.tallerwebi.dominio.excepcion;

public class UnidadInvalida extends Exception {

  private static final long serialVersionUID = 1L;

  public UnidadInvalida(String mensaje) {
    super(mensaje);
  }
}
