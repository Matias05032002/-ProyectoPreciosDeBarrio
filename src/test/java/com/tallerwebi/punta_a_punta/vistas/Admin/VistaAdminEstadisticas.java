package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaAdminEstadisticas extends VistaWeb {

  public VistaAdminEstadisticas(Page page) {
    super(page);
  }

  public void iniciarSesionComoAdmin(String email, String password) {
    page.navigate("localhost:8080/spring/login");
    page.locator("#email").type(email);
    page.locator("#password").type(password);
    page.locator("#btn-login").click();
    page.navigate("localhost:8080/spring/admin/estadisticas");
  }

  public void navegarDirectamente() {
    page.navigate("localhost:8080/spring/admin/estadisticas");
  }

  public boolean mostrarTotalUsuarios() {
    return page.locator(".card.bg-primary .fs-2").isVisible();
  }

  public boolean mostrarTotalReportes() {
    return page.locator(".card.bg-success .fs-2").isVisible();
  }

  public String obtenerTitulo() {
    return obtenerTextoDelElemento("h2");
  }

  public String obtenerURLActualPath() {
    return page.url();
  }
}
