package com.tallerwebi.dominio.Comercio;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

// Categorías que puede tener un comercio. Todo comercio tiene una: si quien lo carga no la elige
// (un vecino que reporta un precio, por ejemplo) se deduce del nombre, y si no hay pistas es "Otro".
public final class TipoDeComercio {

  public static final String OTRO = "Otro";

  public static final List<String> TIPOS = List.of(
    "Almacen",
    "Verduleria",
    "Carniceria",
    "Kiosco",
    "Supermercado",
    OTRO
  );

  // Por orden de prioridad: si el nombre menciona dos rubros gana el primero.
  private static final List<Regla> REGLAS = List.of(
    new Regla("Verduleria", List.of("verduler", "fruter"), List.of()),
    new Regla("Carniceria", List.of("carniceri", "polleri"), List.of()),
    new Regla("Kiosco", List.of("kiosc", "quiosc"), List.of()),
    new Regla(
      "Supermercado",
      List.of("supermerc", "hipermerc", "mayorist"),
      List.of("super", "hiper", "coto", "carrefour", "jumbo", "disco", "vea", "dia")
    ),
    new Regla(
      "Almacen",
      List.of("almacen", "despensa", "fiambreri", "minimerc", "autoservicio"),
      List.of()
    )
  );

  private TipoDeComercio() {}

  // El tipo de la lista que corresponde al texto, o null si no es ninguno.
  public static String normalizar(String tipo) {
    if (tipo == null) {
      return null;
    }
    String buscado = sinTildes(tipo);
    for (String conocido : TIPOS) {
      if (sinTildes(conocido).equals(buscado)) {
        return conocido;
      }
    }
    return null;
  }

  public static String inferirDe(String nombre) {
    if (nombre == null) {
      return OTRO;
    }
    String[] palabras = sinTildes(nombre).split("[^a-z0-9]+");
    for (Regla regla : REGLAS) {
      if (regla.coincideCon(palabras)) {
        return regla.tipo;
      }
    }
    return OTRO;
  }

  // Gana la pista del nombre; si no hay, la sugerencia (por ejemplo la del OCR de un ticket).
  public static String elegir(String nombre, String sugerido) {
    String delNombre = inferirDe(nombre);
    if (!OTRO.equals(delNombre)) {
      return delNombre;
    }
    String valido = normalizar(sugerido);
    return valido != null ? valido : OTRO;
  }

  public static String paraGuardar(String tipo, String nombre) {
    String valido = normalizar(tipo);
    return valido != null ? valido : inferirDe(nombre);
  }

  private static String sinTildes(String texto) {
    return Normalizer
      .normalize(texto, Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")
      .trim()
      .toLowerCase(Locale.ROOT);
  }

  private record Regla(String tipo, List<String> comienzos, List<String> palabras) {
    boolean coincideCon(String... textoPalabras) {
      for (String palabra : textoPalabras) {
        if (palabras.contains(palabra) || empiezaConAlguno(palabra)) {
          return true;
        }
      }
      return false;
    }

    private boolean empiezaConAlguno(String palabra) {
      for (String comienzo : comienzos) {
        if (palabra.startsWith(comienzo)) {
          return true;
        }
      }
      return false;
    }
  }
}
