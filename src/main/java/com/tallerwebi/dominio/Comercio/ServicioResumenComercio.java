package com.tallerwebi.dominio.Comercio;

@FunctionalInterface
public interface ServicioResumenComercio {
  // Devuelve null si el usuario todavía no cargó los datos de su comercio
  ResumenComercio obtenerResumen(Long usuarioId);
}
