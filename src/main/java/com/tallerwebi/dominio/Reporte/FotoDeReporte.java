package com.tallerwebi.dominio.Reporte;

import com.tallerwebi.dominio.Imagen.TipoDeImagen;
import com.tallerwebi.dominio.excepcion.FotoInvalida;

// Reglas de la foto de ticket de un reporte: imagen real (JPG, PNG o WebP) de hasta 10 MB
final class FotoDeReporte {

  private static final int MAX_BYTES = 10 * 1024 * 1024;

  private FotoDeReporte() {}

  static void validar(byte[] foto) throws FotoInvalida {
    if (foto == null || foto.length == 0) {
      throw new FotoInvalida("No se pudo cargar la foto");
    }
    if (foto.length > MAX_BYTES) {
      throw new FotoInvalida("La foto no puede superar los 10 MB");
    }
    if (TipoDeImagen.detectar(foto) == null) {
      throw new FotoInvalida("El archivo debe ser una imagen JPG, PNG o WebP válida");
    }
  }
}
