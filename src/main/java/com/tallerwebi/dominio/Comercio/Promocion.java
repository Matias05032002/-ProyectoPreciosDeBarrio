package com.tallerwebi.dominio.Comercio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Promocion")
public class Promocion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 100, nullable = false)
  private String titulo;

  @Column(length = 500)
  private String descripcion;

  @Column(nullable = false)
  private Double precioPromocional;

  @Column(nullable = false)
  private LocalDateTime fechaInicio;

  @Column(nullable = false)
  private LocalDateTime fechaFin;

  @Column(nullable = false)
  private Boolean activa = true;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comercio_id", nullable = false)
  private Comercio comercio;

  public Promocion() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public Double getPrecioPromocional() {
    return precioPromocional;
  }

  public void setPrecioPromocional(Double precioPromocional) {
    this.precioPromocional = precioPromocional;
  }

  public LocalDateTime getFechaInicio() {
    return fechaInicio;
  }

  public void setFechaInicio(LocalDateTime fechaInicio) {
    this.fechaInicio = fechaInicio;
  }

  public LocalDateTime getFechaFin() {
    return fechaFin;
  }

  public void setFechaFin(LocalDateTime fechaFin) {
    this.fechaFin = fechaFin;
  }

  public Boolean getActiva() {
    return activa;
  }

  public void setActiva(Boolean activa) {
    this.activa = activa;
  }

  public Comercio getComercio() {
    return comercio;
  }

  public void setComercio(Comercio comercio) {
    this.comercio = comercio;
  }
}
