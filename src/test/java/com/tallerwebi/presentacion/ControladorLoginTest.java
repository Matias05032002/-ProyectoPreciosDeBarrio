package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
  private ServicioComercio servicioComercioMock;
  private RedirectAttributes redirectAttrs;

  @BeforeEach
  public void init() {
    datosLoginMock = new DatosLogin("dami@unlam.com", "123");
    usuarioMock = mock(Usuario.class);
    when(usuarioMock.getEmail()).thenReturn("dami@unlam.com");
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    servicioLoginMock = mock(ServicioLogin.class);
    servicioComercioMock = mock(ServicioComercio.class);
    controladorLogin = new ControladorLogin(servicioLoginMock, servicioComercioMock);
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
      "La Matanza",
      requestMock,
      redirectAttrs
    );

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
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
      "La Matanza",
      requestMock,
      redirectAttrs
    );

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/nuevo-usuario"));
  }

  // --- Tests para registrarmeComercio ---

  @Test
  public void registrarmeComercioSiDatosNuevosDeberiaCrearUsuarioYComercioYVolverAlLogin()
    throws UsuarioExistente, ContrasenaInvalida, EmailInvalido {
    when(servicioComercioMock.buscarPorDireccionYLocalidad(anyString(), anyString()))
      .thenReturn(null);
    Usuario usuarioCreado = mock(Usuario.class);
    when(usuarioCreado.getId()).thenReturn(1L);
    when(servicioLoginMock.buscarPorEmail(anyString())).thenReturn(usuarioCreado);

    ModelAndView modelAndView = controladorLogin.registrarmeComercio(
      "comercio@unlam.com",
      "Password1",
      "Almacén Don José",
      "Almacen",
      "Rivadavia 1234",
      "La Matanza",
      requestMock,
      redirectAttrs
    );

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
    verify(servicioLoginMock, times(1)).registrar(any(Usuario.class));
    verify(servicioComercioMock, times(1)).guardarComercio(any(Comercio.class));
  }

  @Test
  public void registrarmeComercioCuandoDireccionYLocalidadYaExistenDeberiaVolverAlFormularioConError()
    throws UsuarioExistente, ContrasenaInvalida, EmailInvalido {
    Comercio comercioExistente = mock(Comercio.class);
    when(servicioComercioMock.buscarPorDireccionYLocalidad("Rivadavia 1234", "La Matanza"))
      .thenReturn(comercioExistente);

    ModelAndView modelAndView = controladorLogin.registrarmeComercio(
      "comercio@unlam.com",
      "Password1",
      "Almacén Don José",
      "Almacen",
      "Rivadavia 1234",
      "La Matanza",
      requestMock,
      redirectAttrs
    );

    assertThat(
      modelAndView.getViewName(),
      org.hamcrest.Matchers.startsWith("redirect:/nuevo-usuario")
    );
    verify(servicioLoginMock, never()).registrar(any(Usuario.class));
    verify(servicioComercioMock, never()).guardarComercio(any(Comercio.class));
  }

  @Test
  public void registrarmeComercioCuandoEmailYaExisteDeberiaVolverAlFormularioConError()
    throws UsuarioExistente, ContrasenaInvalida, EmailInvalido {
    when(servicioComercioMock.buscarPorDireccionYLocalidad(anyString(), anyString()))
      .thenReturn(null);
    doThrow(new UsuarioExistente()).when(servicioLoginMock).registrar(any(Usuario.class));

    ModelAndView modelAndView = controladorLogin.registrarmeComercio(
      "yaexiste@unlam.com",
      "Password1",
      "Almacén Don José",
      "Almacen",
      "Rivadavia 1234",
      "La Matanza",
      requestMock,
      redirectAttrs
    );

    assertThat(
      modelAndView.getViewName(),
      org.hamcrest.Matchers.startsWith("redirect:/nuevo-usuario")
    );
    verify(servicioComercioMock, never()).guardarComercio(any(Comercio.class));
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
}
