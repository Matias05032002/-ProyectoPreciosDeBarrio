package com.tallerwebi.dominio.notificacion;

import java.time.LocalDateTime;

@FunctionalInterface
public interface ServicioInactividad {
  int avisarVecinosInactivos(LocalDateTime ahora);
}
