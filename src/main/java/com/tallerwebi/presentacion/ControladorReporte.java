package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.ReporteExistente;
import jakarta.servlet.http.HttpServletRequest;
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

  @RequestMapping(method = RequestMethod.POST)
  public ModelAndView guardarReporte(@ModelAttribute Reporte reporte, HttpServletRequest request) {
    if (request.getSession().getAttribute("ROL") == null) {
      return new ModelAndView("redirect:/login");
    }
    String error = validarReporte(reporte);
    if (error != null) {
      return vistaConError(error);
    }
    try {
      String email = (String) request.getSession().getAttribute("EMAIL");
      Usuario usuario = servicioLogin.buscarPorEmail(email);
      reporte.setUsuario(usuario);
      servicioReporte.guardarReporte(reporte);
    } catch (ReporteExistente e) {
      return vistaConError("Ya reportaste este precio hoy");
    }
    return new ModelAndView("redirect:/reporte");
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  public ModelAndView buscarReporte(@PathVariable("id") Long id) {
    Reporte reporte = servicioReporte.buscarReporte(id);
    ModelAndView mav = new ModelAndView(VISTA_REPORTES);
    mav.addObject(NOMBRE_REPORTES, Collections.singletonList(reporte));
    return mav;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView listarTodos(HttpServletRequest request) {
    if (request.getSession().getAttribute("ROL") == null) {
      return new ModelAndView("redirect:/login");
    }
    List<Reporte> reportes = servicioReporte.listarTodos();
    ModelAndView mav = new ModelAndView("reporte/lista-reportes");
    mav.addObject(NOMBRE_REPORTES, reportes);
    mav.addObject(NOMBRE_PRODUCTOS, servicioProducto.listarTodos());
    mav.addObject(NOMBRE_COMERCIOS, servicioComercio.listarTodos());
    return mav;
  }

  @RequestMapping(path = "/{id}/dudoso", method = RequestMethod.POST)
  public ModelAndView marcarDudoso(
    @PathVariable("id") Long id,
    @RequestParam("nombreProducto") String nombreProducto,
    HttpServletRequest request
  ) {
    String email = (String) request.getSession().getAttribute("EMAIL");
    Usuario usuario = servicioLogin.buscarPorEmail(email);
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
    if (request.getSession().getAttribute("ROL") == null) {
      return new ModelAndView("redirect:/login");
    }
    List<Reporte> resultados = servicioReporte.buscarPorNombre(nombre);
    ModelAndView mav = new ModelAndView(VISTA_REPORTES);
    mav.addObject(NOMBRE_REPORTES, resultados);
    mav.addObject(NOMBRE_PRODUCTOS, servicioProducto.listarTodos());
    mav.addObject(NOMBRE_COMERCIOS, servicioComercio.listarTodos());
    mav.addObject("busqueda", nombre);
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

  private boolean esVacio(String valor) {
    return valor == null || valor.trim().isEmpty();
  }

  private String validarReporte(Reporte reporte) {
    String errorCampos = validarCamposObligatorios(reporte);
    if (errorCampos != null) return errorCampos;

    String errorUnidad = validarUnidad(reporte.getProducto().getUnidad());
    if (errorUnidad != null) return errorUnidad;

    String errorPrecio = validarPrecio(reporte.getPrecio());
    if (errorPrecio != null) return errorPrecio;

    return null;
  }

  private String validarCamposObligatorios(Reporte reporte) {
    if (reporte.getProducto() == null || reporte.getComercio() == null) {
      return "Todos los campos son obligatorios";
    }
    List<String> campos = java.util.Arrays.asList(
      reporte.getProducto().getNombre(),
      reporte.getProducto().getMarca(),
      reporte.getProducto().getCategoria(),
      reporte.getProducto().getUnidad(),
      reporte.getComercio().getNombre(),
      reporte.getComercio().getDireccion(),
      reporte.getComercio().getLocalidad()
    );
    if (campos.stream().anyMatch(this::esVacio)) {
      return "Todos los campos son obligatorios";
    }
    return null;
  }

  private String validarUnidad(String unidad) {
    if (!unidad.matches("^[0-9]+(g|kg|ml|ML|L|gr|Kg|KG)$")) {
      return "La unidad debe ser un número seguido de g, kg, ml, L o gr (ej: 500g, 1kg, 250ml)";
    }
    return null;
  }

  private String validarPrecio(Double precio) {
    if (precio == null || precio <= 0) {
      return "El precio debe ser mayor a cero";
    }
    return null;
  }
}
