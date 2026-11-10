package com.tallerwebi.dominio.Reporte;

import com.tallerwebi.dominio.excepcion.PrecioIncorrecto;

final class PrecioDeReporte {

  private static final double PRECIO_MAXIMO = 10_000_000.0;

  private PrecioDeReporte() {}

  static void validar(Double precio) throws PrecioIncorrecto {
    if (!esPositivo(precio)) {
      throw new PrecioIncorrecto("El precio debe ser mayor a cero");
    }
    if (precio > PRECIO_MAXIMO) {
      throw new PrecioIncorrecto("El precio no puede superar $10.000.000");
    }
    if (Math.round(precio * 100) != (long) (precio * 100)) {
      throw new PrecioIncorrecto("El precio no puede tener más de 2 decimales");
    }
  }

  // NaN e infinito no son <= 0 pero tampoco son precios: se rechazan junto con el nulo
  private static boolean esPositivo(Double precio) {
    return precio != null && !precio.isNaN() && !precio.isInfinite() && precio > 0;
  }
}
