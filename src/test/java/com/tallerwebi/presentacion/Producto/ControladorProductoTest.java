package com.tallerwebi.presentacion.Producto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import com.tallerwebi.presentacion.ControladorProducto;
import com.tallerwebi.presentacion.DTO.ProductoConPrecio;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorProductoTest {

  private ControladorProducto controladorProducto;
  private ServicioProducto servicioProductoMock;
  private ServicioReporte servicioReporteMock;

  @BeforeEach
  public void init() {
    this.servicioProductoMock = mock(ServicioProducto.class);
    this.servicioReporteMock = mock(ServicioReporte.class);
    this.controladorProducto = new ControladorProducto(servicioProductoMock, servicioReporteMock);
  }

  @Test
  public void listarProductosDeberiaRetornarVistaConListaDeProductos() {
    Producto productos = new Producto();
    List<Producto> listaDeProductos = Arrays.asList(productos);
    when(this.servicioProductoMock.listarTodos()).thenReturn(listaDeProductos);

    ModelAndView modelAndView = controladorProducto.listarProductos();

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("producto/lista-productos"));
    assertThat(modelAndView.getModel().get("productos"), is(Collections.emptyList()));
  }

  @Test
  public void guardarUnNuevoProductoDeberiaRedirigirAProductos() {
    Producto producto = new Producto();
    when(this.servicioProductoMock.guardarProducto(producto)).thenReturn(producto);

    ModelAndView modelAndView = controladorProducto.guardarProducto(producto);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/producto"));
    verify(this.servicioProductoMock, times(1)).guardarProducto(producto);
  }

  @Test
  public void buscarUnProductoPorNombre() {
    Producto producto = new Producto();
    List<Producto> lista = Arrays.asList(producto);
    producto.setNombre("Leche");
    when(this.servicioReporteMock.listarTodos()).thenReturn(Collections.emptyList());
    when(this.servicioProductoMock.buscarPorNombre("Leche")).thenReturn(lista);

    ModelAndView modelAndView = controladorProducto.buscarProducto("Leche");

    assertThat(modelAndView.getViewName(), equalTo("producto/lista-productos"));
    assertThat(modelAndView.getModel().get("productos"), is(notNullValue()));
  }

  @Test
  public void buscarUnProductoQueNoExisteDevuelveListaVacia() {
    when(this.servicioReporteMock.listarTodos()).thenReturn(Collections.emptyList());

    ModelAndView modelAndView = this.controladorProducto.buscarProducto("Producto Inexistente");

    assertThat((List<?>) modelAndView.getModel().get("productos"), is(empty()));
  }

  @Test
  public void productoConPrecioTieneGettersCorrectos() {
    ProductoConPrecio dto = new ProductoConPrecio(
      "Leche",
      "La Serenisima",
      "litro",
      "Lacteos",
      150.0,
      "Almacen Central",
      1L,
      null
    );

    assertThat(dto.getNombre(), equalTo("Leche"));
    assertThat(dto.getMarca(), equalTo("La Serenisima"));
    assertThat(dto.getUnidad(), equalTo("litro"));
    assertThat(dto.getCategoria(), equalTo("Lacteos"));
    assertThat(dto.getPrecioMinimo(), equalTo(150.0));
    assertThat(dto.getComercio(), equalTo("Almacen Central"));
    assertThat(dto.getReporteId(), equalTo(1L));

    dto.setReporteId(2L);
    assertThat(dto.getReporteId(), equalTo(2L));
  }
  @Test
  public void calcularAntiguedadNullDeUnProducto(){
    ProductoConPrecio dto = new ProductoConPrecio(
            "Leche",
            "La Serenisima",
            "litro",
            "Lacteos",
            150.0,
            "Almacen Central",
            1L,
            null
    );



    assertThat(dto.getAntiguedad(), equalTo("Desconocido"));
  }
  @Test
  public void productoConFechaDeHacePocoEsFresco() {
    ProductoConPrecio dto = new ProductoConPrecio(
            "Leche", "La Serenisima", "litro", "Lacteos",
            150.0, "Almacen Central", 1L,
            LocalDateTime.now().minusDays(3)
    );
    assertThat(dto.getAntiguedad(), equalTo("Fresco"));
  }

  @Test
  public void productoConFechaDeHace15DiasEsDesactualizado() {
    ProductoConPrecio dto = new ProductoConPrecio(
            "Leche", "La Serenisima", "litro", "Lacteos",
            150.0, "Almacen Central", 1L,
            LocalDateTime.now().minusDays(15)
    );
    assertThat(dto.getAntiguedad(), equalTo("Desactualizado"));
  }

  @Test
  public void productoConFechaDeHace30DiasEsVencido() {
    ProductoConPrecio dto = new ProductoConPrecio(
            "Leche", "La Serenisima", "litro", "Lacteos",
            150.0, "Almacen Central", 1L,
            LocalDateTime.now().minusDays(50)
    );
    assertThat(dto.getAntiguedad(), equalTo("Vencido"));
  }
}
