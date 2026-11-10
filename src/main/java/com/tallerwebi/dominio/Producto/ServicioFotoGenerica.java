package com.tallerwebi.dominio.Producto;

@FunctionalInterface
public interface ServicioFotoGenerica {
  // Una foto real del tipo de producto, sin marca (una palta, un algodón). Null si no hay.
  String buscarFoto(String nombre, String categoria);
}
