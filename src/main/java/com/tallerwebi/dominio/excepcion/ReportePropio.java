package com.tallerwebi.dominio.excepcion;

public class ReportePropio extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ReportePropio() {
    super("No podés marcar como dudoso un reporte que cargaste vos");
  }
}
