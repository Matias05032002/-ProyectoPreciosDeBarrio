package com.tallerwebi.dominio.Producto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.presentacion.DTO.ProductoConPrecio;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
      List.of(OrdenProducto.PRECIO),
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
      List.of(OrdenProducto.PRECIO),
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
      List.of(OrdenProducto.RECIENTE),
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
      List.of(OrdenProducto.PRECIO),
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
      List.of(OrdenProducto.PRECIO),
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
      List.of(OrdenProducto.PRECIO),
      -34.6100,
      -58.3800
    );

    assertThat(resultado.size(), equalTo(1));
    assertThat(resultado.get(0).getDistanciaKm(), notNullValue());
  }

  @Test
  public void elMismoProductoEnDosLocalesMarcaElMasBarato() {
    Reporte caro = crearReporteEnLocal(1L, 10L, 300.0, ahora(), 0);
    Reporte barato = crearReporteEnLocal(1L, 20L, 200.0, ahora(), 0);
    dadoQueLaBusquedaDevuelve("Leche", caro, barato);

    List<ProductoConPrecio> resultado = buscarPorPrecio("Leche");

    assertThat(resultado.get(0).getPrecioMinimo(), equalTo(200.0));
    assertThat(resultado.get(0).isEsMasBarato(), equalTo(true));
    assertThat(resultado.get(1).isEsMasBarato(), equalTo(false));
    assertThat(resultado.get(0).getLocalesComparados(), equalTo(2));
  }

  @Test
  public void elQueNoEsElMasBaratoGuardaLaDiferenciaConElMasBarato() {
    Reporte caro = crearReporteEnLocal(1L, 10L, 300.0, ahora(), 0);
    Reporte barato = crearReporteEnLocal(1L, 20L, 200.0, ahora(), 0);
    dadoQueLaBusquedaDevuelve("Leche", caro, barato);

    List<ProductoConPrecio> resultado = buscarPorPrecio("Leche");

    assertThat(resultado.get(0).getDiferenciaConElMasBarato(), equalTo(null));
    assertThat(resultado.get(1).getDiferenciaConElMasBarato(), equalTo(100.0));
  }

  @Test
  public void elMasBaratoGuardaCuantoSeAhorraContraElMasCaro() {
    Reporte caro = crearReporteEnLocal(1L, 10L, 300.0, ahora(), 0);
    Reporte medio = crearReporteEnLocal(1L, 20L, 250.0, ahora(), 0);
    Reporte barato = crearReporteEnLocal(1L, 30L, 200.0, ahora(), 0);
    dadoQueLaBusquedaDevuelve("Leche", caro, medio, barato);

    List<ProductoConPrecio> resultado = buscarPorPrecio("Leche");

    assertThat(resultado.get(0).getAhorroMaximo(), equalTo(100.0));
    assertThat(resultado.get(1).getAhorroMaximo(), equalTo(null));
    assertThat(resultado.get(2).getAhorroMaximo(), equalTo(null));
  }

  @Test
  public void siTodosLosLocalesCuestanLoMismoNoHayAhorro() {
    Reporte uno = crearReporteEnLocal(1L, 10L, 200.0, ahora(), 0);
    Reporte otro = crearReporteEnLocal(1L, 20L, 200.0, ahora(), 0);
    dadoQueLaBusquedaDevuelve("Leche", uno, otro);

    List<ProductoConPrecio> resultado = buscarPorPrecio("Leche");

    assertThat(resultado.get(0).getAhorroMaximo(), equalTo(null));
  }

  @Test
  public void elAhorroNoCuentaElPrecioDeUnReporteDudoso() {
    Reporte barato = crearReporteEnLocal(1L, 10L, 200.0, ahora(), 0);
    Reporte normal = crearReporteEnLocal(1L, 20L, 230.0, ahora(), 0);
    Reporte dudoso = crearReporteEnLocal(1L, 30L, 900.0, ahora(), Reporte.PUNTUACION_DUDOSO + 1);
    dadoQueLaBusquedaDevuelve("Leche", barato, normal, dudoso);

    List<ProductoConPrecio> resultado = buscarPorPrecio("Leche");

    assertThat(resultado.get(0).getAhorroMaximo(), equalTo(30.0));
  }

  @Test
  public void buscarPorRubroDevuelveSoloLosProductosDeEsaCategoria() {
    Reporte leche = crearReporteEnLocal(1L, 10L, 200.0, ahora(), 0);
    leche.getProducto().setCategoria("Lacteos");
    Reporte arroz = crearReporteEnLocal(2L, 10L, 300.0, ahora(), 0);
    arroz.getProducto().setCategoria("Almacen");
    dadoQueLaBusquedaDevuelve("", leche, arroz);

    List<ProductoConPrecio> resultado = buscarEnRubro("", "Lacteos");

    assertThat(resultado.size(), equalTo(1));
    assertThat(resultado.get(0).getPrecioMinimo(), equalTo(200.0));
  }

  @Test
  public void elRubroSeComparaSinTildesNiMayusculas() {
    Reporte leche = crearReporteEnLocal(1L, 10L, 200.0, ahora(), 0);
    leche.getProducto().setCategoria("Lacteos");
    dadoQueLaBusquedaDevuelve("", leche);

    assertThat(buscarEnRubro("", "Lácteos").size(), equalTo(1));
    assertThat(buscarEnRubro("", "  lacteos ").size(), equalTo(1));
  }

  @Test
  public void unRubroSinProductosDevuelveListaVacia() {
    Reporte leche = crearReporteEnLocal(1L, 10L, 200.0, ahora(), 0);
    leche.getProducto().setCategoria("Lacteos");
    dadoQueLaBusquedaDevuelve("", leche);

    assertThat(buscarEnRubro("", "Mascotas").isEmpty(), equalTo(true));
  }

  @Test
  public void sinRubroDevuelveTodosLosProductos() {
    Reporte leche = crearReporteEnLocal(1L, 10L, 200.0, ahora(), 0);
    leche.getProducto().setCategoria("Lacteos");
    Reporte arroz = crearReporteEnLocal(2L, 10L, 300.0, ahora(), 0);
    arroz.getProducto().setCategoria("Almacen");
    dadoQueLaBusquedaDevuelve("", leche, arroz);

    assertThat(buscarEnRubro("", null).size(), equalTo(2));
    assertThat(buscarEnRubro("", "").size(), equalTo(2));
  }

  @Test
  public void unProductoEnUnSoloLocalNoSeMarcaComoMasBarato() {
    Reporte unico = crearReporteEnLocal(1L, 10L, 300.0, ahora(), 0);
    dadoQueLaBusquedaDevuelve("Leche", unico);

    List<ProductoConPrecio> resultado = buscarPorPrecio("Leche");

    assertThat(resultado.get(0).isEsMasBarato(), equalTo(false));
    assertThat(resultado.get(0).getLocalesComparados(), equalTo(0));
  }

  @Test
  public void siHayEmpateEnElPrecioMasBajoSeMarcanTodosLosEmpatados() {
    Reporte a = crearReporteEnLocal(1L, 10L, 200.0, ahora(), 0);
    Reporte b = crearReporteEnLocal(1L, 20L, 200.0, ahora(), 0);
    Reporte c = crearReporteEnLocal(1L, 30L, 250.0, ahora(), 0);
    dadoQueLaBusquedaDevuelve("Leche", a, b, c);

    List<ProductoConPrecio> resultado = buscarPorPrecio("Leche");

    assertThat(resultado.get(0).isEsMasBarato(), equalTo(true));
    assertThat(resultado.get(1).isEsMasBarato(), equalTo(true));
    assertThat(resultado.get(2).isEsMasBarato(), equalTo(false));
  }

  @Test
  public void unReporteDudosoNuncaSeMarcaComoElMasBarato() {
    Reporte dudoso = crearReporteEnLocal(1L, 10L, 50.0, ahora(), Reporte.PUNTUACION_DUDOSO + 1);
    Reporte confiable = crearReporteEnLocal(1L, 20L, 200.0, ahora(), 0);
    dadoQueLaBusquedaDevuelve("Leche", dudoso, confiable);

    List<ProductoConPrecio> resultado = buscarPorPrecio("Leche");

    ProductoConPrecio delDudoso = resultado.get(0);
    ProductoConPrecio delConfiable = resultado.get(1);
    assertThat(delDudoso.isEsMasBarato(), equalTo(false));
    assertThat(delConfiable.isEsMasBarato(), equalTo(true));
  }

  @Test
  public void siTodosLosReportesSonDudososNingunoSeMarcaComoElMasBarato() {
    Reporte uno = crearReporteEnLocal(1L, 10L, 50.0, ahora(), Reporte.PUNTUACION_DUDOSO + 1);
    Reporte otro = crearReporteEnLocal(1L, 20L, 60.0, ahora(), Reporte.PUNTUACION_DUDOSO + 1);
    dadoQueLaBusquedaDevuelve("Leche", uno, otro);

    List<ProductoConPrecio> resultado = buscarPorPrecio("Leche");

    assertThat(resultado.get(0).isEsMasBarato(), equalTo(false));
    assertThat(resultado.get(1).isEsMasBarato(), equalTo(false));
  }

  @Test
  public void dejaSoloElReporteMasRecienteDeCadaLocal() {
    Reporte viejo = crearReporteEnLocal(1L, 10L, 100.0, ahora().minusDays(30), 0);
    Reporte nuevo = crearReporteEnLocal(1L, 10L, 300.0, ahora(), 0);
    Reporte otroLocal = crearReporteEnLocal(1L, 20L, 250.0, ahora(), 0);
    dadoQueLaBusquedaDevuelve("Leche", viejo, nuevo, otroLocal);

    List<ProductoConPrecio> resultado = buscarPorPrecio("Leche");

    assertThat(resultado.size(), equalTo(2));
    assertThat(resultado.get(0).getPrecioMinimo(), equalTo(250.0));
    assertThat(resultado.get(1).getPrecioMinimo(), equalTo(300.0));
  }

  @Test
  public void elReporteViejoBaratoNoLeGanaAlNuevoDelMismoLocal() {
    Reporte nuevo = crearReporteEnLocal(1L, 10L, 300.0, ahora(), 0);
    Reporte viejo = crearReporteEnLocal(1L, 10L, 100.0, ahora().minusDays(30), 0);
    Reporte otroLocal = crearReporteEnLocal(1L, 20L, 250.0, ahora(), 0);
    dadoQueLaBusquedaDevuelve("Leche", nuevo, viejo, otroLocal);

    List<ProductoConPrecio> resultado = buscarPorPrecio("Leche");

    assertThat(resultado.size(), equalTo(2));
    assertThat(resultado.get(0).isEsMasBarato(), equalTo(true));
    assertThat(resultado.get(0).getPrecioMinimo(), equalTo(250.0));
  }

  @Test
  public void agrupaLosResultadosPorProductoAlOrdenarPorPrecio() {
    Reporte aEnUno = crearReporteEnLocal(1L, 10L, 10.0, ahora(), 0);
    Reporte aEnDos = crearReporteEnLocal(1L, 20L, 50.0, ahora(), 0);
    Reporte bEnUno = crearReporteEnLocal(2L, 10L, 20.0, ahora(), 0);
    Reporte bEnDos = crearReporteEnLocal(2L, 20L, 30.0, ahora(), 0);
    dadoQueLaBusquedaDevuelve("Leche", aEnDos, bEnDos, bEnUno, aEnUno);

    List<ProductoConPrecio> resultado = buscarPorPrecio("Leche");

    assertThat(precios(resultado), equalTo(Arrays.asList(10.0, 50.0, 20.0, 30.0)));
  }

  @Test
  public void alOrdenarPorRecienteNoAgrupaPeroSiMarcaElMasBarato() {
    Reporte masVieja = crearReporteEnLocal(1L, 10L, 100.0, ahora().minusDays(3), 0);
    Reporte masNueva = crearReporteEnLocal(1L, 20L, 200.0, ahora(), 0);
    dadoQueLaBusquedaDevuelve("Leche", masVieja, masNueva);

    List<ProductoConPrecio> resultado = servicioProducto.buscarProductosConPrecio(
      "Leche",
      null,
      List.of(OrdenProducto.RECIENTE),
      null,
      null
    );

    assertThat(resultado.get(0).getPrecioMinimo(), equalTo(200.0));
    assertThat(resultado.get(1).isEsMasBarato(), equalTo(true));
  }

  @Test
  public void losReportesSinIdDeLocalNoSeDescartanPorDuplicados() {
    Reporte uno = crearReporteEnLocal(1L, null, 100.0, ahora(), 0);
    Reporte otro = crearReporteEnLocal(1L, null, 200.0, ahora(), 0);
    dadoQueLaBusquedaDevuelve("Leche", uno, otro);

    List<ProductoConPrecio> resultado = buscarPorPrecio("Leche");

    assertThat(resultado.size(), equalTo(2));
  }

  // --- varios criterios a la vez: se aplican por prioridad (precio, cercanía, reciente) ---

  // El usuario está en el Obelisco; "cerca" queda a unos 0,9 km y "lejos" a unos 56 km
  private static final double LAT_USUARIO = -34.6037;
  private static final double LNG_USUARIO = -58.3816;

  private Reporte crearReporteConUbicacion(
    Long productoId,
    Long comercioId,
    Double precio,
    LocalDateTime fecha,
    Double latitud,
    Double longitud
  ) {
    Reporte reporte = crearReporteEnLocal(productoId, comercioId, precio, fecha, 0);
    when(reporte.getComercio().getLatitud()).thenReturn(latitud);
    when(reporte.getComercio().getLongitud()).thenReturn(longitud);
    return reporte;
  }

  private Reporte enLaCuadra(Long productoId, Long comercioId, Double precio, LocalDateTime fecha) {
    return crearReporteConUbicacion(productoId, comercioId, precio, fecha, -34.6100, -58.3900);
  }

  private Reporte enLaPlata(Long productoId, Long comercioId, Double precio, LocalDateTime fecha) {
    return crearReporteConUbicacion(productoId, comercioId, precio, fecha, -34.9215, -57.9545);
  }

  private List<ProductoConPrecio> buscarOrdenandoPor(List<OrdenProducto> ordenes) {
    return servicioProducto.buscarProductosConPrecio(
      "Leche",
      null,
      ordenes,
      LAT_USUARIO,
      LNG_USUARIO
    );
  }

  @Test
  public void conPrecioYCercaniaElPrecioEmpataYDecideLaCercania() {
    Reporte lejos = enLaPlata(1L, 10L, 100.0, ahora());
    Reporte cerca = enLaCuadra(1L, 20L, 100.0, ahora());
    dadoQueLaBusquedaDevuelve("Leche", lejos, cerca);

    List<ProductoConPrecio> resultado = buscarOrdenandoPor(
      List.of(OrdenProducto.PRECIO, OrdenProducto.CERCANIA)
    );

    assertThat(
      resultado.get(0).getDistanciaKm() < resultado.get(1).getDistanciaKm(),
      equalTo(true)
    );
  }

  @Test
  public void conPrecioYCercaniaElPrecioTienePrioridadSobreLaCercania() {
    Reporte baratoYLejos = enLaPlata(1L, 10L, 100.0, ahora());
    Reporte carioYCerca = enLaCuadra(1L, 20L, 300.0, ahora());
    dadoQueLaBusquedaDevuelve("Leche", carioYCerca, baratoYLejos);

    List<ProductoConPrecio> resultado = buscarOrdenandoPor(
      List.of(OrdenProducto.PRECIO, OrdenProducto.CERCANIA)
    );

    assertThat(precios(resultado), equalTo(Arrays.asList(100.0, 300.0)));
  }

  @Test
  public void conPrecioYCercaniaSigueAgrupandoLosResultadosPorProducto() {
    Reporte aBarato = enLaCuadra(1L, 10L, 10.0, ahora());
    Reporte aCaro = enLaPlata(1L, 20L, 50.0, ahora());
    Reporte bMedio = enLaCuadra(2L, 10L, 20.0, ahora());
    Reporte bCaro = enLaPlata(2L, 20L, 30.0, ahora());
    dadoQueLaBusquedaDevuelve("Leche", aCaro, bCaro, bMedio, aBarato);

    List<ProductoConPrecio> resultado = buscarOrdenandoPor(
      List.of(OrdenProducto.PRECIO, OrdenProducto.CERCANIA)
    );

    assertThat(precios(resultado), equalTo(Arrays.asList(10.0, 50.0, 20.0, 30.0)));
  }

  @Test
  public void conCercaniaYRecienteLaCercaniaTienePrioridadYLaFechaDesempata() {
    Reporte cercaPeroVieja = enLaCuadra(1L, 10L, 100.0, ahora().minusDays(9));
    Reporte lejosPeroNueva = enLaPlata(1L, 20L, 100.0, ahora());
    Reporte cercaYNueva = enLaCuadra(1L, 30L, 100.0, ahora().minusDays(1));
    dadoQueLaBusquedaDevuelve("Leche", lejosPeroNueva, cercaPeroVieja, cercaYNueva);

    List<ProductoConPrecio> resultado = buscarOrdenandoPor(
      List.of(OrdenProducto.CERCANIA, OrdenProducto.RECIENTE)
    );

    assertThat(
      resultado.stream().map(ProductoConPrecio::getFechaDeReporte).toList(),
      equalTo(
        Arrays.asList(
          cercaYNueva.getFechaDeReporte(),
          cercaPeroVieja.getFechaDeReporte(),
          lejosPeroNueva.getFechaDeReporte()
        )
      )
    );
  }

  @Test
  public void conPrecioYRecientePrimeroElPrecioYDespuesLaFecha() {
    Reporte igualVieja = enLaCuadra(1L, 10L, 100.0, ahora().minusDays(4));
    Reporte igualNueva = enLaCuadra(1L, 20L, 100.0, ahora());
    Reporte masBarataPeroVieja = enLaCuadra(1L, 30L, 80.0, ahora().minusDays(20));
    dadoQueLaBusquedaDevuelve("Leche", igualVieja, masBarataPeroVieja, igualNueva);

    List<ProductoConPrecio> resultado = buscarOrdenandoPor(
      List.of(OrdenProducto.PRECIO, OrdenProducto.RECIENTE)
    );

    assertThat(precios(resultado), equalTo(Arrays.asList(80.0, 100.0, 100.0)));
    assertThat(resultado.get(1).getFechaDeReporte(), equalTo(igualNueva.getFechaDeReporte()));
  }

  @Test
  public void laPrioridadNoDependeDelOrdenEnQueSePidenLosCriterios() {
    Reporte lejos = enLaPlata(1L, 10L, 100.0, ahora());
    Reporte cerca = enLaCuadra(1L, 20L, 100.0, ahora());
    Reporte baratoYLejos = enLaPlata(1L, 30L, 90.0, ahora());
    dadoQueLaBusquedaDevuelve("Leche", lejos, cerca, baratoYLejos);

    List<Double> enOrden = precios(
      buscarOrdenandoPor(List.of(OrdenProducto.PRECIO, OrdenProducto.CERCANIA))
    );
    List<Double> alReves = precios(
      buscarOrdenandoPor(List.of(OrdenProducto.CERCANIA, OrdenProducto.PRECIO))
    );

    assertThat(alReves, equalTo(enOrden));
    assertThat(enOrden.get(0), equalTo(90.0));
  }

  @Test
  public void conCercaniaSinUbicacionDelUsuarioLosDemasCriteriosDecidenElOrden() {
    Reporte vieja = enLaCuadra(1L, 10L, 100.0, ahora().minusDays(6));
    Reporte nueva = enLaCuadra(1L, 20L, 100.0, ahora());
    dadoQueLaBusquedaDevuelve("Leche", vieja, nueva);

    List<ProductoConPrecio> resultado = servicioProducto.buscarProductosConPrecio(
      "Leche",
      null,
      List.of(OrdenProducto.CERCANIA, OrdenProducto.RECIENTE),
      null,
      null
    );

    assertThat(resultado.get(0).getFechaDeReporte(), equalTo(nueva.getFechaDeReporte()));
  }

  @Test
  public void sinCriteriosOrdenaPorPrecio() {
    Reporte caro = crearReporteEnLocal(1L, 10L, 300.0, ahora(), 0);
    Reporte barato = crearReporteEnLocal(1L, 20L, 200.0, ahora(), 0);
    dadoQueLaBusquedaDevuelve("Leche", caro, barato);

    assertThat(precios(buscarOrdenandoPor(List.of())), equalTo(Arrays.asList(200.0, 300.0)));
    assertThat(precios(buscarOrdenandoPor(null)), equalTo(Arrays.asList(200.0, 300.0)));
  }

  private LocalDateTime ahora() {
    return LocalDateTime.now();
  }

  private void dadoQueLaBusquedaDevuelve(String nombre, Reporte... reportes) {
    when(this.repositorioReporteMock.buscarPorNombre(nombre)).thenReturn(Arrays.asList(reportes));
  }

  private List<ProductoConPrecio> buscarPorPrecio(String nombre) {
    return servicioProducto.buscarProductosConPrecio(
      nombre,
      null,
      List.of(OrdenProducto.PRECIO),
      null,
      null
    );
  }

  private List<ProductoConPrecio> buscarEnRubro(String nombre, String categoria) {
    return servicioProducto.buscarProductosConPrecio(
      nombre,
      categoria,
      null,
      List.of(OrdenProducto.PRECIO),
      null,
      null
    );
  }

  private List<Double> precios(List<ProductoConPrecio> productos) {
    List<Double> precios = new ArrayList<>();
    for (ProductoConPrecio producto : productos) {
      precios.add(producto.getPrecioMinimo());
    }
    return precios;
  }

  private Reporte crearReporteEnLocal(
    Long productoId,
    Long comercioId,
    Double precio,
    LocalDateTime fecha,
    int puntuacion
  ) {
    Producto producto = new Producto();
    producto.setId(productoId);
    producto.setNombre("Leche " + productoId);
    Comercio comercio = mock(Comercio.class);
    when(comercio.getId()).thenReturn(comercioId);
    when(comercio.getNombre()).thenReturn("Local " + comercioId);
    Reporte reporte = mock(Reporte.class);
    when(reporte.getProducto()).thenReturn(producto);
    when(reporte.getComercio()).thenReturn(comercio);
    when(reporte.getPrecio()).thenReturn(precio);
    when(reporte.getFechaDeReporte()).thenReturn(fecha);
    when(reporte.getPuntuacion()).thenReturn(puntuacion);
    when(reporte.getUsuariosQueMarcaron()).thenReturn(Collections.emptyList());
    return reporte;
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

  // ── precios del producto en cada local (modal "Ver precios") ───────────────

  @Test
  public void preciosDelProductoDevuelveUnPrecioPorLocalDeMenorAMayor() {
    Reporte caro = crearReporteEnLocal(1L, 10L, 300.0, ahora(), 0);
    Reporte barato = crearReporteEnLocal(1L, 20L, 200.0, ahora(), 0);
    Reporte medio = crearReporteEnLocal(1L, 30L, 250.0, ahora(), 0);
    dadoQueElReporteEs(5L, caro);
    when(this.repositorioReporteMock.buscarPorProducto(1L))
      .thenReturn(Arrays.asList(caro, barato, medio));

    List<Reporte> resultado = servicioProducto.buscarPreciosDelProductoEnCadaLocal(5L);

    assertThat(resultado, equalTo(Arrays.asList(barato, medio, caro)));
  }

  @Test
  public void preciosDelProductoUsaSoloElUltimoReporteDeCadaLocal() {
    Reporte viejoBarato = crearReporteEnLocal(1L, 10L, 100.0, ahora().minusDays(30), 0);
    Reporte nuevo = crearReporteEnLocal(1L, 10L, 300.0, ahora(), 0);
    dadoQueElReporteEs(5L, nuevo);
    when(this.repositorioReporteMock.buscarPorProducto(1L))
      .thenReturn(Arrays.asList(viejoBarato, nuevo));

    List<Reporte> resultado = servicioProducto.buscarPreciosDelProductoEnCadaLocal(5L);

    assertThat(resultado, equalTo(Arrays.asList(nuevo)));
  }

  @Test
  public void preciosDelProductoDejaLosDudososAlFinalAunqueSeanMasBaratos() {
    Reporte dudosoBarato = crearReporteEnLocal(
      1L,
      10L,
      10.0,
      ahora(),
      Reporte.PUNTUACION_DUDOSO + 1
    );
    Reporte confiable = crearReporteEnLocal(1L, 20L, 200.0, ahora(), 0);
    dadoQueElReporteEs(5L, confiable);
    when(this.repositorioReporteMock.buscarPorProducto(1L))
      .thenReturn(Arrays.asList(dudosoBarato, confiable));

    List<Reporte> resultado = servicioProducto.buscarPreciosDelProductoEnCadaLocal(5L);

    assertThat(resultado, equalTo(Arrays.asList(confiable, dudosoBarato)));
  }

  @Test
  public void preciosDelProductoDeUnReporteInexistenteDevuelveListaVacia() {
    when(this.repositorioReporteMock.buscarReporte(99L)).thenReturn(null);

    assertThat(
      servicioProducto.buscarPreciosDelProductoEnCadaLocal(99L),
      equalTo(Collections.emptyList())
    );
    verify(this.repositorioReporteMock, never()).buscarPorProducto(any());
  }

  private void dadoQueElReporteEs(Long reporteId, Reporte reporte) {
    when(this.repositorioReporteMock.buscarReporte(reporteId)).thenReturn(reporte);
  }
}
