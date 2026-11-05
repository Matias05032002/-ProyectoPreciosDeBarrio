package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class VistaDonPeso extends VistaWeb {

  private static final String URL_HOME = "http://localhost:8080/spring/home";

  public VistaDonPeso(Page page) {
    super(page);
  }

  public void iniciarSesion(String email, String clave) {
    page.navigate("http://localhost:8080/spring/login");
    page.locator("#email").fill(email);
    page.locator("#password").fill(clave);
    page.locator("#btn-login").click();
    page.waitForLoadState();
  }

  // El servidor de IA no se usa en los E2E: la respuesta de Don Peso se simula en el navegador
  public void simularRespuesta(int estado, String json) {
    page.route(
      "**/asistente/consultar",
      ruta ->
        ruta.fulfill(
          new com.microsoft.playwright.Route.FulfillOptions()
            .setStatus(estado)
            .setContentType("application/json")
            .setBody(json)
        )
    );
  }

  public void irAlHome() {
    page.navigate(URL_HOME);
  }

  public void abrirElChat() {
    page.locator("#dpw-abrir").click();
  }

  public void cerrarElChat() {
    page.locator("#dpw-cerrar").click();
  }

  public void preguntar(String pregunta) {
    page.locator("#dpw-entrada").fill(pregunta);
    page.locator("#dpw-form button[type=submit]").click();
  }

  public Locator panel() {
    return page.locator("#dpw-panel");
  }

  public Locator botonFlotante() {
    return page.locator("#dpw-abrir");
  }

  public Locator mensajesDelUsuario() {
    return page.locator("#dpw-mensajes .dpw-msg--user");
  }

  public Locator mensajesDeDonPeso() {
    return page.locator("#dpw-mensajes .dpw-msg--bot");
  }
}
