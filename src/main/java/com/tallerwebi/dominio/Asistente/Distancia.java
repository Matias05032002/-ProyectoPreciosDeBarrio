package com.tallerwebi.dominio.Asistente;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Haversine;

final class Distancia {

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
    return Haversine.km(latitud, longitud, comercio.getLatitud(), comercio.getLongitud());
  }
}
