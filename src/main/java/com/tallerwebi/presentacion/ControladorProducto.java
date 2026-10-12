package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Producto.OrdenProducto;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.presentacion.DTO.ProductoConPrecio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/producto")
public class ControladorProducto {

  private ServicioProducto servicioProducto;
  private ServicioLogin servicioLogin;

  private static final String VISTA_PRODUCTOS = "producto/lista-productos";
  private static final String HISTORIAL_BUSQUEDAS = "HISTORIAL_BUSQUEDAS";
  private static final int MAX_HISTORIAL_BUSQUEDAS = 5;

  @Autowired
  public ControladorProducto(ServicioProducto servicioProducto, ServicioLogin servicioLogin) {
    this.servicioProducto = servicioProducto;
    this.servicioLogin = servicioLogin;
  }

  @RequestMapping(method = RequestMethod.POST)
  public ModelAndView guardarProducto(@ModelAttribute Producto producto) {
    servicioProducto.guardarProducto(producto);
    return new ModelAndView("redirect:/producto");
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView listarProductos(HttpServletRequest request) {
    ModelAndView mav = new ModelAndView(VISTA_PRODUCTOS);
    mav.addObject("productos", Collections.emptyList());

    HttpSession session = request.getSession(false);

    if (session != null) {
      @SuppressWarnings("unchecked")
      List<String> historial = (List<String>) session.getAttribute(HISTORIAL_BUSQUEDAS);

      mav.addObject("historialBusquedas", historial != null ? historial : Collections.emptyList());
    } else {
      mav.addObject("historialBusquedas", Collections.emptyList());
    }

    return mav;
  }

  @RequestMapping(path = "/buscar", method = RequestMethod.GET)
  public ModelAndView buscarProducto(
    @RequestParam("nombre") String nombre,
    @RequestParam(value = "orden", required = false, defaultValue = "precio") String orden,
    @RequestParam(value = "dudosoMarcado", required = false) Boolean dudosoMarcado,
    @RequestParam(value = "lat", required = false) Double lat,
    @RequestParam(value = "lng", required = false) Double lng,
    HttpServletRequest request
  ) {
    String email = (String) request.getSession().getAttribute("EMAIL");
    Usuario usuario = email != null ? servicioLogin.buscarPorEmail(email) : null;

    OrdenProducto ordenEnum = "reciente".equals(orden)
      ? OrdenProducto.RECIENTE
      : "cercania".equals(orden) ? OrdenProducto.CERCANIA : OrdenProducto.PRECIO;

    HttpSession session = request.getSession();
    String busquedaLimpia = nombre == null ? "" : nombre.trim();

    @SuppressWarnings("unchecked")
    List<String> historial = (List<String>) session.getAttribute(HISTORIAL_BUSQUEDAS);

    if (historial == null) {
      historial = new ArrayList<>();
    }

    if (!busquedaLimpia.isEmpty()) {
      historial.removeIf(b -> b.equalsIgnoreCase(busquedaLimpia));
      historial.add(0, busquedaLimpia);

      if (historial.size() > MAX_HISTORIAL_BUSQUEDAS) {
        historial.remove(historial.size() - 1);
      }

      session.setAttribute(HISTORIAL_BUSQUEDAS, historial);
    }

    List<ProductoConPrecio> productos = servicioProducto.buscarProductosConPrecio(
      busquedaLimpia,
      usuario,
      ordenEnum,
      lat,
      lng
    );

    ModelAndView mav = new ModelAndView(VISTA_PRODUCTOS);
    mav.addObject("productos", productos);
    mav.addObject("dudosoMarcado", dudosoMarcado != null && dudosoMarcado);
    mav.addObject("orden", orden);
    mav.addObject("busqueda", busquedaLimpia);
    mav.addObject("lat", lat);
    mav.addObject("lng", lng);
    mav.addObject("historialBusquedas", historial);

    return mav;
  }

  @RequestMapping(path = "/buscar/{id}", method = RequestMethod.GET)
  public ModelAndView buscarPorId(@PathVariable Long id) {
    Producto producto = servicioProducto.buscarProductoPorId(id);

    if (producto == null) {
      return new ModelAndView("redirect:/producto/lista");
    }

    ModelAndView mav = new ModelAndView(VISTA_PRODUCTOS);
    mav.addObject("productos", Collections.singletonList(producto));
    return mav;
  }
}
