package com.tallerwebi.punta_a_punta.vistas.Reporte;

import com.microsoft.playwright.Page;
import com.tallerwebi.punta_a_punta.vistas.VistaWeb;

public class VistaTicket extends VistaWeb {

  public VistaTicket(Page page) {
    super(page);
    page.navigate("localhost:8080/spring/reporte/ticket");
  }

  public void iniciarSesionYNavegar(String email, String clave) {
    page.navigate("localhost:8080/spring/login");
    page.locator("#email").type(email);
    page.locator("#password").type(clave);
    page.locator("#btn-login").click();
    page.navigate("localhost:8080/spring/reporte/ticket");
  }

  public String obtenerTitulo() {
    return this.obtenerTextoDelElemento("h2");
  }

  public boolean formularioEstaVisible() {
    return page.locator("#form-ticket").isVisible();
  }

  public boolean inputFotoEstaVisible() {
    return page.locator("#ticket-foto").isVisible();
  }

  public boolean botonAnalizarEstaVisible() {
    return page.locator("button[type='submit']").isVisible();
  }

  public boolean estaEnPaginaLogin() {
    return page.url().contains("/login");
  }

  public boolean hayError() {
    return page.locator(".alert-danger").count() > 0;
  }
}
