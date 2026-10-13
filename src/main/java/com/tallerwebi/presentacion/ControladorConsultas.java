package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Consulta.Consulta;
import com.tallerwebi.dominio.Consulta.ServicioConsulta;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorConsultas {

  private final ServicioConsulta servicioConsulta;

  @Autowired
  public ControladorConsultas(ServicioConsulta servicioConsulta) {
    this.servicioConsulta = servicioConsulta;
  }

  @RequestMapping(path = "/admin/consultas", method = RequestMethod.GET)
  public ModelAndView listarConsultas(HttpServletRequest request) {
    Object rol = request.getSession().getAttribute("ROL");

    if (rol == null || !"ADMIN".equals(rol.toString())) {
      return new ModelAndView("redirect:/home");
    }

    List<Consulta> consultas = servicioConsulta.listarTodas();

    ModelAndView mav = new ModelAndView("admin/consultas");
    mav.addObject("consultas", consultas);
    return mav;
  }
}
