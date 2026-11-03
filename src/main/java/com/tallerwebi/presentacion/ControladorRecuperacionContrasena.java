package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Recuperacion.ServicioRecuperacionContrasena;
import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.TokenDeRecuperacionInvalido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorRecuperacionContrasena {

  private static final String VISTA_OLVIDE = "olvide-contrasena";
  private static final String VISTA_RESTABLECER = "restablecer-contrasena";
  private static final String ATRIBUTO_TOKEN = "token";
  private static final String ATRIBUTO_TOKEN_INVALIDO = "tokenInvalido";
  private static final String MENSAJE_PEDIDO_ENVIADO =
    "Si el email corresponde a una cuenta, te enviamos un link para restablecer la contraseña. " +
    "Revisá tu bandeja de entrada y el correo no deseado.";
  private static final String MENSAJE_CONTRASENA_RESTABLECIDA =
    "Tu contraseña se cambió. Ya podés ingresar con la nueva.";

  private final ServicioRecuperacionContrasena servicioRecuperacion;

  @Autowired
  public ControladorRecuperacionContrasena(ServicioRecuperacionContrasena servicioRecuperacion) {
    this.servicioRecuperacion = servicioRecuperacion;
  }

  @GetMapping("/olvide-contrasena")
  public ModelAndView olvideContrasena() {
    return new ModelAndView(VISTA_OLVIDE);
  }

  // Siempre el mismo mensaje, exista o no la cuenta: así no se puede averiguar quién está registrado
  @PostMapping("/olvide-contrasena")
  public ModelAndView solicitarRecuperacion(
    @RequestParam(value = "email", required = false) String email,
    RedirectAttributes redirectAttributes
  ) {
    servicioRecuperacion.solicitarRecuperacion(email);
    redirectAttributes.addFlashAttribute("pedidoEnviado", MENSAJE_PEDIDO_ENVIADO);
    return new ModelAndView("redirect:/" + VISTA_OLVIDE);
  }

  @GetMapping("/restablecer-contrasena")
  public ModelAndView formularioRestablecer(
    @RequestParam(value = "token", required = false) String token
  ) {
    ModelAndView mav = new ModelAndView(VISTA_RESTABLECER);
    if (servicioRecuperacion.esTokenValido(token)) {
      mav.addObject(ATRIBUTO_TOKEN, token);
    } else {
      mav.addObject(ATRIBUTO_TOKEN_INVALIDO, true);
    }
    return mav;
  }

  @PostMapping("/restablecer-contrasena")
  public ModelAndView restablecer(
    @RequestParam(value = "token", required = false) String token,
    @RequestParam(value = "contrasena", required = false) String contrasena,
    @RequestParam(value = "confirmacion", required = false) String confirmacion,
    RedirectAttributes redirectAttributes
  ) {
    try {
      servicioRecuperacion.restablecerContrasena(token, contrasena, confirmacion);
    } catch (TokenDeRecuperacionInvalido e) {
      ModelAndView mav = new ModelAndView(VISTA_RESTABLECER);
      mav.addObject(ATRIBUTO_TOKEN_INVALIDO, true);
      return mav;
    } catch (ContrasenaInvalida e) {
      ModelAndView mav = new ModelAndView(VISTA_RESTABLECER);
      mav.addObject(ATRIBUTO_TOKEN, token);
      mav.addObject("error", e.getMessage());
      return mav;
    }
    redirectAttributes.addFlashAttribute("contrasenaRestablecida", MENSAJE_CONTRASENA_RESTABLECIDA);
    return new ModelAndView("redirect:/login");
  }
}
