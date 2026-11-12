package com.tallerwebi.dominio.Lista;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Lista.*;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioListaTest {

  private ServicioLista servicioLista;
  private RepositorioLista repositorioLista;
  private RepositorioReporte repositorioReporte;

  @BeforeEach
  void setUp() {
    repositorioLista = mock(RepositorioLista.class);
    repositorioReporte = mock(RepositorioReporte.class);
    servicioLista = new ServicioListaImpl(repositorioLista, repositorioReporte);
  }

  @Test
  void deberiaCrearUnaListaConNombreYUsuario() {
    Lista lista = servicioLista.crearLista("Mi lista del super", 1L);

    assertThat(lista, is(notNullValue()));
    assertThat(lista.getNombre(), is("Mi lista del super"));
    assertThat(lista.getUsuarioId(), is(1L));
    verify(repositorioLista).guardar(any(Lista.class));
  }

  @Test
  void deberiaLanzarExcepcionSiNombreEsVacio() {
    assertThrows(IllegalArgumentException.class, () -> servicioLista.crearLista("", 1L));
    assertThrows(IllegalArgumentException.class, () -> servicioLista.crearLista("   ", 1L));
    assertThrows(IllegalArgumentException.class, () -> servicioLista.crearLista(null, 1L));
  }

  @Test
  void deberiaObtenerListasDeUnUsuario() {
    Lista lista1 = new Lista("Lista 1", 1L);
    Lista lista2 = new Lista("Lista 2", 1L);
    when(repositorioLista.buscarPorUsuarioId(1L)).thenReturn(Arrays.asList(lista1, lista2));

    List<Lista> listas = servicioLista.obtenerListasDeUsuario(1L);

    assertThat(listas, hasSize(2));
    assertThat(listas, containsInAnyOrder(lista1, lista2));
  }

  @Test
  void deberiaRetornarListaVaciaSiUsuarioNoTieneListas() {
    when(repositorioLista.buscarPorUsuarioId(99L)).thenReturn(Collections.emptyList());

    List<Lista> listas = servicioLista.obtenerListasDeUsuario(99L);

    assertThat(listas, is(empty()));
  }

  @Test
  void deberiaAgregarUnProductoALaLista() {
    Lista lista = new Lista("Mi lista", 1L);
    lista.setId(1L);
    Reporte reporte = new Reporte();
    reporte.setId(5L);

    when(repositorioLista.buscarPorId(1L)).thenReturn(lista);
    when(repositorioReporte.buscarReporte(5L)).thenReturn(reporte);

    servicioLista.agregarProductoALista(1L, 5L, 2, 1L);

    verify(repositorioLista).agregarItem(any(ItemLista.class));
  }

  @Test
  void deberiaLanzarExcepcionSiListaNoExisteAlAgregar() {
    when(repositorioLista.buscarPorId(999L)).thenReturn(null);

    assertThrows(
      IllegalArgumentException.class,
      () -> servicioLista.agregarProductoALista(999L, 1L, 1, 1L)
    );
  }

  @Test
  void deberiaEliminarUnItemDeLaLista() {
    Lista lista = new Lista("Mi lista", 1L);
    ItemLista item = new ItemLista(lista, new Reporte(), 1);
    item.setId(1L);
    lista.setItems(new java.util.ArrayList<>(java.util.List.of(item)));

    when(repositorioLista.buscarItemPorId(1L)).thenReturn(item);

    servicioLista.eliminarItemDeLista(1L, 1L);

    verify(repositorioLista).guardar(lista);
    assertThat(lista.getItems(), is(empty()));
  }

  @Test
  void deberiaCalcularElTotalDeUnaLista() {
    Reporte reporte1 = new Reporte();
    reporte1.setPrecio(100.0);

    Reporte reporte2 = new Reporte();
    reporte2.setPrecio(250.0);

    Lista lista = new Lista("Mi lista", 1L);
    ItemLista item1 = new ItemLista(lista, reporte1, 2); // 200
    ItemLista item2 = new ItemLista(lista, reporte2, 1); // 250
    lista.setItems(Arrays.asList(item1, item2));

    assertThat(lista.getTotal(), is(450.0));
  }

  @Test
  void deberiaEditarCantidadDeUnItem() {
    when(repositorioLista.buscarItemPorId(1L)).thenReturn(itemDeLista(1L, 1L));

    servicioLista.editarCantidadItem(1L, 5, 1L);

    verify(repositorioLista).editarCantidadItem(1L, 5);
  }

  // --- solo el dueño puede modificar su lista ---

  @Test
  void agregarProductoAListaAjenaDeberiaLanzarAccesoDenegado() {
    Lista listaDeOtro = new Lista("Lista ajena", 2L);
    listaDeOtro.setId(1L);
    when(repositorioLista.buscarPorId(1L)).thenReturn(listaDeOtro);

    assertThrows(AccesoDenegado.class, () -> servicioLista.agregarProductoALista(1L, 5L, 1, 1L));
    verify(repositorioLista, never()).agregarItem(any(ItemLista.class));
  }

  @Test
  void eliminarItemDeListaAjenaDeberiaLanzarAccesoDenegado() {
    ItemLista itemAjeno = itemDeLista(7L, 2L);
    when(repositorioLista.buscarItemPorId(7L)).thenReturn(itemAjeno);

    assertThrows(AccesoDenegado.class, () -> servicioLista.eliminarItemDeLista(7L, 1L));
    verify(repositorioLista, never()).guardar(any(Lista.class));
    assertThat(itemAjeno.getLista().getItems().size(), is(1));
  }

  @Test
  void editarCantidadDeItemAjenoDeberiaLanzarAccesoDenegado() {
    when(repositorioLista.buscarItemPorId(7L)).thenReturn(itemDeLista(7L, 2L));

    assertThrows(AccesoDenegado.class, () -> servicioLista.editarCantidadItem(7L, 99, 1L));
    verify(repositorioLista, never()).editarCantidadItem(anyLong(), anyInt());
  }

  @Test
  void agregarProductoConCantidadCeroONegativaDeberiaLanzarExcepcion() {
    assertThrows(
      IllegalArgumentException.class,
      () -> servicioLista.agregarProductoALista(1L, 5L, 0, 1L)
    );
    assertThrows(
      IllegalArgumentException.class,
      () -> servicioLista.agregarProductoALista(1L, 5L, -3, 1L)
    );
    verify(repositorioLista, never()).agregarItem(any(ItemLista.class));
  }

  @Test
  void editarCantidadACeroDeberiaLanzarExcepcion() {
    assertThrows(IllegalArgumentException.class, () -> servicioLista.editarCantidadItem(1L, 0, 1L));
    verify(repositorioLista, never()).editarCantidadItem(anyLong(), anyInt());
  }

  @Test
  void deberiaSumarCantidadSiElProductoYaExisteConLosMismosDatos() {
    Lista lista = new Lista("Mi lista", 1L);
    lista.setItems(new ArrayList<>());

    Reporte reporteExistente = crearReporte(10L, 20L, "La Serenisima", 1500.0);
    Reporte reporteNuevo = crearReporte(10L, 20L, "La Serenisima", 1500.0);

    ItemLista itemExistente = new ItemLista(lista, reporteExistente, 2);
    lista.getItems().add(itemExistente);

    when(repositorioLista.buscarPorId(1L)).thenReturn(lista);
    when(repositorioReporte.buscarReporte(5L)).thenReturn(reporteNuevo);

    servicioLista.agregarProductoALista(1L, 5L, 3, 1L);

    assertThat(itemExistente.getCantidad(), is(5));
    verify(repositorioLista, never()).agregarItem(any(ItemLista.class));
  }

  @Test
  void deberiaCrearOtroItemSiCambiaElComercio() {
    verificarQueCreaOtroItem(
      crearReporte(10L, 20L, "La Serenisima", 1500.0),
      crearReporte(10L, 30L, "La Serenisima", 1500.0)
    );
  }

  @Test
  void deberiaCrearOtroItemSiCambiaLaMarca() {
    verificarQueCreaOtroItem(
      crearReporte(10L, 20L, "La Serenisima", 1500.0),
      crearReporte(10L, 20L, "Milkaut", 1500.0)
    );
  }

  @Test
  void deberiaCrearOtroItemSiCambiaElPrecio() {
    verificarQueCreaOtroItem(
      crearReporte(10L, 20L, "La Serenisima", 1500.0),
      crearReporte(10L, 20L, "La Serenisima", 1800.0)
    );
  }

  private void verificarQueCreaOtroItem(Reporte existente, Reporte nuevo) {
    Lista lista = new Lista("Mi lista", 1L);
    lista.setItems(new ArrayList<>());

    lista.getItems().add(new ItemLista(lista, existente, 2));

    when(repositorioLista.buscarPorId(1L)).thenReturn(lista);
    when(repositorioReporte.buscarReporte(5L)).thenReturn(nuevo);

    servicioLista.agregarProductoALista(1L, 5L, 1, 1L);

    verify(repositorioLista).agregarItem(any(ItemLista.class));
  }

  private Reporte crearReporte(Long productoId, Long comercioId, String marca, Double precio) {
    Producto producto = new Producto();
    producto.setId(productoId);
    producto.setMarca(marca);

    Comercio comercio = new Comercio();
    comercio.setId(comercioId);

    Reporte reporte = new Reporte();
    reporte.setProducto(producto);
    reporte.setComercio(comercio);
    reporte.setPrecio(precio);

    return reporte;
  }

  private ItemLista itemDeLista(Long itemId, Long duenioId) {
    Lista lista = new Lista("Lista", duenioId);
    ItemLista item = new ItemLista(lista, new Reporte(), 1);
    item.setId(itemId);
    lista.setItems(new java.util.ArrayList<>(java.util.List.of(item)));
    return item;
  }

  @Test
  void deberiaLanzarExcepcionSiElNombreSuperaLosCienCaracteres() {
    IllegalArgumentException error = assertThrows(
      IllegalArgumentException.class,
      () -> servicioLista.crearLista("a".repeat(101), 1L)
    );

    assertThat(error.getMessage(), is("El nombre de la lista no puede superar los 100 caracteres"));
    verify(repositorioLista, never()).guardar(any(Lista.class));
  }

  @Test
  void deberiaAceptarUnNombreDeExactamenteCienCaracteres() {
    Lista lista = servicioLista.crearLista("a".repeat(100), 1L);

    assertThat(lista.getNombre().length(), is(100));
  }

  @Test
  void deberiaGuardarElNombreSinEspaciosEnLosBordes() {
    Lista lista = servicioLista.crearLista("  Super del jueves  ", 1L);

    assertThat(lista.getNombre(), is("Super del jueves"));
  }
}
