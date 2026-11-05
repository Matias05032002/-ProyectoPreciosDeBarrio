package com.tallerwebi.dominio.excepcion;

public class ComercioYaRegistrado extends Exception {

  private static final long serialVersionUID = 1L;

  public ComercioYaRegistrado() {
    super("Ya existe un comercio en esa dirección y localidad");
  }
}
