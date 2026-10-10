package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Reporte.Reporte;
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

  @Autowired
  public ControladorReporte(
    ServicioReporte servicioReporte,
    ServicioProducto servicioProducto,
    ServicioComercio servicioComercio,
    ServicioLogin servicioLogin
  ) {
    this.servicioReporte = servicioReporte;
    this.servicioProducto = servicioProducto;
    this.servicioComercio = servicioComercio;
    this.servicioLogin = servicioLogin;
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

  private ModelAndView vistaConError(String mensaje) {
    ModelAndView mav = new ModelAndView(VISTA_REPORTES);
    mav.addObject("error", mensaje);
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
