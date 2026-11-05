package com.tallerwebi.dominio.Comercio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
}
