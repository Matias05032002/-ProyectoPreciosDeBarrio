package com.tallerwebi.dominio.Lista;

import java.util.List;

public interface RepositorioLista {
  void guardar(Lista lista);
  Lista buscarPorId(Long id);
  List<Lista> buscarPorUsuarioId(Long usuarioId);
  void agregarItem(ItemLista item);
  void eliminarItem(Long itemId);
  ItemLista buscarItemPorId(Long itemId);
  void eliminarLista(Long listaId);
  void editarCantidadItem(Long itemId, int cantidad);
}
