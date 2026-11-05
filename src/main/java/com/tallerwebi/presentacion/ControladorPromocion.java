package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.MetodoPago;
import com.tallerwebi.dominio.Comercio.Promocion;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Comercio.ServicioPromocion;
import com.tallerwebi.dominio.Comercio.TipoPromocion;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.ServicioReporte;
import jakarta.servlet.http.HttpServletRequest;
import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;
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
  private static final String VISTA_PROMOCION_PRODUCTO = "comercio/promocion-producto";
  private static final String VISTA_PROMOCION_METODO_PAGO = "comercio/promocion-metodo-pago";
  private static final String REDIRECT_MIS_PROMOCIONES = "redirect:/promociones/mis-promociones";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_HOME = "redirect:/home";
  private static final String REDIRECT_PERFIL_COMERCIO = "redirect:/comercio/perfil";
  private static final String ROL_COMERCIO = "COMERCIO";
  private static final String MENSAJE_DATOS_INVALIDOS =
    "Revisá los datos: el precio, el descuento y las fechas deben ser válidos.";
  private static final DateTimeFormatter FORMATO_FECHA_FORMULARIO = DateTimeFormatter.ofPattern(
    "yyyy-MM-dd'T'HH:mm"
  );

  private final ServicioPromocion servicioPromocion;
  private final ServicioComercio servicioComercio;
  private final ServicioReporte servicioReporte;

  @Autowired
  public ControladorPromocion(
    ServicioPromocion servicioPromocion,
    ServicioComercio servicioComercio,
    ServicioReporte servicioReporte
  ) {
    this.servicioPromocion = servicioPromocion;
    this.servicioComercio = servicioComercio;
    this.servicioReporte = servicioReporte;
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
    mav.addObject("proximas", servicioPromocion.listarPromocionesProximas());

    return mav;
  }

  @GetMapping("/mis-promociones")
  public ModelAndView misPromociones(HttpServletRequest request) {
    ModelAndView rechazo = rechazarSiNoEsComercio(request);
    if (rechazo != null) return rechazo;

    return cargarMisPromociones(usuarioDeLaSesion(request));
  }

  @GetMapping("/nueva/producto")
  public ModelAndView formularioPromocionDeProducto(HttpServletRequest request) {
    ModelAndView rechazo = rechazarSiNoEsComercio(request);
    if (rechazo != null) return rechazo;

    return cargarFormulario(usuarioDeLaSesion(request), TipoPromocion.PRODUCTO, null, null);
  }

  @GetMapping("/nueva/metodo-pago")
  public ModelAndView formularioPromocionDeMetodoPago(HttpServletRequest request) {
    ModelAndView rechazo = rechazarSiNoEsComercio(request);
    if (rechazo != null) return rechazo;

    return cargarFormulario(usuarioDeLaSesion(request), TipoPromocion.METODO_PAGO, null, null);
  }

  @PostMapping("/crear/producto")
  public ModelAndView crearPromocionDeProducto(
    @ModelAttribute(ATRIBUTO_PROMOCION) Promocion promocion,
    BindingResult resultado,
    HttpServletRequest request
  ) {
    return crear(promocion, TipoPromocion.PRODUCTO, resultado, request);
  }

  @PostMapping("/crear/metodo-pago")
  public ModelAndView crearPromocionDeMetodoPago(
    @ModelAttribute(ATRIBUTO_PROMOCION) Promocion promocion,
    BindingResult resultado,
    HttpServletRequest request
  ) {
    return crear(promocion, TipoPromocion.METODO_PAGO, resultado, request);
  }

  // El tipo lo decide la ruta, nunca el formulario: cada formulario crea solo su tipo.
  private ModelAndView crear(
    Promocion promocion,
    TipoPromocion tipo,
    BindingResult resultado,
    HttpServletRequest request
  ) {
    ModelAndView rechazo = rechazarSiNoEsComercio(request);
    if (rechazo != null) return rechazo;

    Long usuarioId = usuarioDeLaSesion(request);
    promocion.setTipo(tipo);

    if (resultado.hasErrors()) {
      return cargarFormulario(usuarioId, tipo, MENSAJE_DATOS_INVALIDOS, promocion);
    }

    try {
      servicioPromocion.crearPromocion(promocion, usuarioId);

      return new ModelAndView(REDIRECT_MIS_PROMOCIONES);
    } catch (IllegalArgumentException e) {
      return cargarFormulario(usuarioId, tipo, e.getMessage(), promocion);
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
      return volverAMisPromociones(usuarioId, e.getMessage());
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

  private ModelAndView volverAMisPromociones(Long usuarioId, String error) {
    ModelAndView mav = cargarMisPromociones(usuarioId);

    if (mav.getViewName().startsWith("redirect:")) {
      return mav;
    }

    mav.addObject(ATRIBUTO_ERROR, error);

    return mav;
  }

  private ModelAndView cargarMisPromociones(Long usuarioId) {
    Comercio comercio = servicioComercio.buscarComercioPorUsuarioId(usuarioId);

    if (comercio == null) {
      return new ModelAndView(REDIRECT_PERFIL_COMERCIO);
    }

    ModelAndView mav = new ModelAndView(VISTA_MIS_PROMOCIONES);

    mav.addObject(ATRIBUTO_PROMOCIONES, servicioPromocion.listarPorComercio(comercio.getId()));

    return mav;
  }

  private ModelAndView cargarFormulario(
    Long usuarioId,
    TipoPromocion tipo,
    String error,
    Promocion promocion
  ) {
    Comercio comercio = servicioComercio.buscarComercioPorUsuarioId(usuarioId);

    if (comercio == null) {
      return new ModelAndView(REDIRECT_PERFIL_COMERCIO);
    }

    ModelAndView mav;
    if (tipo == TipoPromocion.METODO_PAGO) {
      mav = new ModelAndView(VISTA_PROMOCION_METODO_PAGO);
      mav.addObject("metodosPago", MetodoPago.values());
    } else {
      mav = new ModelAndView(VISTA_PROMOCION_PRODUCTO);
      mav.addObject("productosDelComercio", nombresDeProductosDelComercio(comercio));
    }

    mav.addObject(ATRIBUTO_PROMOCION, promocion == null ? new Promocion() : promocion);
    if (error != null) {
      mav.addObject(ATRIBUTO_ERROR, error);
    }

    return mav;
  }

  // Sugerencias para el campo "producto": los que ya se reportaron en el comercio, sin repetir
  private List<String> nombresDeProductosDelComercio(Comercio comercio) {
    return new java.util.ArrayList<>(
      servicioReporte
        .buscarPorComercio(comercio.getId())
        .stream()
        .map(Reporte::getProducto)
        .filter(Objects::nonNull)
        .map(producto -> producto.getNombre())
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(TreeSet::new))
    );
  }
}
