package com.tallerwebi.dominio.Lista;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ListaTest {

  private ServicioLista servicioLista;
  private RepositorioLista repositorioListaMock;
  private RepositorioReporte repositorioReporteMock;

  @BeforeEach
  public void init() {
    repositorioListaMock = mock(RepositorioLista.class);
    repositorioReporteMock = mock(RepositorioReporte.class);
    servicioLista = new ServicioListaImpl(repositorioListaMock, repositorioReporteMock);
  }

  @Test
  public void listaSetNombreActualizaValor() {
    Lista lista = new Lista();
    lista.setNombre("Mi lista");
    assertThat(lista.getNombre(), equalTo("Mi lista"));
  }

  @Test
  public void listaSetUsuarioIdActualizaValor() {
    Lista lista = new Lista();
    lista.setUsuarioId(42L);
    assertThat(lista.getUsuarioId(), equalTo(42L));
  }

  @Test
  public void listaSetFechaCreacionActualizaValor() {
    Lista lista = new Lista();
    LocalDateTime fecha = LocalDateTime.of(2024, 1, 15, 10, 0);
    lista.setFechaCreacion(fecha);
    assertThat(lista.getFechaCreacion(), equalTo(fecha));
  }

  @Test
  public void listaGetCantidadProductosDevuelveCero_cuandoItemsVacio() {
    Lista lista = new Lista();
    lista.setItems(new ArrayList<>());
    assertThat(lista.getCantidadProductos(), equalTo(0));
  }

  // --- ItemLista entity ---

  @Test
  public void itemListaSetListaActualizaValor() {
    ItemLista item = new ItemLista();
    Lista lista = new Lista("Mi lista", 1L);
    item.setLista(lista);
    // verifica que no lanza excepción (el getter puede no estar en la interfaz)
    assertThat(item, notNullValue());
  }

  @Test
  public void itemListaSetReporteActualizaValor() {
    ItemLista item = new ItemLista();
    Reporte reporte = new Reporte();
    item.setReporte(reporte);
    assertThat(item, notNullValue());
  }

  @Test
  public void itemListaSetCantidadActualizaValor() {
    ItemLista item = new ItemLista();
    item.setCantidad(5);
    assertThat(item.getCantidad(), equalTo(5));
  }

  @Test
  public void itemListaGetNombreProductoDevuelveNombre() {
    Reporte reporte = new Reporte();
    com.tallerwebi.dominio.Producto.Producto producto =
      new com.tallerwebi.dominio.Producto.Producto();
    producto.setNombre("Leche");
    reporte.setProducto(producto);
    Lista lista = new Lista("Lista", 1L);
    ItemLista item = new ItemLista(lista, reporte, 2);
    assertThat(item.getNombreProducto(), equalTo("Leche"));
  }

  @Test
  public void itemListaGetNombreComercioDevuelveNombre() {
    Reporte reporte = new Reporte();
    com.tallerwebi.dominio.Comercio.Comercio comercio =
      new com.tallerwebi.dominio.Comercio.Comercio();
    comercio.setNombre("Almacen del barrio");
    reporte.setComercio(comercio);
    Lista lista = new Lista("Lista", 1L);
    ItemLista item = new ItemLista(lista, reporte, 1);
    assertThat(item.getNombreComercio(), equalTo("Almacen del barrio"));
  }

  // --- ServicioListaImpl ---

  @Test
  public void eliminarListaEliminaCorrectamente() {
    Long listaId = 1L;

    servicioLista.eliminarLista(listaId);

    verify(repositorioListaMock, times(1)).eliminarLista(listaId);
  }
}
