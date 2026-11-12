package com.tallerwebi.infraestructura.Notificacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.tallerwebi.dominio.notificacion.Notificacion;
import com.tallerwebi.dominio.notificacion.RepositorioNotificacion;
import com.tallerwebi.dominio.notificacion.TipoNotificacion;
import com.tallerwebi.infraestructura.RepositorioNotificacionImpl;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HibernateInfraestructuraTestConfig.class })
public class RepositorioNotificacionTest {

  private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 11, 10, 0);

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioNotificacion repositorio;

  @BeforeEach
  void init() {
    repositorio = new RepositorioNotificacionImpl(sessionFactory);
  }

  private Notificacion guardar(Long usuarioId, String clave, LocalDateTime fecha) {
    Notificacion n = new Notificacion();
    n.setUsuarioId(usuarioId);
    n.setTipo(TipoNotificacion.DUDOSO_EN_TU_COMERCIO);
    n.setMensaje("Mensaje " + clave);
    n.setClave(clave);
    n.setFechaCreacion(fecha);
    repositorio.guardar(n);
    return n;
  }

  @Test
  @Transactional
  @Rollback
  void listarDeberiaDevolverSoloLasDelUsuarioDeLasMasNuevasALasMasViejasConLimite() {
    Notificacion vieja = guardar(1L, "a", AHORA.minusDays(2));
    Notificacion nueva = guardar(1L, "b", AHORA);
    guardar(2L, "c", AHORA);

    List<Notificacion> todas = repositorio.listarDelUsuario(1L, 10);
    List<Notificacion> una = repositorio.listarDelUsuario(1L, 1);

    assertThat(todas, equalTo(List.of(nueva, vieja)));
    assertThat(una, equalTo(List.of(nueva)));
  }

  @Test
  @Transactional
  @Rollback
  void existeDeberiaDetectarLaClaveSoloParaEseUsuario() {
    guardar(1L, "dudoso:5", AHORA);

    assertThat(repositorio.existe(1L, "dudoso:5"), is(true));
    assertThat(repositorio.existe(2L, "dudoso:5"), is(false));
    assertThat(repositorio.existe(1L, "otra"), is(false));
  }

  @Test
  @Transactional
  @Rollback
  void contarNoLeidasDeberiaIgnorarLasLeidasYLasDeOtrosUsuarios() {
    guardar(1L, "a", AHORA);
    Notificacion leida = guardar(1L, "b", AHORA);
    leida.setLeida(true);
    guardar(2L, "c", AHORA);

    assertThat(repositorio.contarNoLeidas(1L), equalTo(1L));
  }

  @Test
  @Transactional
  @Rollback
  void marcarComoLeidaDeberiaFuncionarSoloParaSuDuenio() {
    Notificacion n = guardar(1L, "a", AHORA);

    assertThat(repositorio.marcarComoLeida(n.getId(), 2L), equalTo(0));
    assertThat(repositorio.contarNoLeidas(1L), equalTo(1L));
    assertThat(repositorio.marcarComoLeida(n.getId(), 1L), equalTo(1));
    assertThat(repositorio.contarNoLeidas(1L), equalTo(0L));
  }

  @Test
  @Transactional
  @Rollback
  void marcarTodasComoLeidasDeberiaAfectarSoloAlUsuario() {
    guardar(1L, "a", AHORA);
    guardar(1L, "b", AHORA);
    guardar(2L, "c", AHORA);

    repositorio.marcarTodasComoLeidas(1L);

    assertThat(repositorio.contarNoLeidas(1L), equalTo(0L));
    assertThat(repositorio.contarNoLeidas(2L), equalTo(1L));
  }

  @Test
  @Transactional
  @Rollback
  void buscarPorIdDeberiaDevolverLaNotificacionGuardada() {
    Notificacion n = guardar(1L, "a", AHORA);

    assertThat(repositorio.buscarPorId(n.getId()), equalTo(n));
  }
}
