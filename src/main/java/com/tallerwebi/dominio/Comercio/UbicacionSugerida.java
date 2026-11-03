package com.tallerwebi.dominio.Comercio;

public record UbicacionSugerida(
  String nomenclatura,
  String direccion,
  String localidad,
  double latitud,
  double longitud,
  String fuente
) {}
