package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Comercio.TipoDeComercio;
import com.tallerwebi.dominio.Comercio.UbicacionSugerida;
import com.tallerwebi.dominio.Paginador.ServicioPaginador;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Reporte.ItemTicket;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ReporteConDistancia;
import com.tallerwebi.dominio.Reporte.ResumenDeActividad;
import com.tallerwebi.dominio.Reporte.ServicioOcr;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import com.tallerwebi.dominio.Reporte.TicketLeido;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import com.tallerwebi.dominio.excepcion.CamposObligatoriosVacios;
import com.tallerwebi.dominio.excepcion.FotoInvalida;
import com.tallerwebi.dominio.excepcion.PrecioIncorrecto;
import com.tallerwebi.dominio.excepcion.ReporteExistente;
import com.tallerwebi.dominio.excepcion.ReportePropio;
import com.tallerwebi.dominio.excepcion.UnidadInvalida;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/reporte")
public class ControladorReporte {

  private static final Logger LOGGER = LoggerFactory.getLogger(ControladorReporte.class);

  private ServicioReporte servicioReporte;
  private ServicioProducto servicioProducto;
  private ServicioComercio servicioComercio;
  private ServicioLogin servicioLogin;
  private ServicioPaginador servicioPaginador;
  private static final String VISTA_REPORTES = "reporte/lista-reportes";
  private static final String VISTA_SUBIR_TICKET = "reporte/subir-ticket";
  private static final String NOMBRE_PRODUCTOS = "productos";
  private static final String NOMBRE_COMERCIOS = "comercios";
  private static final String NOMBRE_REPORTES = "reportes";
  private static final String PARAM_NOMBRE = "nombre";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String SESSION_ROL = "ROL";
  private static final String PARAM_VOLVER_A = "volverA";
  private static final String ORIGEN_PERFIL = "perfil";
  private static final String SESSION_EMAIL = "EMAIL";
  private static final String NOMBRE_ERROR = "error";
  private static final String NOMBRE_LECTURA_FALLIDA = "lecturaFallida";
  private static final String PREFIJO_ITEMS = "items[";
  private static final String ROL_COMERCIO = "COMERCIO";
  private static final String PARAM_PAGINA = "pagina";
  private static final int CANTIDAD_DE_ULTIMOS_REPORTES = 6;

  @Autowired
  private ServicioOcr servicioOcr;

  @Autowired
  public ControladorReporte(
    ServicioReporte servicioReporte,
    ServicioProducto servicioProducto,
    ServicioComercio servicioComercio,
    ServicioLogin servicioLogin,
    ServicioOcr servicioOcr,
    ServicioPaginador servicioPaginador
  ) {
    this.servicioReporte = servicioReporte;
    this.servicioProducto = servicioProducto;
    this.servicioComercio = servicioComercio;
    this.servicioLogin = servicioLogin;
    this.servicioOcr = servicioOcr;
    this.servicioPaginador = servicioPaginador;
  }

  public ModelAndView guardarReporte(@ModelAttribute Reporte reporte, HttpServletRequest request) {
    return guardarReporte(reporte, null, request);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ModelAndView guardarReporte(
    @ModelAttribute Reporte reporte,
    @RequestParam(value = "fotoBase64", required = false) String fotoBase64,
    HttpServletRequest request
  ) {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    try {
      String email = (String) request.getSession().getAttribute(SESSION_EMAIL);
      Usuario usuario = servicioLogin.buscarPorEmail(email);
      String errorDeFoto = guardarFoto(reporte, fotoBase64);
      if (errorDeFoto != null) {
        return vistaConError(errorDeFoto, request);
      }
      reporte.setUsuario(usuario);
      servicioReporte.guardarReporte(reporte);
    } catch (ReporteExistente e) {
      return vistaConError("Ya reportaste este precio hoy", request);
    } catch (CamposObligatoriosVacios | UnidadInvalida | PrecioIncorrecto e) {
      return vistaConError(e.getMessage(), request);
    }
    return new ModelAndView("redirect:/reporte?guardado=true");
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  public ModelAndView buscarReporte(@PathVariable("id") Long id) {
    Reporte reporte = servicioReporte.buscarReporte(id);
    if (reporte == null) {
      return new ModelAndView("redirect:/reporte");
    }
    ModelAndView mav = new ModelAndView(VISTA_REPORTES);
    mav.addObject(NOMBRE_REPORTES, Collections.singletonList(reporte));
    return mav;
  }

  @GetMapping("/{id}/foto")
  @ResponseBody
  public byte[] obtenerFoto(@PathVariable("id") Long id) {
    Reporte reporte = servicioReporte.buscarReporte(id);
    if (reporte == null || reporte.getFoto() == null) {
      return new byte[0];
    }
    return reporte.getFoto();
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView listarTodos(
    HttpServletRequest request,
    @RequestParam(value = "guardado", required = false) Boolean guardado
  ) {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    List<Reporte> reportes = servicioReporte.listarTodos();
    ModelAndView mav = new ModelAndView("reporte/lista-reportes");
    mav.addObject(NOMBRE_REPORTES, reportes);
    mav.addObject(NOMBRE_PRODUCTOS, servicioProducto.listarTodos());
    mav.addObject(NOMBRE_COMERCIOS, servicioComercio.listarTodos());
    mav.addObject("guardado", Boolean.TRUE.equals(guardado));

    // Para rol COMERCIO: pasar su propio comercio para pre-seleccionar en el form
    String rol = (String) request.getSession().getAttribute(SESSION_ROL);
    if (ROL_COMERCIO.equals(rol)) {
      Long usuarioId = (Long) request.getSession().getAttribute("ID");
      Comercio comercioDelUsuario = servicioComercio.buscarComercioPorUsuarioId(usuarioId);
      mav.addObject("comercioDelUsuario", comercioDelUsuario);
    }
    return mav;
  }

  @RequestMapping(path = "/{id}/dudoso", method = RequestMethod.POST)
  public ModelAndView marcarDudoso(
    @PathVariable("id") Long id,
    @RequestParam("nombreProducto") String nombreProducto,
    HttpServletRequest request,
    RedirectAttributes redirectAttributes
  ) {
    String email = (String) request.getSession().getAttribute(SESSION_EMAIL);
    Usuario usuario = Optional.ofNullable(email).map(servicioLogin::buscarPorEmail).orElse(null);
    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    try {
      servicioReporte.marcarDudoso(id, usuario);
    } catch (ReportePropio e) {
      redirectAttributes.addFlashAttribute("errorDudoso", e.getMessage());
      return new ModelAndView(urlBusquedaDeProducto(nombreProducto, false));
    }
    return new ModelAndView(urlBusquedaDeProducto(nombreProducto, true));
  }

  @RequestMapping(path = "/{id}/desmarcar-dudoso", method = RequestMethod.POST)
  public ModelAndView desmarcarDudoso(
    @PathVariable("id") Long id,
    @RequestParam("nombreProducto") String nombreProducto,
    HttpServletRequest request,
    RedirectAttributes redirectAttributes
  ) {
    String email = (String) request.getSession().getAttribute(SESSION_EMAIL);
    Usuario usuario = Optional.ofNullable(email).map(servicioLogin::buscarPorEmail).orElse(null);
    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    servicioReporte.desmarcarDudoso(id, usuario);
    redirectAttributes.addFlashAttribute("dudosoDesmarcado", true);
    return new ModelAndView(urlBusquedaDeProducto(nombreProducto, false));
  }

  // El nombre viaja en la URL: se codifica para que "&", "#" o "{" no rompan la redirección
  private String urlBusquedaDeProducto(String nombreProducto, boolean dudosoMarcado) {
    UriComponentsBuilder url = UriComponentsBuilder
      .fromPath("/producto/buscar")
      .queryParam(PARAM_NOMBRE, "{nombre}");
    if (dudosoMarcado) {
      url.queryParam("dudosoMarcado", true);
    }
    return "redirect:" + url.encode().buildAndExpand(nombreProducto).toUriString();
  }

  @RequestMapping(path = "/buscar", method = RequestMethod.GET)
  public ModelAndView buscarPorNombre(
    @RequestParam(PARAM_NOMBRE) String nombre,
    HttpServletRequest request
  ) {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    List<Reporte> resultados = servicioReporte.buscarPorNombre(nombre);
    ModelAndView mav = new ModelAndView(VISTA_REPORTES);
    mav.addObject(NOMBRE_REPORTES, resultados);
    mav.addObject(NOMBRE_PRODUCTOS, servicioProducto.listarTodos());
    mav.addObject(NOMBRE_COMERCIOS, servicioComercio.listarTodos());
    mav.addObject("busqueda", nombre);
    return mav;
  }

  // Lo usa el botón "Buscar en el mapa" del formulario de reporte: devuelve las opciones en JSON.
  // Cualquier cuenta con sesión puede ubicar un local; el resultado solo se guarda si el
  // vecino confirma una opción y envía el reporte.
  @RequestMapping(path = "/ubicaciones/sugerencias", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<List<UbicacionSugerida>> sugerirUbicaciones(
    @RequestParam(value = "direccion", required = false) String direccion,
    @RequestParam(value = "localidad", required = false) String localidad,
    HttpServletRequest request
  ) {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    return ResponseEntity.ok(servicioComercio.sugerirUbicaciones(direccion, localidad));
  }

  // Lo usa el inicio del vecino: los últimos precios reportados, sin los marcados como dudosos
  @RequestMapping(path = "/ultimos", method = RequestMethod.GET)
  @ResponseBody
  public List<Map<String, Object>> ultimosReportes() {
    return servicioReporte
      .ultimosReportesVisibles(CANTIDAD_DE_ULTIMOS_REPORTES)
      .stream()
      .map(this::reporteParaElInicio)
      .collect(java.util.stream.Collectors.toList());
  }

  // Lo mismo, pero los de los comercios más cercanos al vecino, con la distancia en km
  @RequestMapping(path = "/ultimos-cercanos", method = RequestMethod.GET)
  public ResponseEntity<List<Map<String, Object>>> ultimosReportesCercanos(
    @RequestParam("lat") double latitud,
    @RequestParam("lng") double longitud
  ) {
    if (!coordenadasValidas(latitud, longitud)) {
      return ResponseEntity.badRequest().build();
    }
    List<Map<String, Object>> cercanos = servicioReporte
      .ultimosReportesVisiblesMasCercanos(latitud, longitud, CANTIDAD_DE_ULTIMOS_REPORTES)
      .stream()
      .map(c -> {
        Map<String, Object> reporte = reporteParaElInicio(c.reporte());
        reporte.put("distanciaKm", Math.round(c.distanciaKm() * 10) / 10.0);
        return reporte;
      })
      .collect(java.util.stream.Collectors.toList());
    return ResponseEntity.ok(cercanos);
  }

  private boolean coordenadasValidas(double latitud, double longitud) {
    // NaN e infinito nunca cumplen estas comparaciones, así que también quedan afuera
    return Math.abs(latitud) <= 90 && Math.abs(longitud) <= 180;
  }

  private Map<String, Object> reporteParaElInicio(Reporte ultimo) {
    Map<String, Object> reporte = new java.util.LinkedHashMap<>();
    reporte.put("id", ultimo.getId());
    reporte.put("producto", ultimo.getProducto().getNombre());
    reporte.put("marca", ultimo.getProducto().getMarca());
    reporte.put("unidad", ultimo.getProducto().getUnidad());
    reporte.put("precio", ultimo.getPrecio());
    reporte.put("comercio", ultimo.getComercio().getNombre());
    reporte.put("direccion", ultimo.getComercio().getDireccion());
    reporte.put("localidad", ultimo.getComercio().getLocalidad());
    reporte.put("tieneFoto", ultimo.getFoto() != null && ultimo.getFoto().length > 0);
    reporte.put("imagenUrl", ultimo.getProducto().getImagenUrl());
    reporte.put("fecha", ultimo.getFechaDeReporte().toString());
    return reporte;
  }

  // Números de confianza del inicio: cuánto se reportó esta semana y en cuántos comercios hay precios
  @RequestMapping(path = "/resumen", method = RequestMethod.GET)
  @ResponseBody
  public Map<String, Object> resumenDeReportes() {
    ResumenDeActividad actividad = servicioReporte.resumenDeActividad();
    Map<String, Object> resumen = new java.util.LinkedHashMap<>();
    resumen.put("reportesSemana", actividad.getReportesSemana());
    resumen.put("comercios", actividad.getComercios());
    return resumen;
  }

  @RequestMapping(path = "/mis-reportes", method = RequestMethod.GET)
  public ModelAndView misReportes(
    HttpServletRequest request,
    @RequestParam(value = PARAM_PAGINA, required = false, defaultValue = "0") int pagina
  ) {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    Long usuarioId = (Long) request.getSession().getAttribute("ID");
    List<Reporte> todos = servicioReporte.buscarPorUsuarioId(usuarioId);

    int tamano = 9;
    int totalPaginas = servicioPaginador.calcularTotalPaginas(todos.size(), tamano);
    List<Reporte> reportesPagina = servicioPaginador.paginar(todos, pagina, tamano);

    ModelAndView mav = new ModelAndView("reporte/mis-reportes-comercio");
    mav.addObject("reportes", reportesPagina);
    mav.addObject(PARAM_PAGINA, pagina);
    mav.addObject("totalPaginas", totalPaginas);
    return mav;
  }

  @RequestMapping(path = "/{id}/editar", method = RequestMethod.GET)
  public ModelAndView verFormularioEdicion(
    @PathVariable("id") Long id,
    HttpServletRequest request
  ) {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    Long usuarioId = (Long) request.getSession().getAttribute("ID");
    if (!servicioReporte.puedeEditar(id, usuarioId)) {
      return new ModelAndView("redirect:/reporte/mis-reportes");
    }
    ModelAndView mav = new ModelAndView("reporte/editar-reporte");
    mav.addObject("reporte", servicioReporte.buscarReporte(id));
    return mav;
  }

  @RequestMapping(path = "/{id}/editar", method = RequestMethod.POST)
  public ModelAndView editarReporte(
    @PathVariable("id") Long id,
    @ModelAttribute Reporte reporteEditado,
    @RequestParam(value = "fotoBase64", required = false) String fotoBase64,
    HttpServletRequest request
  ) {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    Long usuarioId = (Long) request.getSession().getAttribute("ID");
    String errorDeFoto = guardarFoto(reporteEditado, fotoBase64);
    if (errorDeFoto != null) {
      ModelAndView mav = new ModelAndView("reporte/editar-reporte");
      mav.addObject("reporte", servicioReporte.buscarReporte(id));
      mav.addObject(NOMBRE_ERROR, errorDeFoto);
      return mav;
    }
    try {
      servicioReporte.editarReporte(id, reporteEditado, usuarioId);
    } catch (Exception e) {
      ModelAndView mav = new ModelAndView("reporte/editar-reporte");
      mav.addObject("reporte", servicioReporte.buscarReporte(id));
      mav.addObject(NOMBRE_ERROR, e.getMessage());
      return mav;
    }
    return new ModelAndView("redirect:/reporte/mis-reportes?editado=true");
  }

  @RequestMapping(path = "/{id}/eliminar", method = RequestMethod.POST)
  public ModelAndView eliminarReporte(@PathVariable("id") Long id, HttpServletRequest request) {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    // El dueño sale siempre de la sesión: lo que manda el cliente es solo el id del reporte
    Long usuarioId = (Long) request.getSession().getAttribute("ID");
    // Desde el perfil se vuelve al perfil; cualquier otro valor se ignora (no hay redirecciones libres)
    String destino = ORIGEN_PERFIL.equals(request.getParameter(PARAM_VOLVER_A))
      ? "redirect:/perfil"
      : "redirect:/reporte/mis-reportes";
    try {
      servicioReporte.eliminarReporte(id, usuarioId);
    } catch (AccesoDenegado e) {
      return new ModelAndView(destino + "?errorEliminar=true");
    }
    return new ModelAndView(destino + "?eliminado=true");
  }

  private ModelAndView vistaConError(String mensaje, HttpServletRequest request) {
    ModelAndView mav = new ModelAndView(VISTA_REPORTES);
    mav.addObject(NOMBRE_ERROR, mensaje);
    mav.addObject(NOMBRE_REPORTES, servicioReporte.listarTodos());
    mav.addObject(NOMBRE_PRODUCTOS, servicioProducto.listarTodos());
    mav.addObject(NOMBRE_COMERCIOS, servicioComercio.listarTodos());
    String rol = (String) request.getSession().getAttribute(SESSION_ROL);
    if (ROL_COMERCIO.equals(rol)) {
      Long usuarioId = (Long) request.getSession().getAttribute("ID");
      Comercio comercioDelUsuario = servicioComercio.buscarComercioPorUsuarioId(usuarioId);
      mav.addObject("comercioDelUsuario", comercioDelUsuario);
    }
    return mav;
  }

  @RequestMapping(path = "/mis-productos", method = RequestMethod.GET)
  public ModelAndView misProductos(
    HttpServletRequest request,
    @RequestParam(value = PARAM_PAGINA, required = false, defaultValue = "0") int pagina
  ) {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    Long usuarioId = (Long) request.getSession().getAttribute("ID");
    Comercio comercio = servicioComercio.buscarComercioPorUsuarioId(usuarioId);
    ModelAndView mav = new ModelAndView("reporte/mis-productos-comercio");

    List<Reporte> todos;
    if (comercio == null) {
      todos = java.util.Collections.emptyList();
    } else {
      todos = servicioReporte.buscarPorComercio(comercio.getId());
      mav.addObject("comercio", comercio);
    }

    int tamano = 9;
    int totalPaginas = servicioPaginador.calcularTotalPaginas(todos.size(), tamano);
    List<Reporte> reportesPagina = servicioPaginador.paginar(todos, pagina, tamano);

    mav.addObject("reportes", reportesPagina);
    mav.addObject(PARAM_PAGINA, pagina);
    mav.addObject("totalPaginas", totalPaginas);
    return mav;
  }

  @RequestMapping(path = "/ticket", method = RequestMethod.GET)
  public ModelAndView verFormularioTicket(HttpServletRequest request) {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    ModelAndView mav = new ModelAndView(VISTA_SUBIR_TICKET);
    mav.addObject(NOMBRE_COMERCIOS, servicioComercio.listarTodos());
    return mav;
  }

  @RequestMapping(path = "/ticket", method = RequestMethod.POST)
  public ModelAndView analizarTicket(
    @RequestParam("ticketBase64") String ticketBase64,
    @RequestParam("comercio.nombre") String comercioNombre,
    @RequestParam("comercio.direccion") String comercioDireccion,
    @RequestParam("comercio.localidad") String comercioLocalidad,
    HttpServletRequest request
  ) {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    if (estaVacio(ticketBase64)) {
      ModelAndView mav = new ModelAndView(VISTA_SUBIR_TICKET);
      mav.addObject(NOMBRE_ERROR, "Debes subir una foto del ticket");
      mav.addObject(NOMBRE_COMERCIOS, servicioComercio.listarTodos());
      return mav;
    }
    if (faltanDatosDelComercio(comercioNombre, comercioDireccion, comercioLocalidad)) {
      ModelAndView mav = new ModelAndView(VISTA_SUBIR_TICKET);
      mav.addObject(NOMBRE_ERROR, "Completá el nombre, la dirección y la localidad del comercio");
      mav.addObject(NOMBRE_COMERCIOS, servicioComercio.listarTodos());
      return mav;
    }
    try {
      int inicio = ticketBase64.indexOf(',');
      byte[] bytes = java.util.Base64.getDecoder().decode(ticketBase64.substring(inicio + 1));
      TicketLeido ticket = servicioOcr.leerTicket(bytes);
      ModelAndView mav = new ModelAndView("reporte/confirmar-ticket");
      mav.addObject("items", ticket.items());
      mav.addObject("comercioTipo", ticket.tipoComercio());
      mav.addObject("comercioNombre", comercioNombre);
      mav.addObject("comercioDireccion", comercioDireccion);
      mav.addObject("comercioLocalidad", comercioLocalidad);
      return mav;
    } catch (Exception e) {
      // El vecino ve un mensaje genérico; el motivo real (clave, cuota, modelo) queda en el log
      LOGGER.error("No se pudo leer el ticket con la IA", e);
      ModelAndView mav = new ModelAndView(VISTA_SUBIR_TICKET);
      mav.addObject(NOMBRE_ERROR, "No pudimos leer el ticket en este momento.");
      mav.addObject(NOMBRE_LECTURA_FALLIDA, true);
      mav.addObject(NOMBRE_COMERCIOS, servicioComercio.listarTodos());
      // Se devuelven los datos del comercio para que no tenga que escribirlos de nuevo
      mav.addObject("comercioNombre", comercioNombre);
      mav.addObject("comercioDireccion", comercioDireccion);
      mav.addObject("comercioLocalidad", comercioLocalidad);
      return mav;
    }
  }

  private boolean faltanDatosDelComercio(String nombre, String direccion, String localidad) {
    return estaVacio(nombre) || estaVacio(direccion) || estaVacio(localidad);
  }

  private boolean estaVacio(String texto) {
    return texto == null || texto.isBlank();
  }

  @RequestMapping(path = "/ticket/guardar", method = RequestMethod.POST)
  public ModelAndView guardarTicket(
    @RequestParam("comercioNombre") String comercioNombre,
    @RequestParam("comercioDireccion") String comercioDireccion,
    @RequestParam("comercioLocalidad") String comercioLocalidad,
    @RequestParam Map<String, String> params,
    HttpServletRequest request
  ) {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    String email = (String) request.getSession().getAttribute(SESSION_EMAIL);
    Usuario usuario = servicioLogin.buscarPorEmail(email);

    Comercio comercio = new Comercio();
    comercio.setNombre(comercioNombre);
    comercio.setDireccion(comercioDireccion);
    comercio.setLocalidad(comercioLocalidad);
    // La pista del nombre manda; si no hay, el tipo que dedujo el OCR; si no, "Otro"
    comercio.setTipo(TipoDeComercio.elegir(comercioNombre, params.get("comercioTipo")));

    // Cada producto se guarda por separado: si uno falla, los demás igual se guardan
    List<String> errores = new ArrayList<>();
    int indice = 0;
    while (params.containsKey(PREFIJO_ITEMS + indice + "].nombre")) {
      if (params.get("incluir[" + indice + "]") != null) {
        String error = guardarItemDelTicket(
          params,
          PREFIJO_ITEMS + indice + "].",
          comercio,
          usuario
        );
        if (error != null) {
          errores.add(error);
        }
      }
      indice++;
    }
    if (!errores.isEmpty()) {
      return vistaConError(
        "Algunos productos del ticket no se guardaron. " + String.join(" · ", errores),
        request
      );
    }
    return new ModelAndView("redirect:/reporte?guardado=true");
  }

  // Devuelve null si se guardó (o si se descartó por precio no numérico), o el motivo del error
  private String guardarItemDelTicket(
    Map<String, String> params,
    String prefijo,
    Comercio comercio,
    Usuario usuario
  ) {
    String nombre = params.get(prefijo + "nombre");
    String precioTexto = params.get(prefijo + "precio");
    double precio;
    try {
      precio = Double.parseDouble(Objects.toString(precioTexto, ""));
    } catch (NumberFormatException e) {
      return null;
    }
    String unidad = params.getOrDefault(prefijo + "unidad", "");
    Producto producto = new Producto();
    producto.setNombre(nombre);
    producto.setMarca(params.getOrDefault(prefijo + "marca", "Sin marca"));
    producto.setCategoria(params.getOrDefault(prefijo + "categoria", "Varios"));
    producto.setUnidad(unidad.isBlank() ? "1u" : unidad);
    Reporte reporte = new Reporte();
    reporte.setProducto(producto);
    reporte.setPrecio(precio);
    reporte.setComercio(comercio);
    reporte.setUsuario(usuario);
    try {
      servicioReporte.guardarOReemplazarReporteDeHoy(reporte);
      return null;
    } catch (CamposObligatoriosVacios | UnidadInvalida | PrecioIncorrecto e) {
      return nombre + ": " + e.getMessage();
    }
  }

  @RequestMapping(path = "/ticket/reconocer-producto", method = RequestMethod.POST)
  @ResponseBody
  public String reconocerProducto(
    @RequestParam("imagenBase64") String imagenBase64,
    HttpServletRequest request
  ) {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return "{\"error\":\"no autenticado\"}";
    }
    try {
      int inicio = imagenBase64.indexOf(',');
      byte[] bytes = java.util.Base64.getDecoder().decode(imagenBase64.substring(inicio + 1));
      ItemTicket item = servicioOcr.reconocerProducto(bytes);
      if (item == null) return "{\"error\":\"no detectado\"}";
      com.google.gson.JsonObject json = new com.google.gson.JsonObject();
      json.addProperty("nombre", item.getNombre());
      json.addProperty("marca", item.getMarca());
      json.addProperty("categoria", item.getCategoria());
      json.addProperty("unidad", item.getUnidad());
      return json.toString();
    } catch (Exception e) {
      return "{\"error\":\"" + e.getMessage() + "\"}";
    }
  }

  private String guardarFoto(Reporte reporte, String fotoBase64) {
    if (estaVacio(fotoBase64)) {
      return null;
    }
    if (!fotoBase64.startsWith("data:image/")) {
      return "El archivo seleccionado debe ser una imagen";
    }
    int inicioDelContenido = fotoBase64.indexOf(',');
    if (inicioDelContenido < 0) {
      return "No se pudo cargar la foto";
    }
    try {
      byte[] foto = Base64.getDecoder().decode(fotoBase64.substring(inicioDelContenido + 1));
      servicioReporte.validarFoto(foto);
      reporte.setFoto(foto);
      return null;
    } catch (FotoInvalida e) {
      return e.getMessage();
    } catch (IllegalArgumentException e) {
      return "No se pudo cargar la foto";
    }
  }
}
