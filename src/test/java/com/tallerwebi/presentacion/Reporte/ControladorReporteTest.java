package com.tallerwebi.presentacion.Reporte;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Comercio.UbicacionSugerida;
import com.tallerwebi.dominio.Paginador.ServicioPaginador;
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
import com.tallerwebi.dominio.excepcion.ReportePropio;
import com.tallerwebi.dominio.excepcion.UnidadInvalida;
import com.tallerwebi.presentacion.ControladorReporte;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

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
  private ServicioPaginador servicioPaginadorMock;

  @BeforeEach
  public void init() {
    this.servicioProductoMock = mock(ServicioProducto.class);
    this.servicioComercioMock = mock(ServicioComercio.class);
    this.servicioLoginMock = mock(ServicioLogin.class);
    this.servicioReporteMock = mock(ServicioReporte.class);
    this.servicioOcrMock = mock(ServicioOcr.class);
    this.usuarioMock = mock(Usuario.class);
    this.servicioPaginadorMock = mock(ServicioPaginador.class);
    this.controladorReporte =
      new ControladorReporte(
        servicioReporteMock,
        servicioProductoMock,
        servicioComercioMock,
        servicioLoginMock,
        servicioOcrMock,
        servicioPaginadorMock
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

    ModelAndView modelAndView = controladorReporte.marcarDudoso(
      1L,
      "Leche",
      requestMock,
      new RedirectAttributesModelMap()
    );

    assertThat(
      modelAndView.getViewName(),
      equalToIgnoringCase("redirect:/producto/buscar?nombre=Leche&dudosoMarcado=true")
    );
    verify(servicioReporteMock, times(1)).marcarDudoso(1L, usuarioMock);
  }

  @Test
  public void marcarDudosoUnReportePropioNoDeberiaMostrarElAgradecimientoYDeberiaAvisar() {
    when(servicioLoginMock.buscarPorEmail("test@test.com")).thenReturn(usuarioMock);
    when(servicioReporteMock.marcarDudoso(1L, usuarioMock)).thenThrow(new ReportePropio());
    RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

    ModelAndView modelAndView = controladorReporte.marcarDudoso(
      1L,
      "Leche",
      requestMock,
      redirectAttributes
    );

    assertThat(
      modelAndView.getViewName(),
      equalToIgnoringCase("redirect:/producto/buscar?nombre=Leche")
    );
    assertThat(
      redirectAttributes.getFlashAttributes().get("errorDudoso"),
      equalTo("No podés marcar como dudoso un reporte que cargaste vos")
    );
  }

  @Test
  public void marcarDudosoConUnNombreConCaracteresEspecialesDeberiaCodificarLaUrl() {
    when(servicioLoginMock.buscarPorEmail("test@test.com")).thenReturn(usuarioMock);
    when(servicioReporteMock.marcarDudoso(1L, usuarioMock)).thenReturn(new Reporte());

    ModelAndView modelAndView = controladorReporte.marcarDudoso(
      1L,
      "Leche&dudosoMarcado=false #1 {x}",
      requestMock,
      new RedirectAttributesModelMap()
    );

    assertThat(modelAndView.getViewName(), not(containsString("&dudosoMarcado=false")));
    assertThat(modelAndView.getViewName(), not(containsString("{")));
    assertThat(modelAndView.getViewName(), not(containsString("#")));
    assertThat(modelAndView.getViewName(), org.hamcrest.Matchers.endsWith("&dudosoMarcado=true"));
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
    String fotoBase64 = "data:image/jpeg;base64,/9j/AA==";

    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(servicioLoginMock.buscarPorEmail("test@test.com")).thenReturn(usuarioMock);

    ModelAndView mav = controladorReporte.guardarReporte(reporte, fotoBase64, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/reporte?guardado=true"));
    assertThat(reporte.getFoto(), is(notNullValue()));
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
    when(servicioPaginadorMock.calcularTotalPaginas(anyInt(), anyInt())).thenReturn(0);
    when(servicioPaginadorMock.paginar(anyList(), anyInt(), anyInt()))
      .thenReturn(Collections.emptyList());
    ModelAndView mav = controladorReporte.misProductos(requestMock, 0);

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
    when(servicioPaginadorMock.calcularTotalPaginas(anyInt(), anyInt())).thenReturn(1);
    doReturn(reportes).when(servicioPaginadorMock).paginar(anyList(), anyInt(), anyInt());
    ModelAndView mav = controladorReporte.misProductos(requestMock, 0);

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
  public void analizarTicketSinNombreDeComercioDevuelveError() {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(servicioComercioMock.listarTodos()).thenReturn(Collections.emptyList());

    ModelAndView mav = controladorReporte.analizarTicket(
      "data:image/jpeg;base64,AQID",
      "  ",
      "Calle 1",
      "La Matanza",
      requestMock
    );

    assertThat(mav.getViewName(), equalToIgnoringCase("reporte/subir-ticket"));
    assertThat(
      mav.getModel().get("error"),
      is("Completá el nombre, la dirección y la localidad del comercio")
    );
    verifyNoInteractions(servicioOcrMock);
  }

  @Test
  public void analizarTicketSinLocalidadDeComercioDevuelveError() {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(servicioComercioMock.listarTodos()).thenReturn(Collections.emptyList());

    ModelAndView mav = controladorReporte.analizarTicket(
      "data:image/jpeg;base64,AQID",
      "Almacen",
      "Calle 1",
      "",
      requestMock
    );

    assertThat(mav.getViewName(), equalToIgnoringCase("reporte/subir-ticket"));
    assertThat(mav.getModel().containsKey("error"), is(true));
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
  public void guardarTicketSiUnProductoFallaGuardaLosDemasYMuestraElMotivo() throws Exception {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(servicioLoginMock.buscarPorEmail("test@test.com")).thenReturn(usuarioMock);
    when(
      servicioReporteMock.guardarReporte(
        argThat(r -> r != null && "Ajo".equals(r.getProducto().getNombre()))
      )
    )
      .thenThrow(new UnidadInvalida("La unidad no es valida"));

    java.util.Map<String, String> params = new java.util.HashMap<>();
    params.put("items[0].nombre", "Ajo");
    params.put("items[0].precio", "2490.0");
    params.put("incluir[0]", "on");
    params.put("items[1].nombre", "Jamon Cocido");
    params.put("items[1].precio", "6890.0");
    params.put("incluir[1]", "on");

    ModelAndView mav = controladorReporte.guardarTicket(
      "Dia",
      "Alberdi 5925",
      "Buenos Aires",
      params,
      requestMock
    );

    verify(servicioReporteMock, times(2)).guardarReporte(any());
    assertThat(mav.getViewName(), equalToIgnoringCase("reporte/lista-reportes"));
    assertThat((String) mav.getModel().get("error"), containsString("Ajo"));
  }

  @Test
  public void guardarTicketConProductoYaReportadoHoyMuestraMensajeSinError500() throws Exception {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(servicioLoginMock.buscarPorEmail("test@test.com")).thenReturn(usuarioMock);
    when(servicioReporteMock.guardarReporte(any())).thenThrow(new ReporteExistente());

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

    assertThat(mav.getViewName(), equalToIgnoringCase("reporte/lista-reportes"));
    assertThat((String) mav.getModel().get("error"), containsString("ya lo reportaste hoy"));
  }

  @Test
  public void guardarTicketConUnidadVaciaGuardaElProductoComoUnaUnidad() throws Exception {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(servicioLoginMock.buscarPorEmail("test@test.com")).thenReturn(usuarioMock);

    java.util.Map<String, String> params = new java.util.HashMap<>();
    params.put("items[0].nombre", "Ajo");
    params.put("items[0].precio", "2490.0");
    params.put("items[0].unidad", "");
    params.put("incluir[0]", "on");

    controladorReporte.guardarTicket("Dia", "Alberdi 5925", "Buenos Aires", params, requestMock);

    ArgumentCaptor<Reporte> reporte = ArgumentCaptor.forClass(Reporte.class);
    verify(servicioReporteMock).guardarReporte(reporte.capture());
    assertThat(reporte.getValue().getProducto().getUnidad(), equalTo("1u"));
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

  // --- verFormularioEdicion ---

  @Test
  public void verFormularioEdicionRedirigeAlLoginSiNoHaySesion() {
    when(sessionMock.getAttribute("ROL")).thenReturn(null);

    ModelAndView mav = controladorReporte.verFormularioEdicion(1L, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void verFormularioEdicionRedirigeAMisReportesSiNoPuedeEditar() {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioReporteMock.puedeEditar(1L, 1L)).thenReturn(false);

    ModelAndView mav = controladorReporte.verFormularioEdicion(1L, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/reporte/mis-reportes"));
  }

  @Test
  public void verFormularioEdicionDevuelveLaVistaConElReporte() {
    Reporte reporte = new Reporte();
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioReporteMock.puedeEditar(1L, 1L)).thenReturn(true);
    when(servicioReporteMock.buscarReporte(1L)).thenReturn(reporte);

    ModelAndView mav = controladorReporte.verFormularioEdicion(1L, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("reporte/editar-reporte"));
    assertThat(mav.getModel().get("reporte"), is(reporte));
  }

  // --- editarReporte ---

  @Test
  public void editarReporteRedirigeAlLoginSiNoHaySesion() throws Exception {
    when(sessionMock.getAttribute("ROL")).thenReturn(null);

    ModelAndView mav = controladorReporte.editarReporte(1L, new Reporte(), null, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void editarReporteExitosoRedirigeAMisReportes() throws Exception {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(sessionMock.getAttribute("ID")).thenReturn(1L);

    ModelAndView mav = controladorReporte.editarReporte(1L, new Reporte(), null, requestMock);

    assertThat(
      mav.getViewName(),
      equalToIgnoringCase("redirect:/reporte/mis-reportes?editado=true")
    );
    verify(servicioReporteMock, times(1)).editarReporte(eq(1L), any(Reporte.class), eq(1L));
  }

  @Test
  public void editarReporteConExcepcionVuelveAlFormularioConError() throws Exception {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    doThrow(new com.tallerwebi.dominio.excepcion.FueraDePlazoDeEdicion("Fuera de plazo"))
      .when(servicioReporteMock)
      .editarReporte(eq(1L), any(Reporte.class), eq(1L));
    when(servicioReporteMock.buscarReporte(1L)).thenReturn(new Reporte());

    ModelAndView mav = controladorReporte.editarReporte(1L, new Reporte(), null, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("reporte/editar-reporte"));
    assertThat(mav.getModel().get("error"), is("Fuera de plazo"));
  }

  @Test
  public void editarReporteConFotoValidaGuardaLosBytesEnElReporte() throws Exception {
    Reporte reporte = new Reporte();
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(sessionMock.getAttribute("ID")).thenReturn(1L);

    ModelAndView mav = controladorReporte.editarReporte(
      1L,
      reporte,
      "data:image/jpeg;base64,/9j/AA==",
      requestMock
    );

    assertThat(
      mav.getViewName(),
      equalToIgnoringCase("redirect:/reporte/mis-reportes?editado=true")
    );
    assertThat(reporte.getFoto(), is(notNullValue()));
  }

  @Test
  public void editarReporteConFotoInvalidaVuelveAlFormularioConError() throws Exception {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    when(sessionMock.getAttribute("ID")).thenReturn(1L);
    when(servicioReporteMock.buscarReporte(1L)).thenReturn(new Reporte());

    ModelAndView mav = controladorReporte.editarReporte(
      1L,
      new Reporte(),
      "no-es-una-imagen",
      requestMock
    );

    assertThat(mav.getViewName(), equalToIgnoringCase("reporte/editar-reporte"));
    assertThat(mav.getModel().containsKey("error"), is(true));
  }

  @Test
  public void sugerirUbicacionesDevuelveLasOpcionesDelServicioSiHaySesion() {
    when(sessionMock.getAttribute("ROL")).thenReturn("USER");
    UbicacionSugerida opcion = new UbicacionSugerida(
      "CARHUE 1526, Mataderos, Buenos Aires",
      "Carhue 1526",
      "Mataderos",
      -34.6595,
      -58.5003,
      "georef"
    );
    when(servicioComercioMock.sugerirUbicaciones("crhue 1526", "Buenos Aires"))
      .thenReturn(List.of(opcion));

    ResponseEntity<List<UbicacionSugerida>> respuesta = controladorReporte.sugerirUbicaciones(
      "crhue 1526",
      "Buenos Aires",
      requestMock
    );

    assertThat(respuesta.getStatusCode().value(), is(200));
    assertThat(respuesta.getBody(), contains(opcion));
  }

  @Test
  public void sugerirUbicacionesSinSesionDevuelveNoAutorizadoYNoConsultaNada() {
    when(sessionMock.getAttribute("ROL")).thenReturn(null);

    ResponseEntity<List<UbicacionSugerida>> respuesta = controladorReporte.sugerirUbicaciones(
      "crhue 1526",
      "Buenos Aires",
      requestMock
    );

    assertThat(respuesta.getStatusCode().value(), is(401));
    verify(servicioComercioMock, never()).sugerirUbicaciones(any(), any());
  }

  // ── últimos reportes del inicio ───────────────────────────────────────────

  private Reporte reporteDe(
    String producto,
    double precio,
    java.time.LocalDateTime fecha,
    Integer puntuacion
  ) {
    Producto unProducto = new Producto();
    unProducto.setNombre(producto);
    unProducto.setMarca("La Serenísima");
    unProducto.setUnidad("1l");
    Comercio unComercio = new Comercio();
    unComercio.setNombre("Carrefour");
    unComercio.setLocalidad("La Matanza");
    Reporte reporte = new Reporte();
    reporte.setProducto(unProducto);
    reporte.setComercio(unComercio);
    reporte.setPrecio(precio);
    reporte.setFechaDeReporte(fecha);
    reporte.setPuntuacion(puntuacion);
    return reporte;
  }

  @Test
  public void ultimosReportesDeberiaDevolverLosMasRecientesPrimero() {
    java.time.LocalDateTime ahora = java.time.LocalDateTime.of(2026, 11, 3, 20, 0);
    Reporte viejo = reporteDe("Leche", 1150.0, ahora.minusDays(2), 0);
    Reporte nuevo = reporteDe("Aceite", 2320.0, ahora.minusHours(1), 0);
    when(servicioReporteMock.listarTodos()).thenReturn(List.of(viejo, nuevo));

    List<java.util.Map<String, Object>> ultimos = controladorReporte.ultimosReportes();

    assertThat(ultimos, hasSize(2));
    assertThat(ultimos.get(0).get("producto"), is("Aceite"));
    assertThat(ultimos.get(0).get("precio"), is(2320.0));
    assertThat(ultimos.get(0).get("comercio"), is("Carrefour"));
    assertThat(ultimos.get(0).get("localidad"), is("La Matanza"));
    assertThat(ultimos.get(1).get("producto"), is("Leche"));
  }

  @Test
  public void ultimosReportesDeberiaIndicarSiElReporteTieneFoto() {
    java.time.LocalDateTime ahora = java.time.LocalDateTime.of(2026, 11, 3, 20, 0);
    Reporte conFoto = reporteDe("Leche", 1150.0, ahora.minusHours(1), 0);
    conFoto.setFoto(new byte[] { 1, 2, 3 });
    Reporte sinFoto = reporteDe("Aceite", 2320.0, ahora.minusHours(2), 0);
    when(servicioReporteMock.listarTodos()).thenReturn(List.of(conFoto, sinFoto));

    List<java.util.Map<String, Object>> ultimos = controladorReporte.ultimosReportes();

    assertThat(ultimos.get(0).get("tieneFoto"), is(true));
    assertThat(ultimos.get(1).get("tieneFoto"), is(false));
  }

  @Test
  public void ultimosReportesNoDeberiaIncluirLosMarcadosComoDudosos() {
    java.time.LocalDateTime ahora = java.time.LocalDateTime.of(2026, 11, 3, 20, 0);
    Reporte confiable = reporteDe("Leche", 1150.0, ahora.minusHours(3), Reporte.PUNTUACION_DUDOSO);
    Reporte dudoso = reporteDe("Aceite", 99.0, ahora.minusHours(1), Reporte.PUNTUACION_DUDOSO + 1);
    when(servicioReporteMock.listarTodos()).thenReturn(List.of(confiable, dudoso));

    List<java.util.Map<String, Object>> ultimos = controladorReporte.ultimosReportes();

    assertThat(ultimos, hasSize(1));
    assertThat(ultimos.get(0).get("producto"), is("Leche"));
  }

  @Test
  public void ultimosReportesDeberiaDevolverComoMuchoSeis() {
    java.time.LocalDateTime ahora = java.time.LocalDateTime.of(2026, 11, 3, 20, 0);
    List<Reporte> muchos = new java.util.ArrayList<>();
    for (int i = 0; i < 9; i++) {
      muchos.add(reporteDe("Producto " + i, 100.0 + i, ahora.minusHours(i), 0));
    }
    when(servicioReporteMock.listarTodos()).thenReturn(muchos);

    assertThat(controladorReporte.ultimosReportes(), hasSize(6));
  }

  @Test
  public void ultimosReportesSinReportesDeberiaDevolverListaVacia() {
    when(servicioReporteMock.listarTodos()).thenReturn(Collections.emptyList());

    assertThat(controladorReporte.ultimosReportes(), is(empty()));
  }

  @Test
  public void resumenDeberiaContarSoloLosReportesDeLosUltimosSieteDias() {
    java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
    Reporte reciente = reporteDe("Leche", 1150.0, ahora.minusDays(1), 0);
    Reporte viejo = reporteDe("Aceite", 2320.0, ahora.minusDays(30), 0);
    when(servicioReporteMock.listarTodos()).thenReturn(List.of(reciente, viejo));

    java.util.Map<String, Object> resumen = controladorReporte.resumenDeReportes();

    assertThat(resumen.get("reportesSemana"), is(1L));
  }

  @Test
  public void resumenDeberiaContarComerciosDistintosConPrecios() {
    java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
    Reporte unoEnElPrimerComercio = reporteDe("Leche", 1150.0, ahora.minusHours(1), 0);
    unoEnElPrimerComercio.getComercio().setId(1L);
    Reporte otroEnElPrimerComercio = reporteDe("Aceite", 2320.0, ahora.minusHours(2), 0);
    otroEnElPrimerComercio.getComercio().setId(1L);
    Reporte enElSegundoComercio = reporteDe("Arroz", 900.0, ahora.minusHours(3), 0);
    enElSegundoComercio.getComercio().setId(2L);
    when(servicioReporteMock.listarTodos())
      .thenReturn(List.of(unoEnElPrimerComercio, otroEnElPrimerComercio, enElSegundoComercio));

    assertThat(controladorReporte.resumenDeReportes().get("comercios"), is(2L));
  }

  @Test
  public void resumenNoDeberiaContarLosReportesDudosos() {
    java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
    Reporte dudoso = reporteDe("Leche", 1.0, ahora.minusHours(1), Reporte.PUNTUACION_DUDOSO + 1);
    when(servicioReporteMock.listarTodos()).thenReturn(List.of(dudoso));

    assertThat(controladorReporte.resumenDeReportes().get("reportesSemana"), is(0L));
  }

  @Test
  public void resumenSinReportesDeberiaDevolverCeros() {
    when(servicioReporteMock.listarTodos()).thenReturn(Collections.emptyList());

    java.util.Map<String, Object> resumen = controladorReporte.resumenDeReportes();

    assertThat(resumen.get("reportesSemana"), is(0L));
    assertThat(resumen.get("comercios"), is(0L));
  }
}
