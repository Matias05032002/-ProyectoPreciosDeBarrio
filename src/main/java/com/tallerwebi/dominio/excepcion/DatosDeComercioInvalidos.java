package com.tallerwebi.dominio.excepcion;

public class DatosDeComercioInvalidos extends Exception {

  private static final long serialVersionUID = 1L;

  public DatosDeComercioInvalidos(String mensaje) {
    super(mensaje);
  }
}
