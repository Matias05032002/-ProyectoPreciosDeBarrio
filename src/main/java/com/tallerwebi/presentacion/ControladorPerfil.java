package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorPerfil {

  private static final String REDIRECT_LOGIN = "redirect:/login";
  private final ServicioLogin servicioLogin;
  private final ServicioReporte servicioReporte;

  @Autowired
  public ControladorPerfil(ServicioLogin servicioLogin, ServicioReporte servicioReporte) {
    this.servicioLogin = servicioLogin;
    this.servicioReporte = servicioReporte;
  }

  @RequestMapping(path = "/perfil", method = RequestMethod.GET)
  public ModelAndView verPerfil(HttpServletRequest request) {
    HttpSession sesion = request.getSession(false);

    if (sesion == null || sesion.getAttribute("ID") == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Long usuarioId = Long.valueOf(sesion.getAttribute("ID").toString());
    Usuario usuario = servicioLogin.buscarPorId(usuarioId);

    if (usuario == null) {
      sesion.invalidate();
      return new ModelAndView(REDIRECT_LOGIN);
    }

    List<Reporte> reportes = servicioReporte.buscarPorUsuarioId(usuarioId);

    Map<String, Object> modelo = new java.util.HashMap<>();
    modelo.put("usuario", usuario);
    modelo.put("reportes", reportes);
    modelo.put("cantidadReportes", reportes.size());

    return new ModelAndView("perfil", modelo);
  }

  @RequestMapping(path = "/perfil/actualizar-email", method = RequestMethod.POST)
  public ModelAndView actualizarEmail(
    @RequestParam("email") String email,
    HttpServletRequest request
  ) {
    HttpSession sesion = request.getSession(false);

    if (sesion == null || sesion.getAttribute("ID") == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Long usuarioId = Long.valueOf(sesion.getAttribute("ID").toString());
    boolean actualizado = servicioLogin.actualizarEmail(usuarioId, email);

    if (actualizado) {
      sesion.setAttribute("EMAIL", email.trim());
      return new ModelAndView("redirect:/perfil?emailActualizado");
    }

    return new ModelAndView("redirect:/perfil?errorEmail");
  }

  @RequestMapping(path = "/perfil/cambiar-contrasena", method = RequestMethod.POST)
  public ModelAndView cambiarContrasena(
    @RequestParam("contrasenaActual") String contrasenaActual,
    @RequestParam("contrasenaNueva") String contrasenaNueva,
    HttpServletRequest request
  ) {
    HttpSession sesion = request.getSession(false);

    if (sesion == null || sesion.getAttribute("ID") == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Long usuarioId = Long.valueOf(sesion.getAttribute("ID").toString());

    boolean cambiada = servicioLogin.cambiarContrasena(
      usuarioId,
      contrasenaActual,
      contrasenaNueva
    );

    if (cambiada) {
      return new ModelAndView("redirect:/perfil?contrasenaActualizada");
    }

    return new ModelAndView("redirect:/perfil?errorContrasena");
  }
}
