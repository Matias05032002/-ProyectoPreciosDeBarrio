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

    return true;
  }
}
