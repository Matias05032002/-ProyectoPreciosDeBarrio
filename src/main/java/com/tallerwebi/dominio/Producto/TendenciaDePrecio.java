package com.tallerwebi.dominio.Producto;

import com.tallerwebi.dominio.Reporte.Reporte;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Cuánto cambió el precio de un producto en un comercio: el último precio reportado contra el
 * anterior que ese mismo comercio tuvo en los 30 días previos. Los reportes dudosos no cuentan.
 * Solo habla de un comercio: el precio de otro local no dice nada sobre este.
 */
public record TendenciaDePrecio(int porcentaje, LocalDate desde) {
  static final int DIAS_DE_VIGENCIA = 30;
  static final int MINIMO_DE_REPORTES = 2;
  // Un cambio menor no se muestra: es ruido, no una tendencia
  static final int CAMBIO_MINIMO_EN_PORCENTAJE = 2;

  public boolean sube() {
    return porcentaje > 0;
  }

  public boolean baja() {
    return porcentaje < 0;
  }

  // Para mostrar "bajó 8%": la dirección la dice el texto, no el signo
  public int porcentajeAbsoluto() {
    return Math.abs(porcentaje);
  }

  /**
   * La tendencia del precio del comercio, o null si no hay dos precios comparables: el último debe
   * ser de los últimos 30 días y el anterior, de otro día y hasta 30 días antes del último.
   */
  public static TendenciaDePrecio calcular(List<Reporte> reportesDelComercio, LocalDateTime ahora) {
    if (reportesDelComercio == null) {
      return null;
    }
    List<Reporte> validos = reportesDelComercio
      .stream()
      .filter(TendenciaDePrecio::sirve)
      .sorted(Comparator.comparing(Reporte::getFechaDeReporte).reversed())
      .toList();
    if (validos.size() < MINIMO_DE_REPORTES) {
      return null;
    }
    Reporte actual = validos.get(0);
    if (!actual.getFechaDeReporte().isAfter(ahora.minusDays(DIAS_DE_VIGENCIA))) {
      return null;
    }
    Reporte anterior = validos
      .stream()
      .skip(1)
      .filter(r -> esDeUnDiaAnteriorYCercano(r, actual))
      .findFirst()
      .orElse(null);
    if (anterior == null) {
      return null;
    }
    int cambio = (int) Math.round(
      ((actual.getPrecio() - anterior.getPrecio()) * 100) / anterior.getPrecio()
    );
    return Math.abs(cambio) < CAMBIO_MINIMO_EN_PORCENTAJE
      ? null
      : new TendenciaDePrecio(cambio, anterior.getFechaDeReporte().toLocalDate());
  }

  private static boolean esDeUnDiaAnteriorYCercano(Reporte candidato, Reporte actual) {
    LocalDate fecha = candidato.getFechaDeReporte().toLocalDate();
    LocalDate fechaActual = actual.getFechaDeReporte().toLocalDate();
    return fecha.isBefore(fechaActual) && !fecha.isBefore(fechaActual.minusDays(DIAS_DE_VIGENCIA));
  }

  private static boolean sirve(Reporte reporte) {
    boolean dudoso =
      reporte.getPuntuacion() != null && reporte.getPuntuacion() > Reporte.PUNTUACION_DUDOSO;
    return (
      !dudoso &&
      reporte.getFechaDeReporte() != null &&
      reporte.getPrecio() != null &&
      reporte.getPrecio() > 0
    );
  }
}
