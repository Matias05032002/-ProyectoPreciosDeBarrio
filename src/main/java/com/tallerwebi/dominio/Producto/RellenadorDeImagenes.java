package com.tallerwebi.dominio.Producto;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

// Al arrancar la app completa la foto de los productos que quedaron sin ninguna (los cargados antes
// de que existiera la búsqueda automática). Corre en segundo plano para no demorar el arranque.
@Component
public class RellenadorDeImagenes implements ApplicationListener<ContextRefreshedEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RellenadorDeImagenes.class);

  private final ServicioProducto servicioProducto;
  private final AtomicBoolean iniciado = new AtomicBoolean(false);

  @Autowired
  public RellenadorDeImagenes(ServicioProducto servicioProducto) {
    this.servicioProducto = servicioProducto;
  }

  // Jetty publica el evento una vez por contexto (raíz y servlet): solo el primero lanza el hilo
  @Override
  public void onApplicationEvent(ContextRefreshedEvent evento) {
    if (iniciado.compareAndSet(false, true)) {
      Thread hilo = new Thread(this::rellenar, "rellenador-de-imagenes");
      hilo.setDaemon(true);
      hilo.start();
    }
  }

  int rellenar() {
    List<Long> ids;
    try {
      ids = servicioProducto.listarIdsDeProductosSinImagen();
    } catch (RuntimeException excepcion) {
      LOGGER.warn("No se pudieron listar los productos sin foto", excepcion);
      return 0;
    }
    int completados = 0;
    for (Long id : ids) {
      try {
        LOGGER.info("Buscando la foto del producto {}", id);
        servicioProducto.completarImagenDelProducto(id);
        completados++;
      } catch (RuntimeException excepcion) {
        LOGGER.warn("No se pudo completar la foto del producto {}", id, excepcion);
      }
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Fotos completadas: {} de {} productos sin foto", completados, ids.size());
    }
    return completados;
  }
}
