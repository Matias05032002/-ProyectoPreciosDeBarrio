package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.RolInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    usuarioEsperado.setPassword(
      new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(password)
    );
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
    usuario.setRol("USER");
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
    usuario.setRol("USER");
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
    usuario.setRol("USER");
    assertThrows(EmailInvalido.class, () -> this.servicioLogin.registrar(usuario));
    verify(this.repositorioUsuarioMock, times(0)).guardar(usuario);
  }

  @Test
  public void registrarUsuarioConContrasenaSinMayusculaDeberiaLanzarExcepcion() {
    Usuario usuario = new Usuario();
    usuario.setEmail("test@test.com");
    usuario.setPassword("contrasena1");
    usuario.setRol("USER");
    assertThrows(ContrasenaInvalida.class, () -> this.servicioLogin.registrar(usuario));
    verify(this.repositorioUsuarioMock, times(0)).guardar(usuario);
  }

  @Test
  public void registrarUsuarioConContrasenaMuyCortaDeberiaLanzarExcepcion() {
    Usuario usuario = new Usuario();
    usuario.setEmail("test@test.com");
    usuario.setPassword("Pass1");
    usuario.setRol("USER");
    assertThrows(ContrasenaInvalida.class, () -> this.servicioLogin.registrar(usuario));
    verify(this.repositorioUsuarioMock, times(0)).guardar(usuario);
  }

  @Test
  public void registrarUsuarioConRolComercioDeberiaGuardarRolComercio()
    throws UsuarioExistente, ContrasenaInvalida, EmailInvalido {
    // preparacion
    Usuario usuario = new Usuario();
    usuario.setEmail("comercio@test.com");
    usuario.setPassword("Contrasena1");
    usuario.setRol("COMERCIO");
    when(this.repositorioUsuarioMock.buscar(usuario.getEmail())).thenReturn(null);

    // ejecucion
    this.servicioLogin.registrar(usuario);

    // validacion
    assertThat(usuario.getRol(), equalTo("COMERCIO"));
    verify(this.repositorioUsuarioMock, times(1)).guardar(usuario);
  }

  @Test
  public void registrarUsuarioSinRolDeberiaLanzarExcepcion() {
    Usuario usuario = new Usuario();
    usuario.setEmail("vecino@test.com");
    usuario.setPassword("Contrasena1");
    when(this.repositorioUsuarioMock.buscar(usuario.getEmail())).thenReturn(null);

    assertThrows(RolInvalido.class, () -> this.servicioLogin.registrar(usuario));
    verify(this.repositorioUsuarioMock, times(0)).guardar(usuario);
  }

  @Test
  public void consultarUsuarioInactivoDeberiaRetornarNull() {
    // preparacion
    String email = "inactivo@test.com";
    String password = "Password1";
    Usuario usuarioInactivo = new Usuario();
    usuarioInactivo.setEmail(email);
    usuarioInactivo.setPassword(
      new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(password)
    );
    // no se llama a activar(), queda activo = false
    when(this.repositorioUsuarioMock.buscar(email)).thenReturn(usuarioInactivo);

    // ejecucion
    Usuario resultado = this.servicioLogin.consultarUsuario(email, password);

    // validacion
    assertThat(resultado, equalTo(null));
  }

  @Test
  void generarHash() {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    System.out.println("test: " + encoder.encode("test"));
    System.out.println("TestComercio1: " + encoder.encode("TestComercio1"));
  }

  @Test
  public void buscarPorIdDeberiaRetornarUsuarioEncontrado() {
    // Preparación
    Long id = 1L;
    Usuario usuario = new Usuario();
    usuario.setEmail("test@test.com");

    when(this.repositorioUsuarioMock.buscarPorId(id)).thenReturn(usuario);

    // Ejecución
    Usuario resultado = this.servicioLogin.buscarPorId(id);

    // Validación
    assertThat(resultado, equalTo(usuario));
    verify(this.repositorioUsuarioMock, times(1)).buscarPorId(id);
  }

  @Test
  public void actualizarEmailConEmailValidoDeberiaActualizarUsuario() {
    // Preparación
    Long id = 1L;
    Usuario usuario = new Usuario();
    usuario.setEmail("anterior@test.com");
    usuario.setId(id);

    when(this.repositorioUsuarioMock.buscarPorId(id)).thenReturn(usuario);
    when(this.repositorioUsuarioMock.buscar("nuevo@test.com")).thenReturn(null);

    // Ejecución
    boolean resultado = this.servicioLogin.actualizarEmail(id, "nuevo@test.com");

    // Validación
    assertThat(resultado, equalTo(true));
    assertThat(usuario.getEmail(), equalTo("nuevo@test.com"));
    verify(this.repositorioUsuarioMock, times(1)).modificar(usuario);
  }

  @Test
  public void actualizarEmailConEmailInvalidoDeberiaRetornarFalse() {
    // Ejecución
    boolean resultado = this.servicioLogin.actualizarEmail(1L, "email-invalido");

    // Validación
    assertThat(resultado, equalTo(false));
    verify(this.repositorioUsuarioMock, never()).modificar(any(Usuario.class));
  }

  @Test
  public void actualizarEmailSiYaExisteDeberiaRetornarFalse() {
    // Preparación
    Long id = 1L;
    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setEmail("anterior@test.com");

    Usuario otroUsuario = new Usuario();
    otroUsuario.setId(2L);
    otroUsuario.setEmail("ocupado@test.com");

    when(this.repositorioUsuarioMock.buscarPorId(id)).thenReturn(usuario);
    when(this.repositorioUsuarioMock.buscar("ocupado@test.com")).thenReturn(otroUsuario);

    // Ejecución
    boolean resultado = this.servicioLogin.actualizarEmail(id, "ocupado@test.com");

    // Validación
    assertThat(resultado, equalTo(false));
    verify(this.repositorioUsuarioMock, never()).modificar(any(Usuario.class));
  }

  @Test
  public void cambiarContrasenaConDatosValidosDeberiaActualizarHash() {
    // Preparación
    Long id = 1L;
    String contrasenaActual = "Contrasena1";
    String contrasenaNueva = "NuevaClave1";

    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setPassword(new BCryptPasswordEncoder().encode(contrasenaActual));

    when(this.repositorioUsuarioMock.buscarPorId(id)).thenReturn(usuario);

    // Ejecución
    boolean resultado = this.servicioLogin.cambiarContrasena(id, contrasenaActual, contrasenaNueva);

    // Validación
    assertThat(resultado, equalTo(true));
    assertThat(
      new BCryptPasswordEncoder().matches(contrasenaNueva, usuario.getPassword()),
      equalTo(true)
    );
    verify(this.repositorioUsuarioMock, times(1)).modificar(usuario);
  }

  @Test
  public void cambiarContrasenaConContrasenaActualIncorrectaDeberiaRetornarFalse() {
    // Preparación
    Long id = 1L;
    Usuario usuario = new Usuario();
    usuario.setPassword(new BCryptPasswordEncoder().encode("Contrasena1"));

    when(this.repositorioUsuarioMock.buscarPorId(id)).thenReturn(usuario);

    // Ejecución
    boolean resultado = this.servicioLogin.cambiarContrasena(id, "Incorrecta1", "NuevaClave1");

    // Validación
    assertThat(resultado, equalTo(false));
    verify(this.repositorioUsuarioMock, never()).modificar(any(Usuario.class));
  }

  @Test
  public void cambiarContrasenaConContrasenaNuevaInvalidaDeberiaRetornarFalse() {
    // Preparación
    Long id = 1L;
    Usuario usuario = new Usuario();
    usuario.setPassword(new BCryptPasswordEncoder().encode("Contrasena1"));

    when(this.repositorioUsuarioMock.buscarPorId(id)).thenReturn(usuario);

    // Ejecución: no contiene mayúscula
    boolean resultado = this.servicioLogin.cambiarContrasena(id, "Contrasena1", "contrasena2");

    // Validación
    assertThat(resultado, equalTo(false));
    verify(this.repositorioUsuarioMock, never()).modificar(any(Usuario.class));
  }
}
