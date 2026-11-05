package com.tallerwebi.dominio.excepcion;

public class FotoInvalida extends Exception {

  private static final long serialVersionUID = 1L;

  public FotoInvalida() {
    super("La foto tiene que ser una imagen JPG, PNG o WebP de hasta 5 MB");
  }

  public FotoInvalida(String mensaje) {
    super(mensaje);
  }
}
