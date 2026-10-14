package com.tallerwebi.dominio.Lista;

import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioListaImpl implements ServicioLista {

  private final RepositorioLista repositorioLista;
  private final RepositorioReporte repositorioReporte;

  @Autowired
  public ServicioListaImpl(
    RepositorioLista repositorioLista,
    RepositorioReporte repositorioReporte
  ) {
    this.repositorioLista = repositorioLista;
    this.repositorioReporte = repositorioReporte;
  }

  @Override
  public Lista crearLista(String nombre, Long usuarioId) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre de la lista no puede estar vacío");
    }
    Lista lista = new Lista(nombre, usuarioId);
    repositorioLista.guardar(lista);
    return lista;
  }

  @Override
  public Lista obtenerListaPorId(Long id) {
    return repositorioLista.buscarPorId(id);
  }

  @Override
  public List<Lista> obtenerListasDeUsuario(Long usuarioId) {
    return repositorioLista.buscarPorUsuarioId(usuarioId);
  }

  @Override
  public void agregarProductoALista(Long listaId, Long reporteId, int cantidad) {
    Lista lista = repositorioLista.buscarPorId(listaId);
    if (lista == null) throw new IllegalArgumentException("Lista no encontrada");

    Reporte reporte = repositorioReporte.buscarReporte(reporteId);
    if (reporte == null) throw new IllegalArgumentException("Reporte no encontrado");

    ItemLista item = new ItemLista(lista, reporte, cantidad);
    repositorioLista.agregarItem(item);
  }

  @Override
  public void eliminarItemDeLista(Long itemId) {
    repositorioLista.eliminarItem(itemId);
  }

  @Override
  public void eliminarLista(Long listaId) {
    repositorioLista.eliminarLista(listaId);
  }

  @Override
  public void editarCantidadItem(Long itemId, int cantidad) {
    repositorioLista.editarCantidadItem(itemId, cantidad);
  }
}
