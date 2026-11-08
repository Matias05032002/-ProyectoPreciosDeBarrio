package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Lista.ServicioLista;
import com.tallerwebi.dominio.Paginador.ServicioPaginador;
import com.tallerwebi.dominio.Producto.OrdenProducto;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.presentacion.DTO.ProductoConPrecio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/producto")
public class ControladorProducto {

  private ServicioProducto servicioProducto;
  private ServicioLogin servicioLogin;
  private ServicioLista servicioLista;
  private ServicioPaginador servicioPaginador;

  private static final String VISTA_PRODUCTOS = "producto/lista-productos";
  private static final String HISTORIAL_BUSQUEDAS = "HISTORIAL_BUSQUEDAS";
  private static final int MAX_HISTORIAL_BUSQUEDAS = 5;

  @Autowired
  public ControladorProducto(
    ServicioProducto servicioProducto,
    ServicioLogin servicioLogin,
    ServicioLista servicioLista,
    ServicioPaginador servicioPaginador
  ) {
    this.servicioProducto = servicioProducto;
    this.servicioLogin = servicioLogin;
    this.servicioLista = servicioLista;
    this.servicioPaginador = servicioPaginador;
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
      Long usuarioId = (Long) session.getAttribute("ID");
      if (usuarioId != null) {
        mav.addObject("listas", servicioLista.obtenerListasDeUsuario(usuarioId));
      }
    } else {
      mav.addObject("historialBusquedas", Collections.emptyList());
    }
    return mav;
  }

  @RequestMapping(path = "/buscar", method = RequestMethod.GET)
  public ModelAndView buscarProducto(
    @RequestParam(value = "nombre", required = false, defaultValue = "") String nombre,
    @RequestParam(value = "orden", required = false, defaultValue = "precio") String orden,
    @RequestParam(value = "dudosoMarcado", required = false) Boolean dudosoMarcado,
    @RequestParam(value = "lat", required = false) Double lat,
    @RequestParam(value = "lng", required = false) Double lng,
    @RequestParam(value = "pagina", required = false, defaultValue = "0") int pagina,
    HttpServletRequest request
  ) {
    String email = (String) request.getSession().getAttribute("EMAIL");
    Usuario usuario = email != null ? servicioLogin.buscarPorEmail(email) : null;

    // Se puede ordenar por varios criterios a la vez (?orden=precio,cercania)
    List<OrdenProducto> ordenes = OrdenProducto.desde(orden);
    List<String> clavesDeOrden = ordenes.stream().map(OrdenProducto::clave).toList();
    HttpSession session = request.getSession();
    String busquedaLimpia = nombre == null ? "" : nombre.trim();
    List<String> historial = actualizarHistorial(session, busquedaLimpia);

    // Rubro opcional (?categoria=Lacteos): llega por la URL de los atajos de la pantalla
    String categoria = rubroDe(request);
    List<ProductoConPrecio> todos = buscarSegunRubro(
      busquedaLimpia,
      categoria,
      usuario,
      ordenes,
      lat,
      lng
    );

    // Paginación
    int tamano = 9;
    int totalPaginas = servicioPaginador.calcularTotalPaginas(todos.size(), tamano);
    List<ProductoConPrecio> productos = servicioPaginador.paginar(todos, pagina, tamano);

    ModelAndView mav = new ModelAndView(VISTA_PRODUCTOS);
    mav.addObject("productos", productos);
    mav.addObject("dudosoMarcado", dudosoMarcado != null && dudosoMarcado);
    mav.addObject("orden", String.join(",", clavesDeOrden));
    mav.addObject("ordenes", clavesDeOrden);
    mav.addObject("busqueda", busquedaLimpia);
    mav.addObject("categoria", categoria);
    mav.addObject("lat", lat);
    mav.addObject("lng", lng);
    mav.addObject("historialBusquedas", historial);
    mav.addObject("pagina", pagina);
    mav.addObject("totalPaginas", totalPaginas);
    Long usuarioId = (Long) session.getAttribute("ID");
    if (usuarioId != null) {
      mav.addObject("listas", servicioLista.obtenerListasDeUsuario(usuarioId));
      agregarNotificacionDeValoracion(mav, productos);
    }
    return mav;
  }

  // Guarda la búsqueda al principio del historial de la sesión (sin repetidas y con tope)
  private List<String> actualizarHistorial(HttpSession session, String busqueda) {
    @SuppressWarnings("unchecked")
    List<String> historial = (List<String>) session.getAttribute(HISTORIAL_BUSQUEDAS);
    if (historial == null) historial = new ArrayList<>();
    if (busqueda.isEmpty()) return historial;

    historial.removeIf(b -> b.equalsIgnoreCase(busqueda));
    historial.add(0, busqueda);
    if (historial.size() > MAX_HISTORIAL_BUSQUEDAS) historial.remove(historial.size() - 1);
    session.setAttribute(HISTORIAL_BUSQUEDAS, historial);
    return historial;
  }

  private String rubroDe(HttpServletRequest request) {
    String categoria = request.getParameter("categoria");
    return categoria == null || categoria.isBlank() ? null : categoria.trim();
  }

  private List<ProductoConPrecio> buscarSegunRubro(
    String busqueda,
    String categoria,
    Usuario usuario,
    List<OrdenProducto> ordenes,
    Double lat,
    Double lng
  ) {
    if (categoria == null) {
      return servicioProducto.buscarProductosConPrecio(busqueda, usuario, ordenes, lat, lng);
    }
    return servicioProducto.buscarProductosConPrecio(
      busqueda,
      categoria,
      usuario,
      ordenes,
      lat,
      lng
    );
  }

  private void agregarNotificacionDeValoracion(
    ModelAndView mav,
    List<ProductoConPrecio> productos
  ) {
    for (ProductoConPrecio producto : productos) {
      if (producto.getReporteId() != null && !producto.isYaMarcoDudoso()) {
        mav.addObject("productoAValorar", producto);
        return;
      }
    }
  }

  // Lo usa el modal "Ver precios": el mismo producto del reporte, con su precio en cada comercio
  @RequestMapping(path = "/precios/{reporteId}", method = RequestMethod.GET)
  @ResponseBody
  public List<Map<String, Object>> preciosEnCadaLocal(@PathVariable("reporteId") Long reporteId) {
    List<Map<String, Object>> precios = new ArrayList<>();
    for (Reporte reporte : servicioProducto.buscarPreciosDelProductoEnCadaLocal(reporteId)) {
      Comercio comercio = reporte.getComercio();
      Producto producto = reporte.getProducto();
      ProductoConPrecio resumen = new ProductoConPrecio(
        producto.getNombre(),
        producto.getMarca(),
        producto.getUnidad(),
        producto.getCategoria(),
        reporte.getPrecio(),
        comercio.getNombre(),
        reporte.getId(),
        reporte.getFechaDeReporte(),
        reporte.getPuntuacion()
      );
      Map<String, Object> precio = new LinkedHashMap<>();
      precio.put("reporteId", reporte.getId());
      precio.put("comercioId", comercio.getId());
      precio.put("comercio", comercio.getNombre());
      precio.put("direccion", comercio.getDireccion());
      precio.put("localidad", comercio.getLocalidad());
      precio.put("latitud", comercio.getLatitud());
      precio.put("longitud", comercio.getLongitud());
      precio.put("precio", reporte.getPrecio());
      precio.put("antiguedad", resumen.getAntiguedad());
      precio.put("esDudoso", resumen.isEsDudoso());
      precios.add(precio);
    }
    return precios;
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
