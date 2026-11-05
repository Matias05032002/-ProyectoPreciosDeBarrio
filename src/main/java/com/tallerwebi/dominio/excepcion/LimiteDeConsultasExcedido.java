package com.tallerwebi.dominio.excepcion;

public class LimiteDeConsultasExcedido extends Exception {

  private static final long serialVersionUID = 1L;

  public LimiteDeConsultasExcedido() {
    super("Hiciste muchas consultas seguidas. Esperá un rato y volvé a preguntarle a Don Peso.");
  }
}
