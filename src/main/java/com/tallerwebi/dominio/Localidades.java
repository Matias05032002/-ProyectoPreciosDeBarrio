package com.tallerwebi.dominio;

import java.util.List;

/**
 * Localidades que se pueden elegir al registrarse. Tiene que coincidir con las opciones del
 * select de nuevo-usuario.html: el navegador solo deja elegir estas, pero un POST armado a mano
 * podría mandar cualquier texto.
 */
public final class Localidades {

  private static final List<String> VALIDAS = List.of(
    "Almirante Brown",
    "Avellaneda",
    "Bahia Blanca",
    "Berazategui",
    "Berisso",
    "Brandsen",
    "Buenos Aires",
    "Campana",
    "Canuelas",
    "Ensenada",
    "Escobar",
    "Esteban Echeverria",
    "Exaltacion de la Cruz",
    "Ezeiza",
    "Florencio Varela",
    "General Las Heras",
    "General Rodriguez",
    "Hurlingham",
    "Ituzaingo",
    "Jose C. Paz",
    "La Matanza",
    "La Plata",
    "Lanus",
    "Lobos",
    "Lomas de Zamora",
    "Lujan",
    "Magdalena",
    "Malvinas Argentinas",
    "Mar del Plata",
    "Marcos Paz",
    "Merlo",
    "Moreno",
    "Moron",
    "Navarro",
    "Pilar",
    "Presidente Peron",
    "Punta Indio",
    "Quilmes",
    "Ramallo",
    "San Fernando",
    "San Isidro",
    "San Martin",
    "San Miguel",
    "San Nicolas",
    "San Vicente",
    "Tigre",
    "Tres de Febrero",
    "Vicente Lopez",
    "Zarate"
  );

  private Localidades() {}

  public static boolean esValida(String localidad) {
    return localidad != null && VALIDAS.contains(localidad.trim());
  }
}
