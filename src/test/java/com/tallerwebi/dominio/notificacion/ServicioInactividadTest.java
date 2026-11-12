package com.tallerwebi.dominio.notificacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioInactividadTest {

  private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 12, 10, 0);

  private RepositorioReporte repositorioReporte;
  private ServicioNotificacion servicioNotificacion;
  private ServicioInactividad servicio;

  @BeforeEach
  public void init() {
    repositorioReporte = mock(RepositorioReporte.class);
    servicioNotificacion = mock(ServicioNotificacion.class);
    servicio = new ServicioInactividadImpl(repositorioReporte, servicioNotificacion);
  }

  @Test
  public void deberiaBuscarALosQueNoReportanDesdeHaceCatorceDias() {
    when(repositorioReporte.vecinosSinReportarDesde(any())).thenReturn(List.of());

    servicio.avisarVecinosInactivos(AHORA);

    verify(repositorioReporte).vecinosSinReportarDesde(AHORA.minusDays(14));
  }

  @Test
  public void deberiaAvisarACadaVecinoInactivoConUnaClaveQueDependeDeSuUltimoReporte() {
    when(repositorioReporte.vecinosSinReportarDesde(any()))
      .thenReturn(List.<Object[]>of(new Object[] { 7L, LocalDateTime.of(2026, 10, 20, 15, 30) }));
    when(servicioNotificacion.notificar(any(), any(), any(), any(), any())).thenReturn(true);

    int avisados = servicio.avisarVecinosInactivos(AHORA);

    assertThat(avisados, is(1));
    verify(servicioNotificacion)
      .notificar(
        eq(7L),
        eq(TipoNotificacion.RECORDATORIO_DE_INACTIVIDAD),
        eq(ServicioInactividadImpl.MENSAJE),
        eq("/reporte"),
        eq("inactividad:2026-10-20")
      );
  }

  @Test
  public void noDeberiaContarLosAvisosQueYaExistian() {
    when(repositorioReporte.vecinosSinReportarDesde(any()))
      .thenReturn(
        List.<Object[]>of(
          new Object[] { 7L, LocalDateTime.of(2026, 10, 20, 15, 30) },
          new Object[] { 8L, LocalDateTime.of(2026, 10, 1, 9, 0) }
        )
      );
    when(servicioNotificacion.notificar(eq(7L), any(), any(), any(), any())).thenReturn(false);
    when(servicioNotificacion.notificar(eq(8L), any(), any(), any(), any())).thenReturn(true);

    assertThat(servicio.avisarVecinosInactivos(AHORA), is(1));
  }

  @Test
  public void sinInactivosNoDeberiaAvisarANadie() {
    when(repositorioReporte.vecinosSinReportarDesde(any())).thenReturn(List.of());

    assertThat(servicio.avisarVecinosInactivos(AHORA), is(0));
    verifyNoInteractions(servicioNotificacion);
  }
}
