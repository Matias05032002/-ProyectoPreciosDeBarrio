package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.Promocion;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Comercio.ServicioPromocion;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/promociones")
public class ControladorPromocion {

  private static final String ATRIBUTO_PROMOCION = "promocion";

  private final ServicioPromocion servicioPromocion;
  private final ServicioComercio servicioComercio;

  @Autowired
  public ControladorPromocion(
    ServicioPromocion servicioPromocion,
    ServicioComercio servicioComercio
  ) {
    this.servicioPromocion = servicioPromocion;
    this.servicioComercio = servicioComercio;
  }

  @GetMapping
  public ModelAndView listarActivas() {
    List<Promocion> promociones = servicioPromocion.listarPromocionesActivas();

    ModelAndView mav = new ModelAndView("comercio/promociones");
    mav.addObject("promociones", promociones);

    return mav;
  }

  @GetMapping("/mis-promociones")
  public ModelAndView misPromociones(HttpServletRequest request) {
    Object idSesion = request.getSession().getAttribute("ID");

    if (idSesion == null) {
      return new ModelAndView("redirect:/login");
    }

    Long usuarioId = ((Number) idSesion).longValue();

    Comercio comercio = servicioComercio.buscarComercioPorUsuarioId(usuarioId);

    if (comercio == null) {
      return new ModelAndView("redirect:/comercio/perfil");
    }

    List<Promocion> promociones = servicioPromocion.listarPorComercio(comercio.getId());

    ModelAndView mav = new ModelAndView("comercio/mis-promociones");

    mav.addObject("promociones", promociones);
    mav.addObject(ATRIBUTO_PROMOCION, new Promocion());

    return mav;
  }

  @PostMapping("/crear")
  public ModelAndView crear(
    @ModelAttribute(ATRIBUTO_PROMOCION) Promocion promocion,
    HttpServletRequest request
  ) {
    Object idSesion = request.getSession().getAttribute("ID");

    if (idSesion == null) {
      return new ModelAndView("redirect:/login");
    }

    Long usuarioId = ((Number) idSesion).longValue();

    try {
      servicioPromocion.crearPromocion(promocion, usuarioId);

      return new ModelAndView("redirect:/promociones/mis-promociones");
    } catch (IllegalArgumentException e) {
      ModelAndView mav = cargarMisPromociones(usuarioId);

      if (mav.getViewName().startsWith("redirect:")) {
        return mav;
      }

      mav.addObject("error", e.getMessage());
      mav.addObject(ATRIBUTO_PROMOCION, promocion);

      return mav;
    }
  }

  @PostMapping("/{id}/desactivar")
  public ModelAndView desactivar(@PathVariable("id") Long id, HttpServletRequest request) {
    Object idSesion = request.getSession().getAttribute("ID");

    if (idSesion == null) {
      return new ModelAndView("redirect:/login");
    }

    Long usuarioId = ((Number) idSesion).longValue();

    try {
      servicioPromocion.desactivarPromocion(id, usuarioId);

      return new ModelAndView("redirect:/promociones/mis-promociones");
    } catch (IllegalArgumentException e) {
      ModelAndView mav = cargarMisPromociones(usuarioId);

      if (mav.getViewName().startsWith("redirect:")) {
        return mav;
      }

      mav.addObject("error", e.getMessage());

      return mav;
    }
  }

  private ModelAndView cargarMisPromociones(Long usuarioId) {
    Comercio comercio = servicioComercio.buscarComercioPorUsuarioId(usuarioId);

    if (comercio == null) {
      return new ModelAndView("redirect:/comercio/perfil");
    }

    ModelAndView mav = new ModelAndView("comercio/mis-promociones");

    mav.addObject("promociones", servicioPromocion.listarPorComercio(comercio.getId()));

    mav.addObject(ATRIBUTO_PROMOCION, new Promocion());

    return mav;
  }
}
