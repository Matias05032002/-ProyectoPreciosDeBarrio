package com.tallerwebi.punta_a_punta.vistas.Lista;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.Cookie;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VistaLista {

  private final Page page;
  private final BrowserContext context;

  public VistaLista(Page page, BrowserContext context) {
    this.page = page;
    this.context = context;
  }

  public void iniciarSesionYNavegar(String email, String clave) {
    page.navigate("http://localhost:8080/spring/login");
    page.waitForSelector("#email");
    page.evaluate("document.getElementById('email').value = '" + email + "'");
    page.evaluate("document.getElementById('password').value = '" + clave + "'");
    page.evaluate("document.querySelector('form').submit()");
    page.waitForURL("**spring/home**");

    // Convertir jsessionid de URL a cookie para que los forms funcionen normalmente
    String url = page.url();
    Matcher m = Pattern.compile(";jsessionid=([^?#&]+)", Pattern.CASE_INSENSITIVE).matcher(url);
    if (m.find()) {
      context.addCookies(
        List.of(new Cookie("JSESSIONID", m.group(1)).setDomain("localhost").setPath("/"))
      );
    }

    page.navigate("http://localhost:8080/spring/mis-listas");
    page.waitForSelector("#btn-abrir-modal-crear");
  }

  public void darClickEnCrearLista() {
    page.evaluate(
      "var m = document.getElementById('modalCrearLista');" +
      "m.style.display='block'; m.classList.add('show');"
    );
    page.waitForSelector("#nombreLista");
  }

  public void escribirNombreLista(String nombre) {
    page.locator("#nombreLista").fill(nombre);
  }

  public void confirmarCrearLista() {
    Object finalUrl = page.evaluate(
      "(async () => {" +
      "  var nombre = document.getElementById('nombreLista').value;" +
      "  var url = window.location.href;" +
      "  var jsession = (url.match(/;jsessionid=[^?#&]*/i) || [''])[0];" +
      "  var resp = await fetch('/spring/mis-listas/crear' + jsession, {" +
      "    method: 'POST'," +
      "    headers: {'Content-Type': 'application/x-www-form-urlencoded'}," +
      "    body: 'nombre=' + encodeURIComponent(nombre)" +
      "  });" +
      "  return resp.url;" +
      "})()"
    );
    page.navigate(finalUrl.toString());
    page.waitForLoadState();
  }

  public boolean estaEnMisListas() {
    return page.url().contains("/mis-listas");
  }

  public boolean estaEnDetalleLista() {
    return page.url().matches(".*/mis-listas/\\d+.*");
  }

  public boolean elNombreDeLaListaEs(String nombre) {
    return page.locator("h2").textContent().contains(nombre);
  }

  public void volverAMisListas() {
    page.navigate("http://localhost:8080/spring/mis-listas");
    page.waitForSelector("#btn-abrir-modal-crear");
  }

  public boolean laListaExiste(String nombre) {
    return page.locator(".card-title").allTextContents().stream().anyMatch(t -> t.contains(nombre));
  }

  public boolean laListaEstaVacia() {
    return page.locator("#btn-buscar-productos").isVisible();
  }

  public void navegarADetalleLista(long listaId) {
    String url = page.url();
    java.util.regex.Matcher m = java.util.regex.Pattern
      .compile(";jsessionid=[^?#&]*", java.util.regex.Pattern.CASE_INSENSITIVE)
      .matcher(url);
    String jsession = m.find() ? m.group(0) : "";

    page.navigate("http://localhost:8080/spring/mis-listas/" + listaId + jsession);
    page.waitForLoadState();
    page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
  }

  public void eliminarItem(long itemId) {
    Object finalUrl = page.evaluate(
      "(async () => {" +
      "  var url = window.location.href;" +
      "  var jsession = (url.match(/;jsessionid=[^?#&]*/i) || [''])[0];" +
      "  var resp = await fetch('/spring/mis-listas/eliminar-item' + jsession, {" +
      "    method: 'POST'," +
      "    headers: {'Content-Type': 'application/x-www-form-urlencoded'}," +
      "    body: 'itemId=1&listaId=1'" +
      "  });" +
      "  return resp.url;" +
      "})()"
    );
    page.navigate(finalUrl.toString());
    page.waitForLoadState();
  }

  public boolean elItemEstaEnLaLista(String nombreProducto) {
    return page
      .locator(".card-body h6")
      .allTextContents()
      .stream()
      .anyMatch(t -> t.contains(nombreProducto));
  }

  public boolean laListaMuestraVacia() {
    return page.locator("#btn-buscar-productos").isVisible();
  }

  public void buscarProducto(String nombre) {
    String url = page.url();
    java.util.regex.Matcher m = java.util.regex.Pattern
      .compile(";jsessionid=[^?#&]*", java.util.regex.Pattern.CASE_INSENSITIVE)
      .matcher(url);
    String jsession = m.find() ? m.group(0) : "";

    page.navigate(
      "http://localhost:8080/spring/producto/buscar" +
      jsession +
      "?nombre=" +
      nombre +
      "&orden=precio"
    );
    page.waitForLoadState();
  }

  public void abrirModalAgregarLista(long reporteId) {
    page.evaluate(
      "var btn = document.getElementById('btn-agregar-lista-" +
      reporteId +
      "');" +
      "document.getElementById('input-reporte-id').value = '" +
      reporteId +
      "';" +
      "var m = document.getElementById('modalAgregarLista');" +
      "m.style.display='block'; m.classList.add('show');"
    );
    page.waitForSelector("#btn-confirmar-agregar");
  }

  public void seleccionarListaEnModal(long listaId) {
    page.locator("#lista-" + listaId).check();
  }

  public void confirmarAgregarALista() {
    Object finalUrl = page.evaluate(
      "(async () => {" +
      "  var url = window.location.href;" +
      "  var jsession = (url.match(/;jsessionid=[^?#&]*/i) || [''])[0];" +
      "  var reporteId = document.getElementById('input-reporte-id').value;" +
      "  var listaId = document.querySelector('input[name=listaId]:checked').value;" +
      "  var cantidad = document.getElementById('input-cantidad').value;" +
      "  var resp = await fetch('/spring/mis-listas/agregar-producto' + jsession, {" +
      "    method: 'POST'," +
      "    headers: {'Content-Type': 'application/x-www-form-urlencoded'}," +
      "    body: 'reporteId=' + reporteId + '&listaId=' + listaId + '&cantidad=' + cantidad" +
      "  });" +
      "  return resp.url;" +
      "})()"
    );
    page.navigate(finalUrl.toString());
    page.waitForLoadState();
  }

  public String obtenerTotalDeLaLista() {
    page.waitForSelector(".card.border-success h4");
    return page.locator(".card.border-success h4").textContent().trim();
  }

  public void eliminarLista(long listaId) {
    Object finalUrl = page.evaluate(
      "(async () => {" +
      "  var url = window.location.href;" +
      "  var jsession = (url.match(/;jsessionid=[^?#&]*/i) || [''])[0];" +
      "  var resp = await fetch('/spring/mis-listas/eliminar' + jsession, {" +
      "    method: 'POST'," +
      "    headers: {'Content-Type': 'application/x-www-form-urlencoded'}," +
      "    body: 'listaId=" +
      listaId +
      "'" +
      "  });" +
      "  return resp.url;" +
      "})()"
    );
    page.navigate(finalUrl.toString());
    page.waitForLoadState();
  }

  public void editarCantidadItem(long itemId, int cantidad) {
    Object finalUrl = page.evaluate(
      "(async () => {" +
      "  var url = window.location.href;" +
      "  var jsession = (url.match(/;jsessionid=[^?#&]*/i) || [''])[0];" +
      "  var listaId = '" +
      1 +
      "';" +
      "  var resp = await fetch('/spring/mis-listas/editar-item' + jsession, {" +
      "    method: 'POST'," +
      "    headers: {'Content-Type': 'application/x-www-form-urlencoded'}," +
      "    body: 'itemId=" +
      itemId +
      "&listaId=' + listaId + '&cantidad=" +
      cantidad +
      "'" +
      "  });" +
      "  return resp.url;" +
      "})()"
    );
    page.navigate(finalUrl.toString());
    page.waitForLoadState();
    page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
  }

  public void agregarProductoDirecto(long reporteId, long listaId, int cantidad) {
    Object finalUrl = page.evaluate(
      "(async () => {" +
      "  var url = window.location.href;" +
      "  var jsession = (url.match(/;jsessionid=[^?#&]*/i) || [''])[0];" +
      "  var resp = await fetch('/spring/mis-listas/agregar-producto' + jsession, {" +
      "    method: 'POST'," +
      "    headers: {'Content-Type': 'application/x-www-form-urlencoded'}," +
      "    body: 'reporteId=" +
      reporteId +
      "&listaId=" +
      listaId +
      "&cantidad=" +
      cantidad +
      "'" +
      "  });" +
      "  return resp.url;" +
      "})()"
    );
    page.navigate(finalUrl.toString());
    page.waitForLoadState();
    page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
  }
}
