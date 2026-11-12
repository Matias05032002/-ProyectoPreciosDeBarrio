package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.HorarioComercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Comercio.TipoDeComercio;
import com.tallerwebi.dominio.Comercio.UbicacionSugerida;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.FotoInvalida;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
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
  private static final String REDIRECT_DASHBOARD = "redirect:/comercio/dashboard";
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

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView listarTodos(HttpServletRequest request) {
    // El listado es para los vecinos: un comercio tiene su propio panel
    if (esComercio(request)) return new ModelAndView(REDIRECT_DASHBOARD);
    List<Comercio> comercios = servicioComercio.listarTodos();
    Map<Long, Boolean> estadosAbierto = new java.util.LinkedHashMap<>();
    for (Comercio comercio : comercios) {
      estadosAbierto.put(comercio.getId(), servicioComercio.estaAbierto(comercio.getId()));
    }
    ModelAndView mav = new ModelAndView("comercio/lista-comercios");
    mav.addObject("comercios", comercios);
    mav.addObject("estadosAbierto", estadosAbierto);
    mav.addObject("tiposComercio", TipoDeComercio.TIPOS);
    return mav;
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  public ModelAndView buscarPorId(@PathVariable("id") Long id) {
    Comercio comercio = servicioComercio.buscarComercio(id);
    // Un comercio que quedó sin ubicación se vuelve a buscar cuando alguien lo mira
    servicioComercio.ubicarSiFalta(comercio);
    List<Reporte> reportes = servicioReporte.buscarPorComercio(id);
    ModelAndView mav = new ModelAndView("comercio/comercios");
    mav.addObject(VISTA_COMERCIO, comercio);
    mav.addObject("reportes", reportes);
    if (comercio != null) {
      // Sin horarios cargados "abierto" queda en null y la vista muestra "Sin horario cargado"
      mav.addObject("abierto", servicioComercio.estaAbierto(id));
      mav.addObject("horarios", servicioComercio.obtenerHorarios(id));
    }
    return mav;
  }

  @RequestMapping(path = "/buscar", method = RequestMethod.GET)
  public ModelAndView buscarComercioPorNombre(
    @RequestParam("nombre") String nombre,
    HttpServletRequest request
  ) {
    if (esComercio(request)) return new ModelAndView(REDIRECT_DASHBOARD);
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
    if (!esComercio(request)) return new ModelAndView(REDIRECT_HOME);
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
    if (!esComercio(request)) return new ModelAndView(REDIRECT_HOME);
    servicioComercio.vincularComercioAUsuario(comercio, usuarioId);
    return new ModelAndView(REDIRECT_PERFIL);
  }

  // latitudConfirmada/longitudConfirmada llegan cuando el comerciante eligió o ajustó su local
  // en el mapa; si no vienen, el servicio ubica el local a partir de la dirección
  @RequestMapping(path = "/perfil/editar", method = RequestMethod.POST)
  public ModelAndView editarPerfil(
    @ModelAttribute Comercio comercio,
    @RequestParam(value = "latitudConfirmada", required = false) Double latitudConfirmada,
    @RequestParam(value = "longitudConfirmada", required = false) Double longitudConfirmada,
    HttpServletRequest request
  ) {
    Long usuarioId = (Long) request.getSession().getAttribute("ID");
    if (usuarioId == null) return new ModelAndView(REDIRECT_LOGIN);
    if (!esComercio(request)) return new ModelAndView(REDIRECT_HOME);
    Comercio existente = servicioComercio.buscarComercioPorUsuarioId(usuarioId);
    if (existente == null) return new ModelAndView(REDIRECT_PERFIL);
    servicioComercio.actualizarDatosDelComercio(
      existente,
      comercio,
      latitudConfirmada,
      longitudConfirmada
    );
    return new ModelAndView(REDIRECT_PERFIL + "?editado=true");
  }

  @PostMapping("/perfil/solicitar-verificacion")
  public ModelAndView solicitarVerificacion(HttpServletRequest request) {
    Long usuarioId = (Long) request.getSession().getAttribute("ID");

    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    if (!esComercio(request)) {
      return new ModelAndView(REDIRECT_HOME);
    }

    Comercio comercio = servicioComercio.buscarComercioPorUsuarioId(usuarioId);

    if (comercio == null) {
      return new ModelAndView(REDIRECT_PERFIL);
    }

    servicioComercio.solicitarVerificacion(comercio.getId());

    return new ModelAndView(REDIRECT_PERFIL + "?verificacionSolicitada=true");
  }

  // Lo usa el botón "Buscar en el mapa" del perfil: devuelve las opciones en JSON
  @RequestMapping(path = "/ubicaciones/sugerencias", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<List<UbicacionSugerida>> sugerirUbicaciones(
    @RequestParam(value = "direccion", required = false) String direccion,
    @RequestParam(value = "localidad", required = false) String localidad,
    HttpServletRequest request
  ) {
    if (!esComercio(request)) {
      return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
    }
    return ResponseEntity.ok(servicioComercio.sugerirUbicaciones(direccion, localidad));
  }

  // Lo usa el mapa chico del inicio: solo los comercios que ya tienen ubicación
  @RequestMapping(path = "/mapa", method = RequestMethod.GET)
  @ResponseBody
  public List<Map<String, Object>> comerciosParaElMapa() {
    return servicioComercio
      .listarTodos()
      .stream()
      .filter(c -> c.getLatitud() != null && c.getLongitud() != null)
      .map(c -> {
        Map<String, Object> punto = new java.util.LinkedHashMap<>();
        punto.put("id", c.getId());
        punto.put("nombre", c.getNombre());
        punto.put("tipo", c.getTipo());
        punto.put("latitud", c.getLatitud());
        punto.put("longitud", c.getLongitud());
        punto.put("localidad", c.getLocalidad());
        return punto;
      })
      .collect(Collectors.toList());
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
    if (!esComercio(request)) return new ModelAndView(REDIRECT_HOME);
    if (!contrasenaNueva.equals(confirmarContrasena)) {
      return new ModelAndView(REDIRECT_PERFIL + "?errorContrasena=noCoinciden");
    }
    boolean ok = servicioLogin.cambiarContrasena(usuarioId, contrasenaActual, contrasenaNueva);
    if (!ok) {
      return new ModelAndView(REDIRECT_PERFIL + "?errorContrasena=incorrecta");
    }
    return new ModelAndView(REDIRECT_PERFIL + "?contrasenaCambiada=true");
  }

  // El perfil de comercio es solo para cuentas COMERCIO: el vecino tiene el suyo en /perfil
  private boolean esComercio(HttpServletRequest request) {
    return ROL_COMERCIO.equals(request.getSession().getAttribute(ROL_SESION));
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
    List<Object[]> productosDudosos = servicioReporte.productosDudososPorComercio(comercio.getId());
    List<Object[]> vecinos = servicioReporte.contarUsuariosDistintosPorComercio(comercio.getId());
    Long totalDudosos = servicioReporte.contarReportesDudososPorComercio(comercio.getId());
    List<Object[]> rangosEtarios = servicioReporte.rangoEtariosVisitantes(comercio.getId());

    ModelAndView mav = new ModelAndView("comercio/dashboard-comercio");
    mav.addObject(VISTA_COMERCIO, comercio);
    mav.addObject("totalPrecios", reportes != null ? reportes.size() : 0);
    mav.addObject("totalVecinos", vecinos != null ? vecinos.size() : 0);
    mav.addObject("totalDudosos", totalDudosos != null ? totalDudosos : 0L);
    mav.addObject("precioPromedio", servicioReporte.precioPromedioDelComercio(comercio.getId()));
    mav.addObject(
      "productoEstrella",
      servicioReporte.productoEstrellaDelComercio(comercio.getId())
    );
    mav.addObject("rangosEtarios", rangosEtarios);
    mav.addObject("topVecinos", vecinos);
    List<Object[]> productosMasBuscados = servicioReporte.productosMasBuscadosPorLocalidad(
      comercio.getLocalidad(),
      5
    );
    mav.addObject("productosMasBuscados", productosMasBuscados);
    mav.addObject("productosDudosos", productosDudosos);
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

    try {
      servicioComercio.guardarFoto(comercio.getId(), archivo.getBytes());
    } catch (FotoInvalida | IOException exc) {
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
}
