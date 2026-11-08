package com.tallerwebi.presentacion.Interceptores;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

// Segunda barrera de seguridad: AuthInterceptor ya garantiza que hay sesión, y este
// verifica que el rol de la sesión pueda entrar a la ruta. Los controladores siguen
// chequeando lo suyo (defensa en profundidad), pero ninguna ruta sensible queda librada
// a que alguien se acuerde de escribir el if.
public class RolInterceptor implements HandlerInterceptor {

  public static final String ROL_ADMIN = "ADMIN";
  public static final String ROL_COMERCIO = "COMERCIO";

  private static final String ATRIBUTO_ROL = "ROL";
  private static final String DESTINO_NO_AUTORIZADO = "/home";

  private record Regla(String patron, String rolPermitido, String rolProhibido, boolean esJson) {
    static Regla soloPara(String patron, String rol, boolean esJson) {
      return new Regla(patron, rol, null, esJson);
    }

    static Regla prohibidoPara(String patron, String rol, boolean esJson) {
      return new Regla(patron, null, rol, esJson);
    }

    boolean permite(String rol) {
      return rolPermitido != null ? rolPermitido.equals(rol) : !rolProhibido.equals(rol);
    }
  }

  // Gana la primera regla que coincide: las rutas JSON van antes que su carpeta general
  private static final List<Regla> REGLAS = List.of(
    Regla.soloPara("/admin/**", ROL_ADMIN, false),
    Regla.soloPara("/inicio-comercio/**", ROL_COMERCIO, true),
    Regla.soloPara("/comercio/perfil/**", ROL_COMERCIO, false),
    Regla.soloPara("/comercio/perfil", ROL_COMERCIO, false),
    Regla.soloPara("/comercio/dashboard", ROL_COMERCIO, false),
    Regla.soloPara("/comercio/ubicaciones/sugerencias", ROL_COMERCIO, true),
    Regla.soloPara("/promociones/mis-promociones", ROL_COMERCIO, false),
    Regla.soloPara("/promociones/nueva/**", ROL_COMERCIO, false),
    Regla.soloPara("/promociones/crear/**", ROL_COMERCIO, false),
    Regla.soloPara("/promociones/*/desactivar", ROL_COMERCIO, false),
    Regla.prohibidoPara("/producto/precios/**", ROL_COMERCIO, true),
    Regla.prohibidoPara("/producto/**", ROL_COMERCIO, false),
    Regla.prohibidoPara("/mis-listas/**", ROL_COMERCIO, false)
  );

  private final AntPathMatcher comparador = new AntPathMatcher();

  @Override
  public boolean preHandle(
    HttpServletRequest request,
    HttpServletResponse response,
    Object handler
  ) throws Exception {
    String ruta = rutaSinContexto(request);
    Regla regla = buscarRegla(ruta);
    if (regla == null || regla.permite(rolDeLaSesion(request))) {
      return true;
    }
    if (regla.esJson()) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
    } else {
      response.sendRedirect(request.getContextPath() + DESTINO_NO_AUTORIZADO);
    }
    return false;
  }

  private Regla buscarRegla(String ruta) {
    for (Regla regla : REGLAS) {
      if (comparador.match(regla.patron(), ruta)) {
        return regla;
      }
    }
    return null;
  }

  private String rolDeLaSesion(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session == null) {
      return null;
    }
    Object rol = session.getAttribute(ATRIBUTO_ROL);
    return rol instanceof String texto ? texto : null;
  }

  // Se normaliza para que "/admin/../admin//x" o ";jsessionid" no esquiven la regla
  private String rutaSinContexto(HttpServletRequest request) {
    String ruta = request.getRequestURI();
    String contexto = request.getContextPath();
    if (contexto != null && !contexto.isEmpty() && ruta.startsWith(contexto)) {
      ruta = ruta.substring(contexto.length());
    }
    int parametros = ruta.indexOf(';');
    if (parametros >= 0) {
      ruta = ruta.substring(0, parametros);
    }
    return java.net.URI
      .create("http://x" + (ruta.startsWith("/") ? ruta : "/" + ruta))
      .normalize()
      .getPath()
      .replaceAll("/{2,}", "/");
  }
}
