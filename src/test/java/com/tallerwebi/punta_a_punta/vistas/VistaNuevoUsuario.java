package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaNuevoUsuario extends VistaWeb {

  public VistaNuevoUsuario(Page page) {
    super(page);
  }

  public void escribirEMAIL(String email) {
    this.escribirEnElElemento("#email", email);
  }

  public void escribirClave(String clave) {
    this.escribirEnElElemento("#password", clave);
  }

  public void darClickEnRegistrarme() {
    this.darClickEnElElemento("#btn-registrarme");
  }

  public void seleccionarRol(String rol) {
    this.darClickEnElElemento("#rol" + rol);
  }

  public String obtenerMensajeDeError() {
    return this.obtenerTextoDelElemento("p.alert.alert-danger");
  }

  public void escribirDireccion(String direccion) {
    this.escribirEnElElemento("#direccion-c", direccion);
  }

  public void seleccionarTipoComercio(String tipo) {
    this.page.selectOption("#tipo-comercio", tipo);
  }

  public void seleccionarLocalidadComercio(String localidad) {
    this.page.selectOption("#localidad-c", localidad);
  }

  public String obtenerMensajeDeErrorComercio() {
    return this.obtenerTextoDelElemento("#form-comercio .error-msg");
  }

  public void darClickEnRegistrarmeComercio() {
    this.darClickEnElElemento("#btn-registrarme-comercio");
  }

  public void escribirEmailComercio(String email) {
    this.escribirEnElElemento("#email-c", email);
  }

  public void escribirClaveComercio(String clave) {
    this.escribirEnElElemento("#password-c", clave);
  }
}
