package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import com.tallerwebi.dominio.Recuperacion.RepositorioTokenRecuperacion;
import com.tallerwebi.dominio.Recuperacion.TokenRecuperacion;
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
public class RepositorioTokenRecuperacionTest {

  private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 3, 9, 0);

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioTokenRecuperacion repositorio;

  @BeforeEach
  public void init() {
    repositorio = new RepositorioTokenRecuperacionImpl(sessionFactory);
  }

  private TokenRecuperacion guardar(
    String hash,
    Long usuarioId,
    LocalDateTime creado,
    int minutos
  ) {
    TokenRecuperacion token = new TokenRecuperacion(
      hash,
      usuarioId,
      creado,
      creado.plusMinutes(minutos)
    );
    repositorio.guardar(token);
    return token;
  }

  @Test
  @Transactional
  @Rollback
  public void buscarPorHashDevuelveElTokenGuardado() {
    TokenRecuperacion guardado = guardar("a".repeat(64), 1L, AHORA, 30);

    assertThat(repositorio.buscarPorHash("a".repeat(64)), equalTo(guardado));
  }

  @Test
  @Transactional
  @Rollback
  public void buscarPorHashInexistenteDevuelveNull() {
    assertThat(repositorio.buscarPorHash("b".repeat(64)), nullValue());
  }

  @Test
  @Transactional
  @Rollback
  public void buscarVigentesDejaAfueraLosUsadosLosVencidosYLosDeOtroUsuario() {
    TokenRecuperacion vigenteViejo = guardar("1".repeat(64), 1L, AHORA.minusMinutes(20), 30);
    TokenRecuperacion vigenteNuevo = guardar("2".repeat(64), 1L, AHORA.minusMinutes(5), 30);
    guardar("3".repeat(64), 1L, AHORA.minusMinutes(40), 30);
    TokenRecuperacion usado = guardar("4".repeat(64), 1L, AHORA.minusMinutes(1), 30);
    usado.marcarUsado();
    repositorio.actualizar(usado);
    guardar("5".repeat(64), 2L, AHORA.minusMinutes(1), 30);

    List<TokenRecuperacion> vigentes = repositorio.buscarVigentesDelUsuario(1L, AHORA);

    assertThat(vigentes, equalTo(List.of(vigenteNuevo, vigenteViejo)));
  }
}
