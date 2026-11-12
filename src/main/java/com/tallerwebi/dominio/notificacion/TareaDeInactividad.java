package com.tallerwebi.dominio.notificacion;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Una vez por día (10:00) recuerda a los vecinos que dejaron de reportar.
// @EnableScheduling activa los @Scheduled de toda la app: se encuentra por el escaneo de dominio.
@EnableScheduling
@Component
public class TareaDeInactividad {

  private static final Logger LOGGER = LoggerFactory.getLogger(TareaDeInactividad.class);

  private final ServicioInactividad servicioInactividad;

  @Autowired
  public TareaDeInactividad(ServicioInactividad servicioInactividad) {
    this.servicioInactividad = servicioInactividad;
  }

  @Scheduled(cron = "0 0 10 * * *")
  public int ejecutar() {
    try {
      int avisados = servicioInactividad.avisarVecinosInactivos(LocalDateTime.now());
      if (avisados > 0 && LOGGER.isInfoEnabled()) {
        LOGGER.info("Vecinos avisados por inactividad: {}", avisados);
      }
      return avisados;
    } catch (RuntimeException excepcion) {
      // Un fallo de hoy no debe frenar la tarea de mañana
      LOGGER.warn("No se pudo avisar a los vecinos inactivos", excepcion);
      return 0;
    }
  }
}
