package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comercio.ServicioRegistroComercio;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.ComercioYaRegistrado;
import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.DatosDeComercioInvalidos;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorLogin {

  private ServicioLogin servicioLogin;
  private ServicioRegistroComercio servicioRegistroComercio;
  private static final String VISTA_NUEVO_USUARIO = "nuevo-usuario";
  private static final String ATTR_ERROR = "error";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String ATTR_USUARIO = "usuario";
  private static final String ATTR_REGISTRO_EXITOSO = "registroExitoso";
  private static final String MENSAJE_REGISTRO_EXITOSO = "¡Tu cuenta se creó! Ya podés ingresar.";
  private static final String MENSAJE_CUENTA_BLOQUEADA =
    "Demasiados intentos fallidos. Esperá unos minutos antes de volver a intentar.";

  @Autowired
  public ControladorLogin(
    ServicioLogin servicioLogin,
    ServicioRegistroComercio servicioRegistroComercio
  ) {
    this.servicioLogin = servicioLogin;
    this.servicioRegistroComercio = servicioRegistroComercio;
  }

  @RequestMapping("/login")
  public ModelAndView irALogin() {
    Map<String, Object> modelo = new ModelMap();
    modelo.put("datosLogin", new DatosLogin());
    return new ModelAndView("login", modelo);
  }

  @RequestMapping(path = "/validar-login", method = RequestMethod.POST)
  public ModelAndView validarLogin(
    @ModelAttribute("datosLogin") DatosLogin datosLogin,
    HttpServletRequest request
  ) {
    Usuario usuarioBuscado = servicioLogin.consultarUsuario(
      datosLogin.getEmail(),
      datosLogin.getPassword()
    );
    if (usuarioBuscado != null) {
      request.getSession().setAttribute("ROL", usuarioBuscado.getRol());
      request.getSession().setAttribute("EMAIL", usuarioBuscado.getEmail());
      request.getSession().setAttribute("ID", usuarioBuscado.getId());
      request.getSession().setAttribute("NOMBRE", usuarioBuscado.getNombre());
      return new ModelAndView("redirect:/home");
    } else {
      Map<String, Object> model = new ModelMap();
      model.put(
        ATTR_ERROR,
        servicioLogin.estaBloqueado(datosLogin.getEmail())
          ? MENSAJE_CUENTA_BLOQUEADA
          : "Usuario o clave incorrecta"
      );
      return new ModelAndView("login", model);
    }
  }

  @RequestMapping(path = "/registrarme", method = RequestMethod.POST)
  public ModelAndView registrarme(
    @RequestParam("email") String email,
    @RequestParam("password") String password,
    @RequestParam("nombre") String nombre,
    @RequestParam("apellido") String apellido,
    @RequestParam("fechaNacimiento") String fechaNacimiento,
    @RequestParam("localidad") String localidad,
    HttpServletRequest request,
    RedirectAttributes redirectAttrs
  ) {
    Usuario usuario = new Usuario();
    usuario.setEmail(email);
    usuario.setPassword(password);
    usuario.setNombre(nombre);
    usuario.setApellido(apellido);
    usuario.setLocalidad(localidad);
    usuario.setRol("USER");

    try {
      usuario.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
      servicioLogin.registrar(usuario);
      redirectAttrs.addFlashAttribute(ATTR_REGISTRO_EXITOSO, MENSAJE_REGISTRO_EXITOSO);
      return new ModelAndView(REDIRECT_LOGIN);
    } catch (DateTimeParseException e) {
      redirectAttrs.addFlashAttribute("error", "La fecha de nacimiento no es válida");
      return new ModelAndView("redirect:/nuevo-usuario");
    } catch (Exception e) {
      redirectAttrs.addFlashAttribute("error", e.getMessage());
      return new ModelAndView("redirect:/nuevo-usuario");
    }
  }

  @RequestMapping(path = "/registrarme-comercio", method = RequestMethod.POST)
  public ModelAndView registrarmeComercio(
    @RequestParam("email") String email,
    @RequestParam("password") String password,
    @RequestParam("nombre") String nombreIngresado,
    @RequestParam("tipo") String tipo,
    @RequestParam("direccion") String direccionIngresada,
    @RequestParam("localidad") String localidadIngresada,
    HttpServletRequest request,
    RedirectAttributes redirectAttrs
  ) {
    try {
      servicioRegistroComercio.registrar(
        email,
        password,
        nombreIngresado,
        tipo,
        direccionIngresada,
        localidadIngresada
      );
    } catch (
      DatosDeComercioInvalidos
      | ComercioYaRegistrado
      | UsuarioExistente
      | EmailInvalido
      | ContrasenaInvalida e
    ) {
      return new ModelAndView(
        "redirect:/nuevo-usuario?errorComercio=" +
        java.net.URLEncoder.encode(
          e.getMessage() != null ? e.getMessage() : "Error al registrar",
          java.nio.charset.StandardCharsets.UTF_8
        )
      );
    }
    redirectAttrs.addFlashAttribute(ATTR_REGISTRO_EXITOSO, MENSAJE_REGISTRO_EXITOSO);
    return new ModelAndView(REDIRECT_LOGIN);
  }

  @RequestMapping(path = "/nuevo-usuario", method = RequestMethod.GET)
  public ModelAndView nuevoUsuario(
    @RequestParam(value = "errorComercio", required = false) String errorComercio,
    @RequestParam(value = ATTR_ERROR, required = false) String error
  ) {
    Map<String, Object> model = new ModelMap();
    model.put(ATTR_USUARIO, new Usuario());
    if (errorComercio != null && !errorComercio.isEmpty()) model.put(
      "errorComercio",
      errorComercio
    );
    if (error != null && !error.isEmpty()) model.put(ATTR_ERROR, error);
    return new ModelAndView(VISTA_NUEVO_USUARIO, model);
  }

  @RequestMapping(path = "/home", method = RequestMethod.GET)
  public ModelAndView irAHome() {
    return new ModelAndView("home");
  }

  @RequestMapping(path = "/", method = RequestMethod.GET)
  public ModelAndView inicio() {
    return new ModelAndView(REDIRECT_LOGIN);
  }

  @RequestMapping(path = "/logout", method = RequestMethod.GET)
  public ModelAndView logout(HttpServletRequest request) {
    request.getSession().invalidate();
    return new ModelAndView(REDIRECT_LOGIN);
  }
}
