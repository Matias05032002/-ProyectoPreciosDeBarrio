package com.tallerwebi.dominio.Comercio;

public enum MetodoPago {
  EFECTIVO("Efectivo", "bi-cash-coin"),
  DEBITO("Tarjeta de débito", "bi-credit-card-2-front"),
  CREDITO("Tarjeta de crédito", "bi-credit-card"),
  TRANSFERENCIA("Transferencia", "bi-bank"),
  BILLETERA_VIRTUAL("Billetera virtual / QR", "bi-qr-code");

  private final String etiqueta;
  private final String icono;

  MetodoPago(String etiqueta, String icono) {
    this.etiqueta = etiqueta;
    this.icono = icono;
  }

  public String getEtiqueta() {
    return etiqueta;
  }

  public String getIcono() {
    return icono;
  }
}
