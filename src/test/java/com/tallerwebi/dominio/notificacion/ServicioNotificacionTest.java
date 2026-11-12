package com.tallerwebi.dominio.notificacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.notificacion.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ServicioNotificacionTest {

  private RepositorioNotificacion repositorio;
  private ServicioNotificacion servicio;

  @BeforeEach
  void init() {
    repositorio = mock(RepositorioNotificacion.class);
    servicio = new ServicioNotificacionImpl(repositorio);
  }

  @Test
  void notificarDeberiaGuardarUnaNotificacionNoLeidaConFecha() {
    boolean creada = servicio.notificar(
      7L,
      TipoNotificacion.DUDOSO_EN_TU_COMERCIO,
      "Mensaje",
      "/comercio/dashboard",
      "k1"
    );

    assertThat(creada, is(true));
    ArgumentCaptor<Notificacion> guardada = ArgumentCaptor.forClass(Notificacion.class);
    verify(repositorio).guardar(guardada.capture());
    assertThat(guardada.getValue().getUsuarioId(), equalTo(7L));
    assertThat(guardada.getValue().getTipo(), equalTo(TipoNotificacion.DUDOSO_EN_TU_COMERCIO));
    assertThat(guardada.getValue().getMensaje(), equalTo("Mensaje"));
    assertThat(guardada.getValue().getEnlace(), equalTo("/comercio/dashboard"));
    assertThat(guardada.getValue().isLeida(), is(false));
    assertThat(guardada.getValue().getFechaCreacion() != null, is(true));
  }

  @Test
  void notificarConUnaClaveYaUsadaNoDeberiaDuplicar() {
    when(repositorio.existe(7L, "k1")).thenReturn(true);

    boolean creada = servicio.notificar(
      7L,
      TipoNotificacion.DUDOSO_EN_TU_COMERCIO,
      "Mensaje",
      null,
      "k1"
    );

    assertThat(creada, is(false));
    verify(repositorio, never()).guardar(any());
  }

  @Test
  void notificarSinClaveDeberiaGuardarSiempre() {
    assertThat(
      servicio.notificar(7L, TipoNotificacion.DUDOSO_EN_TU_COMERCIO, "Mensaje", null, null),
      is(true)
    );
    verify(repositorio, never()).existe(any(), any());
  }

  @Test
  void notificarSinDestinatarioOMensajeVacioNoDeberiaGuardar() {
    assertThat(
      servicio.notificar(null, TipoNotificacion.DUDOSO_EN_TU_COMERCIO, "Mensaje", null, null),
      is(false)
    );
    assertThat(
      servicio.notificar(7L, TipoNotificacion.DUDOSO_EN_TU_COMERCIO, "  ", null, null),
      is(false)
    );
    assertThat(
      servicio.notificar(7L, TipoNotificacion.DUDOSO_EN_TU_COMERCIO, null, null, null),
      is(false)
    );
    verify(repositorio, never()).guardar(any());
  }

  @Test
  void notificarDeberiaRecortarUnMensajeMuyLargo() {
    servicio.notificar(7L, TipoNotificacion.DUDOSO_EN_TU_COMERCIO, "a".repeat(500), null, null);

    ArgumentCaptor<Notificacion> guardada = ArgumentCaptor.forClass(Notificacion.class);
    verify(repositorio).guardar(guardada.capture());
    assertThat(
      guardada.getValue().getMensaje().length(),
      equalTo(Notificacion.LARGO_MAXIMO_MENSAJE)
    );
  }

  @Test
  void notificarConUnEnlaceQueNoEsInternoLoDescartaPorSeguridad() {
    servicio.notificar(
      7L,
      TipoNotificacion.DUDOSO_EN_TU_COMERCIO,
      "Mensaje",
      "https://malo.com",
      null
    );
    servicio.notificar(7L, TipoNotificacion.DUDOSO_EN_TU_COMERCIO, "Mensaje", "//malo.com", null);

    ArgumentCaptor<Notificacion> guardada = ArgumentCaptor.forClass(Notificacion.class);
    verify(repositorio, org.mockito.Mockito.times(2)).guardar(guardada.capture());
    assertThat(guardada.getAllValues().get(0).getEnlace(), equalTo(null));
    assertThat(guardada.getAllValues().get(1).getEnlace(), equalTo(null));
  }

  @Test
  void listarDeberiaPedirLasUltimasDelUsuario() {
    Notificacion notificacion = new Notificacion();
    when(repositorio.listarDelUsuario(7L, ServicioNotificacionImpl.MAXIMO_A_LISTAR))
      .thenReturn(List.of(notificacion));

    assertThat(servicio.listar(7L), equalTo(List.of(notificacion)));
  }

  @Test
  void contarNoLeidasDeberiaDelegarEnElRepositorio() {
    when(repositorio.contarNoLeidas(7L)).thenReturn(3L);

    assertThat(servicio.contarNoLeidas(7L), equalTo(3L));
  }

  @Test
  void marcarComoLeidaDeberiaDevolverTrueSoloSiEraDelUsuario() {
    when(repositorio.marcarComoLeida(5L, 7L)).thenReturn(1);
    when(repositorio.marcarComoLeida(6L, 7L)).thenReturn(0);

    assertThat(servicio.marcarComoLeida(5L, 7L), is(true));
    assertThat(servicio.marcarComoLeida(6L, 7L), is(false));
  }

  @Test
  void marcarTodasComoLeidasDeberiaDelegarEnElRepositorio() {
    servicio.marcarTodasComoLeidas(7L);

    verify(repositorio).marcarTodasComoLeidas(7L);
  }

  @Test
  void buscarEnlaceDeUnaNotificacionDelUsuarioDeberiaDevolverloSoloSiEsSuya() {
    Notificacion n = new Notificacion();
    n.setUsuarioId(7L);
    n.setEnlace("/comercio/dashboard");
    when(repositorio.buscarPorId(5L)).thenReturn(n);

    assertThat(servicio.enlaceDe(5L, 7L), equalTo("/comercio/dashboard"));
    assertThat(servicio.enlaceDe(5L, 8L), equalTo(null));
    assertThat(servicio.enlaceDe(99L, 7L), equalTo(null));
  }
}
