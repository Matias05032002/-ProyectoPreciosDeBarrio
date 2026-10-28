package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import com.tallerwebi.punta_a_punta.vistas.VistaNuevoUsuario;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaLoginE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaLogin vistaLogin;

  @BeforeAll
  static void abrirNavegador() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch();
    //browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(500));
  }

  @AfterAll
  static void cerrarNavegador() {
    playwright.close();
  }

  @BeforeEach
  void crearContextoYPagina() {
    ReiniciarDB.limpiarBaseDeDatos();

    context = browser.newContext();
    Page page = context.newPage();
    vistaLogin = new VistaLogin(page);
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void deberiaDarUnErrorAlIntentarIniciarSesionConUnUsuarioQueNoExiste() {
    dadoQueElUsuarioCargaSusDatosDeLoginCon("damian@unlam.edu.ar", "unlam");
    cuandoElUsuarioTocaElBotonDeLogin();
    entoncesDeberiaVerUnMensajeDeError();
  }

  @Test
  void deberiaNavegarAlHomeSiElUsuarioExiste() throws MalformedURLException {
    dadoQueElUsuarioCargaSusDatosDeLoginCon("test@unlam.edu.ar", "test");
    cuandoElUsuarioTocaElBotonDeLogin();
    entoncesDeberiaSerRedirigidoALaVistaDeHome();
  }

  @Test
  void deberiaRegistrarUnUsuarioEIniciarSesionExistosamente() throws MalformedURLException {
    dadoQueElUsuarioNavegaALaVistaDeRegistro();
    dadoQueElUsuarioSeRegistraCon("juan@unlam.edu.ar", "Juan1234");
    dadoQueElUsuarioEstaEnLaVistaDeLogin();
    dadoQueElUsuarioCargaSusDatosDeLoginCon("juan@unlam.edu.ar", "Juan1234");
    cuandoElUsuarioTocaElBotonDeLogin();
    entoncesDeberiaSerRedirigidoALaVistaDeHome();
  }

  @Test
  void deberiaRegistrarUnComercioYVerHomeDeComercio() throws MalformedURLException {
    dadoQueElUsuarioNavegaALaVistaDeRegistro();
    dadoQueElUsuarioSeRegistraComoComercio("comercio@unlam.edu.ar", "Contrasena1");
    dadoQueElUsuarioEstaEnLaVistaDeLogin();
    dadoQueElUsuarioCargaSusDatosDeLoginCon("comercio@unlam.edu.ar", "Contrasena1");
    cuandoElUsuarioTocaElBotonDeLogin();
    entoncesDeberiaVerElHomeDeComercios();
  }

  @Test
  void deberiaVolverAlFormularioConErrorSiEmailYaExisteAlRegistrarVecino() {
    dadoQueElUsuarioNavegaALaVistaDeRegistro();
    dadoQueElUsuarioSeRegistraCon("duplicado@unlam.edu.ar", "Password1");
    // segundo intento con mismo email
    dadoQueElUsuarioNavegaALaVistaDeRegistro();
    dadoQueElUsuarioSeRegistraCon("duplicado@unlam.edu.ar", "Password1");

    VistaNuevoUsuario vista = new VistaNuevoUsuario(context.pages().get(0));
    String error = vista.obtenerMensajeDeError();
    assertThat(error, org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyOrNullString()));
  }

  @Test
  void deberiaVolverAlFormComercioConErrorSiDireccionYLocalidadYaExisten()
    throws MalformedURLException {
    // Registrar primer comercio
    dadoQueElUsuarioNavegaALaVistaDeRegistro();
    dadoQueElUsuarioSeRegistraComoComercio("comercio1@unlam.edu.ar", "Contrasena1");
    // Intentar registrar otro con misma dirección+localidad (Rivadavia 999, La Matanza)
    dadoQueElUsuarioNavegaALaVistaDeRegistro();
    dadoQueElUsuarioSeRegistraComoComercio("comercio2@unlam.edu.ar", "Contrasena2");

    // El form de comercio queda visible con el error gracias al script switchTab
    Page page = context.pages().get(0);
    String error = page.locator("#form-comercio .error-msg").textContent();
    assertThat(error, org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyOrNullString()));
  }

  private void dadoQueElUsuarioSeRegistraComoComercioCompleto(
    String email,
    String clave,
    String nombre,
    String tipo,
    String direccion,
    String localidad
  ) {
    VistaNuevoUsuario vistaNuevoUsuario = new VistaNuevoUsuario(context.pages().get(0));
    vistaNuevoUsuario.seleccionarRol("Comercio");
    vistaNuevoUsuario.escribirEmailComercio(email);
    vistaNuevoUsuario.escribirClaveComercio(clave);
    // nombre va al campo nombre-comercio, reutilizamos el id correcto:
    vistaNuevoUsuario.page.fill("#nombre-comercio", nombre);
    vistaNuevoUsuario.seleccionarTipoComercio(tipo);
    vistaNuevoUsuario.escribirDireccion(direccion);
    vistaNuevoUsuario.seleccionarLocalidadComercio(localidad);
    vistaNuevoUsuario.darClickEnRegistrarmeComercio();
  }

  private void dadoQueElUsuarioEstaEnLaVistaDeLogin() {
    vistaLogin = new VistaLogin(context.pages().get(0));
  }

  private void cuandoElUsuarioTocaElBotonDeLogin() {
    vistaLogin.darClickEnIniciarSesion();
  }

  private void entoncesDeberiaSerRedirigidoALaVistaDeHome() throws MalformedURLException {
    URL url = vistaLogin.obtenerURLActual();
    assertThat(url.getPath(), matchesPattern("^/spring/home(?:;jsessionid=[^/\\s]+)?$"));
  }

  private void entoncesDeberiaVerUnMensajeDeError() {
    String texto = vistaLogin.obtenerMensajeDeError();
    assertThat(texto, equalToIgnoringCase("Usuario o clave incorrecta"));
  }

  private void dadoQueElUsuarioCargaSusDatosDeLoginCon(String email, String clave) {
    vistaLogin.escribirEMAIL(email);
    vistaLogin.escribirClave(clave);
  }

  private void dadoQueElUsuarioNavegaALaVistaDeRegistro() {
    vistaLogin = new VistaLogin(context.pages().get(0));
    vistaLogin.darClickEnRegistrarse();
  }

  private void dadoQueElUsuarioSeRegistraCon(String email, String clave) {
    VistaNuevoUsuario vistaNuevoUsuario = new VistaNuevoUsuario(context.pages().get(0));
    vistaNuevoUsuario.page.fill("#nombre", "Juan");
    vistaNuevoUsuario.page.fill("#apellido", "Test");
    vistaNuevoUsuario.page.selectOption("#localidad-u", "La Matanza");
    vistaNuevoUsuario.escribirEMAIL(email);
    vistaNuevoUsuario.escribirClave(clave);
    vistaNuevoUsuario.darClickEnRegistrarme();
  }

  private void dadoQueElUsuarioSeRegistraComoComercio(String email, String clave) {
    VistaNuevoUsuario vistaNuevoUsuario = new VistaNuevoUsuario(context.pages().get(0));
    vistaNuevoUsuario.seleccionarRol("Comercio");
    vistaNuevoUsuario.page.fill("#nombre-comercio", "Comercio Test");
    vistaNuevoUsuario.seleccionarTipoComercio("Almacen");
    vistaNuevoUsuario.escribirDireccion("Rivadavia 999");
    vistaNuevoUsuario.seleccionarLocalidadComercio("La Matanza");
    vistaNuevoUsuario.page.fill("#email-c", email);
    vistaNuevoUsuario.page.fill("#password-c", clave);
    vistaNuevoUsuario.darClickEnRegistrarmeComercio();
  }

  private void entoncesDeberiaVerElHomeDeComercios() {
    Page page = context.pages().get(0);
    assertThat(page.textContent("body"), org.hamcrest.Matchers.containsString("Mi comercio"));
  }
}
