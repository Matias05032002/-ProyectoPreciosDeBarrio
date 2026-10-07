package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Producto.OrdenProducto;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.presentacion.DTO.ProductoConPrecio;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
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
  public ModelAndView listarProductos() {
    ModelAndView mav = new ModelAndView(VISTA_PRODUCTOS);
    mav.addObject("productos", Collections.emptyList());
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
    List<ProductoConPrecio> productos = servicioProducto.buscarProductosConPrecio(
      nombre,
      usuario,
      ordenEnum,
      lat,
      lng
    );
    ModelAndView mav = new ModelAndView(VISTA_PRODUCTOS);
    mav.addObject("productos", productos);
    mav.addObject("dudosoMarcado", dudosoMarcado != null && dudosoMarcado);
    mav.addObject("orden", orden);
    mav.addObject("busqueda", nombre);
    mav.addObject("lat", lat);
    mav.addObject("lng", lng);
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
