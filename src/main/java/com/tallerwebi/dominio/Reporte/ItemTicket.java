package com.tallerwebi.dominio.Reporte;

public class ItemTicket {

  private String nombre;
  private double precio;
  private String categoria;
  private String marca;
  private String unidad;

  public ItemTicket(String nombre, double precio, String categoria, String marca, String unidad) {
    this.nombre = nombre;
    this.precio = precio;
    this.categoria = categoria != null ? categoria : "Varios";
    this.marca = marca != null ? marca : "Sin marca";
    this.unidad = unidad != null ? unidad : "1u";
  }

  // Constructor compatible con código viejo
  public ItemTicket(String nombre, double precio, String categoria, String marca) {
    this(nombre, precio, categoria, marca, "1u");
  }

  public ItemTicket(String nombre, double precio) {
    this(nombre, precio, "Varios", "Sin marca", "1u");
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public double getPrecio() {
    return precio;
  }

  public void setPrecio(double precio) {
    this.precio = precio;
  }

  public String getCategoria() {
    return categoria;
  }

  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }

  public String getMarca() {
    return marca;
  }

  public void setMarca(String marca) {
    this.marca = marca;
  }

  public String getUnidad() {
    return unidad;
  }

  public void setUnidad(String unidad) {
    this.unidad = unidad;
  }
}
