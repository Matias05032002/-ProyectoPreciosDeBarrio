package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Reporte.ItemTicket;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioOcr;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.CamposObligatoriosVacios;
import com.tallerwebi.dominio.excepcion.PrecioIncorrecto;
import com.tallerwebi.dominio.excepcion.ReporteExistente;
import com.tallerwebi.dominio.excepcion.UnidadInvalida;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/reporte")
public class ControladorReporte {

  private ServicioReporte servicioReporte;
  private ServicioProducto servicioProducto;
  private ServicioComercio servicioComercio;
  private ServicioLogin servicioLogin;
  private static final String VISTA_REPORTES = "reporte/lista-reportes";
  private static final String NOMBRE_PRODUCTOS = "productos";
  private static final String NOMBRE_COMERCIOS = "comercios";
  private static final String NOMBRE_REPORTES = "reportes";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String SESSION_ROL = "ROL";
  private static final String NOMBRE_ERROR = "error";

  @Autowired
  private ServicioOcr servicioOcr;

  @Autowired
  public ControladorReporte(
    ServicioReporte servicioReporte,
    ServicioProducto servicioProducto,
    ServicioComercio servicioComercio,
    ServicioLogin servicioLogin,
    ServicioOcr servicioOcr
  ) {
    this.servicioReporte = servicioReporte;
    this.servicioProducto = servicioProducto;
    this.servicioComercio = servicioComercio;
    this.servicioLogin = servicioLogin;
    this.servicioOcr = servicioOcr;
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
      String email = (String) request.getSession().getAttribute("EMAIL");
      Usuario usuario = servicioLogin.buscarPorEmail(email);
      String errorDeFoto = guardarFoto(reporte, fotoBase64);
      if (errorDeFoto != null) {
        return vistaConError(errorDeFoto);
      }
      reporte.setUsuario(usuario);
      servicioReporte.guardarReporte(reporte);
    } catch (ReporteExistente e) {
      return vistaConError("Ya reportaste este precio hoy");
    } catch (CamposObligatoriosVacios | UnidadInvalida | PrecioIncorrecto e) {
      return vistaConError(e.getMessage());
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
    mav.addObject("guardado", guardado != null && guardado);
    return mav;
  }

  @RequestMapping(path = "/{id}/dudoso", method = RequestMethod.POST)
  public ModelAndView marcarDudoso(
    @PathVariable("id") Long id,
    @RequestParam("nombreProducto") String nombreProducto,
    HttpServletRequest request
  ) {
    String email = (String) request.getSession().getAttribute("EMAIL");
    if (email == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    Usuario usuario = servicioLogin.buscarPorEmail(email);
    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    servicioReporte.marcarDudoso(id, usuario);
    return new ModelAndView(
      "redirect:/producto/buscar?nombre=" + nombreProducto + "&dudosoMarcado=true"
    );
  }

  @RequestMapping(path = "/buscar", method = RequestMethod.GET)
  public ModelAndView buscarPorNombre(
    @RequestParam("nombre") String nombre,
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

  @RequestMapping(path = "/mis-reportes", method = RequestMethod.GET)
  public ModelAndView misReportes(HttpServletRequest request) {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    Long usuarioId = (Long) request.getSession().getAttribute("ID");
    List<Reporte> reportes = servicioReporte.buscarPorUsuarioId(usuarioId);
    ModelAndView mav = new ModelAndView("reporte/mis-reportes-comercio");
    mav.addObject(NOMBRE_REPORTES, reportes);
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

  private ModelAndView vistaConError(String mensaje) {
    ModelAndView mav = new ModelAndView(VISTA_REPORTES);
    mav.addObject(NOMBRE_ERROR, mensaje);
    mav.addObject(NOMBRE_REPORTES, servicioReporte.listarTodos());
    mav.addObject(NOMBRE_PRODUCTOS, servicioProducto.listarTodos());
    mav.addObject(NOMBRE_COMERCIOS, servicioComercio.listarTodos());
    return mav;
  }

  @RequestMapping(path = "/mis-productos", method = RequestMethod.GET)
  public ModelAndView misProductos(HttpServletRequest request) {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    Long usuarioId = (Long) request.getSession().getAttribute("ID");
    Comercio comercio = servicioComercio.buscarComercioPorUsuarioId(usuarioId);
    ModelAndView mav = new ModelAndView("reporte/mis-productos-comercio");
    if (comercio == null) {
      mav.addObject("reportes", java.util.Collections.emptyList());
    } else {
      List<Reporte> reportes = servicioReporte.buscarPorComercio(comercio.getId());
      mav.addObject("reportes", reportes);
    }
    return mav;
  }

  @RequestMapping(path = "/ticket", method = RequestMethod.GET)
  public ModelAndView verFormularioTicket(HttpServletRequest request) {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    ModelAndView mav = new ModelAndView("reporte/subir-ticket");
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
    if (ticketBase64 == null || ticketBase64.isBlank()) {
      ModelAndView mav = new ModelAndView("reporte/subir-ticket");
      mav.addObject(NOMBRE_ERROR, "Debes subir una foto del ticket");
      mav.addObject(NOMBRE_COMERCIOS, servicioComercio.listarTodos());
      return mav;
    }
    try {
      int inicio = ticketBase64.indexOf(',');
      byte[] bytes = java.util.Base64.getDecoder().decode(ticketBase64.substring(inicio + 1));
      List<ItemTicket> items = servicioOcr.extraerProductosDeTicket(bytes);
      ModelAndView mav = new ModelAndView("reporte/confirmar-ticket");
      mav.addObject("items", items);
      mav.addObject("comercioNombre", comercioNombre);
      mav.addObject("comercioDireccion", comercioDireccion);
      mav.addObject("comercioLocalidad", comercioLocalidad);
      return mav;
    } catch (Exception e) {
      ModelAndView mav = new ModelAndView("reporte/subir-ticket");
      mav.addObject(NOMBRE_ERROR, "No se pudo procesar el ticket");
      mav.addObject(NOMBRE_COMERCIOS, servicioComercio.listarTodos());
      return mav;
    }
  }

  @RequestMapping(path = "/ticket/guardar", method = RequestMethod.POST)
  public ModelAndView guardarTicket(
    @RequestParam("comercioNombre") String comercioNombre,
    @RequestParam("comercioDireccion") String comercioDireccion,
    @RequestParam("comercioLocalidad") String comercioLocalidad,
    @RequestParam Map<String, String> params,
    HttpServletRequest request
  ) throws CamposObligatoriosVacios, ReporteExistente, UnidadInvalida, PrecioIncorrecto {
    if (request.getSession().getAttribute(SESSION_ROL) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    String email = (String) request.getSession().getAttribute("EMAIL");
    Usuario usuario = servicioLogin.buscarPorEmail(email);

    Comercio comercio = new Comercio();
    comercio.setNombre(comercioNombre);
    comercio.setDireccion(comercioDireccion);
    comercio.setLocalidad(comercioLocalidad);

    int indice = 0;
    while (params.containsKey("items[" + indice + "].nombre")) {
      String incluir = params.get("incluir[" + indice + "]");
      if (incluir != null) {
        String nombre = params.get("items[" + indice + "].nombre");
        String precioStr = params.get("items[" + indice + "].precio");
        try {
          double precio = Double.parseDouble(precioStr);
          Producto producto = new Producto();
          producto.setNombre(nombre);
          producto.setMarca("Sin marca");
          producto.setCategoria("Varios");
          producto.setUnidad("1g");
          Reporte reporte = new Reporte();
          reporte.setProducto(producto);
          reporte.setPrecio(precio);
          reporte.setComercio(comercio);
          reporte.setUsuario(usuario);
          servicioReporte.guardarReporte(reporte);
        } catch (NumberFormatException ex) {
          assert ex != null;
        }
      }
      indice++;
    }
    return new ModelAndView("redirect:/reporte?guardado=true");
  }

  private String guardarFoto(Reporte reporte, String fotoBase64) {
    if (fotoBase64 == null || fotoBase64.isBlank()) {
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
      if (foto.length > 10 * 1024 * 1024) {
        return "La foto no puede superar los 10 MB";
      }
      reporte.setFoto(foto);
      return null;
    } catch (IllegalArgumentException e) {
      return "No se pudo cargar la foto";
    }
  }
}
