package com.tallerwebi.dominio.Recuperacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Email.ServicioEmail;
import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.TokenDeRecuperacionInvalido;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ServicioRecuperacionContrasenaTest {

  private static final String EMAIL = "vecina@test.com";
  private static final Long USUARIO_ID = 5L;
  private static final String URL_BASE = "https://pdb.test/spring";
  private static final ZoneId ZONA = ZoneId.of("America/Argentina/Buenos_Aires");
  private static final Instant INSTANTE = Instant.parse("2026-11-03T12:00:00Z");
  private static final LocalDateTime AHORA = LocalDateTime.ofInstant(INSTANTE, ZONA);

  private RepositorioTokenRecuperacion repositorioTokenMock;
  private RepositorioUsuario repositorioUsuarioMock;
  private ServicioLogin servicioLoginMock;
  private ServicioEmail servicioEmailMock;
  private ServicioRecuperacionContrasena servicio;
  private Usuario usuario;

  @BeforeEach
  public void init() {
    repositorioTokenMock = mock(RepositorioTokenRecuperacion.class);
    repositorioUsuarioMock = mock(RepositorioUsuario.class);
    servicioLoginMock = mock(ServicioLogin.class);
    servicioEmailMock = mock(ServicioEmail.class);
    servicio = conReloj(INSTANTE);

    usuario = mock(Usuario.class);
    when(usuario.getId()).thenReturn(USUARIO_ID);
    when(usuario.getEmail()).thenReturn(EMAIL);
    when(usuario.getNombre()).thenReturn("Ana");
    when(usuario.getActivo()).thenReturn(true);
    when(repositorioUsuarioMock.buscar(EMAIL)).thenReturn(usuario);
    when(repositorioTokenMock.buscarVigentesDelUsuario(eq(USUARIO_ID), any()))
      .thenReturn(Collections.emptyList());
  }

  private ServicioRecuperacionContrasena conReloj(Instant instante) {
    return new ServicioRecuperacionContrasenaImpl(
      repositorioTokenMock,
      repositorioUsuarioMock,
      servicioLoginMock,
      servicioEmailMock,
      Clock.fixed(instante, ZONA),
      URL_BASE + "/"
    );
  }

  private String cuerpoDelMailEnviado() {
    ArgumentCaptor<String> cuerpo = ArgumentCaptor.forClass(String.class);
    verify(servicioEmailMock).enviar(eq(EMAIL), anyString(), cuerpo.capture());
    return cuerpo.getValue();
  }

  private String tokenDelMail(String cuerpo) {
    Matcher matcher = Pattern.compile("token=([A-Za-z0-9_-]+)").matcher(cuerpo);
    assertThat(matcher.find(), equalTo(true));
    return matcher.group(1);
  }

  private TokenRecuperacion tokenGuardado() {
    ArgumentCaptor<TokenRecuperacion> guardado = ArgumentCaptor.forClass(TokenRecuperacion.class);
    verify(repositorioTokenMock).guardar(guardado.capture());
    return guardado.getValue();
  }

  @Test
  public void solicitarConEmailRegistradoGuardaElTokenYMandaElLinkPorMail() {
    servicio.solicitarRecuperacion("  Vecina@Test.com ");

    String cuerpo = cuerpoDelMailEnviado();
    assertThat(cuerpo, containsString(URL_BASE + "/restablecer-contrasena?token="));
    assertThat(cuerpo, containsString("Hola Ana,"));
    assertThat(cuerpo, containsString("30 minutos"));

    TokenRecuperacion guardado = tokenGuardado();
    assertThat(guardado.getUsuarioId(), equalTo(USUARIO_ID));
    assertThat(guardado.getFechaVencimiento(), equalTo(AHORA.plusMinutes(30)));
    assertThat(guardado.getUsado(), equalTo(false));
    //Testear que el token se haya generado
  }

  @Test
  public void enLaBaseSeGuardaElHashDelTokenYNoElTokenDelMail() {
    servicio.solicitarRecuperacion(EMAIL);

    String token = tokenDelMail(cuerpoDelMailEnviado());
    TokenRecuperacion guardado = tokenGuardado();

    assertThat(token.length() >= 43, equalTo(true));
    assertThat(guardado.getTokenHash(), not(equalTo(token)));
    assertThat(guardado.getTokenHash(), matchesPattern("[0-9a-f]{64}"));
    assertThat(guardado.getTokenHash(), equalTo(ServicioRecuperacionContrasenaImpl.hashear(token))); // Validacion erronea ya que siempre da verde aunque cambiemos la logica
  }

  @Test
  public void cadaPedidoGeneraUnTokenDistinto() {
    servicio.solicitarRecuperacion(EMAIL);
    when(repositorioUsuarioMock.buscar("otra@test.com")).thenReturn(usuario);
    ArgumentCaptor<TokenRecuperacion> guardados = ArgumentCaptor.forClass(TokenRecuperacion.class);

    servicio.solicitarRecuperacion("otra@test.com");

    verify(repositorioTokenMock, times(2)).guardar(guardados.capture());
    assertThat(
      guardados.getAllValues().get(0).getTokenHash(),
      not(equalTo(guardados.getAllValues().get(1).getTokenHash()))
    );
  }

  @Test
  public void solicitarConEmailNoRegistradoNoHaceNadaNiAvisa() {
    when(repositorioUsuarioMock.buscar("nadie@test.com")).thenReturn(null);

    servicio.solicitarRecuperacion("nadie@test.com");

    verify(repositorioTokenMock, never()).guardar(any());
    verifyNoInteractions(servicioEmailMock);
  }

  @Test
  public void solicitarConCuentaInactivaNoMandaMail() {
    when(usuario.getActivo()).thenReturn(false);

    servicio.solicitarRecuperacion(EMAIL);

    verifyNoInteractions(servicioEmailMock);
  }

  @Test
  public void solicitarConEmailVacioONuloNoConsultaNada() {
    servicio.solicitarRecuperacion(null);
    servicio.solicitarRecuperacion("   ");

    verifyNoInteractions(repositorioUsuarioMock, repositorioTokenMock, servicioEmailMock);
  }

  @Test
  public void unSegundoPedidoEnMenosDeDosMinutosNoMandaOtroMail() {
    TokenRecuperacion reciente = new TokenRecuperacion(
      "hash",
      USUARIO_ID,
      AHORA.minusSeconds(30),
      AHORA.plusMinutes(29)
    );
    when(repositorioTokenMock.buscarVigentesDelUsuario(USUARIO_ID, AHORA))
      .thenReturn(List.of(reciente));

    servicio.solicitarRecuperacion(EMAIL);

    verify(repositorioTokenMock, never()).guardar(any());
    verifyNoInteractions(servicioEmailMock);
  }

  @Test
  public void unPedidoNuevoDaDeBajaLosLinksAnteriores() {
    TokenRecuperacion anterior = new TokenRecuperacion(
      "hash-viejo",
      USUARIO_ID,
      AHORA.minusMinutes(10),
      AHORA.plusMinutes(20)
    );
    when(repositorioTokenMock.buscarVigentesDelUsuario(USUARIO_ID, AHORA))
      .thenReturn(List.of(anterior));

    servicio.solicitarRecuperacion(EMAIL);

    assertThat(anterior.getUsado(), equalTo(true));
    verify(repositorioTokenMock).actualizar(anterior);
    verify(repositorioTokenMock).guardar(any());
  }

  @Test
  public void unTokenVigenteEsValido() {
    TokenRecuperacion vigente = new TokenRecuperacion(
      "h",
      USUARIO_ID,
      AHORA,
      AHORA.plusMinutes(30)
    );
    when(repositorioTokenMock.buscarPorHash(ServicioRecuperacionContrasenaImpl.hashear("abc")))
      .thenReturn(vigente);

    assertThat(servicio.esTokenValido("abc"), equalTo(true));
  }

  @Test
  public void unTokenVencidoUsadoInexistenteOVacioNoEsValido() {
    TokenRecuperacion vencido = new TokenRecuperacion(
      "h",
      USUARIO_ID,
      AHORA.minusMinutes(40),
      AHORA.minusMinutes(10)
    );
    TokenRecuperacion usado = new TokenRecuperacion("h2", USUARIO_ID, AHORA, AHORA.plusMinutes(30));
    usado.marcarUsado();
    when(repositorioTokenMock.buscarPorHash(ServicioRecuperacionContrasenaImpl.hashear("vencido")))
      .thenReturn(vencido);
    when(repositorioTokenMock.buscarPorHash(ServicioRecuperacionContrasenaImpl.hashear("usado")))
      .thenReturn(usado);

    assertThat(servicio.esTokenValido("vencido"), equalTo(false));
    assertThat(servicio.esTokenValido("usado"), equalTo(false));
    assertThat(servicio.esTokenValido("inexistente"), equalTo(false));
    assertThat(servicio.esTokenValido(null), equalTo(false));
    assertThat(servicio.esTokenValido(" "), equalTo(false));
  }

  @Test
  public void elLinkVenceALosTreintaMinutos() {
    servicio.solicitarRecuperacion(EMAIL);
    String token = tokenDelMail(cuerpoDelMailEnviado());
    TokenRecuperacion guardado = tokenGuardado();
    when(repositorioTokenMock.buscarPorHash(guardado.getTokenHash())).thenReturn(guardado);

    assertThat(conReloj(INSTANTE.plusSeconds(29 * 60)).esTokenValido(token), equalTo(true));
    assertThat(conReloj(INSTANTE.plusSeconds(30 * 60)).esTokenValido(token), equalTo(false));
  }

  @Test
  public void restablecerConTokenValidoCambiaLaContrasenaYDaDeBajaElLink()
    throws ContrasenaInvalida {
    TokenRecuperacion vigente = new TokenRecuperacion(
      "h",
      USUARIO_ID,
      AHORA,
      AHORA.plusMinutes(30)
    );
    when(repositorioTokenMock.buscarPorHash(ServicioRecuperacionContrasenaImpl.hashear("abc")))
      .thenReturn(vigente);

    servicio.restablecerContrasena("abc", "NuevaClave1", "NuevaClave1");

    verify(servicioLoginMock).restablecerContrasena(USUARIO_ID, "NuevaClave1");
    assertThat(vigente.getUsado(), equalTo(true));
    verify(repositorioTokenMock).actualizar(vigente);
  }

  @Test
  public void restablecerConTokenInvalidoLanzaExcepcionSinTocarLaContrasena()
    throws ContrasenaInvalida {
    assertThrows(
      TokenDeRecuperacionInvalido.class,
      () -> servicio.restablecerContrasena("inexistente", "NuevaClave1", "NuevaClave1")
    );
    verify(servicioLoginMock, never()).restablecerContrasena(any(), any());
  }

  @Test
  public void restablecerConContrasenasDistintasLanzaExcepcionYElLinkSigueVigente()
    throws ContrasenaInvalida {
    TokenRecuperacion vigente = new TokenRecuperacion(
      "h",
      USUARIO_ID,
      AHORA,
      AHORA.plusMinutes(30)
    );
    when(repositorioTokenMock.buscarPorHash(ServicioRecuperacionContrasenaImpl.hashear("abc")))
      .thenReturn(vigente);

    ContrasenaInvalida error = assertThrows(
      ContrasenaInvalida.class,
      () -> servicio.restablecerContrasena("abc", "NuevaClave1", "OtraClave1")
    );

    assertThat(error.getMessage(), containsString("no coinciden"));
    assertThat(vigente.getUsado(), equalTo(false));
    verify(servicioLoginMock, never()).restablecerContrasena(any(), any());
  }

  @Test
  public void siLaContrasenaNuevaEsDebilElLinkSigueVigente() throws ContrasenaInvalida {
    TokenRecuperacion vigente = new TokenRecuperacion(
      "h",
      USUARIO_ID,
      AHORA,
      AHORA.plusMinutes(30)
    );
    when(repositorioTokenMock.buscarPorHash(ServicioRecuperacionContrasenaImpl.hashear("abc")))
      .thenReturn(vigente);
    doThrow(new ContrasenaInvalida("La contraseña debe contener al menos una mayúscula"))
      .when(servicioLoginMock)
      .restablecerContrasena(USUARIO_ID, "debil1234");

    assertThrows(
      ContrasenaInvalida.class,
      () -> servicio.restablecerContrasena("abc", "debil1234", "debil1234")
    );

    assertThat(vigente.getUsado(), equalTo(false));
    verify(repositorioTokenMock, never()).actualizar(any());
  }
}
