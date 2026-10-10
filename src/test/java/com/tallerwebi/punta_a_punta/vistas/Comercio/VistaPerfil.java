package com.tallerwebi.punta_a_punta.vistas.Comercio;

import com.microsoft.playwright.Page;
import com.tallerwebi.punta_a_punta.vistas.VistaWeb;

public class VistaPerfil extends VistaWeb {

  private static final String URL = "http://localhost:8080/spring/comercio/perfil";

  public VistaPerfil(Page page) {
    super(page);
    page.navigate(URL);
  }

  public void escribirNombreComercio(String nombre) {
    this.escribirEnElElemento("input[name='nombre']", nombre);
  }

  public void seleccionarTipo(String tipo) {
    this.page.selectOption("select[name='tipo']", tipo);
  }

  public void escribirDireccion(String direccion) {
    this.escribirEnElElemento("input[name='direccion']", direccion);
  }

  public void escribirLocalidad(String localidad) {
    this.escribirEnElElemento("input[name='localidad']", localidad);
  }

  public void escribirDescripcion(String descripcion) {
    this.escribirEnElElemento("textarea[name='descripcion']", descripcion);
  }

  public void darClickEnGuardar() {
    this.darClickEnElElemento("button[type='submit']");
  }

  public boolean muestraFormularioDeRegistro() {
    return this.page.locator("input[name='nombre']").isVisible();
  }

  public boolean muestraDatosDelComercio() {
    return this.page.locator("dl.row").isVisible();
  }
}
