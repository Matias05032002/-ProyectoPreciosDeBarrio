package com.tallerwebi.dominio.excepcion;

public class FueraDePlazoDeEdicion extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public FueraDePlazoDeEdicion(String mensaje) {
    super(mensaje);
  }
}
