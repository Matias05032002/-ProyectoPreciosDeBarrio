package com.tallerwebi.dominio.notificacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class TareaDeInactividadTest {

  @Test
  public void deberiaDevolverCuantosVecinosAvisoElServicio() {
    ServicioInactividad servicio = mock(ServicioInactividad.class);
    when(servicio.avisarVecinosInactivos(any(LocalDateTime.class))).thenReturn(3);

    assertThat(new TareaDeInactividad(servicio).ejecutar(), is(3));
  }

  @Test
  public void unFalloNoDeberiaFrenarLaTarea() {
    ServicioInactividad servicio = mock(ServicioInactividad.class);
    when(servicio.avisarVecinosInactivos(any(LocalDateTime.class)))
      .thenThrow(new IllegalStateException("base caída"));

    assertThat(new TareaDeInactividad(servicio).ejecutar(), is(0));
  }
}
