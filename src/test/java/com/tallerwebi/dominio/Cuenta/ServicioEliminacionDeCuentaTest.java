package com.tallerwebi.dominio.Cuenta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.Promocion;
import com.tallerwebi.dominio.Comercio.RepositorioComercio;
import com.tallerwebi.dominio.Comercio.RepositorioPromocion;
import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ServicioEliminacionDeCuentaTest {

  private static final String CLAVE = "Clave1234";

  private RepositorioUsuario repositorioUsuario;
  private RepositorioComercio repositorioComercio;
  private RepositorioPromocion repositorioPromocion;
  private ServicioEliminacionDeCuenta servicio;

  @BeforeEach
  public void init() {
    repositorioUsuario = mock(RepositorioUsuario.class);
    repositorioComercio = mock(RepositorioComercio.class);
    repositorioPromocion = mock(RepositorioPromocion.class);
    servicio =
      new ServicioEliminacionDeCuentaImpl(
        repositorioUsuario,
        repositorioComercio,
        repositorioPromocion
      );
  }

  @Test
  public void conLaContrasenaCorrectaDeberiaEliminarLaCuenta() {
    Usuario vecino = unUsuario(7L, "USER");
    when(repositorioUsuario.buscarPorId(7L)).thenReturn(vecino);

    assertThat(servicio.eliminarCuenta(7L, CLAVE), is(true));
    verify(repositorioUsuario).modificar(vecino);
  }

  @Test
  public void deberiaDesactivarLaCuentaYBorrarSusDatosPersonales() {
    Usuario vecino = unUsuario(7L, "USER");
    when(repositorioUsuario.buscarPorId(7L)).thenReturn(vecino);

    servicio.eliminarCuenta(7L, CLAVE);

    assertThat(vecino.getActivo(), is(false));
    assertThat(vecino.getVerificado(), is(false));
    assertThat(vecino.getNombre(), is("Usuario eliminado"));
    assertThat(vecino.getApellido(), is(nullValue()));
    assertThat(vecino.getFechaNacimiento(), is(nullValue()));
    assertThat(vecino.getLocalidad(), is(nullValue()));
  }

  @Test
  public void deberiaLiberarElEmailParaQuePuedaRegistrarseDeNuevo() {
    Usuario vecino = unUsuario(7L, "USER");
    when(repositorioUsuario.buscarPorId(7L)).thenReturn(vecino);

    servicio.eliminarCuenta(7L, CLAVE);

    assertThat(vecino.getEmail(), is("cuenta-eliminada-7@eliminada.invalid"));
  }

  @Test
  public void laContrasenaViejaNoDeberiaServirDespuesDeEliminar() {
    Usuario vecino = unUsuario(7L, "USER");
    when(repositorioUsuario.buscarPorId(7L)).thenReturn(vecino);

    servicio.eliminarCuenta(7L, CLAVE);

    assertThat(new BCryptPasswordEncoder().matches(CLAVE, vecino.getPassword()), is(false));
  }

  @Test
  public void conUnaContrasenaIncorrectaNoDeberiaEliminarNada() {
    Usuario vecino = unUsuario(7L, "USER");
    when(repositorioUsuario.buscarPorId(7L)).thenReturn(vecino);

    assertThat(servicio.eliminarCuenta(7L, "OtraClave1"), is(false));
    assertThat(vecino.getActivo(), is(true));
    assertThat(vecino.getNombre(), is("Ana"));
    verify(repositorioUsuario, never()).modificar(any());
  }

  @Test
  public void sinContrasenaNoDeberiaEliminarNada() {
    when(repositorioUsuario.buscarPorId(7L)).thenReturn(unUsuario(7L, "USER"));

    assertThat(servicio.eliminarCuenta(7L, null), is(false));
    verify(repositorioUsuario, never()).modificar(any());
  }

  @Test
  public void unUsuarioInexistenteNoDeberiaPoderEliminarse() {
    when(repositorioUsuario.buscarPorId(99L)).thenReturn(null);

    assertThat(servicio.eliminarCuenta(99L, CLAVE), is(false));
  }

  @Test
  public void unaCuentaYaEliminadaNoDeberiaEliminarseDeNuevo() {
    Usuario eliminada = unUsuario(7L, "USER");
    eliminada.setActivo(false);
    when(repositorioUsuario.buscarPorId(7L)).thenReturn(eliminada);

    assertThat(servicio.eliminarCuenta(7L, CLAVE), is(false));
    verify(repositorioUsuario, never()).modificar(any());
  }

  @Test
  public void elAdministradorNoDeberiaPoderEliminarSuCuenta() {
    Usuario admin = unUsuario(1L, "ADMIN");
    when(repositorioUsuario.buscarPorId(1L)).thenReturn(admin);

    assertThrows(AccesoDenegado.class, () -> servicio.eliminarCuenta(1L, CLAVE));
    assertThat(admin.getActivo(), is(true));
    verify(repositorioUsuario, never()).modificar(any());
  }

  @Test
  public void unVecinoNoDeberiaTocarComerciosNiPromociones() {
    when(repositorioUsuario.buscarPorId(7L)).thenReturn(unUsuario(7L, "USER"));

    servicio.eliminarCuenta(7L, CLAVE);

    verifyNoInteractions(repositorioComercio, repositorioPromocion);
  }

  @Test
  public void elDuenioDeUnComercioDeberiaDesactivarloJuntoConSusPromociones() {
    Usuario duenio = unUsuario(8L, "COMERCIO");
    Comercio comercio = new Comercio();
    comercio.setId(3L);
    comercio.setActivo(true);
    Promocion una = unaPromocion();
    Promocion otra = unaPromocion();
    when(repositorioUsuario.buscarPorId(8L)).thenReturn(duenio);
    when(repositorioComercio.buscarComercioPorUsuarioId(8L)).thenReturn(comercio);
    when(repositorioPromocion.listarPorComercio(3L)).thenReturn(List.of(una, otra));

    assertThat(servicio.eliminarCuenta(8L, CLAVE), is(true));

    assertThat(comercio.getActivo(), is(false));
    verify(repositorioComercio).actualizarComercio(comercio);
    assertThat(una.getActiva(), is(false));
    assertThat(otra.getActiva(), is(false));
    verify(repositorioPromocion).actualizar(una);
    verify(repositorioPromocion).actualizar(otra);
  }

  @Test
  public void unDuenioSinComercioDeberiaPoderEliminarSuCuenta() {
    Usuario duenio = unUsuario(8L, "COMERCIO");
    when(repositorioUsuario.buscarPorId(8L)).thenReturn(duenio);
    when(repositorioComercio.buscarComercioPorUsuarioId(8L)).thenReturn(null);

    assertThat(servicio.eliminarCuenta(8L, CLAVE), is(true));
    assertThat(duenio.getActivo(), is(false));
  }

  private Promocion unaPromocion() {
    Promocion promocion = new Promocion();
    promocion.setActiva(true);
    return promocion;
  }

  private Usuario unUsuario(Long id, String rol) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setEmail("ana@test.com");
    usuario.setPassword(new BCryptPasswordEncoder().encode(CLAVE));
    usuario.setRol(rol);
    usuario.setActivo(true);
    usuario.setVerificado(true);
    usuario.setNombre("Ana");
    usuario.setApellido("Perez");
    usuario.setFechaNacimiento(LocalDate.of(1990, 5, 1));
    usuario.setLocalidad("Ramos Mejía");
    return usuario;
  }
}
