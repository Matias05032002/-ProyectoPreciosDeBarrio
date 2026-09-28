package com.tallerwebi.dominio.Producto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioProductoTest {

  private ServicioProducto servicioProducto;
  private RepositorioProducto repositorioProductoMock;

  @BeforeEach
  public void init() {
    this.repositorioProductoMock = mock(RepositorioProducto.class);
    this.servicioProducto = new ServicioProductoImpl(this.repositorioProductoMock);
  }

  @Test
  public void guardarProductoNuevo() {
    Producto producto = new Producto();
    producto.setNombre("Aceite");
    when(this.repositorioProductoMock.guardarProducto(producto)).thenReturn(producto);

    Producto resultado = this.servicioProducto.guardarProducto(producto);

    assertThat(resultado, equalTo(producto));
    verify(this.repositorioProductoMock, times(1)).guardarProducto(producto);
  }

  @Test
  public void buscarProductoPorNombreYMeDevuelveTodosLosProductosConEseNombre() {
    Producto producto = new Producto();
    producto.setNombre("Aceite");
    List<Producto> lista = Arrays.asList(producto);
    when(this.repositorioProductoMock.buscarPorNombre("Aceite")).thenReturn(lista);

    List<Producto> resultado = this.servicioProducto.buscarPorNombre("Aceite");

    assertThat(resultado, equalTo(lista));
    verify(this.repositorioProductoMock, times(1)).buscarPorNombre("Aceite");
  }

  @Test
  public void buscarProductoPorNombreSiNoExisteRetornaNull() {
    when(this.repositorioProductoMock.buscarProductoPorNombreExacto("Inexistente"))
      .thenReturn(null);

    Producto resultado = this.servicioProducto.buscarProductoPorNombreExacto("Inexistente");

    assertThat(resultado, equalTo(null));
    verify(this.repositorioProductoMock, times(1)).buscarProductoPorNombreExacto("Inexistente");
  }

  @Test
  public void listarTodosLosProductos() {
    List<Producto> lista = Arrays.asList(new Producto(), new Producto());
    when(this.repositorioProductoMock.listarTodos()).thenReturn(lista);

    List<Producto> resultado = this.servicioProducto.listarTodos();

    assertThat(resultado, equalTo(lista));
    verify(this.repositorioProductoMock, times(1)).listarTodos();
  }

  @Test
  public void buscarProductoPorId() {
    Producto producto = new Producto();
    producto.setNombre("Leche");
    when(this.repositorioProductoMock.buscarProductoPorId(1L)).thenReturn(producto);

    Producto resultado = this.servicioProducto.buscarProductoPorId(1L);

    assertThat(resultado, equalTo(producto));
    verify(this.repositorioProductoMock, times(1)).buscarProductoPorId(1L);
  }

  @Test
  public void buscarProductoPorNombreExacto() {
    Producto producto = new Producto();
    producto.setNombre("Leche");
    when(this.repositorioProductoMock.buscarProductoPorNombreExacto("Leche")).thenReturn(producto);

    Producto resultado = this.servicioProducto.buscarProductoPorNombreExacto("Leche");

    assertThat(resultado, equalTo(producto));
    verify(this.repositorioProductoMock, times(1)).buscarProductoPorNombreExacto("Leche");
  }

  @Test
  public void productoTieneGettersYSettersCorretos() {
    Producto producto = new Producto();
    producto.setId(1L);
    producto.setNombre("Leche");
    producto.setCategoria("Lacteos");
    producto.setUnidad("litro");
    producto.setPrecio(150.0);
    producto.setDescripcion("Leche entera");
    producto.setMarca("La Serenisima");
    producto.setDisponible(true);
    java.time.LocalDateTime fecha = java.time.LocalDateTime.now();
    producto.setFechaDeSubida(fecha);

    assertThat(producto.getId(), equalTo(1L));
    assertThat(producto.getNombre(), equalTo("Leche"));
    assertThat(producto.getCategoria(), equalTo("Lacteos"));
    assertThat(producto.getUnidad(), equalTo("litro"));
    assertThat(producto.getPrecio(), equalTo(150.0));
    assertThat(producto.getDescripcion(), equalTo("Leche entera"));
    assertThat(producto.getMarca(), equalTo("La Serenisima"));
    assertThat(producto.getDisponible(), equalTo(true));
    assertThat(producto.getFechaDeSubida(), equalTo(fecha));
  }
}
