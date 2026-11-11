package com.tallerwebi.dominio.Comercio;

import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

// Al arrancar la app les pone categoría a los comercios que quedaron sin ninguna.
@Component
public class CompletadorDeTipos implements ApplicationListener<ContextRefreshedEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CompletadorDeTipos.class);

  private final ServicioComercio servicioComercio;
  private final AtomicBoolean iniciado = new AtomicBoolean(false);

  @Autowired
  public CompletadorDeTipos(ServicioComercio servicioComercio) {
    this.servicioComercio = servicioComercio;
  }

  // Jetty publica el evento una vez por contexto (raíz y servlet): solo el primero trabaja
  @Override
  public void onApplicationEvent(ContextRefreshedEvent evento) {
    if (iniciado.compareAndSet(false, true)) {
      completar();
    }
  }

  int completar() {
    try {
      int completados = servicioComercio.completarTiposFaltantes();
      if (completados > 0 && LOGGER.isInfoEnabled()) {
        LOGGER.info("Comercios a los que se les completó la categoría: {}", completados);
      }
      return completados;
    } catch (RuntimeException excepcion) {
      LOGGER.warn("No se pudieron completar las categorías de los comercios", excepcion);
      return 0;
    }
  }
}
