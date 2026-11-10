package com.tallerwebi.dominio.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Un producto del catálogo es el que buscamos si coinciden nombre, marca y tamaño (la misma clave
// que identifica a un producto en la base) y el resultado no es otra cosa más larga que lo nombra de
// pasada ("Vinagre de manzana en cápsulas con bicarbonato" no es "Bicarbonato"). Lo que no se pide
// no se exige: una marca "Sin marca" o una unidad como "1u". Si se pide un tamaño, el resultado
// tiene que decirlo.
final class CoincidenciaDeProducto {

  private static final Pattern MEDIDA = Pattern.compile(
    "(\\d+(?:[.,]\\d+)?)\\s*(kg|kilos?|gramos?|grs?|g|ml|cc|litros?|lts?|l)(?![a-z])"
  );
  private static final String SIN_MARCA = "sin marca";
  private static final int MILI_POR_UNIDAD = 1000;
  private static final int MAXIMO_DE_PALABRAS_DE_MAS = 2;

  private CoincidenciaDeProducto() {}

  static boolean coincide(
    String nombre,
    String marca,
    String unidad,
    String nombreEncontrado,
    String marcaEncontrada
  ) {
    return (
      CoincidenciaDeNombre.coincide(nombre, nombreEncontrado) &&
      sinPalabrasDeMas(nombre, marca, nombreEncontrado) &&
      coincideLaMarca(marca, nombreEncontrado + " " + marcaEncontrada) &&
      coincideElTamano(unidad, nombreEncontrado)
    );
  }

  // Las palabras del resultado que no están en el nombre ni en la marca buscados (los números y
  // medidas no cuentan)
  private static boolean sinPalabrasDeMas(String nombre, String marca, String nombreEncontrado) {
    List<String> propias = new ArrayList<>(CoincidenciaDeNombre.palabrasUtiles(nombre));
    propias.addAll(CoincidenciaDeNombre.palabrasUtiles(marca));
    long deMas = CoincidenciaDeNombre
      .palabrasUtiles(
        MEDIDA.matcher(CoincidenciaDeNombre.normalizar(nombreEncontrado)).replaceAll(" ")
      )
      .stream()
      .filter(palabra -> !palabra.matches(".*\\d.*"))
      .filter(palabra -> !CoincidenciaDeNombre.aparece(palabra, propias))
      .count();
    return deMas <= MAXIMO_DE_PALABRAS_DE_MAS;
  }

  // Una marca real: lo que no sea vacío ni "Sin marca"
  static boolean tieneMarca(String marca) {
    return (
      !CoincidenciaDeNombre.palabrasUtiles(marca).isEmpty() &&
      !SIN_MARCA.equals(CoincidenciaDeNombre.normalizar(marca).trim())
    );
  }

  private static boolean coincideLaMarca(String marca, String textoEncontrado) {
    if (!tieneMarca(marca)) {
      return true;
    }
    List<String> palabras = CoincidenciaDeNombre.palabrasUtiles(marca);
    List<String> encontradas = CoincidenciaDeNombre.palabrasUtiles(textoEncontrado);
    return palabras
      .stream()
      .allMatch(palabra -> CoincidenciaDeNombre.aparece(palabra, encontradas));
  }

  private static boolean coincideElTamano(String unidad, String nombreEncontrado) {
    List<Long> buscadas = medidas(unidad);
    List<Long> encontradas = medidas(nombreEncontrado);
    return buscadas.isEmpty() || encontradas.contains(buscadas.get(0));
  }

  // Todas las medidas del texto en gramos o mililitros, para que "1kg" y "1000 gr" sean iguales.
  private static List<Long> medidas(String texto) {
    List<Long> medidas = new ArrayList<>();
    Matcher coincidencia = MEDIDA.matcher(CoincidenciaDeNombre.normalizar(texto));
    while (coincidencia.find()) {
      double cantidad = Double.parseDouble(coincidencia.group(1).replace(',', '.'));
      medidas.add(Math.round(cantidad * factor(coincidencia.group(2))));
    }
    return medidas;
  }

  private static int factor(String unidad) {
    return unidad.startsWith("k") || unidad.startsWith("l") ? MILI_POR_UNIDAD : 1;
  }
}
