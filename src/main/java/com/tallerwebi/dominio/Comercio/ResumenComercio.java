package com.tallerwebi.dominio.Comercio;

import java.util.List;

// Lo que el comercio ve de un vistazo al entrar a su inicio
public record ResumenComercio(
  String nombre,
  boolean verificado,
  boolean solicitudVerificacion,
  int preciosReportados,
  int vecinos,
  int promocionesActivas,
  int porcentajePerfil,
  List<PasoPerfil> pasos,
  List<UltimoPrecio> ultimosPrecios
) {
  public record PasoPerfil(String clave, String titulo, boolean completo) {}

  public record UltimoPrecio(String producto, double precio, String fecha) {}
}
