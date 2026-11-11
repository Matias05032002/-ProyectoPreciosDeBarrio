package com.tallerwebi.dominio.Comercio;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

// Foto real que se muestra cuando un comercio todavía no subió su logo: depende de su tipo.
public final class ImagenDeComercio {

  public static final List<String> TIPOS = List.of(
    "Almacen",
    "Verduleria",
    "Carniceria",
    "Kiosco",
    "Supermercado"
  );

  private static final String CARPETA = "/img/comercio/";

  private ImagenDeComercio() {}

  // null si el tipo no tiene foto (por ejemplo "Otro"): la vista muestra la letra inicial.
  public static String rutaPara(String tipo) {
    String nombre = nombreDe(tipo);
    return nombre == null ? null : CARPETA + nombre + ".jpg";
  }

  private static String nombreDe(String tipo) {
    if (tipo == null) {
      return null;
    }
    String normalizado = Normalizer
      .normalize(tipo, Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")
      .trim()
      .toLowerCase(Locale.ROOT);
    for (String conocido : TIPOS) {
      if (conocido.toLowerCase(Locale.ROOT).equals(normalizado)) {
        return normalizado;
      }
    }
    return null;
  }
}
