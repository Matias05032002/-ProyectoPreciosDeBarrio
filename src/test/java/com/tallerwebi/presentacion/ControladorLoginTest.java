package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Comercio.ServicioRegistroComercio;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.ComercioYaRegistrado;
import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.DatosDeComercioInvalidos;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

public class ControladorLoginTest {

  private ControladorLogin controladorLogin;
  private Usuario usuarioMock;
  private DatosLogin datosLoginMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private ServicioLogin servicioLoginMock;
  private ServicioRegistroComercio servicioRegistroComercioMock;
  private RedirectAttributes redirectAttrs;

  @BeforeEach
  public void init() {
    datosLoginMock = new DatosLogin("dami@unlam.com", "123");
    usuarioMock = mock(Usuario.class);
    when(usuarioMock.getEmail()).thenReturn("dami@unlam.com");
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    servicioLoginMock = mock(ServicioLogin.class);
    servicioRegistroComercioMock = mock(ServicioRegistroComercio.class);
    controladorLogin = new ControladorLogin(servicioLoginMock, servicioRegistroComercioMock);
    redirectAttrs = new RedirectAttributesModelMap();
  }

  @Test
  public void loginConUsuarioYPasswordIncorrectosDeberiaLlevarALoginNuevamente() {
    when(servicioLoginMock.consultarUsuario(anyString(), anyString())).thenReturn(null);

    ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Usuario o clave incorrecta")
    );
    verify(sessionMock, times(0)).setAttribute("ROL", "ADMIN");
  }

  @Test
  public void loginConUsuarioYPasswordCorrectosDeberiaLLevarAHome() {
    Usuario usuarioEncontradoMock = mock(Usuario.class);
    when(usuarioEncontradoMock.getRol()).thenReturn("ADMIN");
    when(requestMock.getSession()).thenReturn(sessionMock);
    when(servicioLoginMock.consultarUsuario(anyString(), anyString()))
      .thenReturn(usuarioEncontradoMock);

    ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/home"));
    verify(sessionMock, times(1)).setAttribute("ROL", usuarioEncontradoMock.getRol());
  }

  @Test
  public void registrameSiUsuarioNoExisteDeberiaCrearUsuarioYVolverAlLogin()
    throws UsuarioExistente, ContrasenaInvalida, EmailInvalido {
    ModelAndView modelAndView = controladorLogin.registrarme(
      "dami@unlam.com",
      "Password1",
      "Dami",
      "Lopez",
      "1995-04-12",
      "La Matanza",
      requestMock,
      redirectAttrs
    );

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void registrarmeDeberiaGuardarLaLocalidadElegidaEnElUsuario()
    throws UsuarioExistente, ContrasenaInvalida, EmailInvalido {
    controladorLogin.registrarme(
      "dami@unlam.com",
      "Password1",
      "Dami",
      "Lopez",
      "1995-04-12",
      "La Matanza",
      requestMock,
      redirectAttrs
    );

    ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
    verify(servicioLoginMock).registrar(captor.capture());
    assertThat(captor.getValue().getLocalidad(), equalTo("La Matanza"));
  }

  @Test
  public void registrarmeDeberiaGuardarLaFechaDeNacimientoDelUsuario()
    throws UsuarioExistente, ContrasenaInvalida, EmailInvalido {
    controladorLogin.registrarme(
      "dami@unlam.com",
      "Password1",
      "Dami",
      "Lopez",
      "1995-04-12",
      "La Matanza",
      requestMock,
      redirectAttrs
    );

    ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
    verify(servicioLoginMock).registrar(captor.capture());
    assertThat(
      captor.getValue().getFechaNacimiento(),
      equalTo(java.time.LocalDate.of(1995, 4, 12))
    );
  }

  @Test
  public void registrarmeConFechaConFormatoRotoDeberiaVolverAlFormularioConErrorSinRegistrar()
    throws UsuarioExistente, ContrasenaInvalida, EmailInvalido {
    ModelAndView modelAndView = controladorLogin.registrarme(
      "dami@unlam.com",
      "Password1",
      "Dami",
      "Lopez",
      "no-es-una-fecha",
      "La Matanza",
      requestMock,
      redirectAttrs
    );

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/nuevo-usuario"));
    assertThat(
      redirectAttrs.getFlashAttributes().get("error"),
      equalTo("La fecha de nacimiento no es válida")
    );
    verify(servicioLoginMock, never()).registrar(any(Usuario.class));
  }

  @Test
  public void registrarmeSiUsuarioExisteDeberiaVolverAFormularioYMostrarError()
    throws UsuarioExistente, ContrasenaInvalida, EmailInvalido {
    doThrow(new UsuarioExistente()).when(servicioLoginMock).registrar(any(Usuario.class));

    ModelAndView modelAndView = controladorLogin.registrarme(
      "dami@unlam.com",
      "Password1",
      "Dami",
      "Lopez",
      "1995-04-12",
      "La Matanza",
      requestMock,
      redirectAttrs
    );

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/nuevo-usuario"));
  }

  @Test
  public void errorEnRegistrarmeDeberiaVolverAFormularioYMostrarError()
    throws UsuarioExistente, ContrasenaInvalida, EmailInvalido {
    doThrow(new RuntimeException("error")).when(servicioLoginMock).registrar(any(Usuario.class));

    ModelAndView modelAndView = controladorLogin.registrarme(
      "dami@unlam.com",
      "Password1",
      "Dami",
      "Lopez",
      "1995-04-12",
      "La Matanza",
      requestMock,
      redirectAttrs
    );

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/nuevo-usuario"));
  }

  // --- Tests para registrarmeComercio ---

  private ModelAndView registrarComercio(String direccion, String localidad) {
    return controladorLogin.registrarmeComercio(
      "comercio@unlam.com",
      "Password1",
      "Almacén Don José",
      "Almacen",
      direccion,
      localidad,
      requestMock,
      redirectAttrs
    );
  }

  @Test
  public void registrarmeComercioConDatosValidosDeberiaPedirleAlServicioElRegistroYVolverAlLogin()
    throws Exception {
    ModelAndView modelAndView = registrarComercio("Rivadavia 1234", "La Matanza");

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
    verify(servicioRegistroComercioMock, times(1))
      .registrar(
        "comercio@unlam.com",
        "Password1",
        "Almacén Don José",
        "Almacen",
        "Rivadavia 1234",
        "La Matanza"
      );
  }

  @Test
  public void registrarmeComercioCuandoElComercioYaTieneDuenoDeberiaVolverAlFormularioConError()
    throws Exception {
    doThrow(new ComercioYaRegistrado())
      .when(servicioRegistroComercioMock)
      .registrar(any(), any(), any(), any(), any(), any());

    ModelAndView modelAndView = registrarComercio("Rivadavia 1234", "La Matanza");

    assertThat(
      modelAndView.getViewName(),
      equalTo(
        "redirect:/nuevo-usuario?errorComercio=Ya+existe+un+comercio+en+esa+direcci%C3%B3n+y+localidad"
      )
    );
  }

  @Test
  public void registrarmeComercioCuandoEmailYaExisteDeberiaVolverAlFormularioConError()
    throws Exception {
    doThrow(new UsuarioExistente())
      .when(servicioRegistroComercioMock)
      .registrar(any(), any(), any(), any(), any(), any());

    ModelAndView modelAndView = registrarComercio("Rivadavia 1234", "La Matanza");

    assertThat(
      modelAndView.getViewName(),
      org.hamcrest.Matchers.startsWith("redirect:/nuevo-usuario?errorComercio=")
    );
  }

  @Test
  public void registrarmeComercioConDatosInvalidosDeberiaMostrarElMotivoDelServicio()
    throws Exception {
    doThrow(new DatosDeComercioInvalidos("Elegí una localidad de la lista"))
      .when(servicioRegistroComercioMock)
      .registrar(any(), any(), any(), any(), any(), any());

    ModelAndView modelAndView = registrarComercio("Rivadavia 1234", "Narnia");

    assertThat(
      modelAndView.getViewName(),
      equalTo("redirect:/nuevo-usuario?errorComercio=Eleg%C3%AD+una+localidad+de+la+lista")
    );
  }

  // --- Otros tests ---

  @Test
  public void irALoginDeberiaRetornarVistaLoginConDatosLogin() {
    ModelAndView modelAndView = controladorLogin.irALogin();

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
    assertThat(modelAndView.getModel().get("datosLogin"), instanceOf(DatosLogin.class));
  }

  @Test
  public void nuevoUsuarioDeberiaRetornarVistaNuevoUsuarioConUsuarioVacio() {
    org.springframework.ui.ExtendedModelMap model = new org.springframework.ui.ExtendedModelMap();
    ModelAndView modelAndView = controladorLogin.nuevoUsuario(null, null);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
    assertThat(modelAndView.getModel().get("usuario"), instanceOf(Usuario.class));
  }

  @Test
  public void irAHomeDeberiaRetornarVistaHome() {
    ModelAndView modelAndView = controladorLogin.irAHome();

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("home"));
  }

  @Test
  public void inicioDeberiaRedirigirALogin() {
    ModelAndView modelAndView = controladorLogin.inicio();

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void logoutDeberiaInvalidarSesionYRedirigirAlLogin() {
    when(requestMock.getSession()).thenReturn(sessionMock);

    ModelAndView modelAndView = controladorLogin.logout(requestMock);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
    verify(sessionMock, times(1)).invalidate();
  }

  @Test
  public void loginFallidoConEmailBloqueadoDeberiaAvisarQueEsperePorLosIntentos() {
    when(servicioLoginMock.consultarUsuario(any(), any())).thenReturn(null);
    when(servicioLoginMock.estaBloqueado("dami@unlam.com")).thenReturn(true);

    ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      org.hamcrest.Matchers.containsString("Demasiados intentos")
    );
  }

  @Test
  public void loginFallidoSinBloqueoDeberiaSeguirDiciendoUsuarioOClaveIncorrecta() {
    when(servicioLoginMock.consultarUsuario(any(), any())).thenReturn(null);
    when(servicioLoginMock.estaBloqueado("dami@unlam.com")).thenReturn(false);

    ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Usuario o clave incorrecta")
    );
  }

  @Test
  public void registrarmeExitosoDeberiaAvisarQueLaCuentaSeCreo() {
    controladorLogin.registrarme(
      "dami@unlam.com",
      "Password1",
      "Dami",
      "Lopez",
      "1995-04-12",
      "La Matanza",
      requestMock,
      redirectAttrs
    );

    assertThat(
      redirectAttrs.getFlashAttributes().get("registroExitoso"),
      equalTo("¡Tu cuenta se creó! Ya podés ingresar.")
    );
  }

  @Test
  public void registrarmeFallidoNoDeberiaAvisarQueLaCuentaSeCreo() throws Exception {
    doThrow(new UsuarioExistente()).when(servicioLoginMock).registrar(any(Usuario.class));

    controladorLogin.registrarme(
      "dami@unlam.com",
      "Password1",
      "Dami",
      "Lopez",
      "1995-04-12",
      "La Matanza",
      requestMock,
      redirectAttrs
    );

    assertThat(redirectAttrs.getFlashAttributes().containsKey("registroExitoso"), equalTo(false));
  }

  @Test
  public void registrarmeComercioExitosoDeberiaAvisarQueLaCuentaSeCreo() {
    registrarComercio("Rivadavia 1234", "La Matanza");

    assertThat(
      redirectAttrs.getFlashAttributes().get("registroExitoso"),
      equalTo("¡Tu cuenta se creó! Ya podés ingresar.")
    );
  }
}
