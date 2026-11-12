package com.tallerwebi.dominio.notificacion;

import java.time.LocalDateTime;

@FunctionalInterface
public interface ServicioAvisoDePromociones {
  int avisarPromocionesNuevas(LocalDateTime ahora);
}
