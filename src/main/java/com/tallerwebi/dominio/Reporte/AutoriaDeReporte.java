package com.tallerwebi.dominio.Reporte;

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
}
