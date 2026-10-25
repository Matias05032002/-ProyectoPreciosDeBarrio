package com.tallerwebi.dominio.Producto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.presentacion.DTO.ProductoConPrecio;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;

public class ServicioProductoTest {

  private ServicioProducto servicioProducto;
  private RepositorioProducto repositorioProductoMock;
  private RepositorioReporte repositorioReporteMock;

  @BeforeEach
  public void init() {
    this.repositorioProductoMock = mock(RepositorioProducto.class);
    this.repositorioReporteMock = mock(RepositorioReporte.class);
    this.servicioProducto =
      new ServicioProductoImpl(this.repositorioProductoMock, this.repositorioReporteMock);
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

  @Test
  public void buscarProductosConPrecioRetornaListaVaciaSiNoHayReportes() {
    when(this.repositorioProductoMock.buscarPorNombre("Leche"))
      .thenReturn(Arrays.asList(new Producto()));
    when(this.repositorioReporteMock.listarTodos()).thenReturn(Collections.emptyList());

    List<ProductoConPrecio> resultado = servicioProducto.buscarProductosConPrecio(
      "algo",
      null,
      OrdenProducto.PRECIO,
      null,
      null
    );

    assertThat(resultado, equalTo(Collections.emptyList()));
  }

  @Test
  public void buscarProductosConPrecioOrdenaPorPrecioAscendente() {
    com.tallerwebi.dominio.Reporte.Reporte reporte1 = crearReporteMock("Leche", 200.0, null);
    com.tallerwebi.dominio.Reporte.Reporte reporte2 = crearReporteMock("Leche", 100.0, null);
    when(this.repositorioReporteMock.buscarPorNombre("Leche"))
      .thenReturn(Arrays.asList(reporte1, reporte2));

    List<ProductoConPrecio> resultado = servicioProducto.buscarProductosConPrecio(
      "Leche",
      null,
      OrdenProducto.PRECIO,
      null,
      null
    );

    assertThat(resultado.get(0).getPrecioMinimo(), equalTo(100.0));
    assertThat(resultado.get(1).getPrecioMinimo(), equalTo(200.0));
  }

  @Test
  public void buscarProductosConPrecioOrdenaPorReciente() {
    java.time.LocalDateTime antigua = java.time.LocalDateTime.now().minusDays(5);
    java.time.LocalDateTime reciente = java.time.LocalDateTime.now();
    com.tallerwebi.dominio.Reporte.Reporte r1 = crearReporteMock("Aceite", 50.0, antigua);
    com.tallerwebi.dominio.Reporte.Reporte r2 = crearReporteMock("Aceite", 60.0, reciente);
    when(this.repositorioReporteMock.buscarPorNombre("Aceite")).thenReturn(Arrays.asList(r1, r2));

    List<ProductoConPrecio> resultado = servicioProducto.buscarProductosConPrecio(
      "Aceite",
      null,
      OrdenProducto.RECIENTE,
      null,
      null
    );

    assertThat(resultado.get(0).getFechaDeReporte(), equalTo(reciente));
  }

  @Test
  public void buscarProductosConPrecioConReporteSinProductoLoIgnora() {
    com.tallerwebi.dominio.Reporte.Reporte reporteSinProducto = mock(
      com.tallerwebi.dominio.Reporte.Reporte.class
    );
    when(reporteSinProducto.getProducto()).thenReturn(null);
    when(this.repositorioReporteMock.buscarPorNombre("X"))
      .thenReturn(Arrays.asList(reporteSinProducto));

    List<ProductoConPrecio> resultado = servicioProducto.buscarProductosConPrecio(
      "X",
      null,
      OrdenProducto.PRECIO,
      null,
      null
    );

    assertThat(resultado.size(), equalTo(0));
  }

  @Test
  public void buscarProductosConPrecioConReportesNullRetornaVacio() {
    when(this.repositorioReporteMock.buscarPorNombre("Y")).thenReturn(null);

    List<ProductoConPrecio> resultado = servicioProducto.buscarProductosConPrecio(
      "Y",
      null,
      OrdenProducto.PRECIO,
      null,
      null
    );

    assertThat(resultado.size(), equalTo(0));
  }

  @Test
  public void buscarProductosConPrecioCalculaDistanciaConCoordenadas() {
    Producto producto = new Producto();
    producto.setNombre("Pan");
    com.tallerwebi.dominio.Comercio.Comercio comercio = mock(
      com.tallerwebi.dominio.Comercio.Comercio.class
    );
    when(comercio.getLatitud()).thenReturn(-34.6037);
    when(comercio.getLongitud()).thenReturn(-58.3816);
    when(comercio.getNombre()).thenReturn("Almacen");
    com.tallerwebi.dominio.Reporte.Reporte reporte = mock(
      com.tallerwebi.dominio.Reporte.Reporte.class
    );
    when(reporte.getProducto()).thenReturn(producto);
    when(reporte.getComercio()).thenReturn(comercio);
    when(reporte.getPrecio()).thenReturn(50.0);
    when(reporte.getPuntuacion()).thenReturn(0);
    when(reporte.getFechaDeReporte()).thenReturn(null); // <-- agregar esto
    when(reporte.getUsuariosQueMarcaron()).thenReturn(Collections.emptyList());
    when(this.repositorioReporteMock.buscarPorNombre("Pan")).thenReturn(Arrays.asList(reporte));

    List<ProductoConPrecio> resultado = servicioProducto.buscarProductosConPrecio(
      "Pan",
      null,
      OrdenProducto.PRECIO,
      -34.6100,
      -58.3800
    );

    assertThat(resultado.size(), equalTo(1));
    assertThat(resultado.get(0).getDistanciaKm(), notNullValue());
  }

  private com.tallerwebi.dominio.Reporte.Reporte crearReporteMock(
    String nombreProducto,
    Double precio,
    java.time.LocalDateTime fecha
  ) {
    Producto producto = new Producto();
    producto.setNombre(nombreProducto);
    com.tallerwebi.dominio.Reporte.Reporte reporte = mock(
      com.tallerwebi.dominio.Reporte.Reporte.class
    );
    when(reporte.getProducto()).thenReturn(producto);
    when(reporte.getPrecio()).thenReturn(precio);
    when(reporte.getFechaDeReporte()).thenReturn(fecha);
    when(reporte.getComercio()).thenReturn(null);
    when(reporte.getPuntuacion()).thenReturn(0);
    when(reporte.getUsuariosQueMarcaron()).thenReturn(Collections.emptyList());
    return reporte;
  }
}
