package com.tallerwebi.dominio.Comercio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ServicioResumenComercioTest {

  private static final Long USUARIO_ID = 3L;
  private static final Long COMERCIO_ID = 8L;

  private ServicioComercio servicioComercioMock;
  private ServicioReporte servicioReporteMock;
  private ServicioPromocion servicioPromocionMock;
  private ServicioResumenComercio servicio;

  @BeforeEach
  public void init() {
    servicioComercioMock = mock(ServicioComercio.class);
    servicioReporteMock = mock(ServicioReporte.class);
    servicioPromocionMock = mock(ServicioPromocion.class);
    servicio =
      new ServicioResumenComercioImpl(
        servicioComercioMock,
        servicioReporteMock,
        servicioPromocionMock
      );
  }

  @Test
  public void obtenerResumenSinComercioDevuelveNulo() {
    when(servicioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(null);

    assertThat(servicio.obtenerResumen(USUARIO_ID), nullValue());
  }

  @Test
  public void obtenerResumenDeUnPerfilVacioTieneCeroPorCientoYSoloUnaPromoSiEstaVigente() {
    when(servicioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(comercioBasico());

    ResumenComercio resumen = servicio.obtenerResumen(USUARIO_ID);

    assertThat(resumen.nombre(), equalTo("Almacen Don Jose"));
    assertThat(resumen.porcentajePerfil(), equalTo(0));
    assertThat(resumen.pasos(), hasSize(5));
    assertThat(resumen.pasos().stream().noneMatch(ResumenComercio.PasoPerfil::completo), is(true));
    assertThat(resumen.preciosReportados(), equalTo(0));
    assertThat(resumen.ultimosPrecios(), empty());
  }

  @Test
  public void obtenerResumenDeUnPerfilCompletoTieneCienPorCiento() {
    Comercio comercio = comercioBasico();
    comercio.setFoto(new byte[] { 1, 2, 3 });
    comercio.setDescripcion("El almacén de siempre");
    comercio.setLatitud(-34.6);
    comercio.setLongitud(-58.4);
    comercio.setVerificado(true);
    when(servicioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(comercio);
    when(servicioComercioMock.obtenerHorarios(COMERCIO_ID))
      .thenReturn(Arrays.asList(new HorarioComercio()));

    ResumenComercio resumen = servicio.obtenerResumen(USUARIO_ID);

    assertThat(resumen.porcentajePerfil(), equalTo(100));
    assertThat(resumen.verificado(), is(true));
  }

  @Test
  public void obtenerResumenCalculaElPorcentajeConLosPasosCompletos() {
    Comercio comercio = comercioBasico();
    comercio.setDescripcion("Hola");
    comercio.setLatitud(-34.6);
    comercio.setLongitud(-58.4);
    when(servicioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(comercio);

    ResumenComercio resumen = servicio.obtenerResumen(USUARIO_ID);

    assertThat(resumen.porcentajePerfil(), equalTo(40));
    assertThat(
      resumen
        .pasos()
        .stream()
        .filter(ResumenComercio.PasoPerfil::completo)
        .map(p -> p.clave())
        .toList(),
      containsInAnyOrder("descripcion", "ubicacion")
    );
  }

  @Test
  public void obtenerResumenCuentaSoloLasPromocionesVigentes() {
    when(servicioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(comercioBasico());
    Promocion vigente = promocion(true, -1, 1);
    Promocion desactivada = promocion(false, -1, 1);
    Promocion vencida = promocion(true, -5, -1);
    Promocion futura = promocion(true, 1, 5);
    when(servicioPromocionMock.listarPorComercio(COMERCIO_ID))
      .thenReturn(Arrays.asList(vigente, desactivada, vencida, futura));

    assertThat(servicio.obtenerResumen(USUARIO_ID).promocionesActivas(), equalTo(1));
  }

  @Test
  public void obtenerResumenCuentaReportesYVecinosDistintos() {
    when(servicioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(comercioBasico());
    when(servicioReporteMock.buscarPorComercio(COMERCIO_ID))
      .thenReturn(Arrays.asList(reporte("Leche", 100.0, 1), reporte("Pan", 50.0, 2)));
    when(servicioReporteMock.contarUsuariosDistintosPorComercio(COMERCIO_ID))
      .thenReturn(Arrays.asList(new Object[] { "ana", 2L }, new Object[] { "luis", 1L }));

    ResumenComercio resumen = servicio.obtenerResumen(USUARIO_ID);

    assertThat(resumen.preciosReportados(), equalTo(2));
    assertThat(resumen.vecinos(), equalTo(2));
  }

  @Test
  public void obtenerResumenDevuelveLosCincoPreciosMasRecientesPrimero() {
    when(servicioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(comercioBasico());
    List<Reporte> reportes = new ArrayList<>();
    for (int dias = 7; dias >= 1; dias--) {
      reportes.add(reporte("Producto " + dias, 10.0 * dias, dias));
    }
    when(servicioReporteMock.buscarPorComercio(COMERCIO_ID)).thenReturn(reportes);

    List<ResumenComercio.UltimoPrecio> ultimos = servicio
      .obtenerResumen(USUARIO_ID)
      .ultimosPrecios();

    assertThat(ultimos, hasSize(5));
    assertThat(ultimos.get(0).producto(), equalTo("Producto 1"));
    assertThat(ultimos.get(4).producto(), equalTo("Producto 5"));
  }

  @Test
  public void obtenerResumenIgnoraReportesSinProducto() {
    when(servicioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(comercioBasico());
    Reporte sinProducto = new Reporte();
    sinProducto.setPrecio(10.0);
    when(servicioReporteMock.buscarPorComercio(COMERCIO_ID))
      .thenReturn(Collections.singletonList(sinProducto));

    assertThat(servicio.obtenerResumen(USUARIO_ID).ultimosPrecios(), empty());
  }

  private Comercio comercioBasico() {
    Comercio comercio = new Comercio();
    comercio.setId(COMERCIO_ID);
    comercio.setNombre("Almacen Don Jose");
    return comercio;
  }

  private Promocion promocion(boolean activa, int diasInicio, int diasFin) {
    Promocion promocion = new Promocion();
    promocion.setActiva(activa);
    promocion.setFechaInicio(LocalDateTime.now().plusDays(diasInicio));
    promocion.setFechaFin(LocalDateTime.now().plusDays(diasFin));
    return promocion;
  }

  private Reporte reporte(String producto, double precio, int haceDias) {
    Producto p = new Producto();
    p.setNombre(producto);
    Reporte reporte = new Reporte();
    reporte.setProducto(p);
    reporte.setPrecio(precio);
    reporte.setFechaDeReporte(LocalDateTime.now().minusDays(haceDias));
    return reporte;
  }

  // Casos de borde del resumen del comercio: pasos del perfil, promociones vigentes y fechas de los precios

  private Comercio comercio;

  private void prepararCasosDeBorde() {
    comercio = new Comercio();
    comercio.setId(COMERCIO_ID);
    comercio.setNombre("Almacén Don José");
    when(servicioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(comercio);
  }

  // ---------- pasos del perfil ----------

  @Test
  public void unaFotoDeUnSoloByteCuentaComoFotoCargada() {
    prepararCasosDeBorde();
    comercio.setFoto(new byte[] { 1 });

    assertThat(paso("foto"), equalTo(true));
  }

  @Test
  public void unaFotoVaciaNoCuentaComoFotoCargada() {
    prepararCasosDeBorde();
    comercio.setFoto(new byte[0]);

    assertThat(paso("foto"), equalTo(false));
  }

  @Test
  public void conSoloLaLatitudLaUbicacionNoEstaCompleta() {
    prepararCasosDeBorde();
    comercio.setLatitud(-34.6);
    comercio.setLongitud(null);

    assertThat(paso("ubicacion"), equalTo(false));
  }

  @Test
  public void conSoloLaLongitudLaUbicacionNoEstaCompleta() {
    prepararCasosDeBorde();
    comercio.setLatitud(null);
    comercio.setLongitud(-58.4);

    assertThat(paso("ubicacion"), equalTo(false));
  }

  // ---------- fecha de los últimos precios ----------

  @Test
  public void unPrecioConFechaMuestraLaFechaEnTexto() {
    prepararCasosDeBorde();
    LocalDateTime fecha = LocalDateTime.of(2026, 11, 10, 15, 30);
    when(servicioReporteMock.buscarPorComercio(COMERCIO_ID)).thenReturn(List.of(reporte(fecha)));

    ResumenComercio resumen = servicio.obtenerResumen(USUARIO_ID);

    assertThat(resumen.ultimosPrecios().get(0).fecha(), equalTo(fecha.toString()));
  }

  @Test
  public void unPrecioSinFechaNoMuestraFecha() {
    prepararCasosDeBorde();
    when(servicioReporteMock.buscarPorComercio(COMERCIO_ID)).thenReturn(List.of(reporte(null)));

    ResumenComercio resumen = servicio.obtenerResumen(USUARIO_ID);

    assertThat(resumen.ultimosPrecios().get(0).fecha(), nullValue());
  }

  // ---------- promociones vigentes ----------

  @Test
  public void unaPromocionYaIniciadaYAunNoVencidaEstaVigente() {
    prepararCasosDeBorde();
    prometer(promocion(true, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)));

    assertThat(servicio.obtenerResumen(USUARIO_ID).promocionesActivas(), equalTo(1));
  }

  @Test
  public void unaPromocionQueTodaviaNoEmpezoNoEstaVigente() {
    prepararCasosDeBorde();
    prometer(promocion(true, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

    assertThat(servicio.obtenerResumen(USUARIO_ID).promocionesActivas(), equalTo(0));
  }

  @Test
  public void unaPromocionYaVencidaNoEstaVigente() {
    prepararCasosDeBorde();
    prometer(promocion(true, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1)));

    assertThat(servicio.obtenerResumen(USUARIO_ID).promocionesActivas(), equalTo(0));
  }

  @Test
  public void unaPromocionSinFechasPeroActivaEstaVigente() {
    prepararCasosDeBorde();
    prometer(promocion(true, null, null));

    assertThat(servicio.obtenerResumen(USUARIO_ID).promocionesActivas(), equalTo(1));
  }

  @Test
  public void unaPromocionDesactivadaNoEstaVigenteAunqueLasFechasCoincidan() {
    prepararCasosDeBorde();
    prometer(promocion(false, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)));

    assertThat(servicio.obtenerResumen(USUARIO_ID).promocionesActivas(), equalTo(0));
  }

  // ---------- armado de datos ----------

  private boolean paso(String clave) {
    return servicio
      .obtenerResumen(USUARIO_ID)
      .pasos()
      .stream()
      .filter(paso -> paso.clave().equals(clave))
      .findFirst()
      .orElseThrow()
      .completo();
  }

  private void prometer(Promocion promocion) {
    when(servicioPromocionMock.listarPorComercio(COMERCIO_ID)).thenReturn(List.of(promocion));
  }

  private Promocion promocion(boolean activa, LocalDateTime inicio, LocalDateTime fin) {
    Promocion promocion = new Promocion();
    promocion.setActiva(activa);
    promocion.setFechaInicio(inicio);
    promocion.setFechaFin(fin);
    return promocion;
  }

  private Reporte reporte(LocalDateTime fecha) {
    Producto producto = new Producto();
    producto.setNombre("Leche");
    Reporte reporte = new Reporte();
    reporte.setProducto(producto);
    reporte.setPrecio(1150.0);
    reporte.setFechaDeReporte(fecha);
    return reporte;
  }

  @Nested
  class CasosDeBordeDelResumen {

    private static final Long USUARIO_ID = 7L;
    private static final Long COMERCIO_ID = 3L;

    private ServicioComercio servicioComercio;
    private ServicioReporte servicioReporte;
    private ServicioPromocion servicioPromocion;
    private ServicioResumenComercio servicio;
    private Comercio comercio;

    @BeforeEach
    public void init() {
      servicioComercio = mock(ServicioComercio.class);
      servicioReporte = mock(ServicioReporte.class);
      servicioPromocion = mock(ServicioPromocion.class);
      servicio =
        new ServicioResumenComercioImpl(servicioComercio, servicioReporte, servicioPromocion);
      comercio = new Comercio();
      comercio.setId(COMERCIO_ID);
      comercio.setNombre("Almacén Don José");
      when(servicioComercio.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(comercio);
    }

    // ---------- pasos del perfil ----------

    @Test
    public void unaFotoDeUnSoloByteCuentaComoFotoCargada() {
      comercio.setFoto(new byte[] { 1 });

      assertThat(paso("foto"), equalTo(true));
    }

    @Test
    public void unaFotoVaciaNoCuentaComoFotoCargada() {
      comercio.setFoto(new byte[0]);

      assertThat(paso("foto"), equalTo(false));
    }

    @Test
    public void conSoloLaLatitudLaUbicacionNoEstaCompleta() {
      comercio.setLatitud(-34.6);
      comercio.setLongitud(null);

      assertThat(paso("ubicacion"), equalTo(false));
    }

    @Test
    public void conSoloLaLongitudLaUbicacionNoEstaCompleta() {
      comercio.setLatitud(null);
      comercio.setLongitud(-58.4);

      assertThat(paso("ubicacion"), equalTo(false));
    }

    // ---------- fecha de los últimos precios ----------

    @Test
    public void unPrecioConFechaMuestraLaFechaEnTexto() {
      LocalDateTime fecha = LocalDateTime.of(2026, 11, 10, 15, 30);
      when(servicioReporte.buscarPorComercio(COMERCIO_ID)).thenReturn(List.of(reporte(fecha)));

      ResumenComercio resumen = servicio.obtenerResumen(USUARIO_ID);

      assertThat(resumen.ultimosPrecios().get(0).fecha(), equalTo(fecha.toString()));
    }

    @Test
    public void unPrecioSinFechaNoMuestraFecha() {
      when(servicioReporte.buscarPorComercio(COMERCIO_ID)).thenReturn(List.of(reporte(null)));

      ResumenComercio resumen = servicio.obtenerResumen(USUARIO_ID);

      assertThat(resumen.ultimosPrecios().get(0).fecha(), nullValue());
    }

    // ---------- promociones vigentes ----------

    @Test
    public void unaPromocionYaIniciadaYAunNoVencidaEstaVigente() {
      prometer(promocion(true, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)));

      assertThat(servicio.obtenerResumen(USUARIO_ID).promocionesActivas(), equalTo(1));
    }

    @Test
    public void unaPromocionQueTodaviaNoEmpezoNoEstaVigente() {
      prometer(promocion(true, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

      assertThat(servicio.obtenerResumen(USUARIO_ID).promocionesActivas(), equalTo(0));
    }

    @Test
    public void unaPromocionYaVencidaNoEstaVigente() {
      prometer(promocion(true, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1)));

      assertThat(servicio.obtenerResumen(USUARIO_ID).promocionesActivas(), equalTo(0));
    }

    @Test
    public void unaPromocionSinFechasPeroActivaEstaVigente() {
      prometer(promocion(true, null, null));

      assertThat(servicio.obtenerResumen(USUARIO_ID).promocionesActivas(), equalTo(1));
    }

    @Test
    public void unaPromocionDesactivadaNoEstaVigenteAunqueLasFechasCoincidan() {
      prometer(promocion(false, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)));

      assertThat(servicio.obtenerResumen(USUARIO_ID).promocionesActivas(), equalTo(0));
    }

    // ---------- armado de datos ----------

    private boolean paso(String clave) {
      return servicio
        .obtenerResumen(USUARIO_ID)
        .pasos()
        .stream()
        .filter(paso -> paso.clave().equals(clave))
        .findFirst()
        .orElseThrow()
        .completo();
    }

    private void prometer(Promocion promocion) {
      when(servicioPromocion.listarPorComercio(COMERCIO_ID)).thenReturn(List.of(promocion));
    }

    private Promocion promocion(boolean activa, LocalDateTime inicio, LocalDateTime fin) {
      Promocion promocion = new Promocion();
      promocion.setActiva(activa);
      promocion.setFechaInicio(inicio);
      promocion.setFechaFin(fin);
      return promocion;
    }

    private Reporte reporte(LocalDateTime fecha) {
      Producto producto = new Producto();
      producto.setNombre("Leche");
      Reporte reporte = new Reporte();
      reporte.setProducto(producto);
      reporte.setPrecio(1150.0);
      reporte.setFechaDeReporte(fecha);
      return reporte;
    }
  }
}
