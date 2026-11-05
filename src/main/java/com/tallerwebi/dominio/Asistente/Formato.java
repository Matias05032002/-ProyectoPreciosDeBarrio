package com.tallerwebi.dominio.Asistente;

import com.tallerwebi.dominio.Comercio.Promocion;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Reporte.Reporte;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;

// Formatos y criterios que comparten las secciones del contexto de Don Peso
final class Formato {

  static final Locale NUMEROS = Locale.US;
  private static final DateTimeFormatter DIA_MES = DateTimeFormatter.ofPattern("dd/MM");

  private Formato() {}

  static String dinero(double monto) {
    return String.format(NUMEROS, "$%.2f", monto);
  }

  static String clave(Producto producto) {
    return PalabrasClave.normalizar(
      producto.getNombre() + "|" + producto.getMarca() + "|" + producto.getUnidad()
    );
  }

  static String hasta(Promocion promocion) {
    return promocion.getFechaFin() == null
      ? ""
      : " hasta el " + promocion.getFechaFin().format(DIA_MES);
  }

  static Comparator<Reporte> porPrecio() {
    return Comparator.comparingDouble(Reporte::getPrecio);
  }
}
