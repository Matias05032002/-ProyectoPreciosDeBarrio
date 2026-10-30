package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.HorarioComercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/comercio")
public class ControladorComercio {

  private ServicioComercio servicioComercio;
  private ServicioReporte servicioReporte;
  private ServicioLogin servicioLogin;
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String ROL_COMERCIO = "COMERCIO";
  private static final String VISTA_COMERCIO = "comercio";
  private static final String REDIRECT_PERFIL = "redirect:/comercio/perfil";
  private static final String ROL_SESION = "ROL";
  private static final String REDIRECT_HOME = "redirect:/home";
  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(
    ControladorComercio.class
  );

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
    Map<Long, Boolean> estadosAbierto = new java.util.LinkedHashMap<>();
    for (Comercio comercio : comercios) {
      estadosAbierto.put(comercio.getId(), servicioComercio.estaAbierto(comercio.getId()));
    }
    ModelAndView mav = new ModelAndView("comercio/lista-comercios");
    mav.addObject("comercios", comercios);
    mav.addObject("estadosAbierto", estadosAbierto);
    return mav;
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  public ModelAndView buscarPorId(@PathVariable("id") Long id) {
    Comercio comercio = servicioComercio.buscarComercio(id);
    List<Reporte> reportes = servicioReporte.buscarPorComercio(id);
    ModelAndView mav = new ModelAndView("comercio/comercios");
    mav.addObject(VISTA_COMERCIO, comercio);
    mav.addObject("reportes", reportes);
    return mav;
  }

  @RequestMapping(path = "/buscar", method = RequestMethod.GET)
  public ModelAndView buscarComercioPorNombre(@RequestParam("nombre") String nombre) {
    List<Comercio> comercios = servicioComercio.buscarComercioPorNombre(nombre);
    Map<Long, Boolean> estadosAbierto = new java.util.LinkedHashMap<>();
    for (Comercio comercio : comercios) {
      estadosAbierto.put(comercio.getId(), servicioComercio.estaAbierto(comercio.getId()));
    }
    ModelAndView mav = new ModelAndView("comercio/lista-comercios");
    mav.addObject("comercios", comercios);
    mav.addObject("estadosAbierto", estadosAbierto);
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
    mav.addObject(VISTA_COMERCIO, comercio);
    mav.addObject("usuario", usuario);
    mav.addObject("cantidadProductos", cantidadProductos);
    return mav;
  }

  @RequestMapping(path = "/perfil", method = RequestMethod.POST)
  public ModelAndView guardarPerfil(@ModelAttribute Comercio comercio, HttpServletRequest request) {
    Long usuarioId = (Long) request.getSession().getAttribute("ID");
    if (usuarioId == null) return new ModelAndView(REDIRECT_LOGIN);
    servicioComercio.vincularComercioAUsuario(comercio, usuarioId);
    return new ModelAndView(REDIRECT_PERFIL);
  }

  @RequestMapping(path = "/perfil/editar", method = RequestMethod.POST)
  public ModelAndView editarPerfil(@ModelAttribute Comercio comercio, HttpServletRequest request) {
    Long usuarioId = (Long) request.getSession().getAttribute("ID");
    if (usuarioId == null) return new ModelAndView(REDIRECT_LOGIN);
    Comercio existente = servicioComercio.buscarComercioPorUsuarioId(usuarioId);
    if (existente == null) return new ModelAndView(REDIRECT_PERFIL);
    existente.setNombre(comercio.getNombre());
    existente.setTipo(comercio.getTipo());
    existente.setDireccion(comercio.getDireccion());
    existente.setLocalidad(comercio.getLocalidad());
    existente.setDescripcion(comercio.getDescripcion());
    servicioComercio.actualizarComercio(existente);
    return new ModelAndView(REDIRECT_PERFIL + "?editado=true");
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
      return new ModelAndView(REDIRECT_PERFIL + "?errorContrasena=noCoinciden");
    }
    boolean ok = servicioLogin.cambiarContrasena(usuarioId, contrasenaActual, contrasenaNueva);
    if (!ok) {
      return new ModelAndView(REDIRECT_PERFIL + "?errorContrasena=incorrecta");
    }
    return new ModelAndView(REDIRECT_PERFIL + "?contrasenaCambiada=true");
  }

  @RequestMapping(path = "/dashboard", method = RequestMethod.GET)
  public ModelAndView verDashboard(HttpServletRequest request) {
    Long usuarioId = (Long) request.getSession().getAttribute("ID");
    if (usuarioId == null) return new ModelAndView(REDIRECT_LOGIN);
    String rol = (String) request.getSession().getAttribute(ROL_SESION);
    if (!ROL_COMERCIO.equals(rol)) return new ModelAndView(REDIRECT_HOME);
    Comercio comercio = servicioComercio.buscarComercioPorUsuarioId(usuarioId);
    if (comercio == null) return new ModelAndView(REDIRECT_PERFIL);

    List<Reporte> reportes = servicioReporte.buscarPorComercio(comercio.getId());
    List<Object[]> vecinos = servicioReporte.contarUsuariosDistintosPorComercio(comercio.getId());
    Long totalDudosos = servicioReporte.contarReportesDudososPorComercio(comercio.getId());
    List<Object[]> rangosEtarios = servicioReporte.rangoEtariosVisitantes(comercio.getId());

    ModelAndView mav = new ModelAndView("comercio/dashboard-comercio");
    mav.addObject(VISTA_COMERCIO, comercio);
    mav.addObject("totalPrecios", reportes != null ? reportes.size() : 0);
    mav.addObject("totalVecinos", vecinos != null ? vecinos.size() : 0);
    mav.addObject("totalDudosos", totalDudosos != null ? totalDudosos : 0L);
    mav.addObject("precioPromedio", calcularPrecioPromedio(reportes));
    mav.addObject("productoEstrella", calcularProductoEstrella(reportes));
    mav.addObject("rangosEtarios", rangosEtarios);
    mav.addObject("topVecinos", vecinos);
    List<Object[]> productosMasBuscados = servicioReporte.productosMasBuscadosPorLocalidad(
      comercio.getLocalidad(),
      5
    );
    mav.addObject("productosMasBuscados", productosMasBuscados);
    return mav;
  }

  @GetMapping("/perfil/horarios")
  public ModelAndView mostrarHorarios(HttpSession session) {
    String rol = (String) session.getAttribute(ROL_SESION);
    if (!ROL_COMERCIO.equals(rol)) {
      return new ModelAndView(REDIRECT_HOME);
    }

    Long usuarioId = (Long) session.getAttribute("ID");
    Comercio comercio = servicioComercio.buscarComercioPorUsuarioId(usuarioId);
    if (comercio == null) {
      return new ModelAndView(REDIRECT_PERFIL);
    }

    List<HorarioComercio> horarios = servicioComercio.obtenerHorarios(comercio.getId());

    if (horarios.isEmpty()) {
      horarios = new java.util.ArrayList<>();
      for (int i = 1; i <= 7; i++) {
        HorarioComercio horario = new HorarioComercio();
        horario.setDiaSemana(i);
        horario.setAbierto(false);
        horarios.add(horario);
      }
    }

    ModelAndView mav = new ModelAndView("comercio/horarios-comercio");
    mav.addObject(VISTA_COMERCIO, comercio);
    mav.addObject("horarios", horarios);
    return mav;
  }

  @PostMapping("/perfil/horarios")
  public ModelAndView guardarHorarios(HttpSession session, HttpServletRequest request) {
    String rol = (String) session.getAttribute(ROL_SESION);
    if (!ROL_COMERCIO.equals(rol)) {
      return new ModelAndView(REDIRECT_HOME);
    }

    Long usuarioId = (Long) session.getAttribute("ID");
    Comercio comercio = servicioComercio.buscarComercioPorUsuarioId(usuarioId);
    if (comercio == null) {
      return new ModelAndView(REDIRECT_PERFIL);
    }

    List<HorarioComercio> horarios = new java.util.ArrayList<>();
    for (int dia = 1; dia <= 7; dia++) {
      HorarioComercio horario = new HorarioComercio();
      horario.setDiaSemana(dia);
      String abiertoParam = request.getParameter("abierto_" + dia);
      horario.setAbierto("on".equals(abiertoParam));
      String apertura = request.getParameter("apertura_" + dia);
      String cierre = request.getParameter("cierre_" + dia);
      if (apertura != null && !apertura.isEmpty()) {
        horario.setHoraApertura(java.time.LocalTime.parse(apertura));
      }
      if (cierre != null && !cierre.isEmpty()) {
        horario.setHoraCierre(java.time.LocalTime.parse(cierre));
      }
      horarios.add(horario);
    }

    servicioComercio.guardarHorarios(comercio.getId(), horarios);
    return new ModelAndView(REDIRECT_PERFIL + "/horarios?guardado");
  }

  private static final long MAX_FOTO_BYTES = 5 * 1024 * 1024; // 5 MB
  private static final String[] TIPOS_PERMITIDOS = { "image/jpeg", "image/png", "image/webp" };

  @RequestMapping(path = "/perfil/foto", method = RequestMethod.POST)
  public ModelAndView subirFoto(@RequestParam("foto") MultipartFile archivo, HttpSession session) {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Foto recibida: tipo={}, size={}", archivo.getContentType(), archivo.getSize());
    }
    String rol = (String) session.getAttribute(ROL_SESION);
    if (!ROL_COMERCIO.equals(rol)) return new ModelAndView(REDIRECT_HOME);
    Long usuarioId = (Long) session.getAttribute("ID");
    Comercio comercio = servicioComercio.buscarComercioPorUsuarioId(usuarioId);
    if (comercio == null) return new ModelAndView(REDIRECT_PERFIL);

    String tipo = archivo.getContentType();
    boolean tipoValido = false;
    for (String permitido : TIPOS_PERMITIDOS) {
      if (permitido.equals(tipo)) {
        tipoValido = true;
        break;
      }
    }
    if (!tipoValido || archivo.getSize() > MAX_FOTO_BYTES) {
      return new ModelAndView(REDIRECT_PERFIL + "?errorFoto=true");
    }

    try {
      servicioComercio.guardarFoto(comercio.getId(), archivo.getBytes(), tipo);
    } catch (Exception exc) {
      return new ModelAndView(REDIRECT_PERFIL + "?errorFoto=true");
    }
    return new ModelAndView(REDIRECT_PERFIL + "?fotoActualizada=true");
  }

  @RequestMapping(path = "/{id}/foto", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<byte[]> verFoto(@PathVariable("id") Long id) {
    Comercio comercio = servicioComercio.buscarComercio(id);
    if (comercio == null || comercio.getFoto().length == 0) {
      return ResponseEntity.notFound().build();
    }
    String tipo = comercio.getFotoTipo() != null ? comercio.getFotoTipo() : "image/jpeg";
    return ResponseEntity.ok().contentType(MediaType.parseMediaType(tipo)).body(comercio.getFoto());
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
