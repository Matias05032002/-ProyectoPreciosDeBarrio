package com.tallerwebi.presentacion.Interceptores;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(
    HttpServletRequest request,
    HttpServletResponse response,
    Object handler
  ) throws Exception {
    HttpSession session = request.getSession(false);
    boolean logueado = session != null && session.getAttribute("EMAIL") != null;

    if (!logueado) {
      response.sendRedirect(request.getContextPath() + "/login");
      return false;
    }

    // Con sesión iniciada la página es privada: sin esto, "atrás" tras cerrar sesión podría
    // mostrar datos desde la caché del navegador
    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    response.setHeader("Pragma", "no-cache");
    return true;
  }
}
