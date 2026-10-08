package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioLoginTest {

  private ServicioLogin servicioLogin;
  private RepositorioUsuario repositorioUsuarioMock;

  @BeforeEach
  public void init() {
    this.repositorioUsuarioMock = mock(RepositorioUsuario.class);
    this.servicioLogin = new ServicioLoginImpl(this.repositorioUsuarioMock);
  }

  @Test
  public void consultarUsuarioDeberiaLlamarAlRepositorio() {
    // preparacion
    String email = "test@test.com";
    String password = "Password1";
    Usuario usuarioEsperado = new Usuario();
    usuarioEsperado.setEmail(email);
    usuarioEsperado.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(password));
    usuarioEsperado.activar();
    when(this.repositorioUsuarioMock.buscar(email)).thenReturn(usuarioEsperado);

    // ejecucion
    Usuario usuarioObtenido = this.servicioLogin.consultarUsuario(email, password);

    // validacion
    assertThat(usuarioObtenido, equalTo(usuarioEsperado));
    assertThat(usuarioEsperado.getPassword().startsWith("$2a$"), equalTo(true));
    assertThat(usuarioEsperado.getActivo(), equalTo(true));
    verify(this.repositorioUsuarioMock, times(1)).buscar(email);
  }

  @Test
  public void registrarUsuarioSiNoExisteDeberiaGuardarlo()
    throws UsuarioExistente, ContrasenaInvalida, EmailInvalido {
    // preparacion
    Usuario usuario = new Usuario();
    usuario.setEmail("nuevo@test.com");
    usuario.setPassword("Contrasena1");
    when(this.repositorioUsuarioMock.buscar(usuario.getEmail())).thenReturn(null);

    // ejecucion
    this.servicioLogin.registrar(usuario);

    // validacion
    verify(this.repositorioUsuarioMock, times(1)).guardar(usuario);
  }

  @Test
  public void registrarUsuarioSiExisteDeberiaLanzarExcepcion() {
    // preparacion
    Usuario usuario = new Usuario();
    usuario.setEmail("existe@test.com");
    usuario.setPassword("Contrasena1");
    when(this.repositorioUsuarioMock.buscar(usuario.getEmail())).thenReturn(new Usuario());

    // ejecucion y validacion
    assertThrows(UsuarioExistente.class, () -> this.servicioLogin.registrar(usuario));
    verify(this.repositorioUsuarioMock, times(0)).guardar(usuario);
  }

  @Test
  public void registrarUsuarioConEmailInvalidoDeberiaLanzarExcepcion() {
    Usuario usuario = new Usuario();
    usuario.setEmail("emailinvalido");
    usuario.setPassword("Contrasena1");

    assertThrows(EmailInvalido.class, () -> this.servicioLogin.registrar(usuario));
    verify(this.repositorioUsuarioMock, times(0)).guardar(usuario);
  }

  @Test
  public void registrarUsuarioConContrasenaSinMayusculaDeberiaLanzarExcepcion() {
    Usuario usuario = new Usuario();
    usuario.setEmail("test@test.com");
    usuario.setPassword("contrasena1");

    assertThrows(ContrasenaInvalida.class, () -> this.servicioLogin.registrar(usuario));
    verify(this.repositorioUsuarioMock, times(0)).guardar(usuario);
  }

  @Test
  public void registrarUsuarioConContrasenaMuyCortaDeberiaLanzarExcepcion() {
    Usuario usuario = new Usuario();
    usuario.setEmail("test@test.com");
    usuario.setPassword("Pass1");

    assertThrows(ContrasenaInvalida.class, () -> this.servicioLogin.registrar(usuario));
    verify(this.repositorioUsuarioMock, times(0)).guardar(usuario);
  }
}
