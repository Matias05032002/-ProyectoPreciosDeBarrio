package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/comercio")
public class ControladorComercio {

  private ServicioComercio servicioComercio;
  private ServicioReporte servicioReporte;
  private ServicioLogin servicioLogin;
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String ROL_COMERCIO = "COMERCIO";

  @Autowired
  public ControladorComercio(
    ServicioComercio servicioComercio,
    ServicioReporte servicioReporte,
    ServicioLogin servicioLogin
  ) {
    this.servicioComercio = servicioComercio;
    this.servicioReporte = servicioReporte;
    this.servicioLogin = servicioLogin;
  }

  @RequestMapping(method = RequestMethod.POST)
  public ModelAndView guardarComercio(@ModelAttribute Comercio comercio) {
    servicioComercio.guardarComercio(comercio);
    return new ModelAndView("redirect:/comercio");
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView listarTodos() {
    List<Comercio> comercios = servicioComercio.listarTodos();
    ModelAndView mav = new ModelAndView("comercio/lista-comercios");
    mav.addObject("comercios", comercios);
    return mav;
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  public ModelAndView buscarPorId(@PathVariable("id") Long id) {
    Comercio comercio = servicioComercio.buscarComercio(id);
    List<Reporte> reportes = servicioReporte.buscarPorComercio(id);
    ModelAndView mav = new ModelAndView("comercio/comercios");
    mav.addObject("comercio", comercio);
    mav.addObject("reportes", reportes);
    return mav;
  }

  @RequestMapping(path = "/buscar", method = RequestMethod.GET)
  public ModelAndView buscarComercioPorNombre(@RequestParam("nombre") String nombre) {
    List<Comercio> comercios = servicioComercio.buscarComercioPorNombre(nombre);
    ModelAndView mav = new ModelAndView("comercio/lista-comercios");
    mav.addObject("comercios", comercios);
    return mav;
  }

  @RequestMapping(path = "/perfil", method = RequestMethod.GET)
  public ModelAndView verPerfil(HttpServletRequest request) {
    Long usuarioId = (Long) request.getSession().getAttribute("ID");
    if (usuarioId == null) return new ModelAndView(REDIRECT_LOGIN);
    Comercio comercio = servicioComercio.buscarComercioPorUsuarioId(usuarioId);
    Usuario usuario = servicioLogin.buscarPorId(usuarioId);
    long cantidadProductos = 0;
    if (comercio != null) {
      List<Reporte> reportes = servicioReporte.buscarPorComercio(comercio.getId());
      cantidadProductos = reportes != null ? reportes.size() : 0;
    }
    ModelAndView mav = new ModelAndView("comercio/perfil-comercio");
    mav.addObject("comercio", comercio);
    mav.addObject("usuario", usuario);
    mav.addObject("cantidadProductos", cantidadProductos);
    return mav;
  }

  @RequestMapping(path = "/perfil", method = RequestMethod.POST)
  public ModelAndView guardarPerfil(@ModelAttribute Comercio comercio, HttpServletRequest request) {
    Long usuarioId = (Long) request.getSession().getAttribute("ID");
    if (usuarioId == null) return new ModelAndView(REDIRECT_LOGIN);
    servicioComercio.vincularComercioAUsuario(comercio, usuarioId);
    return new ModelAndView("redirect:/comercio/perfil");
  }

  @RequestMapping(path = "/perfil/editar", method = RequestMethod.POST)
  public ModelAndView editarPerfil(@ModelAttribute Comercio comercio, HttpServletRequest request) {
    Long usuarioId = (Long) request.getSession().getAttribute("ID");
    if (usuarioId == null) return new ModelAndView(REDIRECT_LOGIN);
    Comercio existente = servicioComercio.buscarComercioPorUsuarioId(usuarioId);
    if (existente == null) return new ModelAndView("redirect:/comercio/perfil");
    existente.setNombre(comercio.getNombre());
    existente.setTipo(comercio.getTipo());
    existente.setDireccion(comercio.getDireccion());
    existente.setLocalidad(comercio.getLocalidad());
    existente.setDescripcion(comercio.getDescripcion());
    servicioComercio.actualizarComercio(existente);
    return new ModelAndView("redirect:/comercio/perfil?editado=true");
  }

  @RequestMapping(path = "/perfil/cambiar-contrasena", method = RequestMethod.POST)
  public ModelAndView cambiarContrasena(
    @RequestParam("contrasenaActual") String contrasenaActual,
    @RequestParam("contrasenaNueva") String contrasenaNueva,
    @RequestParam("confirmarContrasena") String confirmarContrasena,
    HttpServletRequest request
  ) {
    Long usuarioId = (Long) request.getSession().getAttribute("ID");
    if (usuarioId == null) return new ModelAndView(REDIRECT_LOGIN);
    if (!contrasenaNueva.equals(confirmarContrasena)) {
      return new ModelAndView("redirect:/comercio/perfil?errorContrasena=noCoinciden");
    }
    boolean ok = servicioLogin.cambiarContrasena(usuarioId, contrasenaActual, contrasenaNueva);
    if (!ok) {
      return new ModelAndView("redirect:/comercio/perfil?errorContrasena=incorrecta");
    }
    return new ModelAndView("redirect:/comercio/perfil?contrasenaCambiada=true");
  }

  @RequestMapping(path = "/dashboard", method = RequestMethod.GET)
  public ModelAndView verDashboard(HttpServletRequest request) {
    Long usuarioId = (Long) request.getSession().getAttribute("ID");
    if (usuarioId == null) return new ModelAndView(REDIRECT_LOGIN);
    String rol = (String) request.getSession().getAttribute("ROL");
    if (!ROL_COMERCIO.equals(rol)) return new ModelAndView("redirect:/home");
    Comercio comercio = servicioComercio.buscarComercioPorUsuarioId(usuarioId);
    if (comercio == null) return new ModelAndView("redirect:/comercio/perfil");

    List<Reporte> reportes = servicioReporte.buscarPorComercio(comercio.getId());
    List<Object[]> vecinos = servicioReporte.contarUsuariosDistintosPorComercio(comercio.getId());
    Long totalDudosos = servicioReporte.contarReportesDudososPorComercio(comercio.getId());
    List<Object[]> rangosEtarios = servicioReporte.rangoEtariosVisitantes(comercio.getId());

    ModelAndView mav = new ModelAndView("comercio/dashboard-comercio");
    mav.addObject("comercio", comercio);
    mav.addObject("totalPrecios", reportes != null ? reportes.size() : 0);
    mav.addObject("totalVecinos", vecinos != null ? vecinos.size() : 0);
    mav.addObject("totalDudosos", totalDudosos != null ? totalDudosos : 0L);
    mav.addObject("precioPromedio", calcularPrecioPromedio(reportes));
    mav.addObject("productoEstrella", calcularProductoEstrella(reportes));
    mav.addObject("rangosEtarios", rangosEtarios);
    mav.addObject("topVecinos", vecinos);
    return mav;
  }

  private double calcularPrecioPromedio(List<Reporte> reportes) {
    if (reportes == null || reportes.isEmpty()) return 0.0;
    long count = reportes.stream().filter(r -> r.getPrecio() != null).count();
    if (count == 0) return 0.0;
    double suma = reportes
      .stream()
      .filter(r -> r.getPrecio() != null)
      .mapToDouble(Reporte::getPrecio)
      .sum();
    return suma / count;
  }

  private String calcularProductoEstrella(List<Reporte> reportes) {
    if (reportes == null || reportes.isEmpty()) return "-";
    return reportes
      .stream()
      .filter(r -> r.getProducto() != null && r.getProducto().getNombre() != null)
      .collect(Collectors.groupingBy(r -> r.getProducto().getNombre(), Collectors.counting()))
      .entrySet()
      .stream()
      .max(Map.Entry.comparingByValue())
      .map(Map.Entry::getKey)
      .orElse("-");
  }
}
