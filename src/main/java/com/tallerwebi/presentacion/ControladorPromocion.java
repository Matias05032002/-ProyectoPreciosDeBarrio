package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.Promocion;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Comercio.ServicioPromocion;
import jakarta.servlet.http.HttpServletRequest;
import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/promociones")
public class ControladorPromocion {

  private static final String ATRIBUTO_PROMOCION = "promocion";
  private static final String ATRIBUTO_ERROR = "error";
  private static final String ATRIBUTO_PROMOCIONES = "promociones";
  private static final String VISTA_MIS_PROMOCIONES = "comercio/mis-promociones";
  private static final String REDIRECT_MIS_PROMOCIONES = "redirect:/promociones/mis-promociones";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_HOME = "redirect:/home";
  private static final String REDIRECT_PERFIL_COMERCIO = "redirect:/comercio/perfil";
  private static final String ROL_COMERCIO = "COMERCIO";
  private static final String MENSAJE_DATOS_INVALIDOS =
    "Revisá los datos: el precio y las fechas deben ser válidos.";
  private static final DateTimeFormatter FORMATO_FECHA_FORMULARIO = DateTimeFormatter.ofPattern(
    "yyyy-MM-dd'T'HH:mm"
  );

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

  // Los <input type="datetime-local"> mandan "2026-11-03T10:30": se lee y se vuelve a escribir
  // en ese mismo formato, así el formulario conserva lo que el comerciante cargó si hay un error.
  @InitBinder
  public void configurarFechas(WebDataBinder binder) {
    binder.registerCustomEditor(
      LocalDateTime.class,
      new PropertyEditorSupport() {
        @Override
        public void setAsText(String texto) {
          if (texto == null || texto.isBlank()) {
            setValue(null);
          } else {
            setValue(LocalDateTime.parse(texto.trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
          }
        }

        @Override
        public String getAsText() {
          LocalDateTime fecha = (LocalDateTime) getValue();
          return fecha == null ? "" : fecha.format(FORMATO_FECHA_FORMULARIO);
        }
      }
    );
  }

  @GetMapping
  public ModelAndView listarActivas() {
    List<Promocion> promociones = servicioPromocion.listarPromocionesActivas();

    ModelAndView mav = new ModelAndView("comercio/promociones");
    mav.addObject(ATRIBUTO_PROMOCIONES, promociones);

    return mav;
  }

  @GetMapping("/mis-promociones")
  public ModelAndView misPromociones(HttpServletRequest request) {
    ModelAndView rechazo = rechazarSiNoEsComercio(request);
    if (rechazo != null) return rechazo;

    return cargarMisPromociones(usuarioDeLaSesion(request));
  }

  @PostMapping("/crear")
  public ModelAndView crear(
    @ModelAttribute(ATRIBUTO_PROMOCION) Promocion promocion,
    BindingResult resultado,
    HttpServletRequest request
  ) {
    ModelAndView rechazo = rechazarSiNoEsComercio(request);
    if (rechazo != null) return rechazo;

    Long usuarioId = usuarioDeLaSesion(request);

    if (resultado.hasErrors()) {
      return volverAlFormulario(usuarioId, MENSAJE_DATOS_INVALIDOS, promocion);
    }

    try {
      servicioPromocion.crearPromocion(promocion, usuarioId);

      return new ModelAndView(REDIRECT_MIS_PROMOCIONES);
    } catch (IllegalArgumentException e) {
      return volverAlFormulario(usuarioId, e.getMessage(), promocion);
    }
  }

  @PostMapping("/{id}/desactivar")
  public ModelAndView desactivar(@PathVariable("id") Long id, HttpServletRequest request) {
    ModelAndView rechazo = rechazarSiNoEsComercio(request);
    if (rechazo != null) return rechazo;

    Long usuarioId = usuarioDeLaSesion(request);

    try {
      servicioPromocion.desactivarPromocion(id, usuarioId);

      return new ModelAndView(REDIRECT_MIS_PROMOCIONES);
    } catch (IllegalArgumentException e) {
      return volverAlFormulario(usuarioId, e.getMessage(), null);
    }
  }

  // Sin sesión va al login; con sesión de otro rol (vecino, admin) va al inicio.
  private ModelAndView rechazarSiNoEsComercio(HttpServletRequest request) {
    Object idSesion = request.getSession().getAttribute("ID");
    if (idSesion == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }
    if (!ROL_COMERCIO.equals(request.getSession().getAttribute("ROL"))) {
      return new ModelAndView(REDIRECT_HOME);
    }
    return null;
  }

  private Long usuarioDeLaSesion(HttpServletRequest request) {
    return ((Number) request.getSession().getAttribute("ID")).longValue();
  }

  private ModelAndView volverAlFormulario(Long usuarioId, String error, Promocion promocion) {
    ModelAndView mav = cargarMisPromociones(usuarioId);

    if (mav.getViewName().startsWith("redirect:")) {
      return mav;
    }

    mav.addObject(ATRIBUTO_ERROR, error);
    if (promocion != null) {
      mav.addObject(ATRIBUTO_PROMOCION, promocion);
    }

    return mav;
  }

  private ModelAndView cargarMisPromociones(Long usuarioId) {
    Comercio comercio = servicioComercio.buscarComercioPorUsuarioId(usuarioId);

    if (comercio == null) {
      return new ModelAndView(REDIRECT_PERFIL_COMERCIO);
    }

    ModelAndView mav = new ModelAndView(VISTA_MIS_PROMOCIONES);

    mav.addObject(ATRIBUTO_PROMOCIONES, servicioPromocion.listarPorComercio(comercio.getId()));
    mav.addObject(ATRIBUTO_PROMOCION, new Promocion());

    return mav;
  }
}
