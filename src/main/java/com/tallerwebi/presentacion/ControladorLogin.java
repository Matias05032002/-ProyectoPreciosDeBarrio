package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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
  private ServicioComercio servicioComercio;
  private static final String VISTA_NUEVO_USUARIO = "nuevo-usuario";
  private static final String ATTR_ERROR = "error";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String ATTR_USUARIO = "usuario";

  @Autowired
  public ControladorLogin(ServicioLogin servicioLogin, ServicioComercio servicioComercio) {
    this.servicioLogin = servicioLogin;
    this.servicioComercio = servicioComercio;
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
      return new ModelAndView("redirect:/home");
    } else {
      Map<String, Object> model = new ModelMap();
      model.put(ATTR_ERROR, "Usuario o clave incorrecta");
      return new ModelAndView("login", model);
    }
  }

  @RequestMapping(path = "/registrarme", method = RequestMethod.POST)
  public ModelAndView registrarme(
    @RequestParam("email") String email,
    @RequestParam("password") String password,
    @RequestParam("nombre") String nombre,
    @RequestParam("apellido") String apellido,
    @RequestParam("localidad") String localidad,
    HttpServletRequest request,
    RedirectAttributes redirectAttrs
  ) {
    Usuario usuario = new Usuario();
    usuario.setEmail(email);
    usuario.setPassword(password);
    usuario.setNombre(nombre);
    usuario.setApellido(apellido);
    usuario.setRol("USER");

    try {
      servicioLogin.registrar(usuario);
      return new ModelAndView(REDIRECT_LOGIN);
    } catch (Exception e) {
      redirectAttrs.addFlashAttribute("error", e.getMessage());
      return new ModelAndView("redirect:/nuevo-usuario");
    }
  }

  @RequestMapping(path = "/registrarme-comercio", method = RequestMethod.POST)
  public ModelAndView registrarmeComercio(
    @RequestParam("email") String email,
    @RequestParam("password") String password,
    @RequestParam("nombre") String nombre,
    @RequestParam("tipo") String tipo,
    @RequestParam("direccion") String direccion,
    @RequestParam("localidad") String localidad,
    HttpServletRequest request,
    RedirectAttributes redirectAttrs
  ) {
    Comercio existente = servicioComercio.buscarPorDireccionYLocalidad(direccion, localidad);
    if (existente != null) {
      return new ModelAndView(
        "redirect:/nuevo-usuario?errorComercio=Ya+existe+un+comercio+en+esa+direcci%C3%B3n+y+localidad"
      );
    }

    Usuario usuario = new Usuario();
    usuario.setEmail(email);
    usuario.setPassword(password);
    usuario.setNombre(nombre);
    usuario.setRol("COMERCIO");

    try {
      servicioLogin.registrar(usuario);
    } catch (Exception e) {
      return new ModelAndView(
        "redirect:/nuevo-usuario?errorComercio=" +
        java.net.URLEncoder.encode(
          e.getMessage() != null ? e.getMessage() : "Error al registrar",
          java.nio.charset.StandardCharsets.UTF_8
        )
      );
    }

    Usuario usuarioCreado = servicioLogin.buscarPorEmail(email);
    Comercio comercio = new Comercio();
    comercio.setNombre(nombre);
    comercio.setTipo(tipo);
    comercio.setDireccion(direccion);
    comercio.setLocalidad(localidad);
    comercio.setActivo(true);
    comercio.setFechaRegistro(LocalDateTime.now());
    if (usuarioCreado != null) {
      comercio.setUsuarioId(usuarioCreado.getId());
    }

    servicioComercio.guardarComercio(comercio);
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
