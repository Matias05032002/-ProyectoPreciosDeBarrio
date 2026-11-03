package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaRecuperacion extends VistaWeb {

  private static final String BASE = "http://localhost:8080/spring";

  public VistaRecuperacion(Page page) {
    super(page);
  }

  public void irAlLogin() {
    page.navigate(BASE + "/login");
  }

  public void darClickEnOlvideMiContrasena() {
    page.locator("#link-olvide-contrasena").click();
    page.waitForLoadState();
  }

  public void irAPedirLink() {
    page.navigate(BASE + "/olvide-contrasena");
  }

  public void escribirEmail(String email) {
    page.locator("#email").fill(email);
  }

  public void darClickEnEnviarLink() {
    page.locator("#btn-enviar-link").click();
    page.waitForLoadState();
  }

  public boolean muestraPedidoEnviado() {
    return page.locator("#pedido-enviado").isVisible();
  }

  public void abrirLink(String token) {
    page.navigate(BASE + "/restablecer-contrasena?token=" + token);
  }

  public boolean muestraTokenInvalido() {
    return page.locator("#token-invalido").isVisible();
  }

  public void darClickEnPedirOtroLink() {
    page.locator("#btn-pedir-otro-link").click();
    page.waitForLoadState();
  }

  public void escribirContrasenaNueva(String contrasena) {
    page.locator("#contrasena").fill(contrasena);
  }

  public void repetirContrasena(String contrasena) {
    page.locator("#confirmacion").fill(contrasena);
  }

  // Si todo sale bien la app redirige al login: se espera ese cambio de página antes de seguir
  public void darClickEnGuardarContrasena() {
    page.locator("#btn-restablecer").click();
    page.waitForURL("**/login**");
  }

  // Si la contraseña no sirve la app vuelve a mostrar el formulario con el error
  public void darClickEnGuardarContrasenaInvalida() {
    page.locator("#btn-restablecer").click();
    page.waitForSelector("#error-restablecer");
  }

  public boolean muestraErrorAlRestablecer() {
    return page.locator("#error-restablecer").isVisible();
  }

  public boolean muestraAvisoDeContrasenaRestablecida() {
    return page.locator("#contrasena-restablecida").isVisible();
  }

  public void iniciarSesion(String email, String clave) {
    page.locator("#email").fill(email);
    page.locator("#password").fill(clave);
    page.locator("#btn-login").click();
  }

  public boolean ingresoCorrectamente() {
    try {
      page.waitForURL(url -> !url.contains("login"), new Page.WaitForURLOptions().setTimeout(5000));
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean muestraErrorDeLogin() {
    try {
      page.waitForSelector("#error-login", new Page.WaitForSelectorOptions().setTimeout(5000));
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
