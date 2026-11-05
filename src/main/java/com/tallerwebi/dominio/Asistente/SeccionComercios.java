package com.tallerwebi.dominio.Asistente;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.HorarioComercio;
import com.tallerwebi.dominio.Comercio.RepositorioComercio;
import com.tallerwebi.dominio.Comercio.RepositorioHorario;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

// Comercios cercanos (o con horario, si el vecino pregunta por eso) y si están abiertos ahora
class SeccionComercios {

  private static final int MAX_COMERCIOS = 6;
  private static final int MAX_COMERCIOS_HORARIO = 8;

  private final RepositorioComercio repositorioComercio;
  private final RepositorioHorario repositorioHorario;
  private final Clock reloj;

  SeccionComercios(
    RepositorioComercio repositorioComercio,
    RepositorioHorario repositorioHorario,
    Clock reloj
  ) {
    this.repositorioComercio = repositorioComercio;
    this.repositorioHorario = repositorioHorario;
    this.reloj = reloj;
  }

  String construir(String pregunta, Double latitud, Double longitud) {
    boolean ubicado = latitud != null && longitud != null;
    if (!ubicado && !pideHorarios(pregunta)) {
      return "";
    }
    List<Comercio> elegidos = ubicado
      ? repositorioComercio
        .listarTodos()
        .stream()
        .filter(c -> !Boolean.FALSE.equals(c.getActivo()))
        .filter(c -> c.getLatitud() != null && c.getLongitud() != null)
        .sorted(Comparator.comparingDouble(c -> Distancia.km(latitud, longitud, c)))
        .limit(MAX_COMERCIOS)
        .toList()
      : repositorioComercio
        .listarTodos()
        .stream()
        .filter(c -> !Boolean.FALSE.equals(c.getActivo()))
        .sorted(Comparator.comparing(Comercio::getNombre, Comparator.nullsLast(String::compareTo)))
        .limit(MAX_COMERCIOS_HORARIO)
        .toList();
    if (elegidos.isEmpty()) {
      return "";
    }
    StringBuilder sb = new StringBuilder(
      ubicado ? "\nComercios más cercanos al vecino:\n" : "\nComercios y su horario de atención:\n"
    );
    LocalDateTime ahora = LocalDateTime.now(reloj);
    elegidos.forEach(c -> sb.append(describirComercio(c, latitud, longitud, ahora)));
    return sb.toString();
  }

  private String describirComercio(
    Comercio comercio,
    Double latitud,
    Double longitud,
    LocalDateTime ahora
  ) {
    String distancia = Distancia.hayDistancia(comercio, latitud, longitud)
      ? String.format(Formato.NUMEROS, ", a %.1f km", Distancia.km(latitud, longitud, comercio))
      : "";
    List<HorarioComercio> horarios = repositorioHorario.buscarPorComercioId(comercio.getId());
    return String.format(
      Formato.NUMEROS,
      "- %s (%s), %s%s, %s%n",
      comercio.getNombre(),
      comercio.getTipo() == null ? "comercio" : comercio.getTipo(),
      comercio.getDireccion(),
      distancia,
      EstadoHorario.describir(horarios, ahora)
    );
  }

  // Sin ubicación solo se listan comercios si el vecino pregunta por horarios
  private boolean pideHorarios(String pregunta) {
    String texto = PalabrasClave.normalizar(pregunta == null ? "" : pregunta);
    return (
      texto.contains("abiert") ||
      texto.contains("abre") ||
      texto.contains("cierr") ||
      texto.contains("horario")
    );
  }
}
