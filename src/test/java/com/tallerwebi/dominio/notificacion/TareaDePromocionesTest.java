package com.tallerwebi.dominio.notificacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class TareaDePromocionesTest {

  @Test
  public void deberiaDevolverCuantosAvisosCreoElServicio() {
    ServicioAvisoDePromociones servicio = mock(ServicioAvisoDePromociones.class);
    when(servicio.avisarPromocionesNuevas(any(LocalDateTime.class))).thenReturn(4);

    assertThat(new TareaDePromociones(servicio).ejecutar(), is(4));
  }

  @Test
  public void unFalloNoDeberiaFrenarLaTarea() {
    ServicioAvisoDePromociones servicio = mock(ServicioAvisoDePromociones.class);
    when(servicio.avisarPromocionesNuevas(any(LocalDateTime.class)))
      .thenThrow(new IllegalStateException("base caída"));

    assertThat(new TareaDePromociones(servicio).ejecutar(), is(0));
  }
}
