package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Admin.ServicioAdmin;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorAdmin {

  private final ServicioAdmin servicioAdmin;

  @Autowired
  public ControladorAdmin(ServicioAdmin servicioAdmin) {
    this.servicioAdmin = servicioAdmin;
  }

  @RequestMapping(path = "/admin/estadisticas", method = RequestMethod.GET)
  public ModelAndView verEstadisticas(HttpSession session) {
    String rol = (String) session.getAttribute("ROL");
    if (rol == null || !"ADMIN".equals(rol)) {
      return new ModelAndView("redirect:/home");
    }

    ModelAndView mav = new ModelAndView("admin/estadisticas");
    mav.addObject("totalUsuarios", servicioAdmin.totalUsuarios());
    mav.addObject("totalReportes", servicioAdmin.totalReportes());
    mav.addObject("productosMasReportados", servicioAdmin.productosMasReportados());
    mav.addObject("comerciosMasReportados", servicioAdmin.comerciosMasReportados());
    mav.addObject("reportesPorMes", servicioAdmin.reportesPorMes());
    mav.addObject("productosMasDudosos", servicioAdmin.productosMasDudosos());
    mav.addObject("usuariosMasActivos", servicioAdmin.usuariosMasActivos());
    mav.addObject("distribucionPorRangoEtario", servicioAdmin.distribucionPorRangoEtario());
    return mav;
  }
}
