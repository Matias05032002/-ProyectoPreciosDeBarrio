package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Lista.*;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
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

    servicioLista.agregarProductoALista(1L, 5L, 2);

    verify(repositorioLista).agregarItem(any(ItemLista.class));
  }

  @Test
  void deberiaLanzarExcepcionSiListaNoExisteAlAgregar() {
    when(repositorioLista.buscarPorId(999L)).thenReturn(null);

    assertThrows(
      IllegalArgumentException.class,
      () -> servicioLista.agregarProductoALista(999L, 1L, 1)
    );
  }

  @Test
  void deberiaEliminarUnItemDeLaLista() {
    servicioLista.eliminarItemDeLista(1L);

    verify(repositorioLista).eliminarItem(1L);
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
}
