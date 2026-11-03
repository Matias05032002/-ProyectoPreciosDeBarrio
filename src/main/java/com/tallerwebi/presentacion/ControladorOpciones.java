package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Consulta.Consulta;
import com.tallerwebi.dominio.Consulta.ServicioConsulta;
import com.tallerwebi.dominio.excepcion.DatosInvalidos;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorOpciones {

  private final ServicioConsulta servicioConsulta;

  @Autowired
  public ControladorOpciones(ServicioConsulta servicioConsulta) {
    this.servicioConsulta = servicioConsulta;
  }

  @RequestMapping(path = "/sobre-la-app", method = RequestMethod.GET)
  public ModelAndView sobreLaApp() {
    return new ModelAndView("sobre-la-app");
  }

  @RequestMapping(path = "/feedback", method = RequestMethod.GET)
  public ModelAndView darFeedback() {
    return new ModelAndView("feedback");
  }

  @RequestMapping(path = "/feedback", method = RequestMethod.POST)
  public ModelAndView guardarFeedback(
    @ModelAttribute Consulta consulta,
    HttpServletRequest request
  ) {
    ModelAndView mav = new ModelAndView("feedback");
    try {
      servicioConsulta.guardar(consulta);
      mav.addObject("mensajeExito", "Tu mensaje fue enviado correctamente.");
      mav.addObject("consulta", new Consulta());
    } catch (DatosInvalidos e) {
      mav.addObject("error", e.getMessage());
      mav.addObject("consulta", consulta);
    }
    return mav;
  }
}
