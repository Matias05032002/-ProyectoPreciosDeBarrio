package com.tallerwebi.dominio.Comercio;

public record UbicacionSugerida(
  String nomenclatura,
  String localidad,
  double latitud,
  double longitud,
  String fuente
) {}
