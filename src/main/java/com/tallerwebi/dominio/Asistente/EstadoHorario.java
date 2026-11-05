package com.tallerwebi.dominio.Asistente;

import com.tallerwebi.dominio.Comercio.HorarioComercio;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

// Traduce los horarios cargados de un comercio a una frase que Don Peso pueda usar:
// si está abierto ahora y, si no, cuándo vuelve a abrir
final class EstadoHorario {

  static final String SIN_HORARIO = "sin horario cargado";
  private static final Locale ESPANOL = Locale.of("es", "AR");
  private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
  private static final int DIAS_DE_LA_SEMANA = 7;

  private EstadoHorario() {}

  static String describir(List<HorarioComercio> horarios, LocalDateTime ahora) {
    if (horarios == null || horarios.isEmpty()) {
      return SIN_HORARIO;
    }
    int hoy = ahora.getDayOfWeek().getValue();
    LocalTime hora = ahora.toLocalTime();

    HorarioComercio deHoy = atencion(horarios, hoy);
    if (deHoy != null) {
      if (estaAbierto(deHoy, hora)) {
        return "abierto ahora, cierra a las " + HORA.format(deHoy.getHoraCierre());
      }
      if (hora.isBefore(deHoy.getHoraApertura())) {
        return "cerrado ahora, abre hoy a las " + HORA.format(deHoy.getHoraApertura());
      }
    }
    for (int salto = 1; salto <= DIAS_DE_LA_SEMANA; salto++) {
      int dia = ((hoy - 1 + salto) % DIAS_DE_LA_SEMANA) + 1;
      HorarioComercio proximo = atencion(horarios, dia);
      if (proximo != null) {
        String cuando = salto == 1 ? "mañana" : nombreDelDia(dia);
        return "cerrado ahora, abre " + cuando + " a las " + HORA.format(proximo.getHoraApertura());
      }
    }
    return "cerrado, no tiene días de atención cargados";
  }

  // Un día solo cuenta si está marcado como abierto y tiene las dos horas cargadas
  private static HorarioComercio atencion(List<HorarioComercio> horarios, int dia) {
    return horarios
      .stream()
      .filter(h -> h.getDiaSemana() != null && h.getDiaSemana() == dia)
      .filter(h -> Boolean.TRUE.equals(h.getAbierto()))
      .filter(h -> h.getHoraApertura() != null && h.getHoraCierre() != null)
      .findFirst()
      .orElse(null);
  }

  // Si cierra después de medianoche (cierre antes que apertura), abierto es "desde la apertura o hasta el cierre"
  private static boolean estaAbierto(HorarioComercio horario, LocalTime hora) {
    if (horario.getHoraCierre().isBefore(horario.getHoraApertura())) {
      return !hora.isBefore(horario.getHoraApertura()) || !hora.isAfter(horario.getHoraCierre());
    }
    return !hora.isBefore(horario.getHoraApertura()) && !hora.isAfter(horario.getHoraCierre());
  }

  private static String nombreDelDia(int dia) {
    return DayOfWeek.of(dia).getDisplayName(TextStyle.FULL, ESPANOL);
  }
}
