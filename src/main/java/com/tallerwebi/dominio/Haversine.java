package com.tallerwebi.dominio;

public final class Haversine {

  private static final double RADIO_DE_LA_TIERRA_KM = 6371.0;

  private Haversine() {}

  // Haversine: distancia en línea recta entre dos puntos, en kilómetros
  public static double km(double latitud1, double longitud1, double latitud2, double longitud2) {
    double dLat = Math.toRadians(latitud2 - latitud1);
    double dLng = Math.toRadians(longitud2 - longitud1);
    double haversine =
      Math.pow(Math.sin(dLat / 2), 2) +
      Math.cos(Math.toRadians(latitud1)) *
        Math.cos(Math.toRadians(latitud2)) *
        Math.pow(Math.sin(dLng / 2), 2);
    return RADIO_DE_LA_TIERRA_KM * 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
  }
}
