package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;
import com.tallerwebi.punta_a_punta.vistas.VistaRecuperacion;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaRecuperacionContrasenaE2E {

  private static final String EMAIL = "test-comercio@unlam.edu.ar";
  private static final String TOKEN = "token-de-prueba-e2e";
  private static final String CLAVE_NUEVA = "ClaveNueva1";

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaRecuperacion vistaRecuperacion;

  @BeforeAll
  static void abrirNavegador() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch();
  }

  @AfterAll
  static void cerrarNavegador() {
    playwright.close();
  }

  @BeforeEach
  void crearContextoYPagina() {
    ReiniciarDB.limpiarBaseDeDatos();
    context = browser.newContext();
    vistaRecuperacion = new VistaRecuperacion(context.newPage());
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void deberiaLlegarAPedirElLinkDesdeElLogin() {
    vistaRecuperacion.irAlLogin();

    vistaRecuperacion.darClickEnOlvideMiContrasena();

    assertThat(context.pages().get(0).url(), containsString("/olvide-contrasena"));
  }

  @Test
  void deberiaMostrarElMismoMensajeSeaOnoUnaCuentaExistente() {
    // Con un email que no existe no se manda ningún mail, y la respuesta no delata nada
    vistaRecuperacion.irAPedirLink();
    vistaRecuperacion.escribirEmail("nadie@ejemplo.com");

    vistaRecuperacion.darClickEnEnviarLink();

    assertThat(vistaRecuperacion.muestraPedidoEnviado(), is(true));
  }

  @Test
  void deberiaAvisarQueUnLinkInventadoNoSirve() {
    vistaRecuperacion.abrirLink("un-link-que-nunca-se-genero");

    assertThat(vistaRecuperacion.muestraTokenInvalido(), is(true));
  }

  @Test
  void deberiaPoderPedirUnLinkNuevoDesdeUnLinkInvalido() {
    vistaRecuperacion.abrirLink("un-link-que-nunca-se-genero");

    vistaRecuperacion.darClickEnPedirOtroLink();

    assertThat(context.pages().get(0).url(), containsString("/olvide-contrasena"));
  }

  @Test
  void deberiaRestablecerLaContrasenaYPoderIngresarConLaNueva() {
    ReiniciarDB.sembrarTokenDeRecuperacion(2, TOKEN);
    vistaRecuperacion.abrirLink(TOKEN);

    vistaRecuperacion.escribirContrasenaNueva(CLAVE_NUEVA);
    vistaRecuperacion.repetirContrasena(CLAVE_NUEVA);
    vistaRecuperacion.darClickEnGuardarContrasena();

    assertThat(vistaRecuperacion.muestraAvisoDeContrasenaRestablecida(), is(true));

    vistaRecuperacion.iniciarSesion(EMAIL, CLAVE_NUEVA);
    assertThat(vistaRecuperacion.ingresoCorrectamente(), is(true));
  }

  @Test
  void laContrasenaViejaDejaDeFuncionarTrasRestablecer() {
    ReiniciarDB.sembrarTokenDeRecuperacion(2, TOKEN);
    vistaRecuperacion.abrirLink(TOKEN);
    vistaRecuperacion.escribirContrasenaNueva(CLAVE_NUEVA);
    vistaRecuperacion.repetirContrasena(CLAVE_NUEVA);
    vistaRecuperacion.darClickEnGuardarContrasena();

    vistaRecuperacion.iniciarSesion(EMAIL, "test");

    assertThat(vistaRecuperacion.muestraErrorDeLogin(), is(true));
  }

  @Test
  void elLinkSoloSirveUnaVez() {
    ReiniciarDB.sembrarTokenDeRecuperacion(2, TOKEN);
    vistaRecuperacion.abrirLink(TOKEN);
    vistaRecuperacion.escribirContrasenaNueva(CLAVE_NUEVA);
    vistaRecuperacion.repetirContrasena(CLAVE_NUEVA);
    vistaRecuperacion.darClickEnGuardarContrasena();

    vistaRecuperacion.abrirLink(TOKEN);

    assertThat(vistaRecuperacion.muestraTokenInvalido(), is(true));
  }

  @Test
  void deberiaRechazarUnaContrasenaSinMayuscula() {
    ReiniciarDB.sembrarTokenDeRecuperacion(2, TOKEN);
    vistaRecuperacion.abrirLink(TOKEN);

    vistaRecuperacion.escribirContrasenaNueva("sinmayuscula1");
    vistaRecuperacion.repetirContrasena("sinmayuscula1");
    vistaRecuperacion.darClickEnGuardarContrasenaInvalida();

    assertThat(vistaRecuperacion.muestraErrorAlRestablecer(), is(true));
  }
}
