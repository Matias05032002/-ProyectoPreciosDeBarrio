package com.tallerwebi.dominio.notificacion;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Cada hora avisa a los vecinos de las promociones que acaban de empezar en su localidad.
// Las tareas @Scheduled se activan con @EnableScheduling (ver TareaDeInactividad).
@Component
public class TareaDePromociones {

  private static final Logger LOGGER = LoggerFactory.getLogger(TareaDePromociones.class);

  private final ServicioAvisoDePromociones servicioAvisoDePromociones;

  @Autowired
  public TareaDePromociones(ServicioAvisoDePromociones servicioAvisoDePromociones) {
    this.servicioAvisoDePromociones = servicioAvisoDePromociones;
  }

  @Scheduled(cron = "0 0 * * * *")
  public int ejecutar() {
    try {
      int avisos = servicioAvisoDePromociones.avisarPromocionesNuevas(LocalDateTime.now());
      if (avisos > 0 && LOGGER.isInfoEnabled()) {
        LOGGER.info("Avisos de promociones nuevas: {}", avisos);
      }
      return avisos;
    } catch (RuntimeException excepcion) {
      LOGGER.warn("No se pudo avisar de las promociones nuevas", excepcion);
      return 0;
    }
  }
}
