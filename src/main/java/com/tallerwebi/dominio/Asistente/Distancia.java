package com.tallerwebi.dominio.Asistente;

import com.tallerwebi.dominio.Comercio.Comercio;

final class Distancia {

  private static final int RADIO_TIERRA_KM = 6371;

  private Distancia() {}

  static boolean hayDistancia(Comercio comercio, Double latitud, Double longitud) {
    return (
      latitud != null &&
      longitud != null &&
      comercio.getLatitud() != null &&
      comercio.getLongitud() != null
    );
  }

  // Sin coordenadas del comercio no se puede medir: se devuelve 0 para no descartarlo
  static double km(Double latitud, Double longitud, Comercio comercio) {
    if (comercio.getLatitud() == null || comercio.getLongitud() == null) {
      return 0.0;
    }
    double dLat = Math.toRadians(comercio.getLatitud() - latitud);
    double dLon = Math.toRadians(comercio.getLongitud() - longitud);
    double haversine =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(Math.toRadians(latitud)) *
        Math.cos(Math.toRadians(comercio.getLatitud())) *
        Math.sin(dLon / 2) *
        Math.sin(dLon / 2);
    return RADIO_TIERRA_KM * 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
  }
}
