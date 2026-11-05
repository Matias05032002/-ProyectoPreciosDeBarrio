package com.tallerwebi.dominio.Asistente;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.tallerwebi.dominio.Comercio.HorarioComercio;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class EstadoHorarioTest {

  // 2026-11-04 es miércoles (día 3)
  private static final LocalDateTime MIERCOLES_10 = LocalDateTime.of(2026, 11, 4, 10, 0);

  private HorarioComercio dia(int dia, String apertura, String cierre) {
    HorarioComercio horario = new HorarioComercio();
    horario.setDiaSemana(dia);
    horario.setAbierto(true);
    horario.setHoraApertura(LocalTime.parse(apertura));
    horario.setHoraCierre(LocalTime.parse(cierre));
    return horario;
  }

  private HorarioComercio cerrado(int dia) {
    HorarioComercio horario = new HorarioComercio();
    horario.setDiaSemana(dia);
    horario.setAbierto(false);
    return horario;
  }

  @Test
  public void sinHorariosCargadosLoDice() {
    assertThat(
      EstadoHorario.describir(Collections.emptyList(), MIERCOLES_10),
      equalTo("sin horario cargado")
    );
    assertThat(EstadoHorario.describir(null, MIERCOLES_10), equalTo("sin horario cargado"));
  }

  @Test
  public void dentroDelHorarioEstaAbiertoYDiceCuandoCierra() {
    List<HorarioComercio> horarios = List.of(dia(3, "09:00", "20:00"));

    assertThat(
      EstadoHorario.describir(horarios, MIERCOLES_10),
      equalTo("abierto ahora, cierra a las 20:00")
    );
  }

  @Test
  public void antesDeAbrirDiceCuandoAbreHoy() {
    List<HorarioComercio> horarios = List.of(dia(3, "14:00", "20:00"));

    assertThat(
      EstadoHorario.describir(horarios, MIERCOLES_10),
      equalTo("cerrado ahora, abre hoy a las 14:00")
    );
  }

  @Test
  public void despuesDeCerrarDiceCuandoAbreManiana() {
    List<HorarioComercio> horarios = List.of(dia(3, "08:00", "09:00"), dia(4, "09:30", "18:00"));

    assertThat(
      EstadoHorario.describir(horarios, MIERCOLES_10),
      equalTo("cerrado ahora, abre mañana a las 09:30")
    );
  }

  @Test
  public void saltaLosDiasCerradosYNombraElDiaQueAbre() {
    List<HorarioComercio> horarios = new ArrayList<>();
    horarios.add(dia(3, "08:00", "09:00"));
    horarios.add(cerrado(4));
    horarios.add(cerrado(5));
    horarios.add(dia(6, "10:00", "13:00"));

    assertThat(
      EstadoHorario.describir(horarios, MIERCOLES_10),
      equalTo("cerrado ahora, abre sábado a las 10:00")
    );
  }

  @Test
  public void unComercioQueCierraDespuesDeMedianocheSigueAbiertoDeMadrugada() {
    List<HorarioComercio> horarios = List.of(dia(3, "20:00", "02:00"));

    assertThat(
      EstadoHorario.describir(horarios, LocalDateTime.of(2026, 11, 4, 1, 0)),
      equalTo("abierto ahora, cierra a las 02:00")
    );
    assertThat(
      EstadoHorario.describir(horarios, LocalDateTime.of(2026, 11, 4, 22, 0)),
      equalTo("abierto ahora, cierra a las 02:00")
    );
  }

  @Test
  public void sinNingunDiaDeAtencionLoDice() {
    List<HorarioComercio> horarios = List.of(cerrado(1), cerrado(2));

    assertThat(
      EstadoHorario.describir(horarios, MIERCOLES_10),
      equalTo("cerrado, no tiene días de atención cargados")
    );
  }
}
