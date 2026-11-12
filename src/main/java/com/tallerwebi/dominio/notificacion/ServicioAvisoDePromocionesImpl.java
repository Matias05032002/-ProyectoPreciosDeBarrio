package com.tallerwebi.dominio.notificacion;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.Promocion;
import com.tallerwebi.dominio.Comercio.RepositorioPromocion;
import com.tallerwebi.dominio.RepositorioUsuario;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioAvisoDePromocionesImpl implements ServicioAvisoDePromociones {

  // La tarea corre seguido, pero si la app estuvo apagada se recuperan las de los últimos dos días
  static final int DIAS_DE_MARGEN = 2;
  static final String ENLACE = "/promociones";

  private final RepositorioPromocion repositorioPromocion;
  private final RepositorioUsuario repositorioUsuario;
  private final ServicioNotificacion servicioNotificacion;

  @Autowired
  public ServicioAvisoDePromocionesImpl(
    RepositorioPromocion repositorioPromocion,
    RepositorioUsuario repositorioUsuario,
    ServicioNotificacion servicioNotificacion
  ) {
    this.repositorioPromocion = repositorioPromocion;
    this.repositorioUsuario = repositorioUsuario;
    this.servicioNotificacion = servicioNotificacion;
  }

  @Override
  public int avisarPromocionesNuevas(LocalDateTime ahora) {
    int avisos = 0;
    for (Promocion promocion : repositorioPromocion.listarIniciadasEntre(
      ahora.minusDays(DIAS_DE_MARGEN),
      ahora
    )) {
      avisos += avisarVecinos(promocion);
    }
    return avisos;
  }

  private int avisarVecinos(Promocion promocion) {
    Comercio comercio = promocion.getComercio();
    if (comercio == null || comercio.getLocalidad() == null || comercio.getLocalidad().isBlank()) {
      return 0;
    }
    String mensaje = "Nueva promo en " + comercio.getNombre() + ": " + promocion.getTitulo();
    String clave = "promo:" + promocion.getId();
    int avisos = 0;
    for (Long vecinoId : repositorioUsuario.idsDeVecinosDeLocalidad(
      comercio.getLocalidad().trim()
    )) {
      if (
        servicioNotificacion.notificar(
          vecinoId,
          TipoNotificacion.PROMOCION_EN_TU_BARRIO,
          mensaje,
          ENLACE,
          clave
        )
      ) {
        avisos++;
      }
    }
    return avisos;
  }
}
