package com.tallerwebi.dominio.Producto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.Reporte.Reporte;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TendenciaDePrecioTest {

  private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 12, 12, 0);

  @Test
  public void deberiaCalcularQueElPrecioBajoDesdeElReporteAnterior() {
    TendenciaDePrecio tendencia = TendenciaDePrecio.calcular(
      List.of(reporte(1000, 20), reporte(900, 1)),
      AHORA
    );

    assertThat(tendencia.porcentaje(), is(-10));
    assertThat(tendencia.desde(), is(AHORA.minusDays(20).toLocalDate()));
  }

  @Test
  public void deberiaCalcularQueElPrecioSubio() {
    TendenciaDePrecio tendencia = TendenciaDePrecio.calcular(
      List.of(reporte(1000, 20), reporte(1100, 1)),
      AHORA
    );

    assertThat(tendencia.porcentaje(), is(10));
  }

  @Test
  public void deberiaCompararConElReporteAnteriorMasRecienteSinImportarElOrden() {
    List<Reporte> reportes = List.of(reporte(1100, 1), reporte(800, 25), reporte(1000, 10));

    TendenciaDePrecio tendencia = TendenciaDePrecio.calcular(reportes, AHORA);

    assertThat(tendencia.porcentaje(), is(10));
    assertThat(tendencia.desde(), is(AHORA.minusDays(10).toLocalDate()));
  }

  @Test
  public void sinCambiosSignificativosNoDeberiaHaberTendencia() {
    assertThat(
      TendenciaDePrecio.calcular(List.of(reporte(1000, 20), reporte(1010, 1)), AHORA),
      is(nullValue())
    );
  }

  @Test
  public void conUnSoloReporteNoDeberiaHaberTendencia() {
    assertThat(TendenciaDePrecio.calcular(List.of(reporte(1000, 1)), AHORA), is(nullValue()));
  }

  @Test
  public void dosReportesDelMismoDiaNoDeberianCompararse() {
    assertThat(
      TendenciaDePrecio.calcular(List.of(reporte(1000, 1), reporte(900, 1)), AHORA),
      is(nullValue())
    );
  }

  @Test
  public void unReporteAnteriorDeMasDeTreintaDiasNoDeberiaCompararse() {
    assertThat(
      TendenciaDePrecio.calcular(List.of(reporte(1000, 50), reporte(900, 1)), AHORA),
      is(nullValue())
    );
  }

  @Test
  public void unPrecioActualVencidoNoDeberiaTenerTendencia() {
    assertThat(
      TendenciaDePrecio.calcular(List.of(reporte(1000, 80), reporte(900, 40)), AHORA),
      is(nullValue())
    );
  }

  @Test
  public void noDeberiaContarLosReportesDudosos() {
    Reporte dudosoIntermedio = reporte(500, 10);
    dudosoIntermedio.setPuntuacion(Reporte.PUNTUACION_DUDOSO + 1);
    Reporte dudosoUltimo = reporte(100, 0);
    dudosoUltimo.setPuntuacion(Reporte.PUNTUACION_DUDOSO + 1);

    TendenciaDePrecio tendencia = TendenciaDePrecio.calcular(
      List.of(reporte(1000, 20), dudosoIntermedio, reporte(900, 1), dudosoUltimo),
      AHORA
    );

    assertThat(tendencia.porcentaje(), is(-10));
    assertThat(tendencia.desde(), is(AHORA.minusDays(20).toLocalDate()));
  }

  @Test
  public void ignoraReportesSinPrecioOSinFecha() {
    Reporte sinPrecio = reporte(0, 2);
    sinPrecio.setPrecio(null);
    Reporte sinFecha = reporte(900, 2);
    sinFecha.setFechaDeReporte(null);

    TendenciaDePrecio tendencia = TendenciaDePrecio.calcular(
      List.of(reporte(1000, 20), reporte(900, 1), sinPrecio, sinFecha),
      AHORA
    );

    assertThat(tendencia.porcentaje(), is(-10));
  }

  @Test
  public void sinReportesNoDeberiaHaberTendencia() {
    assertThat(TendenciaDePrecio.calcular(List.of(), AHORA), is(nullValue()));
    assertThat(TendenciaDePrecio.calcular(null, AHORA), is(nullValue()));
  }

  private Reporte reporte(double precio, int haceDias) {
    Reporte reporte = new Reporte();
    reporte.setPrecio(precio);
    reporte.setFechaDeReporte(AHORA.minusDays(haceDias));
    return reporte;
  }

  @Test
  public void unaTendenciaNegativaBajaYSeMuestraSinSigno() {
    TendenciaDePrecio tendencia = new TendenciaDePrecio(-8, AHORA.toLocalDate());

    assertThat(tendencia.baja(), is(true));
    assertThat(tendencia.sube(), is(false));
    assertThat(tendencia.porcentajeAbsoluto(), is(8));
  }

  @Test
  public void unaTendenciaPositivaSube() {
    TendenciaDePrecio tendencia = new TendenciaDePrecio(5, AHORA.toLocalDate());

    assertThat(tendencia.sube(), is(true));
    assertThat(tendencia.baja(), is(false));
    assertThat(tendencia.porcentajeAbsoluto(), is(5));
  }
}
