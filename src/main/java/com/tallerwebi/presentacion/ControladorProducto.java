package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import com.tallerwebi.presentacion.DTO.ProductoConPrecio;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/producto")
public class ControladorProducto {

  private ServicioProducto servicioProducto;
  private ServicioReporte servicioReporte;
  private static final String VISTA_PRODUCTOS = "producto/lista-productos";

  @Autowired
  public ControladorProducto(ServicioProducto servicioProducto, ServicioReporte servicioReporte) {
    this.servicioProducto = servicioProducto;
    this.servicioReporte = servicioReporte;
  }

  @RequestMapping(method = RequestMethod.POST)
  public ModelAndView guardarProducto(@ModelAttribute Producto producto) {
    servicioProducto.guardarProducto(producto);
    return new ModelAndView("redirect:/producto");
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView listarProductos() {
    ModelAndView mav = new ModelAndView("producto/lista-productos");
    mav.addObject("productos", Collections.emptyList());
    return mav;
  }

  @RequestMapping(path = "/buscar", method = RequestMethod.GET)
  public ModelAndView buscarProducto(@RequestParam("nombre") String nombre) {
    List<Producto> productos = servicioProducto.buscarPorNombre(nombre);
    List<Reporte> reportes = servicioReporte.listarTodos();
    if (reportes == null) reportes = Collections.emptyList();
    List<ProductoConPrecio> productosConPrecio = new ArrayList<>();
    for (Producto producto : productos) {
      productosConPrecio.add(construirProductoConPrecio(producto, reportes));
    }
    ModelAndView mav = new ModelAndView(VISTA_PRODUCTOS);
    mav.addObject("productos", productosConPrecio);
    return mav;
  }

  public ModelAndView buscarPorId(Long id) {
    Producto producto = servicioProducto.buscarProductoPorId(id);
    ModelAndView mav = new ModelAndView(VISTA_PRODUCTOS);
    mav.addObject(VISTA_PRODUCTOS, producto);
    return mav;
  }

  private ProductoConPrecio construirProductoConPrecio(Producto producto, List<Reporte> reportes) {
    Double precioMinimo = null;
    String comercio = null;
    Long reporteId = null;
    for (Reporte reporte : reportes) {
      if (
        reporte.getProducto() != null &&
        reporte.getProducto().getId() != null &&
        reporte.getProducto().getId().equals(producto.getId())
      ) {
        if (precioMinimo == null || reporte.getPrecio() < precioMinimo) {
          precioMinimo = reporte.getPrecio();
          if (reporte.getComercio() != null) {
            comercio = reporte.getComercio().getNombre();
          }
          reporteId = reporte.getId();
        }
      }
    }
    return new ProductoConPrecio(
      producto.getNombre(),
      producto.getMarca(),
      producto.getUnidad(),
      producto.getCategoria(),
      precioMinimo,
      comercio,
      reporteId
    );
  }
}
