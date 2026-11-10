package com.tallerwebi.dominio.Producto;

@FunctionalInterface
public interface ServicioImagenProducto {
  String buscarImagen(String nombre, String marca, String unidad);
}
