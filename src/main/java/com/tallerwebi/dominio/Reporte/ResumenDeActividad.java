package com.tallerwebi.dominio.Reporte;

// Los números de confianza del inicio: cuánto se reportó en la última semana y en cuántos
// comercios hay precios confiables
public class ResumenDeActividad {

  private final long reportesSemana;
  private final long comercios;

  public ResumenDeActividad(long reportesSemana, long comercios) {
    this.reportesSemana = reportesSemana;
    this.comercios = comercios;
  }

  public long getReportesSemana() {
    return reportesSemana;
  }

  public long getComercios() {
    return comercios;
  }
}
