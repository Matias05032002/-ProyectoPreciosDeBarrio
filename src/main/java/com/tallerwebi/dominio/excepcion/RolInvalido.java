package com.tallerwebi.dominio.excepcion;

public class RolInvalido extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public RolInvalido() {
    super("Debe seleccionar un tipo de cuenta");
  }
}
