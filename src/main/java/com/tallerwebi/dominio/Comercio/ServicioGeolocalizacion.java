package com.tallerwebi.dominio.Comercio;

import java.util.List;

// Es un servicio de Spring con un solo método, no una interfaz para usar con lambdas
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface ServicioGeolocalizacion {
  /**
   * Busca dónde queda una dirección del barrio, tolerando errores de tipeo. Devuelve las opciones
   * de la más probable a la menos probable, solo las que tienen coordenadas. Nunca lanza excepción:
   * si los servicios externos fallan devuelve una lista vacía.
   *
   * @param direccion calle y altura tal como la escribió el usuario
   * @param localidad valor del catálogo de {@link com.tallerwebi.dominio.Localidades}
   */
  List<UbicacionSugerida> buscar(String direccion, String localidad);
}
