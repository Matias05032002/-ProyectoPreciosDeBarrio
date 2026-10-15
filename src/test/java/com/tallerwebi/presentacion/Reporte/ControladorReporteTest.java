package com.tallerwebi.presentacion.Reporte;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioOcr;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.CamposObligatoriosVacios;
import com.tallerwebi.dominio.excepcion.PrecioIncorrecto;
import com.tallerwebi.dominio.excepcion.ReporteExistente;
import com.tallerwebi.dominio.excepcion.UnidadInvalida;
import com.tallerwebi.presentacion.ControladorReporte;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorReporteTest {

  private ServicioReporte servicioReporteMock;
  private ControladorReporte controladorReporte;
  private ServicioComercio servicioComercioMock;
  private ServicioProducto servicioProductoMock;
  private ServicioLogin servicioLoginMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private Usuario usuarioMock;
  private ServicioOcr servicioOcrMock;

  @BeforeEach
  public void init() {
    this.servicioProductoMock = mock(ServicioProducto.class);
    this.servicioComercioMock = mock(ServicioComercio.class);
    this.servicioLoginMock = mock(ServicioLogin.class);
    this.servicioReporteMock = mock(ServicioReporte.class);
    this.servicioOcrMock = mock(ServicioOcr.class);
    this.usuarioMock = mock(Usuario.class);
    this.controladorReporte =
      new ControladorReporte(
        servicioReporteMock,
        servicioProductoMock,
        servicioComercioMock,
        servicioLoginMock,
        servicioOcrMock
      );
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("EMAIL")).thenReturn("test@test.com");
  }

  @Test
  public void guardarNuevoReporte() throws Exception {
    Producto producto = new Producto();
    producto.setNombre("Leche");
    producto.setMarca("La Serenisima");
    producto.setCategoria("Lacteos");
    producto.setUnidad("1000ml");

    Comercio comercio = new Comercio();
    comercio.setNombre("Almacen Don Jose");
    comercio.setDireccion("Av. Siempreviva 123");
    comercio.setLocalidad("La Matanza");

    Reporte reporte = new Reporte();
    reporte.setPrecio(100.0);
    reporte.setProducto(producto);
    reporte.setComercio(comercio);

    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(servicioLoginMock.buscarPorEmail("test@test.com")).thenReturn(null);
    when(this.servicioReporteMock.guardarReporte(reporte)).thenReturn(reporte);

    ModelAndView modelAndView = this.controladorReporte.guardarReporte(reporte, requestMock);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/reporte?guardado=true"));
    verify(this.servicioReporteMock, times(1)).guardarReporte(reporte);
  }

  @Test
  public void listarTodosLosReportes() {
    Reporte reporte = new Reporte();
    List<Reporte> listaDeReportes = Arrays.asList(reporte);
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(this.servicioReporteMock.listarTodos()).thenReturn(listaDeReportes);

    ModelAndView modelAndView = this.controladorReporte.listarTodos(requestMock, null);

    assertThat(modelAndView.getModel().get("reportes"), is(listaDeReportes));
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("reporte/lista-reportes"));
  }

  @Test
  public void buscarReportePorId() {
    Reporte reporte = new Reporte();
    when(this.servicioReporteMock.buscarReporte(1L)).thenReturn(reporte);

    ModelAndView modelAndView = this.controladorReporte.buscarReporte(1L);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("reporte/lista-reportes"));
    assertThat(modelAndView.getModel().get("reportes"), is(Collections.singletonList(reporte)));
    verify(this.servicioReporteMock, times(1)).buscarReporte(1L);
  }

  @Test
  public void buscarReportePorNombre() {
    Reporte reporte = new Reporte();
    List<Reporte> listaDeReportesPorNombre = Arrays.asList(reporte);
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(this.servicioReporteMock.buscarPorNombre("Leche")).thenReturn(listaDeReportesPorNombre);

    ModelAndView modelAndView = this.controladorReporte.buscarPorNombre("Leche", requestMock);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("reporte/lista-reportes"));
    assertThat(modelAndView.getModel().get("reportes"), is(listaDeReportesPorNombre));
    verify(this.servicioReporteMock, times(1)).buscarPorNombre("Leche");
  }

  @Test
  public void marcarDudosoUnReporte() {
    Reporte reporte = new Reporte();
    when(servicioLoginMock.buscarPorEmail("test@test.com")).thenReturn(usuarioMock);
    when(servicioReporteMock.marcarDudoso(1L, usuarioMock)).thenReturn(reporte);

    ModelAndView modelAndView = controladorReporte.marcarDudoso(1L, "Leche", requestMock);

    assertThat(
      modelAndView.getViewName(),
      equalToIgnoringCase("redirect:/producto/buscar?nombre=Leche&dudosoMarcado=true")
    );
    verify(servicioReporteMock, times(1)).marcarDudoso(1L, usuarioMock);
  }

  @Test
  public void guardarReporteRedirigeConParametroGuardado() throws Exception {
    Producto producto = new Producto();
    producto.setNombre("Leche");
    producto.setMarca("La Serenisima");
    producto.setCategoria("Lacteos");
    producto.setUnidad("1000ml");

    Comercio comercio = new Comercio();
    comercio.setNombre("Almacen Don Jose");
    comercio.setDireccion("Av. Siempreviva 123");
    comercio.setLocalidad("La Matanza");

    Reporte reporte = new Reporte();
    reporte.setPrecio(100.0);
    reporte.setProducto(producto);
    reporte.setComercio(comercio);

    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(servicioLoginMock.buscarPorEmail("test@test.com")).thenReturn(null);
    when(this.servicioReporteMock.guardarReporte(reporte)).thenReturn(reporte);

    ModelAndView mav = controladorReporte.guardarReporte(reporte, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/reporte?guardado=true"));
  }

  @Test
  public void guardarReporteGuardaLaFotoRecibida() throws Exception {
    Reporte reporte = new Reporte();
    String fotoBase64 = "data:image/jpeg;base64,AQID";

    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(servicioLoginMock.buscarPorEmail("test@test.com")).thenReturn(usuarioMock);

    ModelAndView mav = controladorReporte.guardarReporte(reporte, fotoBase64, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/reporte?guardado=true"));
    assertThat(reporte.getFoto(), is(new byte[] { 1, 2, 3 }));
    verify(servicioReporteMock).guardarReporte(reporte);
  }

  @Test
  public void listarTodosConGuardadoTruePasaAtributoAlModelo() {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(this.servicioReporteMock.listarTodos()).thenReturn(Collections.emptyList());

    ModelAndView mav = controladorReporte.listarTodos(requestMock, true);

    assertThat(mav.getModel().get("guardado"), is(true));
  }

  @Test
  public void misProductosDevuelveLaVistaCuandoComercioNoExiste() {
    when(sessionMock.getAttribute("ROL")).thenReturn("COMERCIO");
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioComercioMock.buscarComercioPorUsuarioId(1L)).thenReturn(null);

    ModelAndView mav = controladorReporte.misProductos(requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("reporte/mis-productos-comercio"));
    assertThat(mav.getModel().get("reportes"), is(Collections.emptyList()));
  }

  @Test
  public void misProductosDevuelveLosReportesDelComercio() {
    Comercio comercio = new Comercio();
    comercio.setId(5L);
    Reporte reporte = new Reporte();
    List<Reporte> reportes = Arrays.asList(reporte);

    when(sessionMock.getAttribute("ROL")).thenReturn("COMERCIO");
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioComercioMock.buscarComercioPorUsuarioId(1L)).thenReturn(comercio);
    when(servicioReporteMock.buscarPorComercio(5L)).thenReturn(reportes);

    ModelAndView mav = controladorReporte.misProductos(requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("reporte/mis-productos-comercio"));
    assertThat(mav.getModel().get("reportes"), is(reportes));
  }

  @Test
  public void verFormularioTicketRedirigeAlLoginSiNoHaySesion() {
    when(sessionMock.getAttribute("ROL")).thenReturn(null);

    ModelAndView mav = controladorReporte.verFormularioTicket(requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void verFormularioTicketDevuelveLaVistaSubirTicket() {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(servicioComercioMock.listarTodos()).thenReturn(Collections.emptyList());

    ModelAndView mav = controladorReporte.verFormularioTicket(requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("reporte/subir-ticket"));
  }

  @Test
  public void analizarTicketRedirigeAlLoginSiNoHaySesion() {
    when(sessionMock.getAttribute("ROL")).thenReturn(null);

    ModelAndView mav = controladorReporte.analizarTicket(
      "data:image/jpeg;base64,AQID",
      "Almacen",
      "Calle 1",
      "La Matanza",
      requestMock
    );

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void analizarTicketConBase64VacioDevuelveError() {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(servicioComercioMock.listarTodos()).thenReturn(Collections.emptyList());

    ModelAndView mav = controladorReporte.analizarTicket(
      "",
      "Almacen",
      "Calle 1",
      "La Matanza",
      requestMock
    );

    assertThat(mav.getViewName(), equalToIgnoringCase("reporte/subir-ticket"));
    assertThat(mav.getModel().get("error"), is("Debes subir una foto del ticket"));
  }

  @Test
  public void guardarTicketRedirigeAlLoginSiNoHaySesion()
    throws CamposObligatoriosVacios, ReporteExistente, UnidadInvalida, PrecioIncorrecto {
    when(sessionMock.getAttribute("ROL")).thenReturn(null);

    java.util.Map<String, String> params = new java.util.HashMap<>();
    ModelAndView mav = controladorReporte.guardarTicket(
      "Almacen",
      "Calle 1",
      "La Matanza",
      params,
      requestMock
    );

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void guardarTicketConItemsValidosGuardaYRedirige() throws Exception {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(servicioLoginMock.buscarPorEmail("test@test.com")).thenReturn(usuarioMock);

    java.util.Map<String, String> params = new java.util.HashMap<>();
    params.put("items[0].nombre", "Leche");
    params.put("items[0].precio", "150.0");
    params.put("incluir[0]", "on");

    ModelAndView mav = controladorReporte.guardarTicket(
      "Almacen",
      "Calle 1",
      "La Matanza",
      params,
      requestMock
    );

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/reporte?guardado=true"));
    verify(servicioReporteMock, times(1)).guardarReporte(any());
  }

  @Test
  public void guardarTicketConPrecioInvalidoNoGuardaElItem() throws Exception {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(servicioLoginMock.buscarPorEmail("test@test.com")).thenReturn(usuarioMock);

    java.util.Map<String, String> params = new java.util.HashMap<>();
    params.put("items[0].nombre", "Leche");
    params.put("items[0].precio", "no-es-numero");
    params.put("incluir[0]", "on");

    ModelAndView mav = controladorReporte.guardarTicket(
      "Almacen",
      "Calle 1",
      "La Matanza",
      params,
      requestMock
    );

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/reporte?guardado=true"));
    verify(servicioReporteMock, never()).guardarReporte(any());
  }

  @Test
  public void guardarTicketSinCheckboxNoGuardaElItem() throws Exception {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(servicioLoginMock.buscarPorEmail("test@test.com")).thenReturn(usuarioMock);

    java.util.Map<String, String> params = new java.util.HashMap<>();
    params.put("items[0].nombre", "Leche");
    params.put("items[0].precio", "150.0");
    // sin incluir[0] → checkbox desmarcado

    ModelAndView mav = controladorReporte.guardarTicket(
      "Almacen",
      "Calle 1",
      "La Matanza",
      params,
      requestMock
    );

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/reporte?guardado=true"));
    verify(servicioReporteMock, never()).guardarReporte(any());
  }

  @Test
  public void analizarTicketConBase64InvalidoDevuelveError() throws Exception {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(servicioComercioMock.listarTodos()).thenReturn(Collections.emptyList());

    ModelAndView mav = controladorReporte.analizarTicket(
      "data:image/jpeg;base64,INVALIDO!!!!",
      "Almacen",
      "Calle 1",
      "La Matanza",
      requestMock
    );

    assertThat(mav.getViewName(), equalToIgnoringCase("reporte/subir-ticket"));
    assertThat(mav.getModel().containsKey("error"), is(true));
  }
}
