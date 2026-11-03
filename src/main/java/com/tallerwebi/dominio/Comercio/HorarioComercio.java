package com.tallerwebi.dominio.Comercio;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "HorarioComercio")
public class HorarioComercio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long comercioId;

  @Column(nullable = false)
  private Integer diaSemana;

  private LocalTime horaApertura;
  private LocalTime horaCierre;

  @Column(nullable = false)
  private Boolean abierto = false;

  public HorarioComercio() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getComercioId() {
    return comercioId;
  }

  public void setComercioId(Long comercioId) {
    this.comercioId = comercioId;
  }

  public Integer getDiaSemana() {
    return diaSemana;
  }

  public void setDiaSemana(Integer diaSemana) {
    this.diaSemana = diaSemana;
  }

  public LocalTime getHoraApertura() {
    return horaApertura;
  }

  public void setHoraApertura(LocalTime horaApertura) {
    this.horaApertura = horaApertura;
  }

  public LocalTime getHoraCierre() {
    return horaCierre;
  }

  public void setHoraCierre(LocalTime horaCierre) {
    this.horaCierre = horaCierre;
  }

  public Boolean getAbierto() {
    return abierto;
  }

  public void setAbierto(Boolean abierto) {
    this.abierto = abierto;
  }

  public String getNombreDia() {
    String[] dias = {
      "",
      "Lunes",
      "Martes",
      "Miércoles",
      "Jueves",
      "Viernes",
      "Sábado",
      "Domingo",
    };
    return (diaSemana != null && diaSemana >= 1 && diaSemana <= 7) ? dias[diaSemana] : "";
  }
}
