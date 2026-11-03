package com.tallerwebi.config;

import io.github.cdimascio.dotenv.Dotenv;

public final class VariablesDeEntorno {

  private VariablesDeEntorno() {}

  public static String leer(String nombre) {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    String valor = dotenv.get(nombre);
    if (valor == null || valor.isBlank()) {
      valor = System.getenv(nombre);
    }
    return valor == null || valor.isBlank() ? null : valor.trim();
  }
}
