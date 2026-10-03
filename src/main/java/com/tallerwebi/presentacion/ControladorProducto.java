package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import com.tallerwebi.presentacion.DTO.ProductoConPrecio;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
  public ModelAndView buscarProducto(
    @RequestParam("nombre") String nombre,
    @RequestParam(value = "dudosoMarcado", required = false) Boolean dudosoMarcado
  ) {
    List<Producto> productos = servicioProducto.buscarPorNombre(nombre);
    List<Reporte> reportes = servicioReporte.listarTodos();
    if (reportes == null) reportes = Collections.emptyList();
    List<ProductoConPrecio> productosConPrecio = new ArrayList<>();
    for (Producto producto : productos) {
      productosConPrecio.addAll(construirProductoConPrecio(producto, reportes));
    }
    ModelAndView mav = new ModelAndView(VISTA_PRODUCTOS);
    mav.addObject("productos", productosConPrecio);
    mav.addObject("dudosoMarcado", dudosoMarcado != null && dudosoMarcado);
    return mav;
  }

  @RequestMapping(path = "/buscar/{id}", method = RequestMethod.GET)
  public ModelAndView buscarPorId(@PathVariable Long id) {
    Producto producto = servicioProducto.buscarProductoPorId(id);
    ModelAndView mav = new ModelAndView(VISTA_PRODUCTOS);
    mav.addObject("productos", Collections.singletonList(producto));
    return mav;
  }

  private List<ProductoConPrecio> construirProductoConPrecio(
    Producto producto,
    List<Reporte> reportes
  ) {
    List<ProductoConPrecio> resultado = new ArrayList<>();
    for (Reporte reporte : reportes) {
      if (
        reporte.getProducto() != null &&
        reporte.getProducto().getId() != null &&
        reporte.getProducto().getId().equals(producto.getId())
      ) {
        String nombreComercio = (reporte.getComercio() != null)
          ? reporte.getComercio().getNombre()
          : null;
        resultado.add(
          new ProductoConPrecio(
            producto.getNombre(),
            producto.getMarca(),
            producto.getUnidad(),
            producto.getCategoria(),
            reporte.getPrecio(),
            nombreComercio,
            reporte.getId(),
            reporte.getFechaDeReporte(),
            reporte.getPuntuacion()
          )
        );
      }
    }
    resultado.sort(Comparator.comparingDouble(ProductoConPrecio::getPrecioMinimo));
    return resultado;
  }
}
