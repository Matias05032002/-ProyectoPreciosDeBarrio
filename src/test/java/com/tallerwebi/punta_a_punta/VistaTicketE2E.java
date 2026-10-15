package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.tallerwebi.punta_a_punta.vistas.Reporte.VistaTicket;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaTicketE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaTicket vistaTicket;

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
    Page page = context.newPage();
    vistaTicket = new VistaTicket(page);
    vistaTicket.iniciarSesionYNavegar("test@unlam.edu.ar", "test");
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void deberiaRedirigirAlLoginSiNoHaySesion() {
    context.close();
    context = browser.newContext();
    Page page = context.newPage();
    vistaTicket = new VistaTicket(page);
    assertThat(vistaTicket.estaEnPaginaLogin(), is(true));
  }

  @Test
  void deberiaMostrarElFormularioDeTicket() {
    assertThat(vistaTicket.formularioEstaVisible(), is(true));
  }

  @Test
  void deberiaMostrarElInputDeFoto() {
    assertThat(vistaTicket.inputFotoEstaVisible(), is(true));
  }

  @Test
  void deberiaMostrarElBotonDeAnalizar() {
    assertThat(vistaTicket.botonAnalizarEstaVisible(), is(true));
  }

  @Test
  void deberiaMostrarElTituloCorrecto() {
    assertThat(vistaTicket.obtenerTitulo(), equalToIgnoringCase("Cargar por ticket de compra"));
  }
}
