package com.tallerwebi.dominio.excepcion;

public class ReporteExistente extends Exception {

  private static final long serialVersionUID = 1L;

  public ReporteExistente() {
    super("Ya reportaste este precio hoy");
  }
}
