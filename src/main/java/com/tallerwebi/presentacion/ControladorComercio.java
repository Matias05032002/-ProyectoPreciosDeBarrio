package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/comercio")
public class ControladorComercio {

  private ServicioComercio servicioComercio;
  private ServicioReporte servicioReporte;

  @Autowired
  public ControladorComercio(ServicioComercio servicioComercio, ServicioReporte servicioReporte) {
    this.servicioComercio = servicioComercio;
    this.servicioReporte = servicioReporte;
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
}
