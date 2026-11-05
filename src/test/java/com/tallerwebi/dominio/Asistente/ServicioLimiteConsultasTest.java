package com.tallerwebi.dominio.Asistente;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tallerwebi.dominio.excepcion.LimiteDeConsultasExcedido;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioLimiteConsultasTest {

  private static final long HORA = 3_600_000L;
  private AtomicLong ahora;
  private ServicioLimiteConsultasImpl servicio;

  @BeforeEach
  void preparar() {
    ahora = new AtomicLong(1_000L);
    servicio = new ServicioLimiteConsultasImpl(3, HORA, ahora::get);
  }

  @Test
  void permiteConsultasHastaElMaximo() {
    for (int i = 0; i < 3; i++) {
      assertDoesNotThrow(() -> servicio.registrarConsulta("usuario-1"));
    }
  }

  @Test
  void rechazaLaConsultaQueSuperaElMaximo() throws Exception {
    for (int i = 0; i < 3; i++) {
      servicio.registrarConsulta("usuario-1");
    }
    assertThrows(LimiteDeConsultasExcedido.class, () -> servicio.registrarConsulta("usuario-1"));
  }

  @Test
  void vuelvePermitirCuandoPasoLaVentana() throws Exception {
    for (int i = 0; i < 3; i++) {
      servicio.registrarConsulta("usuario-1");
    }
    ahora.addAndGet(HORA);
    assertDoesNotThrow(() -> servicio.registrarConsulta("usuario-1"));
  }

  @Test
  void cadaClaveTieneSuPropioCupo() throws Exception {
    for (int i = 0; i < 3; i++) {
      servicio.registrarConsulta("usuario-1");
    }
    assertDoesNotThrow(() -> servicio.registrarConsulta("usuario-2"));
  }

  @Test
  void unaConsultaRechazadaNoExtiendeElBloqueo() throws Exception {
    for (int i = 0; i < 3; i++) {
      servicio.registrarConsulta("usuario-1");
    }
    ahora.addAndGet(HORA - 1);
    assertThrows(LimiteDeConsultasExcedido.class, () -> servicio.registrarConsulta("usuario-1"));
    ahora.addAndGet(1);
    assertDoesNotThrow(() -> servicio.registrarConsulta("usuario-1"));
  }

  @Test
  void conLosValoresPorDefectoPermiteVeinteConsultasPorHora() throws Exception {
    ServicioLimiteConsultasImpl real = new ServicioLimiteConsultasImpl();
    for (int i = 0; i < 20; i++) {
      real.registrarConsulta("usuario-1");
    }
    assertThrows(LimiteDeConsultasExcedido.class, () -> real.registrarConsulta("usuario-1"));
  }
}
