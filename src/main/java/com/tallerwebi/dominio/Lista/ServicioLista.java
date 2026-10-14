package com.tallerwebi.dominio.Lista;

import java.util.List;

public interface ServicioLista {
  Lista crearLista(String nombre, Long usuarioId);
  Lista obtenerListaPorId(Long id);
  List<Lista> obtenerListasDeUsuario(Long usuarioId);
  void agregarProductoALista(Long listaId, Long reporteId, int cantidad);
  void eliminarItemDeLista(Long itemId);
  void eliminarLista(Long listaId);
  void editarCantidadItem(Long itemId, int cantidad);
}
