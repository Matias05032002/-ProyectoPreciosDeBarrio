package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.RolInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorLogin {

  private ServicioLogin servicioLogin;
  private static final String VISTA_NUEVO_USUARIO = "nuevo-usuario";
  private static final String ATTR_ERROR = "error";

  @Autowired
  public ControladorLogin(ServicioLogin servicioLogin) {
    this.servicioLogin = servicioLogin;
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
          @RequestParam String email,
          @RequestParam String password,
          @RequestParam String nombre,
          @RequestParam String apellido,
          @RequestParam String rol,
          HttpServletRequest request) {

    Usuario usuario = new Usuario();
    usuario.setEmail(email);
    usuario.setPassword(password);
    usuario.setNombre(nombre);
    usuario.setApellido(apellido);
    // Solo roles permitidos
    if ("USER".equals(rol) || "COMERCIO".equals(rol)) {
      usuario.setRol(rol);
    } else {
      usuario.setRol("USER");
    }

    ModelAndView mav = new ModelAndView();
    try {
      servicioLogin.registrar(usuario);
      mav.setViewName("redirect:/login");
    } catch (Exception e) {
      mav.setViewName("registro");
      mav.addObject("error", e.getMessage());
    }
    return mav;
  }

  @RequestMapping(path = "/nuevo-usuario", method = RequestMethod.GET)
  public ModelAndView nuevoUsuario() {
    Map<String, Object> model = new ModelMap();
    model.put("usuario", new Usuario());
    return new ModelAndView(VISTA_NUEVO_USUARIO, model);
  }

  @RequestMapping(path = "/home", method = RequestMethod.GET)
  public ModelAndView irAHome() {
    return new ModelAndView("home");
  }

  @RequestMapping(path = "/", method = RequestMethod.GET)
  public ModelAndView inicio() {
    return new ModelAndView("redirect:/login");
  }

  @RequestMapping(path = "/logout", method = RequestMethod.GET)
  public ModelAndView logout(HttpServletRequest request) {
    request.getSession().invalidate();
    return new ModelAndView("redirect:/login");
  }
}
