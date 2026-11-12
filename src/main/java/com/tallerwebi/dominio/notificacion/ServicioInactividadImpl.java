package com.tallerwebi.dominio.notificacion;

import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioInactividadImpl implements ServicioInactividad {

  static final int DIAS_SIN_REPORTAR = 14;
  static final String MENSAJE =
    "Hace más de 14 días que no reportás un precio. Con uno solo ayudás a que los vecinos compren mejor.";
  static final String ENLACE = "/reporte";

  private final RepositorioReporte repositorioReporte;
  private final ServicioNotificacion servicioNotificacion;

  @Autowired
  public ServicioInactividadImpl(
    RepositorioReporte repositorioReporte,
    ServicioNotificacion servicioNotificacion
  ) {
    this.repositorioReporte = repositorioReporte;
    this.servicioNotificacion = servicioNotificacion;
  }

  @Override
  public int avisarVecinosInactivos(LocalDateTime ahora) {
    List<Object[]> inactivos = repositorioReporte.vecinosSinReportarDesde(
      ahora.minusDays(DIAS_SIN_REPORTAR)
    );
    int avisados = 0;
    for (Object[] fila : inactivos) {
      Long usuarioId = (Long) fila[0];
      LocalDateTime ultimoReporte = (LocalDateTime) fila[1];
      // La clave lleva la fecha del último reporte: no se repite hasta que reporte y vuelva a faltar
      String clave = "inactividad:" + ultimoReporte.toLocalDate();
      if (
        servicioNotificacion.notificar(
          usuarioId,
          TipoNotificacion.RECORDATORIO_DE_INACTIVIDAD,
          MENSAJE,
          ENLACE,
          clave
        )
      ) {
        avisados++;
      }
    }
    return avisados;
  }
}
