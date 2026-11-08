package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaLogin extends VistaWeb {

  public VistaLogin(Page page) {
    super(page);
    page.navigate("localhost:8080/spring/login");
  }

  public String obtenerMensajeDeError() {
    return this.obtenerTextoDelElemento("div.alert.alert-danger");
  }

  public void escribirEMAIL(String email) {
    this.escribirEnElElemento("#email", email);
  }

  public void escribirClave(String clave) {
    this.escribirEnElElemento("#password", clave);
  }

  public void darClickEnIniciarSesion() {
    this.darClickEnElElemento("#btn-login");
  }

  public void darClickEnRegistrarse() {
    this.darClickEnElElemento(".register-prompt a");
  }
}