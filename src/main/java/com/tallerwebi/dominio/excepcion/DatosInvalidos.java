package com.tallerwebi.dominio.excepcion;

public class DatosInvalidos extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DatosInvalidos(String mensaje) {
    super(mensaje);
  }
}
