package com.tallerwebi.dominio.Comercio;

public enum TipoPromocion {
  PRODUCTO("Producto"),
  METODO_PAGO("Método de pago");

  private final String etiqueta;

  TipoPromocion(String etiqueta) {
    this.etiqueta = etiqueta;
  }

  public String getEtiqueta() {
    return etiqueta;
  }
}
