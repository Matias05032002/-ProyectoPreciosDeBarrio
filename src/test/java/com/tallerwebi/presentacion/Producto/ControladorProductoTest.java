package com.tallerwebi.presentacion.Producto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import com.tallerwebi.presentacion.ControladorProducto;
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

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/productos"));
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
}
