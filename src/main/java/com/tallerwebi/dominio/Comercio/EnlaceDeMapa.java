package com.tallerwebi.dominio.Comercio;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// Enlace de Google Maps para llegar a un comercio: por coordenadas si las tiene, si no por dirección
public final class EnlaceDeMapa {

    private static final String RUTA = "https://www.google.com/maps/dir/?api=1&destination=";
    private static final String BUSQUEDA = "https://www.google.com/maps/search/?api=1&query=";

    private EnlaceDeMapa() {}

    // Devuelve null si el comercio no tiene ni coordenadas ni dirección
    public static String para(Comercio comercio) {
        if (comercio == null) {
            return null;
        }
        if (comercio.getLatitud() != null && comercio.getLongitud() != null) {
            return RUTA + comercio.getLatitud() + "," + comercio.getLongitud();
        }
        if (comercio.getDireccion() == null || comercio.getDireccion().isBlank()) {
            return null;
        }
        String lugar = comercio.getDireccion().trim();
        if (comercio.getLocalidad() != null && !comercio.getLocalidad().isBlank()) {
            lugar += ", " + comercio.getLocalidad().trim();
        }
        return BUSQUEDA + URLEncoder.encode(lugar, StandardCharsets.UTF_8).replace("+", "%20");
    }
}