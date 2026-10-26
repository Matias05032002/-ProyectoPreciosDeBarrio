package com.tallerwebi.dominio.Reporte;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioReporteTest {

  private ServicioReporte servicioReporte;
  private ServicioComercio servicioComercioMock;
  private ServicioProducto servicioProductoMock;
  private RepositorioReporte repositorioReporteMock;

  @BeforeEach
  public void init() {
    this.repositorioReporteMock = mock(RepositorioReporte.class);
    this.servicioComercioMock = mock(ServicioComercio.class);
    this.servicioProductoMock = mock(ServicioProducto.class);
    this.servicioReporte =
      new ServicioReporteImpl(
        this.servicioComercioMock,
        this.servicioProductoMock,
        this.repositorioReporteMock
      );
  }

  // --- guardarReporte ---

  @Test
  public void guardarReporteNuevo() throws Exception {
    Producto producto = productoValido("Leche", "1000ml");
    Comercio comercio = comercioConNombre("Almacen de matias");
    Reporte reporte = reporteConProductoYComercio(producto, comercio, 100.0);

    when(servicioComercioMock.buscarComercioPorNombre("Almacen de matias"))
      .thenReturn(Arrays.asList(comercio));
    when(servicioProductoMock.buscarProductoPorNombreExacto("Leche")).thenReturn(producto);
    when(repositorioReporteMock.guardarReporte(reporte)).thenReturn(reporte);

    Reporte reporteResultado = servicioReporte.guardarReporte(reporte);

    assertThat(reporteResultado, equalTo(reporte));
    verify(repositorioReporteMock, times(1)).guardarReporte(reporte);
  }

  @Test
  public void guardarReporteDuplicadoDeberiaLanzarExcepcion() throws Exception {
    Producto producto = productoValido("Leche", "1000ml");
    producto.setId(1L);
    Comercio comercio = comercioValido();
    comercio.setId(1L);
    Usuario usuario = new Usuario();
    usuario.setId(1L);
    Reporte reporte = reporteConProductoYComercio(producto, comercio, 100.0);
    reporte.setUsuario(usuario);

    when(servicioProductoMock.buscarProductoPorNombreExacto("Leche")).thenReturn(producto);
    when(servicioComercioMock.buscarComercioPorNombre("Almacen"))
      .thenReturn(Arrays.asList(comercio));
    when(
      repositorioReporteMock.buscarReporteDuplicado(eq(1L), eq(1L), eq(1L), any(LocalDate.class))
    )
      .thenReturn(reporte);

    assertThrows(ReporteExistente.class, () -> servicioReporte.guardarReporte(reporte));
    verify(repositorioReporteMock, times(0)).guardarReporte(reporte);
  }

  @Test
  public void guardarReporteConPrecioNuloDeberiaLanzarPrecioIncorrecto() {
    Reporte reporte = reporteConProductoYComercio(
      productoValido("Leche", "1000ml"),
      comercioValido(),
      null
    );
    assertThrows(PrecioIncorrecto.class, () -> servicioReporte.guardarReporte(reporte));
  }

  @Test
  public void guardarReporteConPrecioCeroDeberiaLanzarPrecioIncorrecto() {
    Reporte reporte = reporteConProductoYComercio(
      productoValido("Leche", "1000ml"),
      comercioValido(),
      0.0
    );
    assertThrows(PrecioIncorrecto.class, () -> servicioReporte.guardarReporte(reporte));
  }

  @Test
  public void guardarReporteConUnidadInvalidaDeberiaLanzarUnidadInvalida() {
    Reporte reporte = reporteConProductoYComercio(
      productoValido("Leche", "litro"),
      comercioValido(),
      100.0
    );
    assertThrows(UnidadInvalida.class, () -> servicioReporte.guardarReporte(reporte));
  }

  @Test
  public void guardarReporteConCampoVacioDeberiaLanzarCamposObligatoriosVacios() {
    Reporte reporte = reporteConProductoYComercio(
      productoValido("", "1000ml"),
      comercioValido(),
      100.0
    );
    assertThrows(CamposObligatoriosVacios.class, () -> servicioReporte.guardarReporte(reporte));
  }

  @Test
  public void guardarReporteConMismoNombreYDistintaDireccionGuardaNuevoComercio() throws Exception {
    Producto producto = productoValido("Leche", "1000ml");
    Comercio comercioExistente = comercioConNombre("Supermercado Dia");
    comercioExistente.setDireccion("Av. Rivadavia 100");
    comercioExistente.setId(1L);
    Comercio comercioNuevo = comercioConNombre("Supermercado Dia");
    comercioNuevo.setDireccion("Av. Rivadavia 500");
    comercioNuevo.setId(2L);
    Reporte reporte = reporteConProductoYComercio(producto, comercioNuevo, 100.0);

    when(servicioProductoMock.buscarProductoPorNombreExacto("Leche")).thenReturn(producto);
    when(servicioComercioMock.buscarComercioPorNombre("Supermercado Dia"))
      .thenReturn(Collections.singletonList(comercioExistente));
    when(servicioComercioMock.guardarComercio(comercioNuevo)).thenReturn(comercioNuevo);
    when(repositorioReporteMock.guardarReporte(reporte)).thenReturn(reporte);

    servicioReporte.guardarReporte(reporte);

    verify(servicioComercioMock).guardarComercio(comercioNuevo);
    assertThat(reporte.getComercio(), equalTo(comercioNuevo));
  }

  @Test
  public void guardarReporteConMismoNombreYDireccionReutilizaComercio() throws Exception {
    Producto producto = productoValido("Leche", "1000ml");
    Comercio comercioExistente = comercioConNombre("Supermercado Dia");
    comercioExistente.setDireccion("Av. Rivadavia 100");
    comercioExistente.setId(1L);
    Comercio comercioIngresado = comercioConNombre("Supermercado Dia");
    comercioIngresado.setDireccion("Av. Rivadavia 100");
    Reporte reporte = reporteConProductoYComercio(producto, comercioIngresado, 100.0);

    when(servicioProductoMock.buscarProductoPorNombreExacto("Leche")).thenReturn(producto);
    when(servicioComercioMock.buscarComercioPorNombre("Supermercado Dia"))
      .thenReturn(Collections.singletonList(comercioExistente));
    when(repositorioReporteMock.guardarReporte(reporte)).thenReturn(reporte);

    servicioReporte.guardarReporte(reporte);

    verify(servicioComercioMock, never()).guardarComercio(any(Comercio.class));
    assertThat(reporte.getComercio(), equalTo(comercioExistente));
  }

  // --- buscar / listar ---

  @Test
  public void buscarReportePorId() {
    Reporte reporte = new Reporte();
    when(repositorioReporteMock.buscarReporte(1L)).thenReturn(reporte);

    assertThat(servicioReporte.buscarReporte(1L), equalTo(reporte));
    verify(repositorioReporteMock, times(1)).buscarReporte(1L);
  }

  @Test
  public void buscarReportePorProducto() {
    Producto producto = new Producto();
    List<Reporte> lista = Arrays.asList(new Reporte());
    when(repositorioReporteMock.buscarPorProducto(producto.getId())).thenReturn(lista);

    assertThat(servicioReporte.buscarPorProducto(producto.getId()), equalTo(lista));
    verify(repositorioReporteMock, times(1)).buscarPorProducto(producto.getId());
  }

  @Test
  public void marcarUnReporteDudoso() {
    Reporte reporte = new Reporte();
    when(repositorioReporteMock.buscarReporte(1L)).thenReturn(reporte); // ← agregar
    when(repositorioReporteMock.marcarDudoso(1L, null)).thenReturn(reporte);

    assertThat(servicioReporte.marcarDudoso(1L, null), equalTo(reporte));
    verify(repositorioReporteMock, times(1)).marcarDudoso(1L, null);
  }

  @Test
  public void listarTodosLosReportes() {
    List<Reporte> lista = Arrays.asList(new Reporte());
    when(repositorioReporteMock.listarTodos()).thenReturn(lista);

    assertThat(servicioReporte.listarTodos(), equalTo(lista));
    verify(repositorioReporteMock, times(1)).listarTodos();
  }

  @Test
  public void buscarReporteDuplicado() {
    Reporte reporte = new Reporte();
    reporte.setFechaDeReporte(LocalDateTime.now());
    LocalDate fecha = reporte.getFechaDeReporte().toLocalDate();
    when(repositorioReporteMock.buscarReporteDuplicado(1L, 1L, 1L, fecha)).thenReturn(reporte);

    assertThat(servicioReporte.buscarReporteDuplicado(1L, 1L, 1L, fecha), equalTo(reporte));
    verify(repositorioReporteMock, times(1)).buscarReporteDuplicado(1L, 1L, 1L, fecha);
  }

  @Test
  public void buscarReportesPorNombre() {
    List<Reporte> reportes = Arrays.asList(new Reporte());
    when(repositorioReporteMock.buscarPorNombre("leche")).thenReturn(reportes);

    assertThat(servicioReporte.buscarPorNombre("leche"), equalTo(reportes));
    verify(repositorioReporteMock, times(1)).buscarPorNombre("leche");
  }

  @Test
  public void buscarPorComercio() {
    List<Reporte> lista = Arrays.asList(new Reporte());
    when(repositorioReporteMock.buscarPorComercio(1L)).thenReturn(lista);

    assertThat(servicioReporte.buscarPorComercio(1L), equalTo(lista));
    verify(repositorioReporteMock, times(1)).buscarPorComercio(1L);
  }

  @Test
  public void buscarReportesPorUsuarioIdDeberiaRetornarLosReportesDelUsuario() {
    List<Reporte> reportes = Arrays.asList(new Reporte());
    when(repositorioReporteMock.buscarPorUsuarioId(5L)).thenReturn(reportes);

    assertThat(servicioReporte.buscarPorUsuarioId(5L), equalTo(reportes));
    verify(repositorioReporteMock, times(1)).buscarPorUsuarioId(5L);
  }

  @Test
  public void buscarReportesPorUsuarioIdSinReportesDeberiaRetornarListaVacia() {
    when(repositorioReporteMock.buscarPorUsuarioId(99L)).thenReturn(Collections.emptyList());

    assertThat(servicioReporte.buscarPorUsuarioId(99L), equalTo(Collections.emptyList()));
    verify(repositorioReporteMock, times(1)).buscarPorUsuarioId(99L);
  }

  @Test
  public void buscarReportesPorComercioIdDeberiaRetornarLosReportesDelComercio() {
    List<Reporte> reportes = Arrays.asList(new Reporte());
    when(repositorioReporteMock.buscarPorComercio(3L)).thenReturn(reportes);

    assertThat(servicioReporte.buscarPorComercio(3L), equalTo(reportes));
    verify(repositorioReporteMock, times(1)).buscarPorComercio(3L);
  }

  @Test
  public void buscarReportesPorComercioIdSinReportesDeberiaRetornarListaVacia() {
    when(repositorioReporteMock.buscarPorComercio(99L)).thenReturn(Collections.emptyList());

    assertThat(servicioReporte.buscarPorComercio(99L), equalTo(Collections.emptyList()));
    verify(repositorioReporteMock, times(1)).buscarPorComercio(99L);
  }

  // --- puedeEditar ---

  @Test
  public void puedeEditarRetornaTrueSiElReporteEsDelUsuarioYEstaEnPlazo() {
    when(repositorioReporteMock.buscarReporte(1L))
      .thenReturn(reporteEditable(1L, LocalDateTime.now().minusHours(1)));
    assertThat(servicioReporte.puedeEditar(1L, 1L), equalTo(true));
  }

  @Test
  public void puedeEditarRetornaFalseSiElReporteNoExiste() {
    when(repositorioReporteMock.buscarReporte(99L)).thenReturn(null);
    assertThat(servicioReporte.puedeEditar(99L, 1L), equalTo(false));
  }

  @Test
  public void puedeEditarRetornaFalseSiElUsuarioNoEsElDuenio() {
    when(repositorioReporteMock.buscarReporte(1L))
      .thenReturn(reporteEditable(1L, LocalDateTime.now().minusHours(1)));
    assertThat(servicioReporte.puedeEditar(1L, 2L), equalTo(false));
  }

  @Test
  public void puedeEditarRetornaFalseSiSuperoPlazoDeEdicion() {
    when(repositorioReporteMock.buscarReporte(1L))
      .thenReturn(reporteEditable(1L, LocalDateTime.now().minusDays(3)));
    assertThat(servicioReporte.puedeEditar(1L, 1L), equalTo(false));
  }

  // --- editarReporte ---

  @Test
  public void editarReporteActualizaLosCamposCorrectamente() throws Exception {
    when(repositorioReporteMock.buscarReporte(1L))
      .thenReturn(reporteEditable(1L, LocalDateTime.now().minusHours(1)));

    servicioReporte.editarReporte(1L, datosEdicion("Manteca", "200g", 250.0), 1L);

    Reporte guardado = repositorioReporteMock.buscarReporte(1L);
    assertThat(guardado.getProducto().getNombre(), equalTo("Manteca"));
    assertThat(guardado.getProducto().getUnidad(), equalTo("200g"));
    assertThat(guardado.getPrecio(), equalTo(250.0));
    verify(repositorioReporteMock, times(1)).guardarReporte(any(Reporte.class));
  }

  @Test
  public void editarReporteLanzaFueraDePlazoSiSuperoPlazo() {
    when(repositorioReporteMock.buscarReporte(1L))
      .thenReturn(reporteEditable(1L, LocalDateTime.now().minusDays(3)));

    assertThrows(
      FueraDePlazoDeEdicion.class,
      () -> servicioReporte.editarReporte(1L, datosEdicion("Leche", "1000ml", 100.0), 1L)
    );
    verify(repositorioReporteMock, never()).guardarReporte(any(Reporte.class));
  }

  @Test
  public void editarReporteLanzaFueraDePlazoSiElUsuarioNoEsElDuenio() {
    when(repositorioReporteMock.buscarReporte(1L))
      .thenReturn(reporteEditable(1L, LocalDateTime.now().minusHours(1)));

    assertThrows(
      FueraDePlazoDeEdicion.class,
      () -> servicioReporte.editarReporte(1L, datosEdicion("Leche", "1000ml", 100.0), 2L)
    );
    verify(repositorioReporteMock, never()).guardarReporte(any(Reporte.class));
  }

  @Test
  public void editarReporteLanzaFueraDePlazoSiElReporteNoExiste() {
    when(repositorioReporteMock.buscarReporte(99L)).thenReturn(null);

    assertThrows(
      FueraDePlazoDeEdicion.class,
      () -> servicioReporte.editarReporte(99L, datosEdicion("Leche", "1000ml", 100.0), 1L)
    );
    verify(repositorioReporteMock, never()).guardarReporte(any(Reporte.class));
  }

  // --- helpers ---

  private Producto productoValido(String nombre, String unidad) {
    Producto p = new Producto();
    p.setNombre(nombre);
    p.setMarca("La Serenisima");
    p.setCategoria("Lacteos");
    p.setUnidad(unidad);
    return p;
  }

  private Comercio comercioValido() {
    return comercioConNombre("Almacen");
  }

  private Comercio comercioConNombre(String nombre) {
    Comercio c = new Comercio();
    c.setNombre(nombre);
    c.setDireccion("Av. Siempreviva 123");
    c.setLocalidad("La Matanza");
    return c;
  }

  private Reporte reporteConProductoYComercio(Producto producto, Comercio comercio, Double precio) {
    Reporte r = new Reporte();
    r.setProducto(producto);
    r.setComercio(comercio);
    r.setPrecio(precio);
    return r;
  }

  private Reporte reporteEditable(Long usuarioId, LocalDateTime fecha) {
    Usuario usuario = new Usuario();
    usuario.setId(usuarioId);
    Reporte r = reporteConProductoYComercio(
      productoValido("Leche", "1000ml"),
      comercioValido(),
      100.0
    );
    r.setUsuario(usuario);
    r.setFechaDeReporte(fecha);
    return r;
  }

  private Reporte datosEdicion(String nombre, String unidad, double precio) {
    return reporteConProductoYComercio(productoValido(nombre, unidad), comercioValido(), precio);
  }
}
