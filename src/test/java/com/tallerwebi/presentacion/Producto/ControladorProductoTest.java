package com.tallerwebi.presentacion.Producto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Producto.OrdenProducto;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.presentacion.ControladorProducto;
import com.tallerwebi.presentacion.DTO.ProductoConPrecio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
  private ServicioLogin servicioLoginMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    this.servicioProductoMock = mock(ServicioProducto.class);
    this.servicioLoginMock = mock(ServicioLogin.class);
    this.controladorProducto = new ControladorProducto(servicioProductoMock, servicioLoginMock);
    this.requestMock = mock(HttpServletRequest.class);
    this.sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("EMAIL")).thenReturn("test@test.com");
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
    ProductoConPrecio dto = new ProductoConPrecio(
      "Leche",
      "La Serenisima",
      "litro",
      "Lacteos",
      150.0,
      "Almacen",
      1L,
      null,
      0
    );
    when(
      this.servicioProductoMock.buscarProductosConPrecio(
          "Leche",
          null,
          OrdenProducto.PRECIO,
          null,
          null
        )
    )
      .thenReturn(Arrays.asList(dto));

    ModelAndView modelAndView = controladorProducto.buscarProducto(
      "Leche",
      "precio",
      null,
      null, // lat
      null, // lng
      requestMock
    );

    assertThat(modelAndView.getViewName(), equalTo("producto/lista-productos"));
    assertThat(modelAndView.getModel().get("productos"), is(notNullValue()));
  }

  @Test
  public void buscarUnProductoQueNoExisteDevuelveListaVacia() {
    when(
      this.servicioProductoMock.buscarProductosConPrecio(
          "Producto Inexistente",
          null,
          OrdenProducto.PRECIO,
          null,
          null
        )
    )
      .thenReturn(Collections.emptyList());

    ModelAndView modelAndView =
      this.controladorProducto.buscarProducto(
          "Producto Inexistente",
          "PRECIO",
          null,
          null,
          null,
          requestMock
        );

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
      null,
      1
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
  public void calcularAntiguedadNullDeUnProducto() {
    ProductoConPrecio dto = new ProductoConPrecio(
      "Leche",
      "La Serenisima",
      "litro",
      "Lacteos",
      150.0,
      "Almacen Central",
      1L,
      null,
      6
    );
    assertThat(dto.getAntiguedad(), equalTo("Desconocido"));
  }

  @Test
  public void productoConFechaDeHacePocoEsFresco() {
    ProductoConPrecio dto = new ProductoConPrecio(
      "Leche",
      "La Serenisima",
      "litro",
      "Lacteos",
      150.0,
      "Almacen Central",
      1L,
      LocalDateTime.now().minusDays(3),
      0
    );
    assertThat(dto.getAntiguedad(), equalTo("Fresco"));
  }

  @Test
  public void productoConFechaDeHace15DiasEsDesactualizado() {
    ProductoConPrecio dto = new ProductoConPrecio(
      "Leche",
      "La Serenisima",
      "litro",
      "Lacteos",
      150.0,
      "Almacen Central",
      1L,
      LocalDateTime.now().minusDays(15),
      0
    );
    assertThat(dto.getAntiguedad(), equalTo("Desactualizado"));
  }

  @Test
  public void productoConFechaDeHace30DiasEsVencido() {
    ProductoConPrecio dto = new ProductoConPrecio(
      "Leche",
      "La Serenisima",
      "litro",
      "Lacteos",
      150.0,
      "Almacen Central",
      1L,
      LocalDateTime.now().minusDays(50),
      0
    );
    assertThat(dto.getAntiguedad(), equalTo("Vencido"));
  }

  @Test
  public void productoConNingunaValoracionEsConfiable() {
    ProductoConPrecio dto = new ProductoConPrecio(
      "Leche",
      "La Serenisima",
      "litro",
      "Lacteos",
      150.0,
      "Almacen Central",
      1L,
      LocalDateTime.now().minusDays(50),
      0
    );
    assertThat(dto.isEsDudoso(), equalTo(false));
  }

  @Test
  public void productoConSeisValoracionesEsDudoso() {
    ProductoConPrecio dto = new ProductoConPrecio(
      "Leche",
      "La Serenisima",
      "litro",
      "Lacteos",
      150.0,
      "Almacen Central",
      1L,
      LocalDateTime.now().minusDays(50),
      7
    );
    assertThat(dto.isEsDudoso(), equalTo(true));
  }

  @Test
  public void productoConMasDeDiezReportesEsDudoso() {
    ProductoConPrecio dto = new ProductoConPrecio(
      "Leche",
      "La Serenisima",
      "litro",
      "Lacteos",
      150.0,
      "Almacen Central",
      1L,
      LocalDateTime.now().minusDays(50),
      11
    );
    assertThat(dto.isEsDudoso(), equalTo(true));
  }
}
