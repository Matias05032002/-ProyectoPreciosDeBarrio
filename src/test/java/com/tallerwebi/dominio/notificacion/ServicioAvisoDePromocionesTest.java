package com.tallerwebi.dominio.notificacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.Promocion;
import com.tallerwebi.dominio.Comercio.RepositorioPromocion;
import com.tallerwebi.dominio.RepositorioUsuario;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioAvisoDePromocionesTest {

  private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 12, 10, 0);

  private RepositorioPromocion repositorioPromocion;
  private RepositorioUsuario repositorioUsuario;
  private ServicioNotificacion servicioNotificacion;
  private ServicioAvisoDePromociones servicio;

  @BeforeEach
  public void init() {
    repositorioPromocion = mock(RepositorioPromocion.class);
    repositorioUsuario = mock(RepositorioUsuario.class);
    servicioNotificacion = mock(ServicioNotificacion.class);
    servicio =
      new ServicioAvisoDePromocionesImpl(
        repositorioPromocion,
        repositorioUsuario,
        servicioNotificacion
      );
    when(servicioNotificacion.notificar(any(), any(), any(), any(), any())).thenReturn(true);
  }

  @Test
  public void deberiaBuscarLasPromocionesQueEmpezaronEnLosUltimosDosDias() {
    when(repositorioPromocion.listarIniciadasEntre(any(), any())).thenReturn(List.of());

    servicio.avisarPromocionesNuevas(AHORA);

    verify(repositorioPromocion).listarIniciadasEntre(AHORA.minusDays(2), AHORA);
  }

  @Test
  public void deberiaAvisarATodosLosVecinosDeLaLocalidadDelComercio() {
    when(repositorioPromocion.listarIniciadasEntre(any(), any()))
      .thenReturn(List.of(unaPromocion(5L, "2x1 en gaseosas", "Almacén Don Pepe", "Ramos Mejía")));
    when(repositorioUsuario.idsDeVecinosDeLocalidad("Ramos Mejía")).thenReturn(List.of(1L, 2L));

    int avisos = servicio.avisarPromocionesNuevas(AHORA);

    assertThat(avisos, is(2));
    verify(servicioNotificacion)
      .notificar(
        1L,
        TipoNotificacion.PROMOCION_EN_TU_BARRIO,
        "Nueva promo en Almacén Don Pepe: 2x1 en gaseosas",
        "/promociones",
        "promo:5"
      );
    verify(servicioNotificacion)
      .notificar(eq(2L), eq(TipoNotificacion.PROMOCION_EN_TU_BARRIO), any(), any(), eq("promo:5"));
  }

  @Test
  public void noDeberiaContarLosAvisosQueYaExistian() {
    when(repositorioPromocion.listarIniciadasEntre(any(), any()))
      .thenReturn(List.of(unaPromocion(5L, "Promo", "Don Pepe", "Ramos Mejía")));
    when(repositorioUsuario.idsDeVecinosDeLocalidad(any())).thenReturn(List.of(1L, 2L));
    when(servicioNotificacion.notificar(eq(1L), any(), any(), any(), any())).thenReturn(false);

    assertThat(servicio.avisarPromocionesNuevas(AHORA), is(1));
  }

  @Test
  public void unComercioSinLocalidadNoDeberiaAvisarANadie() {
    when(repositorioPromocion.listarIniciadasEntre(any(), any()))
      .thenReturn(List.of(unaPromocion(5L, "Promo", "Don Pepe", null)));

    assertThat(servicio.avisarPromocionesNuevas(AHORA), is(0));
    verifyNoInteractions(repositorioUsuario, servicioNotificacion);
  }

  @Test
  public void sinPromocionesNuevasNoDeberiaAvisarANadie() {
    when(repositorioPromocion.listarIniciadasEntre(any(), any())).thenReturn(List.of());

    assertThat(servicio.avisarPromocionesNuevas(AHORA), is(0));
    verifyNoInteractions(servicioNotificacion);
  }

  private Promocion unaPromocion(Long id, String titulo, String comercioNombre, String localidad) {
    Comercio comercio = new Comercio();
    comercio.setNombre(comercioNombre);
    comercio.setLocalidad(localidad);
    Promocion promocion = new Promocion();
    promocion.setId(id);
    promocion.setTitulo(titulo);
    promocion.setComercio(comercio);
    return promocion;
  }
}
