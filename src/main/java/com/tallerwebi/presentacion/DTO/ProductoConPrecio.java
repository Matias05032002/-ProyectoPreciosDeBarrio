package com.tallerwebi.presentacion.DTO;

import com.tallerwebi.dominio.Reporte.Reporte;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@SuppressWarnings("PMD.TooManyFields")
public class ProductoConPrecio {

  private final String antiguedad;
  private String nombre;
  private String marca;
  private String unidad;
  private String categoria;
  private Double precioMinimo;
  private String comercio;
  private Long reporteId;
  private LocalDateTime fechaDeReporte;
  private static final int DIAS_FRESCO = 7;
  private static final int DIAS_DESACTUALIZADO = 45;
  private boolean esDudoso;
  private boolean yaMarcoDudoso;
  private Integer puntuacion;

  public ProductoConPrecio(
    String nombre,
    String marca,
    String unidad,
    String categoria,
    Double precioMinimo,
    String comercio,
    Long reporteId,
    LocalDateTime fechaDeReporte,
    Integer puntuacion
  ) {
    this.nombre = nombre;
    this.marca = marca;
    this.unidad = unidad;
    this.categoria = categoria;
    this.precioMinimo = precioMinimo;
    this.comercio = comercio;
    this.reporteId = reporteId;
    this.antiguedad = calcularAntiguedadDelProducto(fechaDeReporte);
    this.esDudoso = puntuacion != null && puntuacion > Reporte.PUNTUACION_DUDOSO;
    this.puntuacion = puntuacion;
  }

  public String getNombre() {
    return nombre;
  }

  public String getMarca() {
    return marca;
  }

  public String getUnidad() {
    return unidad;
  }

  public String getCategoria() {
    return categoria;
  }

  public Double getPrecioMinimo() {
    return precioMinimo;
  }

  public String getComercio() {
    return comercio;
  }

  public Long getReporteId() {
    return reporteId;
  }

  public void setReporteId(Long reporteId) {
    this.reporteId = reporteId;
  }

  public String getAntiguedad() {
    return antiguedad;
  }

  public LocalDateTime getFechaDeReporte() {
    return fechaDeReporte;
  }

  public void setFechaDeReporte(LocalDateTime fechaDeReporte) {
    this.fechaDeReporte = fechaDeReporte;
  }

  public boolean isYaMarcoDudoso() {
    return yaMarcoDudoso;
  }

  public void setYaMarcoDudoso(boolean yaMarcoDudoso) {
    this.yaMarcoDudoso = yaMarcoDudoso;
  }

  public Integer getPuntuacion() {
    return puntuacion;
  }

  private String calcularAntiguedadDelProducto(LocalDateTime fechaDeReporte) {
    if (fechaDeReporte == null) {
      return "Desconocido";
    }
    long dias = ChronoUnit.DAYS.between(fechaDeReporte, LocalDateTime.now());
    if (dias <= DIAS_FRESCO) {
      return "Fresco";
    } else if (dias <= DIAS_DESACTUALIZADO) {
      return "Desactualizado";
    } else {
      return "Vencido";
    }
  }

  public boolean isEsDudoso() {
    return esDudoso;
  }
}
