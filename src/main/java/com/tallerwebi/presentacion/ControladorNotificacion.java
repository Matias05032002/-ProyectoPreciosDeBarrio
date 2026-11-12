package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.notificacion.ServicioNotificacion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/notificaciones")
public class ControladorNotificacion {

  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_LISTA = "redirect:/notificaciones";

  private final ServicioNotificacion servicioNotificacion;

  @Autowired
  public ControladorNotificacion(ServicioNotificacion servicioNotificacion) {
    this.servicioNotificacion = servicioNotificacion;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView ver(HttpServletRequest request) {
    Long usuarioId = usuarioDeLaSesion(request);
    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    ModelAndView mav = new ModelAndView("notificacion/notificaciones");
    mav.addObject("notificaciones", servicioNotificacion.listar(usuarioId));
    return mav;
  }

  // La campanita de la barra lo consulta al cargar cada página
  @RequestMapping(path = "/contador", method = RequestMethod.GET)
  @ResponseBody
  public Map<String, Object> contador(HttpServletRequest request) {
    Long usuarioId = usuarioDeLaSesion(request);
    long noLeidas = usuarioId == null ? 0L : servicioNotificacion.contarNoLeidas(usuarioId);
    return Map.of("noLeidas", noLeidas);
  }

  @RequestMapping(path = "/{id}/leer", method = RequestMethod.POST)
  public ModelAndView leer(@PathVariable("id") Long id, HttpServletRequest request) {
    Long usuarioId = usuarioDeLaSesion(request);
    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    String enlace = servicioNotificacion.enlaceDe(id, usuarioId);
    servicioNotificacion.marcarComoLeida(id, usuarioId);
    return new ModelAndView(enlace == null ? REDIRECT_LISTA : "redirect:" + enlace);
  }

  @RequestMapping(path = "/leer-todas", method = RequestMethod.POST)
  public ModelAndView leerTodas(HttpServletRequest request) {
    Long usuarioId = usuarioDeLaSesion(request);
    if (usuarioId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    servicioNotificacion.marcarTodasComoLeidas(usuarioId);
    return new ModelAndView(REDIRECT_LISTA);
  }

  private Long usuarioDeLaSesion(HttpServletRequest request) {
    HttpSession sesion = request.getSession();
    Object id = sesion == null ? null : sesion.getAttribute("ID");
    return id == null ? null : Long.valueOf(id.toString());
  }
}
