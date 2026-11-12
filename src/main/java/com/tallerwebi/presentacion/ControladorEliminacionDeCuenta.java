package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Cuenta.ServicioEliminacionDeCuenta;
import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorEliminacionDeCuenta {

  private static final String REDIRECT_LOGIN = "redirect:/login";

  private final ServicioEliminacionDeCuenta servicioEliminacionDeCuenta;

  @Autowired
  public ControladorEliminacionDeCuenta(ServicioEliminacionDeCuenta servicioEliminacionDeCuenta) {
    this.servicioEliminacionDeCuenta = servicioEliminacionDeCuenta;
  }

  @RequestMapping(path = "/perfil/eliminar-cuenta", method = RequestMethod.POST)
  public ModelAndView eliminar(
    @RequestParam("contrasena") String contrasena,
    HttpServletRequest request
  ) {
    HttpSession sesion = request.getSession(false);
    if (sesion == null || sesion.getAttribute("ID") == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    // El id sale siempre de la sesión: nadie puede eliminar la cuenta de otro
    Long usuarioId = Long.valueOf(sesion.getAttribute("ID").toString());
    try {
      if (servicioEliminacionDeCuenta.eliminarCuenta(usuarioId, contrasena)) {
        sesion.invalidate();
        return new ModelAndView("redirect:/login?cuentaEliminada");
      }
    } catch (AccesoDenegado e) {
      return new ModelAndView("redirect:/perfil?errorEliminarCuenta");
    }
    return new ModelAndView("redirect:/perfil?errorEliminarCuenta");
  }
}
