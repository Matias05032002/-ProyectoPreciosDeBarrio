package com.tallerwebi.dominio.Reporte;

import com.tallerwebi.dominio.Usuario;

// Reglas de quién puede marcar un reporte como dudoso
final class MarcasDeDudoso {

  private MarcasDeDudoso() {}

  static boolean esElAutor(Reporte reporte, Usuario usuario) {
    return (
      reporte.getUsuario() != null &&
      usuario != null &&
      reporte.getUsuario().getId().equals(usuario.getId())
    );
  }

  static boolean yaLoMarco(Reporte reporte, Usuario usuario) {
    return (
      usuario != null &&
      reporte
        .getUsuariosQueMarcaron()
        .stream()
        .anyMatch(u -> u.getId() != null && u.getId().equals(usuario.getId()))
    );
  }
}
