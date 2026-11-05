package com.tallerwebi.punta_a_punta;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;
import com.tallerwebi.punta_a_punta.vistas.VistaDonPeso;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaDonPesoE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaDonPeso vistaDonPeso;

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
    vistaDonPeso = new VistaDonPeso(context.newPage());
    vistaDonPeso.iniciarSesion("test-comercio@unlam.edu.ar", "test");
    vistaDonPeso.irAlHome();
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void elChatDeberiaEstarCerradoAlEntrar() {
    assertThat(vistaDonPeso.botonFlotante()).isVisible();
    assertThat(vistaDonPeso.panel()).isHidden();
  }

  @Test
  void deberiaAbrirYCerrarElChat() {
    vistaDonPeso.abrirElChat();
    assertThat(vistaDonPeso.panel()).isVisible();

    vistaDonPeso.cerrarElChat();
    assertThat(vistaDonPeso.panel()).isHidden();
  }

  @Test
  void deberiaMostrarLaPreguntaYLaRespuestaDeDonPeso() {
    vistaDonPeso.simularRespuesta(
      200,
      "{\"respuesta\":\"La leche más barata está en Almacén Don José\"}"
    );

    vistaDonPeso.abrirElChat();
    vistaDonPeso.preguntar("¿Dónde está la leche más barata?");

    assertThat(vistaDonPeso.mensajesDelUsuario().last())
      .containsText("¿Dónde está la leche más barata?");
    assertThat(vistaDonPeso.mensajesDeDonPeso().last())
      .containsText("La leche más barata está en Almacén Don José");
  }

  @Test
  void deberiaAvisarSiSeSuperaElLimiteDeConsultas() {
    vistaDonPeso.simularRespuesta(
      429,
      "{\"error\":\"Hiciste muchas consultas seguidas. Probá de nuevo en un rato.\"}"
    );

    vistaDonPeso.abrirElChat();
    vistaDonPeso.preguntar("¿Cuánto sale el aceite?");

    assertThat(vistaDonPeso.mensajesDeDonPeso().last()).containsText("muchas consultas");
  }

  @Test
  void deberiaMostrarUnMensajeAmableSiElServidorFalla() {
    vistaDonPeso.simularRespuesta(500, "{\"error\":\"No pude responder ahora, probá más tarde\"}");

    vistaDonPeso.abrirElChat();
    vistaDonPeso.preguntar("¿Hay ofertas?");

    assertThat(vistaDonPeso.mensajesDeDonPeso().last()).containsText("probá más tarde");
  }
}
