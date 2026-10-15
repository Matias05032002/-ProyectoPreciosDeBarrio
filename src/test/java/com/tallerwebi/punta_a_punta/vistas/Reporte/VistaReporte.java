package com.tallerwebi.punta_a_punta.vistas.Reporte;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;
import com.tallerwebi.punta_a_punta.vistas.VistaWeb;

public class VistaReporte extends VistaWeb {

  public VistaReporte(Page page) {
    super(page);
    page.navigate("localhost:8080/spring/reporte");
  }

  public void iniciarSesionYNavegar(String email, String clave) {
    page.navigate("localhost:8080/spring/login");
    page.locator("#email").type(email);
    page.locator("#password").type(clave);
    page.locator("#btn-login").click();
    page.navigate("localhost:8080/spring/reporte");
  }

  public String obtenerTitulo() {
    return this.obtenerTextoDelElemento("h2");
  }

  public void escribirNombreProducto(String nombre) {
    this.escribirEnElElemento("#producto-nombre", nombre);
  }

  public void escribirMarcaProducto(String marca) {
    this.escribirEnElElemento("#producto-marca", marca);
  }

  public void escribirCategoriaProducto(String categoria) {
    page.locator("#producto-categoria").selectOption(categoria);
  }

  public void escribirUnidadProducto(String unidad) {
    this.escribirEnElElemento("#producto-unidad", unidad);
  }

  public void escribirNombreComercio(String nombre) {
    this.escribirEnElElemento("#comercio-nombre", nombre);
  }

  public void escribirDireccionComercio(String direccion) {
    this.escribirEnElElemento("#comercio-direccion", direccion);
  }

  public void escribirLocalidadComercio(String localidad) {
    page.locator("#comercio-localidad").selectOption(localidad);
  }

  public void escribirPrecio(String precio) {
    this.escribirEnElElemento("#precio", precio);
  }

  public void darClickEnGuardarReporte() {
    this.darClickEnElElemento("#btn-guardar-reporte");
  }

  public void darClickEnMarcarDudoso() {
    this.darClickEnElElemento("#btn-marcar-dudoso");
  }

  public boolean hayReportesEnLaTabla() {
    return page.url().contains("/reporte") && page.locator("#error").count() == 0;
  }

  public boolean hayError() {
    try {
      page.waitForSelector("#error", new Page.WaitForSelectorOptions().setTimeout(3000));
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public void escribirLatitud(String latitud) {
    page.fill("#latitud", latitud);
  }

  public void escribirLongitud(String longitud) {
    page.fill("#longitud", longitud);
  }

  public void subirFotoYConfirmarRecorte(String rutaImagen) {
    // Cargar el archivo en el input
    page.locator("#foto").setInputFiles(java.nio.file.Paths.get(rutaImagen));
    // Esperar que el modal del cropper aparezca
    page.waitForSelector("#modalCropper.show", new Page.WaitForSelectorOptions().setTimeout(5000));
    // Hacer click en "Usar foto"
    page.locator("#btn-confirmar-crop").click();
    // Esperar que el modal se cierre
    page.waitForSelector(
      "#modalCropper:not(.show)",
      new Page.WaitForSelectorOptions().setTimeout(5000)
    );
  }

  public boolean hayModalCropperVisible() {
    page.waitForTimeout(1000);
    return page.locator("#modalCropper").isVisible();
  }

  public boolean hayPreviewDeFoto() {
    return page.locator("#preview-foto").isVisible();
  }

  public boolean hayMensajeGuardado() {
    try {
      page.waitForSelector(".alert-success", new Page.WaitForSelectorOptions().setTimeout(5000));
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
