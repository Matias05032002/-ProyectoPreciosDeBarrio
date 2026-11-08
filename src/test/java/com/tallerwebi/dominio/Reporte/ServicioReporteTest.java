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
import java.util.ArrayList;
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
    when(servicioProductoMock.buscarPorNombre("Leche")).thenReturn(List.of(producto));
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

    when(servicioProductoMock.buscarPorNombre("Leche")).thenReturn(List.of(producto));
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
  public void guardarReporteConUnidadPorUnidadEsValido() throws Exception {
    Producto producto = productoValido("Ajo", "1u");
    Comercio comercio = comercioConNombre("Dia");
    Reporte reporte = reporteConProductoYComercio(producto, comercio, 2490.0);

    when(servicioComercioMock.buscarComercioPorNombre("Dia")).thenReturn(Arrays.asList(comercio));
    when(servicioProductoMock.buscarPorNombre("Ajo")).thenReturn(List.of(producto));
    when(repositorioReporteMock.guardarReporte(reporte)).thenReturn(reporte);

    servicioReporte.guardarReporte(reporte);

    verify(repositorioReporteMock, times(1)).guardarReporte(reporte);
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

    when(servicioProductoMock.buscarPorNombre("Leche")).thenReturn(List.of(producto));
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

    when(servicioProductoMock.buscarPorNombre("Leche")).thenReturn(List.of(producto));
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
  public void marcarDudosoUnReporteDeUnoMismoDeberiaLanzarReportePropio() {
    Usuario autor = new Usuario();
    autor.setId(7L);
    Reporte reporte = new Reporte();
    reporte.setUsuario(autor);
    when(repositorioReporteMock.buscarReporte(1L)).thenReturn(reporte);

    assertThrows(ReportePropio.class, () -> servicioReporte.marcarDudoso(1L, autor));
    verify(repositorioReporteMock, never()).marcarDudoso(anyLong(), any());
  }

  @Test
  public void marcarDudosoUnReporteAjenoDeberiaMarcarlo() {
    Usuario autor = new Usuario();
    autor.setId(7L);
    Usuario otro = new Usuario();
    otro.setId(8L);
    Reporte reporte = new Reporte();
    reporte.setUsuario(autor);
    when(repositorioReporteMock.buscarReporte(1L)).thenReturn(reporte);
    when(repositorioReporteMock.marcarDudoso(1L, otro)).thenReturn(reporte);

    assertThat(servicioReporte.marcarDudoso(1L, otro), equalTo(reporte));
    verify(repositorioReporteMock, times(1)).marcarDudoso(1L, otro);
  }

  @Test
  public void guardarReporteConPrecioMuyAltoDeberiaLanzarPrecioIncorrecto() {
    Reporte reporte = reporteConProductoYComercio(
      productoValido("Leche", "1000ml"),
      comercioValido(),
      10_000_000.01
    );
    assertThrows(PrecioIncorrecto.class, () -> servicioReporte.guardarReporte(reporte));
  }

  @Test
  public void guardarReporteConElPrecioMaximoEsValido() throws Exception {
    Reporte reporte = reporteConProductoYComercio(
      productoValido("Leche", "1000ml"),
      comercioValido(),
      10_000_000.0
    );
    when(repositorioReporteMock.guardarReporte(reporte)).thenReturn(reporte);

    assertThat(servicioReporte.guardarReporte(reporte), equalTo(reporte));
  }

  @Test
  public void guardarReporteConPrecioNaNDeberiaLanzarPrecioIncorrecto() {
    Reporte reporte = reporteConProductoYComercio(
      productoValido("Leche", "1000ml"),
      comercioValido(),
      Double.NaN
    );
    assertThrows(PrecioIncorrecto.class, () -> servicioReporte.guardarReporte(reporte));
  }

  @Test
  public void guardarReporteConPrecioInfinitoDeberiaLanzarPrecioIncorrecto() {
    Reporte reporte = reporteConProductoYComercio(
      productoValido("Leche", "1000ml"),
      comercioValido(),
      Double.POSITIVE_INFINITY
    );
    assertThrows(PrecioIncorrecto.class, () -> servicioReporte.guardarReporte(reporte));
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

  @Test
  public void productosMasBuscadosPorLocalidadDeberiaDelegar() {
    List<Object[]> esperado = new ArrayList<>();
    esperado.add(new Object[] { "Leche", "Lácteos", 3L });
    when(repositorioReporteMock.productosMasBuscadosPorLocalidad("Matanza", 5))
      .thenReturn(esperado);

    List<Object[]> resultado = servicioReporte.productosMasBuscadosPorLocalidad("Matanza", 5);

    assertThat(resultado, equalTo(esperado));
    verify(repositorioReporteMock, times(1)).productosMasBuscadosPorLocalidad("Matanza", 5);
  }

  @Test
  public void productosMasBuscadosPorLocalidadSinReportesDeberiaRetornarListaVacia() {
    when(repositorioReporteMock.productosMasBuscadosPorLocalidad("Zona Sin Datos", 5))
      .thenReturn(Collections.emptyList());

    List<Object[]> resultado = servicioReporte.productosMasBuscadosPorLocalidad(
      "Zona Sin Datos",
      5
    );

    assertThat(resultado, equalTo(Collections.emptyList()));
    verify(repositorioReporteMock, times(1)).productosMasBuscadosPorLocalidad("Zona Sin Datos", 5);
  }

  // --- editar sin modificar comercio ni producto compartidos ---

  private Reporte reporteDelVecino(Comercio comercio, Producto producto) {
    Usuario usuario = new Usuario();
    usuario.setId(1L);
    Reporte reporte = reporteConProductoYComercio(producto, comercio, 100.0);
    reporte.setUsuario(usuario);
    reporte.setFechaDeReporte(LocalDateTime.now().minusHours(1));
    return reporte;
  }

  @Test
  public void editarElComercioDeUnReporteNoMudaAlComercioOriginal() throws Exception {
    Comercio original = comercioConNombre("Mati");
    original.setDireccion("Carhue 1526");
    original.setLocalidad("Buenos Aires");
    Reporte reporte = reporteDelVecino(original, productoValido("Leche", "1000ml"));
    when(repositorioReporteMock.buscarReporte(1L)).thenReturn(reporte);
    Comercio otro = comercioConNombre("Mati");
    otro.setDireccion("Vito Dumas 3923");
    when(servicioComercioMock.guardarComercio(otro)).thenReturn(otro);
    Reporte datos = reporteConProductoYComercio(productoValido("Leche", "1000ml"), otro, 1212.0);

    servicioReporte.editarReporte(1L, datos, 1L);

    assertThat(original.getDireccion(), equalTo("Carhue 1526"));
    assertThat(original.getLocalidad(), equalTo("Buenos Aires"));
    assertThat(reporte.getComercio(), equalTo(otro));
    verify(servicioComercioMock).guardarComercio(otro);
  }

  @Test
  public void editarConOtroNombreEnLaMismaDireccionNoLeCambiaElNombreAlComercio() throws Exception {
    Comercio original = comercioConNombre("Almacen de Rosa");
    Reporte reporte = reporteDelVecino(original, productoValido("Leche", "1000ml"));
    when(repositorioReporteMock.buscarReporte(1L)).thenReturn(reporte);
    when(servicioComercioMock.buscarPorDireccionYLocalidad("Av. Siempreviva 123", "La Matanza"))
      .thenReturn(original);
    Reporte datos = reporteConProductoYComercio(
      productoValido("Leche", "1000ml"),
      comercioConNombre("Nombre inventado"),
      150.0
    );

    servicioReporte.editarReporte(1L, datos, 1L);

    assertThat(original.getNombre(), equalTo("Almacen de Rosa"));
    assertThat(reporte.getComercio(), equalTo(original));
    verify(servicioComercioMock, never()).guardarComercio(any(Comercio.class));
  }

  @Test
  public void editarElProductoDeUnReporteNoRenombraElProductoOriginal() throws Exception {
    Producto leche = productoValido("Leche", "1000ml");
    Reporte reporte = reporteDelVecino(comercioValido(), leche);
    when(repositorioReporteMock.buscarReporte(1L)).thenReturn(reporte);

    servicioReporte.editarReporte(1L, datosEdicion("Aceite", "900ml", 2500.0), 1L);

    assertThat(leche.getNombre(), equalTo("Leche"));
    assertThat(leche.getUnidad(), equalTo("1000ml"));
    assertThat(reporte.getProducto().getNombre(), equalTo("Aceite"));
  }

  @Test
  public void editarReusaElProductoExistenteConMismoNombreMarcaYUnidad() throws Exception {
    Reporte reporte = reporteDelVecino(comercioValido(), productoValido("Leche", "1000ml"));
    when(repositorioReporteMock.buscarReporte(1L)).thenReturn(reporte);
    Producto manteca = productoValido("Manteca", "200g");
    when(servicioProductoMock.buscarPorNombre("manteca")).thenReturn(List.of(manteca));

    servicioReporte.editarReporte(1L, datosEdicion("manteca", "200G", 250.0), 1L);

    assertThat(reporte.getProducto(), equalTo(manteca));
    verify(servicioProductoMock, never()).guardarProducto(any(Producto.class));
  }

  @Test
  public void guardarReporteEligeElProductoDeLaMismaMarcaYUnidad() throws Exception {
    Producto serenisima = productoValido("Leche", "1000ml");
    Producto otraUnidad = productoValido("Leche", "500ml");
    Producto otraMarca = productoValido("Leche", "1000ml");
    otraMarca.setMarca("Las tres niñas");
    when(servicioProductoMock.buscarPorNombre("Leche"))
      .thenReturn(List.of(otraMarca, otraUnidad, serenisima));
    Reporte reporte = reporteConProductoYComercio(
      productoValido("Leche", "1000ml"),
      comercioValido(),
      100.0
    );

    servicioReporte.guardarReporte(reporte);

    assertThat(reporte.getProducto(), equalTo(serenisima));
    verify(servicioProductoMock, never()).guardarProducto(any(Producto.class));
  }

  @Test
  public void guardarReporteEnUnaDireccionYaCargadaConOtroNombreUsaEseComercio() throws Exception {
    Comercio existente = comercioConNombre("Almacen de Rosa");
    when(servicioComercioMock.buscarPorDireccionYLocalidad("Av. Siempreviva 123", "La Matanza"))
      .thenReturn(existente);
    Reporte reporte = reporteConProductoYComercio(
      productoValido("Leche", "1000ml"),
      comercioConNombre("Lo de Rosa"),
      100.0
    );

    servicioReporte.guardarReporte(reporte);

    assertThat(reporte.getComercio(), equalTo(existente));
    verify(servicioComercioMock, never()).guardarComercio(any(Comercio.class));
  }

  @Test
  public void reportarEnUnComercioExistenteReintentaUbicarloSiNoTieneUbicacion() throws Exception {
    Comercio existente = comercioValido();
    when(servicioComercioMock.buscarComercioPorNombre("Almacen")).thenReturn(List.of(existente));
    Reporte reporte = reporteConProductoYComercio(
      productoValido("Leche", "1000ml"),
      comercioValido(),
      100.0
    );

    servicioReporte.guardarReporte(reporte);

    verify(servicioComercioMock).ubicarSiFalta(existente);
  }

  // --- helpers ---

  // --- eliminarReporte ---

  @Test
  public void eliminarReporteDelAutorLoBorra() {
    when(repositorioReporteMock.buscarReporte(1L))
      .thenReturn(reporteEditable(1L, LocalDateTime.now().minusHours(1)));

    servicioReporte.eliminarReporte(1L, 1L);

    verify(repositorioReporteMock, times(1)).eliminarReporte(1L);
  }

  @Test
  public void eliminarReporteDelAutorFueraDelPlazoDeEdicionTambienLoBorra() {
    when(repositorioReporteMock.buscarReporte(1L))
      .thenReturn(reporteEditable(1L, LocalDateTime.now().minusDays(30)));

    servicioReporte.eliminarReporte(1L, 1L);

    verify(repositorioReporteMock, times(1)).eliminarReporte(1L);
  }

  @Test
  public void eliminarReporteDeOtroUsuarioLanzaAccesoDenegadoYNoBorra() {
    when(repositorioReporteMock.buscarReporte(1L))
      .thenReturn(reporteEditable(1L, LocalDateTime.now().minusHours(1)));

    assertThrows(AccesoDenegado.class, () -> servicioReporte.eliminarReporte(1L, 2L));
    verify(repositorioReporteMock, never()).eliminarReporte(any());
  }

  @Test
  public void eliminarReporteInexistenteLanzaAccesoDenegadoYNoBorra() {
    when(repositorioReporteMock.buscarReporte(99L)).thenReturn(null);

    assertThrows(AccesoDenegado.class, () -> servicioReporte.eliminarReporte(99L, 1L));
    verify(repositorioReporteMock, never()).eliminarReporte(any());
  }

  @Test
  public void eliminarReporteSinUsuarioAsignadoLanzaAccesoDenegado() {
    Reporte huerfano = reporteEditable(1L, LocalDateTime.now());
    huerfano.setUsuario(null);
    when(repositorioReporteMock.buscarReporte(1L)).thenReturn(huerfano);

    assertThrows(AccesoDenegado.class, () -> servicioReporte.eliminarReporte(1L, 1L));
    verify(repositorioReporteMock, never()).eliminarReporte(any());
  }

  @Test
  public void eliminarReporteSinUsuarioEnSesionLanzaAccesoDenegado() {
    when(repositorioReporteMock.buscarReporte(1L))
      .thenReturn(reporteEditable(1L, LocalDateTime.now()));

    assertThrows(AccesoDenegado.class, () -> servicioReporte.eliminarReporte(1L, null));
    verify(repositorioReporteMock, never()).eliminarReporte(any());
  }

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

  @Test
  public void guardarReporteConNombreDeProductoMuyLargoDeberiaLanzarCamposObligatoriosVacios() {
    Reporte reporte = reporteConProductoYComercio(
      productoValido("a".repeat(151), "1000ml"),
      comercioValido(),
      100.0
    );

    CamposObligatoriosVacios error = assertThrows(
      CamposObligatoriosVacios.class,
      () -> servicioReporte.guardarReporte(reporte)
    );

    org.junit.jupiter.api.Assertions.assertEquals(
      "El nombre del producto no puede superar los 150 caracteres",
      error.getMessage()
    );
    verify(repositorioReporteMock, never()).guardarReporte(any(Reporte.class));
  }

  @Test
  public void guardarReporteConDireccionMuyLargaDeberiaLanzarCamposObligatoriosVacios() {
    Comercio comercio = comercioValido();
    comercio.setDireccion("a".repeat(201));
    Reporte reporte = reporteConProductoYComercio(
      productoValido("Leche", "1000ml"),
      comercio,
      100.0
    );

    CamposObligatoriosVacios error = assertThrows(
      CamposObligatoriosVacios.class,
      () -> servicioReporte.guardarReporte(reporte)
    );

    org.junit.jupiter.api.Assertions.assertEquals(
      "La dirección no puede superar los 200 caracteres",
      error.getMessage()
    );
  }

  // --- actividad del inicio y métricas del comercio ---

  private Reporte reporteVisible(
    String producto,
    double precio,
    LocalDateTime fecha,
    Integer puntuacion
  ) {
    Producto unProducto = new Producto();
    unProducto.setNombre(producto);
    Comercio unComercio = new Comercio();
    unComercio.setId(1L);
    Reporte reporte = new Reporte();
    reporte.setProducto(unProducto);
    reporte.setComercio(unComercio);
    reporte.setPrecio(precio);
    reporte.setFechaDeReporte(fecha);
    reporte.setPuntuacion(puntuacion);
    return reporte;
  }

  @Test
  public void ultimosReportesVisiblesDeberiaDevolverLosMasRecientesPrimero() {
    LocalDateTime ahora = LocalDateTime.now();
    Reporte viejo = reporteVisible("Leche", 1150.0, ahora.minusDays(2), 0);
    Reporte nuevo = reporteVisible("Aceite", 2320.0, ahora.minusHours(1), 0);
    when(repositorioReporteMock.listarTodos()).thenReturn(List.of(viejo, nuevo));

    List<Reporte> ultimos = servicioReporte.ultimosReportesVisibles(6);

    assertThat(ultimos, equalTo(List.of(nuevo, viejo)));
  }

  @Test
  public void ultimosReportesVisiblesNoDeberiaIncluirLosDudososNiLosIncompletos() {
    LocalDateTime ahora = LocalDateTime.now();
    Reporte confiable = reporteVisible(
      "Leche",
      1150.0,
      ahora.minusHours(3),
      Reporte.PUNTUACION_DUDOSO
    );
    Reporte dudoso = reporteVisible(
      "Aceite",
      99.0,
      ahora.minusHours(1),
      Reporte.PUNTUACION_DUDOSO + 1
    );
    Reporte sinPrecio = reporteVisible("Arroz", 1.0, ahora.minusHours(2), 0);
    sinPrecio.setPrecio(null);
    Reporte sinFecha = reporteVisible("Fideos", 1.0, null, 0);
    when(repositorioReporteMock.listarTodos())
      .thenReturn(List.of(confiable, dudoso, sinPrecio, sinFecha));

    assertThat(servicioReporte.ultimosReportesVisibles(6), equalTo(List.of(confiable)));
  }

  @Test
  public void ultimosReportesVisiblesDeberiaRespetarElLimite() {
    LocalDateTime ahora = LocalDateTime.now();
    List<Reporte> muchos = new ArrayList<>();
    for (int i = 0; i < 9; i++) {
      muchos.add(reporteVisible("Producto " + i, 100.0 + i, ahora.minusHours(i), 0));
    }
    when(repositorioReporteMock.listarTodos()).thenReturn(muchos);

    assertThat(servicioReporte.ultimosReportesVisibles(6).size(), equalTo(6));
  }

  // --- últimos precios más cercanos al vecino ---

  // El vecino está en el Obelisco
  private static final double LAT_VECINO = -34.6037;
  private static final double LNG_VECINO = -58.3816;

  private Reporte reporteEn(
    String producto,
    long comercioId,
    LocalDateTime fecha,
    Double latitud,
    Double longitud
  ) {
    Reporte reporte = reporteVisible(producto, 100.0, fecha, 0);
    reporte.getComercio().setId(comercioId);
    reporte.getComercio().setLatitud(latitud);
    reporte.getComercio().setLongitud(longitud);
    return reporte;
  }

  // A unos 0,9 km, 4,4 km y 56 km del Obelisco
  private Reporte reporteCerca(LocalDateTime fecha) {
    return reporteEn("Cerca", 1L, fecha, -34.6100, -58.3900);
  }

  private Reporte reporteMedio(LocalDateTime fecha) {
    return reporteEn("Medio", 2L, fecha, -34.5808, -58.4207);
  }

  private Reporte reporteLejos(LocalDateTime fecha) {
    return reporteEn("Lejos", 3L, fecha, -34.9215, -57.9545);
  }

  @Test
  public void ultimosReportesCercanosDeberiaOrdenarlosDelMasCercanoAlMasLejano() {
    LocalDateTime ahora = LocalDateTime.now();
    Reporte lejos = reporteLejos(ahora.minusHours(1));
    Reporte cerca = reporteCerca(ahora.minusHours(5));
    Reporte medio = reporteMedio(ahora.minusHours(3));
    when(repositorioReporteMock.listarTodos()).thenReturn(List.of(lejos, cerca, medio));

    List<ReporteConDistancia> resultado = servicioReporte.ultimosReportesVisiblesMasCercanos(
      LAT_VECINO,
      LNG_VECINO,
      6
    );

    assertThat(
      resultado.stream().map(ReporteConDistancia::reporte).toList(),
      equalTo(List.of(cerca, medio, lejos))
    );
  }

  @Test
  public void ultimosReportesCercanosDeberiaIndicarLaDistanciaEnKilometros() {
    when(repositorioReporteMock.listarTodos())
      .thenReturn(List.of(reporteMedio(LocalDateTime.now().minusHours(1))));

    List<ReporteConDistancia> resultado = servicioReporte.ultimosReportesVisiblesMasCercanos(
      LAT_VECINO,
      LNG_VECINO,
      6
    );

    assertThat(resultado.get(0).distanciaKm(), org.hamcrest.Matchers.closeTo(4.4, 0.1));
  }

  @Test
  public void ultimosReportesCercanosNoDeberiaIncluirDudososIncompletosNiComerciosSinUbicacion() {
    LocalDateTime ahora = LocalDateTime.now();
    Reporte confiable = reporteCerca(ahora.minusHours(1));
    Reporte dudoso = reporteMedio(ahora.minusHours(1));
    dudoso.setPuntuacion(Reporte.PUNTUACION_DUDOSO + 1);
    Reporte sinPrecio = reporteMedio(ahora.minusHours(1));
    sinPrecio.setPrecio(null);
    Reporte sinUbicacion = reporteEn("Sin ubicacion", 4L, ahora.minusHours(1), null, null);
    when(repositorioReporteMock.listarTodos())
      .thenReturn(List.of(confiable, dudoso, sinPrecio, sinUbicacion));

    List<ReporteConDistancia> resultado = servicioReporte.ultimosReportesVisiblesMasCercanos(
      LAT_VECINO,
      LNG_VECINO,
      6
    );

    assertThat(
      resultado.stream().map(ReporteConDistancia::reporte).toList(),
      equalTo(List.of(confiable))
    );
  }

  @Test
  public void ultimosReportesCercanosDeberiaRespetarElLimite() {
    LocalDateTime ahora = LocalDateTime.now();
    List<Reporte> muchos = new ArrayList<>();
    for (int i = 0; i < 9; i++) {
      muchos.add(reporteEn("Producto " + i, i, ahora.minusHours(i), -34.60 - i * 0.01, -58.38));
    }
    when(repositorioReporteMock.listarTodos()).thenReturn(muchos);

    assertThat(
      servicioReporte.ultimosReportesVisiblesMasCercanos(LAT_VECINO, LNG_VECINO, 6).size(),
      equalTo(6)
    );
  }

  @Test
  public void ultimosReportesCercanosSoloDeberiaConsiderarLosCincuentaMasRecientes() {
    LocalDateTime ahora = LocalDateTime.now();
    List<Reporte> reportes = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      reportes.add(reporteLejos(ahora.minusHours(i + 1)));
    }
    Reporte cercaPeroViejo = reporteCerca(ahora.minusDays(40));
    reportes.add(cercaPeroViejo);
    when(repositorioReporteMock.listarTodos()).thenReturn(reportes);

    List<ReporteConDistancia> resultado = servicioReporte.ultimosReportesVisiblesMasCercanos(
      LAT_VECINO,
      LNG_VECINO,
      6
    );

    assertThat(resultado.stream().anyMatch(r -> r.reporte() == cercaPeroViejo), equalTo(false));
  }

  @Test
  public void ultimosReportesCercanosConLaMismaDistanciaDeberiaPonerPrimeroElMasReciente() {
    LocalDateTime ahora = LocalDateTime.now();
    Reporte viejo = reporteCerca(ahora.minusDays(2));
    Reporte nuevo = reporteCerca(ahora.minusHours(1));
    when(repositorioReporteMock.listarTodos()).thenReturn(List.of(viejo, nuevo));

    List<ReporteConDistancia> resultado = servicioReporte.ultimosReportesVisiblesMasCercanos(
      LAT_VECINO,
      LNG_VECINO,
      6
    );

    assertThat(resultado.get(0).reporte(), equalTo(nuevo));
  }

  @Test
  public void resumenDeActividadDeberiaContarSoloLaUltimaSemanaYLosComerciosDistintos() {
    LocalDateTime ahora = LocalDateTime.now();
    Reporte reciente = reporteVisible("Leche", 1150.0, ahora.minusDays(1), 0);
    Reporte viejo = reporteVisible("Aceite", 2320.0, ahora.minusDays(30), 0);
    viejo.getComercio().setId(2L);
    Reporte dudoso = reporteVisible(
      "Arroz",
      1.0,
      ahora.minusHours(1),
      Reporte.PUNTUACION_DUDOSO + 1
    );
    dudoso.getComercio().setId(3L);
    when(repositorioReporteMock.listarTodos()).thenReturn(List.of(reciente, viejo, dudoso));

    ResumenDeActividad resumen = servicioReporte.resumenDeActividad();

    assertThat(resumen.getReportesSemana(), equalTo(1L));
    assertThat(resumen.getComercios(), equalTo(2L));
  }

  @Test
  public void resumenDeActividadSinReportesDeberiaDevolverCeros() {
    when(repositorioReporteMock.listarTodos()).thenReturn(Collections.emptyList());

    ResumenDeActividad resumen = servicioReporte.resumenDeActividad();

    assertThat(resumen.getReportesSemana(), equalTo(0L));
    assertThat(resumen.getComercios(), equalTo(0L));
  }

  @Test
  public void precioPromedioDelComercioDeberiaPromediarLosPreciosReportados() {
    Reporte r1 = reporteVisible("Leche", 100.0, LocalDateTime.now(), 0);
    Reporte r2 = reporteVisible("Aceite", 200.0, LocalDateTime.now(), 0);
    Reporte sinPrecio = reporteVisible("Arroz", 1.0, LocalDateTime.now(), 0);
    sinPrecio.setPrecio(null);
    when(repositorioReporteMock.buscarPorComercio(1L)).thenReturn(List.of(r1, r2, sinPrecio));

    assertThat(servicioReporte.precioPromedioDelComercio(1L), equalTo(150.0));
  }

  @Test
  public void precioPromedioDelComercioSinReportesDeberiaSerCero() {
    when(repositorioReporteMock.buscarPorComercio(1L)).thenReturn(Collections.emptyList());

    assertThat(servicioReporte.precioPromedioDelComercio(1L), equalTo(0.0));
  }

  @Test
  public void productoEstrellaDelComercioDeberiaSerElMasReportado() {
    Reporte leche1 = reporteVisible("Leche", 100.0, LocalDateTime.now(), 0);
    Reporte leche2 = reporteVisible("Leche", 110.0, LocalDateTime.now(), 0);
    Reporte aceite = reporteVisible("Aceite", 200.0, LocalDateTime.now(), 0);
    when(repositorioReporteMock.buscarPorComercio(1L)).thenReturn(List.of(aceite, leche1, leche2));

    assertThat(servicioReporte.productoEstrellaDelComercio(1L), equalTo("Leche"));
  }

  @Test
  public void productoEstrellaDelComercioEnEmpateDeberiaElegirElPrimeroEnOrdenAlfabetico() {
    Reporte leche = reporteVisible("Leche", 100.0, LocalDateTime.now(), 0);
    Reporte aceite = reporteVisible("Aceite", 200.0, LocalDateTime.now(), 0);
    when(repositorioReporteMock.buscarPorComercio(1L)).thenReturn(List.of(leche, aceite));

    assertThat(servicioReporte.productoEstrellaDelComercio(1L), equalTo("Aceite"));
  }

  @Test
  public void productoEstrellaDelComercioSinReportesDeberiaDevolverGuion() {
    when(repositorioReporteMock.buscarPorComercio(1L)).thenReturn(Collections.emptyList());

    assertThat(servicioReporte.productoEstrellaDelComercio(1L), equalTo("-"));
  }

  // --- validarFoto ---

  @Test
  public void validarFotoAceptaJpegPngYWebp() {
    byte[] jpeg = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x00 };
    byte[] png = { (byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A };
    byte[] webp = { 'R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'E', 'B', 'P' };

    org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> servicioReporte.validarFoto(jpeg));
    org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> servicioReporte.validarFoto(png));
    org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> servicioReporte.validarFoto(webp));
  }

  @Test
  public void validarFotoRechazaUnArchivoQueNoEsImagenAunqueDigaSerlo() {
    byte[] texto = "<html>no soy una foto</html>".getBytes();

    assertThrows(FotoInvalida.class, () -> servicioReporte.validarFoto(texto));
  }

  @Test
  public void validarFotoRechazaUnaFotoDeMasDeDiezMegas() {
    byte[] enorme = new byte[10 * 1024 * 1024 + 1];
    enorme[0] = (byte) 0xFF;
    enorme[1] = (byte) 0xD8;
    enorme[2] = (byte) 0xFF;

    FotoInvalida error = assertThrows(
      FotoInvalida.class,
      () -> servicioReporte.validarFoto(enorme)
    );

    assertThat(error.getMessage(), equalTo("La foto no puede superar los 10 MB"));
  }

  @Test
  public void validarFotoRechazaUnaFotoVacia() {
    assertThrows(FotoInvalida.class, () -> servicioReporte.validarFoto(new byte[0]));
  }
}
