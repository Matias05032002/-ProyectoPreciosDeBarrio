package com.tallerwebi.dominio.excepcion;

public class ContrasenaInvalida extends Exception {

  private static final long serialVersionUID = 1L;

  public ContrasenaInvalida(String mensaje) {
    super(mensaje);
  }
}
