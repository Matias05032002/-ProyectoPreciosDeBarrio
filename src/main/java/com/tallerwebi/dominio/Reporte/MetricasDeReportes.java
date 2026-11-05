package com.tallerwebi.dominio.Reporte;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

// Cálculos sobre listas de reportes que usan el inicio del vecino y el panel del comercio
final class MetricasDeReportes {

  private static final int DIAS_DE_ACTIVIDAD = 7;

  private MetricasDeReportes() {}

  // Lo que ve cualquier vecino en el inicio: reportes completos y que nadie marcó como dudosos
  static List<Reporte> visibles(List<Reporte> todos) {
    return sinNulos(todos)
      .stream()
      .filter(MetricasDeReportes::estaCompleto)
      .filter(r -> r.getPuntuacion() == null || r.getPuntuacion() <= Reporte.PUNTUACION_DUDOSO)
      .collect(Collectors.toList());
  }

  static List<Reporte> masRecientes(List<Reporte> visibles, int limite) {
    return visibles
      .stream()
      .sorted(Comparator.comparing(Reporte::getFechaDeReporte).reversed())
      .limit(limite)
      .collect(Collectors.toList());
  }

  static ResumenDeActividad resumen(List<Reporte> visibles) {
    LocalDateTime desde = LocalDateTime.now().minusDays(DIAS_DE_ACTIVIDAD);
    long reportesSemana = visibles
      .stream()
      .filter(r -> r.getFechaDeReporte().isAfter(desde))
      .count();
    long comercios = visibles
      .stream()
      .map(r -> r.getComercio().getId())
      .filter(Objects::nonNull)
      .distinct()
      .count();
    return new ResumenDeActividad(reportesSemana, comercios);
  }

  static double precioPromedio(List<Reporte> reportes) {
    return sinNulos(reportes)
      .stream()
      .filter(r -> r.getPrecio() != null)
      .mapToDouble(Reporte::getPrecio)
      .average()
      .orElse(0.0);
  }

  // El producto con más reportes; en empate, el primero por orden alfabético
  static String productoEstrella(List<Reporte> reportes) {
    return sinNulos(reportes)
      .stream()
      .filter(r -> r.getProducto() != null && r.getProducto().getNombre() != null)
      .collect(Collectors.groupingBy(r -> r.getProducto().getNombre(), Collectors.counting()))
      .entrySet()
      .stream()
      .max(
        Map.Entry
          .<String, Long>comparingByValue()
          .thenComparing(Map.Entry.<String, Long>comparingByKey(Comparator.reverseOrder()))
      )
      .map(Map.Entry::getKey)
      .orElse("-");
  }

  private static List<Reporte> sinNulos(List<Reporte> reportes) {
    return Objects.requireNonNullElse(reportes, List.of());
  }

  private static boolean estaCompleto(Reporte reporte) {
    return (
      reporte.getFechaDeReporte() != null &&
      reporte.getPrecio() != null &&
      reporte.getProducto() != null &&
      reporte.getComercio() != null
    );
  }
}
