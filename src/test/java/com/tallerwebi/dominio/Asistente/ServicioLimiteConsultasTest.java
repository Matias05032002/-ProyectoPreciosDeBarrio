package com.tallerwebi.dominio.Asistente;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tallerwebi.dominio.excepcion.LimiteDeConsultasExcedido;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

  // El limitador olvida a los visitantes que no vuelven, pero solo cuando el mapa supera las 5.000 claves

  private static final long VENTANA = 1_000L;
  private static final int CLAVES_PARA_LIMPIAR = 5_000;

  private AtomicLong relojMemoria;
  private ServicioLimiteConsultasImpl servicioMemoria;

  @BeforeEach
  public void prepararCasosDeBorde() {
    relojMemoria = new AtomicLong(0);
    servicioMemoria = new ServicioLimiteConsultasImpl(20, VENTANA, relojMemoria::get);
  }

  @Test
  public void conExactamenteCincoMilClavesNoLimpiaNada() throws Exception {
    registrarClavesViejas(CLAVES_PARA_LIMPIAR);
    relojMemoria.set(VENTANA + 5);

    servicioMemoria.registrarConsulta("nueva");

    assertThat(claves().size(), equalTo(CLAVES_PARA_LIMPIAR + 1));
  }

  @Test
  public void conMasDeCincoMilClavesOlvidaSoloLasVencidas() throws Exception {
    // 4.999 claves viejas + "justo-vencida" = 5.000: "reciente" entra sin disparar la limpieza
    registrarClavesViejas(CLAVES_PARA_LIMPIAR - 1);
    relojMemoria.set(5);
    servicioMemoria.registrarConsulta("justo-vencida");
    relojMemoria.set(VENTANA - 10);
    servicioMemoria.registrarConsulta("reciente");
    relojMemoria.set(VENTANA + 5);

    servicioMemoria.registrarConsulta("otro");

    // "justo-vencida" cumple la ventana exacta y se olvida; "reciente" todavía cuenta
    assertThat(claves().keySet(), equalTo(java.util.Set.of("reciente", "otro")));
  }

  private void registrarClavesViejas(int cantidad) throws Exception {
    for (int i = 0; i < cantidad; i++) {
      servicioMemoria.registrarConsulta("clave-" + i);
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, ?> claves() throws Exception {
    Field campo = ServicioLimiteConsultasImpl.class.getDeclaredField("consultasPorClave");
    campo.setAccessible(true);
    return (Map<String, ?>) campo.get(servicioMemoria);
  }

  @Nested
  class CasosDeLimpiezaDeMemoria {

    private static final long VENTANA = 1_000L;
    private static final int CLAVES_PARA_LIMPIAR = 5_000;

    private AtomicLong reloj;
    private ServicioLimiteConsultasImpl servicio;

    @BeforeEach
    public void init() {
      reloj = new AtomicLong(0);
      servicio = new ServicioLimiteConsultasImpl(20, VENTANA, reloj::get);
    }

    @Test
    public void conExactamenteCincoMilClavesNoLimpiaNada() throws Exception {
      registrarClavesViejas(CLAVES_PARA_LIMPIAR);
      reloj.set(VENTANA + 5);

      servicio.registrarConsulta("nueva");

      assertThat(claves().size(), equalTo(CLAVES_PARA_LIMPIAR + 1));
    }

    @Test
    public void conMasDeCincoMilClavesOlvidaSoloLasVencidas() throws Exception {
      // 4.999 claves viejas + "justo-vencida" = 5.000: "reciente" entra sin disparar la limpieza
      registrarClavesViejas(CLAVES_PARA_LIMPIAR - 1);
      reloj.set(5);
      servicio.registrarConsulta("justo-vencida");
      reloj.set(VENTANA - 10);
      servicio.registrarConsulta("reciente");
      reloj.set(VENTANA + 5);

      servicio.registrarConsulta("otro");

      // "justo-vencida" cumple la ventana exacta y se olvida; "reciente" todavía cuenta
      assertThat(claves().keySet(), equalTo(java.util.Set.of("reciente", "otro")));
    }

    private void registrarClavesViejas(int cantidad) throws Exception {
      for (int i = 0; i < cantidad; i++) {
        servicio.registrarConsulta("clave-" + i);
      }
    }

    @SuppressWarnings("unchecked")
    private Map<String, ?> claves() throws Exception {
      Field campo = ServicioLimiteConsultasImpl.class.getDeclaredField("consultasPorClave");
      campo.setAccessible(true);
      return (Map<String, ?>) campo.get(servicio);
    }
  }
}
