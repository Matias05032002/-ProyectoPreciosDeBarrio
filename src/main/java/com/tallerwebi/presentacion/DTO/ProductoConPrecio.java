package com.tallerwebi.presentacion.DTO;

public class ProductoConPrecio {

  private String nombre;
  private String marca;
  private String unidad;
  private String categoria;
  private Double precioMinimo;
  private String comercio;
  private Long reporteId;

  public ProductoConPrecio(
    String nombre,
    String marca,
    String unidad,
    String categoria,
    Double precioMinimo,
    String comercio,
    Long reporteId
  ) {
    this.nombre = nombre;
    this.marca = marca;
    this.unidad = unidad;
    this.categoria = categoria;
    this.precioMinimo = precioMinimo;
    this.comercio = comercio;
    this.reporteId = reporteId;
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
}
