package com.tallerwebi.punta_a_punta.vistas.Reporte;

import com.microsoft.playwright.Page;
import com.tallerwebi.punta_a_punta.vistas.VistaWeb;

public class VistaEditarReporte extends VistaWeb {

  public VistaEditarReporte(Page page) {
    super(page);
  }

  public String obtenerTitulo() {
    return this.obtenerTextoDelElemento("h2");
  }

  public void escribirNombreProducto(String nombre) {
    this.page.locator("input[name='producto.nombre']").fill(nombre);
  }

  public void escribirMarcaProducto(String marca) {
    this.page.locator("input[name='producto.marca']").fill(marca);
  }

  public void seleccionarCategoriaProducto(String categoria) {
    this.page.locator("select[name='producto.categoria']").selectOption(categoria);
  }

  public void escribirUnidadProducto(String unidad) {
    this.page.locator("input[name='producto.unidad']").fill(unidad);
  }

  public void escribirNombreComercio(String nombre) {
    this.page.locator("input[name='comercio.nombre']").fill(nombre);
  }

  public void escribirDireccionComercio(String direccion) {
    this.page.locator("input[name='comercio.direccion']").fill(direccion);
  }

  public void seleccionarLocalidadComercio(String localidad) {
    this.page.locator("select[name='comercio.localidad']").selectOption(localidad);
  }

  public void escribirPrecio(String precio) {
    this.page.locator("input[name='precio']").fill(precio);
  }

  public void darClickEnGuardarCambios() {
    this.page.locator("button[type='submit']").click();
  }

  public String urlActual() {
    return this.page.url();
  }
}
