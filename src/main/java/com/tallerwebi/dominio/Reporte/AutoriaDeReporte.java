package com.tallerwebi.dominio.Reporte;

import java.time.LocalDateTime;

final class AutoriaDeReporte {

  private AutoriaDeReporte() {}

  static boolean esAutor(Reporte reporte, Long usuarioId) {
    return (
      reporte != null &&
      reporte.getUsuario() != null &&
      reporte.getUsuario().getId() != null &&
      reporte.getUsuario().getId().equals(usuarioId)
    );
  }

  static boolean estaEnPlazo(Reporte reporte, int diasDePlazo) {
    return (
      reporte.getFechaDeReporte() != null &&
      reporte.getFechaDeReporte().isAfter(LocalDateTime.now().minusDays(diasDePlazo))
    );
  }
}
