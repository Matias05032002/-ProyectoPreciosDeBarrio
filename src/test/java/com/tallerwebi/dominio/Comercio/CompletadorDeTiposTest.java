package com.tallerwebi.dominio.Comercio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.event.ContextRefreshedEvent;

public class CompletadorDeTiposTest {

  private ServicioComercio servicioComercio;
  private CompletadorDeTipos completador;

  @BeforeEach
  void init() {
    servicioComercio = mock(ServicioComercio.class);
    completador = new CompletadorDeTipos(servicioComercio);
  }

  @Test
  void alArrancarDeberiaCompletarLosTiposFaltantes() {
    when(servicioComercio.completarTiposFaltantes()).thenReturn(3);

    assertEquals(3, completador.completar());
  }

  @Test
  void siFallaElArranqueNoDeberiaCortarse() {
    when(servicioComercio.completarTiposFaltantes()).thenThrow(new IllegalStateException("db"));

    assertEquals(0, completador.completar());
  }

  @Test
  void elEventoSeAtiendeUnaSolaVezAunqueLleguePorDosContextos() {
    ContextRefreshedEvent evento = mock(ContextRefreshedEvent.class);

    completador.onApplicationEvent(evento);
    completador.onApplicationEvent(evento);

    verify(servicioComercio, times(1)).completarTiposFaltantes();
  }
}
