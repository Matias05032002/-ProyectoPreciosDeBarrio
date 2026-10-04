package com.tallerwebi.dominio.excepcion;

public class EmailInvalido extends Exception {

  private static final long serialVersionUID = 1L;

  public EmailInvalido(String mensaje) {
    super(mensaje);
  }
}
