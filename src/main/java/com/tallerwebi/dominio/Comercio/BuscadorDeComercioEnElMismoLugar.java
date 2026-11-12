package com.tallerwebi.dominio.Comercio;

import com.tallerwebi.dominio.Haversine;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

final class BuscadorDeComercioEnElMismoLugar {

  private static final double RADIO_DEL_MISMO_LOCAL_KM = 0.01;

  private final RepositorioComercio repositorioComercio;
  private final ServicioGeolocalizacion servicioGeolocalizacion;

  BuscadorDeComercioEnElMismoLugar(
    RepositorioComercio repositorioComercio,
    ServicioGeolocalizacion servicioGeolocalizacion
  ) {
    this.repositorioComercio = repositorioComercio;
    this.servicioGeolocalizacion = servicioGeolocalizacion;
  }

  Comercio porDireccion(String direccion, String localidad) {
    if (direccion == null || direccion.isBlank() || localidad == null) {
      return null;
    }
    Comercio porTexto = porTexto(direccion, localidad);
    if (porTexto != null) {
      return porTexto;
    }
    List<UbicacionSugerida> sugerencias = servicioGeolocalizacion.buscar(direccion, localidad);
    if (sugerencias == null || sugerencias.isEmpty()) {
      return null;
    }
    return porLugar(sugerencias.get(0), localidad);
  }

  // El comercio cargado con esa dirección: primero el texto igual y, si no, el mismo texto escrito
  // con espacios de más, tildes, mayúsculas o puntos ("Av  de los corrales" = "Av de los corrales")
  Comercio porTexto(String direccion, String localidad) {
    if (direccion == null || localidad == null) {
      return null;
    }
    Comercio igual = repositorioComercio.buscarComercioPorDireccionYLocalidad(direccion, localidad);
    return igual != null ? igual : conLaMismaDireccionEscritaDistinto(direccion, localidad);
  }

  private Comercio conLaMismaDireccionEscritaDistinto(String direccion, String localidad) {
    String buscada = direccionNormalizada(direccion);
    return repositorioComercio
      .listarTodos()
      .stream()
      .filter(comercio -> !Boolean.FALSE.equals(comercio.getActivo()))
      .filter(comercio -> localidad.trim().equalsIgnoreCase(comercio.getLocalidad()))
      .filter(comercio -> buscada.equals(direccionNormalizada(comercio.getDireccion())))
      .min(Comparator.comparing(comercio -> comercio.getUsuarioId() == null))
      .orElse(null);
  }

  static String direccionNormalizada(String direccion) {
    if (direccion == null) {
      return "";
    }
    String sinTildes = Normalizer
      .normalize(direccion, Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "");
    String sinSignos = sinTildes.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
    return sinSignos.replaceAll("^(av|avda) ", "avenida ");
  }

  // El comercio de la misma localidad que está en ese punto del mapa (a 10 metros o menos)
  Comercio cercaDe(Double latitud, Double longitud, String localidad) {
    if (latitud == null || longitud == null || localidad == null) {
      return null;
    }
    return repositorioComercio
      .listarTodos()
      .stream()
      .filter(comercio -> !Boolean.FALSE.equals(comercio.getActivo()))
      .filter(comercio -> comercio.getLatitud() != null && comercio.getLongitud() != null)
      .filter(comercio -> localidad.trim().equalsIgnoreCase(comercio.getLocalidad()))
      .filter(comercio -> distanciaKm(latitud, longitud, comercio) <= RADIO_DEL_MISMO_LOCAL_KM)
      .min(Comparator.comparingDouble(comercio -> distanciaKm(latitud, longitud, comercio)))
      .orElse(null);
  }

  private Comercio porLugar(UbicacionSugerida lugar, String localidad) {
    if (lugar.direccion() != null && !lugar.direccion().isBlank()) {
      Comercio porDireccionOficial = porTexto(lugar.direccion(), localidad);
      if (porDireccionOficial != null) {
        return porDireccionOficial;
      }
    }
    return cercaDe(lugar.latitud(), lugar.longitud(), localidad);
  }

  private double distanciaKm(double latitud, double longitud, Comercio comercio) {
    return Haversine.km(latitud, longitud, comercio.getLatitud(), comercio.getLongitud());
  }
}
