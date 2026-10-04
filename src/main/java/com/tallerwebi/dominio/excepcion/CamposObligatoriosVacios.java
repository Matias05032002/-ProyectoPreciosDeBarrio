package com.tallerwebi.dominio.excepcion;

public class CamposObligatoriosVacios extends Exception {

  private static final long serialVersionUID = 1L;

  public CamposObligatoriosVacios(String mensaje) {
    super(mensaje);
  }
}
