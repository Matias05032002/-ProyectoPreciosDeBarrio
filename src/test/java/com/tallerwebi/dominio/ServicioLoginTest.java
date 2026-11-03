package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.DatosInvalidos;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.RolInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import java.time.LocalDate;
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
    usuario.setNombre("Matias");
    usuario.setApellido("Llanos");
    usuario.setLocalidad("Pilar");
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
    usuario.setNombre("Almacen Don Pepe");
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

  // --- validaciones del registro ---

  private Usuario vecinoValido() {
    Usuario usuario = new Usuario();
    usuario.setEmail("vecino@test.com");
    usuario.setPassword("Contrasena1");
    usuario.setRol("USER");
    usuario.setNombre("Matias");
    usuario.setApellido("Llanos");
    usuario.setLocalidad("Pilar");
    return usuario;
  }

  private DatosInvalidos registrarYEsperarError(Usuario usuario) {
    when(this.repositorioUsuarioMock.buscar(usuario.getEmail())).thenReturn(null);
    DatosInvalidos error = assertThrows(
      DatosInvalidos.class,
      () -> this.servicioLogin.registrar(usuario)
    );
    verify(this.repositorioUsuarioMock, never()).guardar(any(Usuario.class));
    return error;
  }

  @Test
  public void registrarVecinoSinNombreDeberiaLanzarDatosInvalidos() {
    Usuario usuario = vecinoValido();
    usuario.setNombre("   ");

    assertThat(registrarYEsperarError(usuario).getMessage(), equalTo("El nombre es obligatorio"));
  }

  @Test
  public void registrarVecinoSinApellidoDeberiaLanzarDatosInvalidos() {
    Usuario usuario = vecinoValido();
    usuario.setApellido(null);

    assertThat(registrarYEsperarError(usuario).getMessage(), equalTo("El apellido es obligatorio"));
  }

  @Test
  public void registrarVecinoConNombreMuyLargoDeberiaLanzarDatosInvalidos() {
    Usuario usuario = vecinoValido();
    usuario.setNombre("a".repeat(101));

    assertThat(
      registrarYEsperarError(usuario).getMessage(),
      equalTo("El nombre no puede superar los 100 caracteres")
    );
  }

  @Test
  public void registrarVecinoConLocalidadInventadaDeberiaLanzarDatosInvalidos() {
    Usuario usuario = vecinoValido();
    usuario.setLocalidad("Narnia");

    assertThat(
      registrarYEsperarError(usuario).getMessage(),
      equalTo("Elegí una localidad de la lista")
    );
  }

  @Test
  public void registrarVecinoSinLocalidadDeberiaLanzarDatosInvalidos() {
    Usuario usuario = vecinoValido();
    usuario.setLocalidad(null);

    assertThat(
      registrarYEsperarError(usuario).getMessage(),
      equalTo("Elegí una localidad de la lista")
    );
  }

  @Test
  public void registrarComercioSinNombreDeberiaLanzarDatosInvalidos() {
    Usuario usuario = new Usuario();
    usuario.setEmail("comercio@test.com");
    usuario.setPassword("Contrasena1");
    usuario.setRol("COMERCIO");
    usuario.setNombre("");

    assertThat(registrarYEsperarError(usuario).getMessage(), equalTo("El nombre es obligatorio"));
  }

  @Test
  public void registrarVecinoDeberiaGuardarNombreYApellidoSinEspacios() throws Exception {
    Usuario usuario = vecinoValido();
    usuario.setNombre("  Matias ");
    usuario.setApellido(" Llanos  ");
    when(this.repositorioUsuarioMock.buscar(usuario.getEmail())).thenReturn(null);

    this.servicioLogin.registrar(usuario);

    assertThat(usuario.getNombre(), equalTo("Matias"));
    assertThat(usuario.getApellido(), equalTo("Llanos"));
    verify(this.repositorioUsuarioMock).guardar(usuario);
  }

  // --- validaciones del perfil ---

  private Usuario usuarioExistente() {
    Usuario usuario = new Usuario();
    usuario.setNombre("Matias");
    usuario.setApellido("Llanos");
    usuario.setFechaNacimiento(LocalDate.of(2002, 3, 5));
    when(this.repositorioUsuarioMock.buscarPorId(1L)).thenReturn(usuario);
    return usuario;
  }

  @Test
  public void actualizarDatosPersonalesConNombreVacioDeberiaLanzarDatosInvalidos() {
    Usuario usuario = usuarioExistente();

    assertThrows(
      DatosInvalidos.class,
      () -> this.servicioLogin.actualizarDatosPersonales(1L, "  ", "Llanos", null)
    );

    assertThat(usuario.getNombre(), equalTo("Matias"));
    verify(this.repositorioUsuarioMock, never()).modificar(any(Usuario.class));
  }

  @Test
  public void actualizarDatosPersonalesConApellidoVacioDeberiaLanzarDatosInvalidos() {
    usuarioExistente();

    assertThrows(
      DatosInvalidos.class,
      () -> this.servicioLogin.actualizarDatosPersonales(1L, "Matias", "", null)
    );
    verify(this.repositorioUsuarioMock, never()).modificar(any(Usuario.class));
  }

  @Test
  public void actualizarDatosPersonalesConFechaFuturaDeberiaLanzarDatosInvalidos() {
    usuarioExistente();
    LocalDate futura = LocalDate.of(2099, 1, 1);

    DatosInvalidos error = assertThrows(
      DatosInvalidos.class,
      () -> this.servicioLogin.actualizarDatosPersonales(1L, "Matias", "Llanos", futura)
    );

    assertThat(error.getMessage(), equalTo("La fecha de nacimiento no puede ser futura"));
    verify(this.repositorioUsuarioMock, never()).modificar(any(Usuario.class));
  }

  @Test
  public void actualizarDatosPersonalesConFechaMuyAntiguaDeberiaLanzarDatosInvalidos() {
    usuarioExistente();
    LocalDate antigua = LocalDate.of(1850, 1, 1);

    assertThrows(
      DatosInvalidos.class,
      () -> this.servicioLogin.actualizarDatosPersonales(1L, "Matias", "Llanos", antigua)
    );
  }

  @Test
  public void actualizarDatosPersonalesValidosDeberiaGuardarlos() {
    Usuario usuario = usuarioExistente();
    LocalDate fecha = LocalDate.of(1990, 6, 15);

    this.servicioLogin.actualizarDatosPersonales(1L, " Ana ", " Perez ", fecha);

    assertThat(usuario.getNombre(), equalTo("Ana"));
    assertThat(usuario.getApellido(), equalTo("Perez"));
    assertThat(usuario.getFechaNacimiento(), equalTo(fecha));
    verify(this.repositorioUsuarioMock).modificar(usuario);
  }

  @Test
  public void actualizarDatosPersonalesSinFechaDeberiaPermitirseYBorrarla() {
    Usuario usuario = usuarioExistente();

    this.servicioLogin.actualizarDatosPersonales(1L, "Matias", "Llanos", null);

    assertThat(usuario.getFechaNacimiento(), equalTo(null));
    verify(this.repositorioUsuarioMock).modificar(usuario);
  }

  // --- bloqueo por intentos fallidos ---

  @Test
  public void despuesDeCincoIntentosFallidosElEmailQuedaBloqueado() {
    when(this.repositorioUsuarioMock.buscar("bloqueo@test.com")).thenReturn(null);

    for (int intento = 0; intento < 5; intento++) {
      assertThat(this.servicioLogin.estaBloqueado("bloqueo@test.com"), equalTo(false));
      this.servicioLogin.consultarUsuario("bloqueo@test.com", "mal");
    }

    assertThat(this.servicioLogin.estaBloqueado("bloqueo@test.com"), equalTo(true));
    assertThat(this.servicioLogin.estaBloqueado("  BLOQUEO@test.com "), equalTo(true));
  }

  @Test
  public void unEmailSinIntentosFallidosNoEstaBloqueado() {
    assertThat(this.servicioLogin.estaBloqueado("limpio@test.com"), equalTo(false));
  }

  @Test
  public void estaBloqueadoConEmailNuloDeberiaDevolverFalse() {
    assertThat(this.servicioLogin.estaBloqueado(null), equalTo(false));
  }

  @Test
  public void consultarUsuarioConEmailNuloDeberiaDevolverNullSinErrorInterno() {
    assertThat(this.servicioLogin.consultarUsuario(null, "Contrasena1"), equalTo(null));
    verifyNoInteractions(this.repositorioUsuarioMock);
  }

  @Test
  public void restablecerContrasenaGuardaLaNuevaEncriptada() throws ContrasenaInvalida {
    Usuario usuario = new Usuario();
    usuario.setEmail("vecina@test.com");
    usuario.setPassword("viejo");
    when(this.repositorioUsuarioMock.buscarPorId(1L)).thenReturn(usuario);

    this.servicioLogin.restablecerContrasena(1L, "NuevaClave1");

    assertThat(
      new BCryptPasswordEncoder().matches("NuevaClave1", usuario.getPassword()),
      equalTo(true)
    );
    verify(this.repositorioUsuarioMock).modificar(usuario);
  }

  @Test
  public void restablecerContrasenaDebilNoCambiaNada() {
    Usuario usuario = new Usuario();
    usuario.setPassword("viejo");
    when(this.repositorioUsuarioMock.buscarPorId(1L)).thenReturn(usuario);

    assertThrows(
      ContrasenaInvalida.class,
      () -> this.servicioLogin.restablecerContrasena(1L, "sinmayuscula1")
    );
    assertThat(usuario.getPassword(), equalTo("viejo"));
    verify(this.repositorioUsuarioMock, never()).modificar(any());
  }

  @Test
  public void restablecerContrasenaDeUnUsuarioInexistenteLanzaExcepcion() {
    when(this.repositorioUsuarioMock.buscarPorId(99L)).thenReturn(null);

    assertThrows(
      UsuarioNoEncontrado.class,
      () -> this.servicioLogin.restablecerContrasena(99L, "NuevaClave1")
    );
  }

  @Test
  public void restablecerContrasenaLevantaElBloqueoPorIntentosFallidos() throws ContrasenaInvalida {
    Usuario usuario = new Usuario();
    usuario.setEmail("bloqueada@test.com");
    usuario.setPassword(new BCryptPasswordEncoder().encode("Original1"));
    usuario.activar();
    when(this.repositorioUsuarioMock.buscar("bloqueada@test.com")).thenReturn(usuario);
    when(this.repositorioUsuarioMock.buscarPorId(1L)).thenReturn(usuario);
    for (int intento = 0; intento < 5; intento++) {
      this.servicioLogin.consultarUsuario("bloqueada@test.com", "Equivocada1");
    }
    assertThat(this.servicioLogin.estaBloqueado("bloqueada@test.com"), equalTo(true));

    this.servicioLogin.restablecerContrasena(1L, "NuevaClave1");

    assertThat(this.servicioLogin.estaBloqueado("bloqueada@test.com"), equalTo(false));
    assertThat(
      this.servicioLogin.consultarUsuario("bloqueada@test.com", "NuevaClave1"),
      equalTo(usuario)
    );
  }
}
