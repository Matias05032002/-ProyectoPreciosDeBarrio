package com.tallerwebi.dominio.Producto;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

// Decide si el nombre de un producto del catálogo corresponde al que escribió el vecino o leyó el
// OCR. Los tickets abrevian ("TOALL FEM NOCT ALAS"), así que una palabra coincide por prefijo.
final class CoincidenciaDeNombre {

  private static final Set<String> PALABRAS_SIN_VALOR = Set.of(
    "de",
    "del",
    "la",
    "las",
    "el",
    "los",
    "en",
    "al",
    "con",
    "sin",
    "por",
    "para",
    "tacc",
    "granel"
  );
  private static final int LARGO_MINIMO_DE_PALABRA = 2;
  private static final int LARGO_MINIMO_PARA_PREFIJO_INVERSO = 4;

  private CoincidenciaDeNombre() {}

  // Coincide si todas las palabras útiles del buscado aparecen en el encontrado.
  static boolean coincide(String buscado, String encontrado) {
    List<String> palabrasBuscadas = palabrasUtiles(buscado);
    List<String> palabrasEncontradas = palabrasUtiles(encontrado);
    return (
      !palabrasBuscadas.isEmpty() &&
      palabrasBuscadas.stream().allMatch(palabra -> aparece(palabra, palabrasEncontradas))
    );
  }

  static boolean aparece(String palabra, List<String> palabras) {
    return palabras
      .stream()
      .anyMatch(otra ->
        otra.startsWith(palabra) ||
        (otra.length() >= LARGO_MINIMO_PARA_PREFIJO_INVERSO && palabra.startsWith(otra))
      );
  }

  static String normalizar(String texto) {
    if (texto == null) {
      return "";
    }
    return Normalizer
      .normalize(texto, Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")
      .toLowerCase(Locale.ROOT);
  }

  static List<String> palabrasUtiles(String texto) {
    return Arrays
      .stream(normalizar(texto).split("[^a-z0-9]+"))
      .filter(palabra -> palabra.length() >= LARGO_MINIMO_DE_PALABRA)
      .filter(palabra -> !PALABRAS_SIN_VALOR.contains(palabra))
      .toList();
  }
}
