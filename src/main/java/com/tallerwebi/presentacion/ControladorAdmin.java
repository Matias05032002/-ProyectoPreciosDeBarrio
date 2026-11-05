package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Admin.ServicioAdmin;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorAdmin {

  private final ServicioAdmin servicioAdmin;
  private final ServicioComercio servicioComercio;

  @Autowired
  public ControladorAdmin(ServicioAdmin servicioAdmin, ServicioComercio servicioComercio) {
    this.servicioAdmin = servicioAdmin;
    this.servicioComercio = servicioComercio;
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

  // Mostrar solicitudes de verificación
  @RequestMapping(path = "/admin/verificaciones", method = RequestMethod.GET)
  public ModelAndView verSolicitudesVerificacion(HttpSession session) {
    String rol = (String) session.getAttribute("ROL");

    if (rol == null || !"ADMIN".equals(rol)) {
      return new ModelAndView("redirect:/home");
    }

    ModelAndView mav = new ModelAndView("admin/verificaciones");

    mav.addObject("solicitudesVerificacion", servicioComercio.listarSolicitudesVerificacion());

    return mav;
  }

  // Aprobar solicitud de verificación
  @RequestMapping(path = "/admin/verificacion/{id}/aprobar", method = RequestMethod.POST)
  public ModelAndView aprobarVerificacion(@PathVariable("id") Long id, HttpSession session) {
    String rol = (String) session.getAttribute("ROL");

    if (rol == null || !"ADMIN".equals(rol)) {
      return new ModelAndView("redirect:/home");
    }

    servicioComercio.aprobarVerificacion(id);

    return new ModelAndView("redirect:/admin/verificaciones?verificacionAprobada=true");
  }
}
