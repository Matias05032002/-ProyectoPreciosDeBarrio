package com.tallerwebi.dominio.Asistente;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

// Saca de la pregunta del vecino las palabras que sirven para buscar productos
final class PalabrasClave {

  private static final int LARGO_MINIMO = 3;
  private static final int MAX_PALABRAS = 5;
  private static final String CONSONANTES_ANTES_DE_ES = "nrlzd";
  private static final Set<String> VACIAS = Set.of(
    "para",
    "con",
    "sin",
    "que",
    "como",
    "donde",
    "cual",
    "cuales",
    "cuanto",
    "cuanta",
    "esta",
    "estan",
    "hay",
    "mas",
    "menos",
    "barato",
    "barata",
    "baratos",
    "baratas",
    "precio",
    "precios",
    "quiero",
    "necesito",
    "comprar",
    "compra",
    "compras",
    "busco",
    "tengo",
    "pesos",
    "algo",
    "unos",
    "unas",
    "una",
    "uno",
    "los",
    "las",
    "del",
    "por",
    "hoy",
    "cerca",
    "favor",
    "podes",
    "puedo",
    "dame",
    "armame",
    "armar",
    "mejor",
    "ahora",
    "todo",
    "toda",
    "todos",
    "todas",
    "mis",
    "mio",
    "tus",
    "sus",
    "ese",
    "esa",
    "eso",
    "este",
    "esto",
    "bien",
    "muy",
    "pero",
    "porque",
    "segun",
    "entre",
    "desde",
    "hasta",
    "presupuesto",
    "disponible",
    "disponibles",
    "productos",
    "producto",
    "lista",
    "listas",
    "promo",
    "promos",
    "promocion",
    "promociones",
    "comercio",
    "comercios",
    "barrio",
    "ayuda",
    "ayudame",
    "decime",
    "dime",
    "saber",
    "sabes",
    "tenes",
    "tienen",
    "tiene"
  );

  private PalabrasClave() {}

  static String normalizar(String texto) {
    if (texto == null) {
      return "";
    }
    String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    return sinTildes.toLowerCase(Locale.ROOT);
  }

  // "aceites" y "aceite" buscan lo mismo: se guarda la palabra sin el plural
  static List<String> extraer(String pregunta) {
    List<String> palabras = new ArrayList<>();
    for (String token : normalizar(pregunta).split("[^a-z0-9]+")) {
      if (token.length() < LARGO_MINIMO || VACIAS.contains(token) || token.matches("\\d+")) {
        continue;
      }
      String singular = singularizar(token);
      if (!palabras.contains(singular) && palabras.size() < MAX_PALABRAS) {
        palabras.add(singular);
      }
    }
    return palabras;
  }

  // Coincide si alguna palabra del texto empieza con la palabra buscada ("pan" no encuentra "empanada")
  static boolean coincide(String texto, List<String> palabras) {
    String[] palabrasDelTexto = normalizar(texto).split("[^a-z0-9]+");
    return palabras
      .stream()
      .anyMatch(buscada ->
        Arrays.stream(palabrasDelTexto).anyMatch(palabra -> palabra.startsWith(buscada))
      );
  }

  private static String singularizar(String palabra) {
    boolean pluralConEs =
      palabra.length() > LARGO_MINIMO + 1 &&
      palabra.endsWith("es") &&
      CONSONANTES_ANTES_DE_ES.indexOf(palabra.charAt(palabra.length() - 3)) >= 0;
    if (pluralConEs) {
      return palabra.substring(0, palabra.length() - 2);
    }
    if (palabra.length() > LARGO_MINIMO && palabra.endsWith("s")) {
      return palabra.substring(0, palabra.length() - 1);
    }
    return palabra;
  }
}
