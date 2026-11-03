package com.tallerwebi.dominio.Lista;

import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioListaImpl implements ServicioLista {

  private static final String LISTA_AJENA = "La lista no pertenece al usuario";
  private static final int CANTIDAD_MINIMA = 1;
  private static final int LARGO_MAXIMO_NOMBRE = 100;

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
    if (nombre.trim().length() > LARGO_MAXIMO_NOMBRE) {
      throw new IllegalArgumentException(
        "El nombre de la lista no puede superar los " + LARGO_MAXIMO_NOMBRE + " caracteres"
      );
    }
    Lista lista = new Lista(nombre.trim(), usuarioId);
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
  public void agregarProductoALista(Long listaId, Long reporteId, int cantidad, Long usuarioId) {
    validarCantidad(cantidad);
    Lista lista = repositorioLista.buscarPorId(listaId);
    if (lista == null) throw new IllegalArgumentException("Lista no encontrada");
    validarDuenio(lista, usuarioId);

    Reporte reporte = repositorioReporte.buscarReporte(reporteId);
    if (reporte == null) throw new IllegalArgumentException("Reporte no encontrado");

    ItemLista item = new ItemLista(lista, reporte, cantidad);
    repositorioLista.agregarItem(item);
  }

  @Override
  public void eliminarItemDeLista(Long itemId, Long usuarioId) {
    ItemLista item = repositorioLista.buscarItemPorId(itemId);
    if (item != null) {
      // El dueño se toma de la lista del ítem, nunca del listaId que manda el formulario
      Lista lista = item.getLista();
      validarDuenio(lista, usuarioId);
      lista.getItems().remove(item);
      repositorioLista.guardar(lista);
    }
  }

  @Override
  public void eliminarLista(Long listaId) {
    repositorioLista.eliminarLista(listaId);
  }

  @Override
  public void editarCantidadItem(Long itemId, int cantidad, Long usuarioId) {
    validarCantidad(cantidad);
    ItemLista item = repositorioLista.buscarItemPorId(itemId);
    if (item == null) throw new IllegalArgumentException("Producto de la lista no encontrado");
    validarDuenio(item.getLista(), usuarioId);
    repositorioLista.editarCantidadItem(itemId, cantidad);
  }

  private void validarDuenio(Lista lista, Long usuarioId) {
    if (lista == null || usuarioId == null || !usuarioId.equals(lista.getUsuarioId())) {
      throw new AccesoDenegado(LISTA_AJENA);
    }
  }

  private void validarCantidad(int cantidad) {
    if (cantidad < CANTIDAD_MINIMA) {
      throw new IllegalArgumentException("La cantidad debe ser al menos 1");
    }
  }
}
