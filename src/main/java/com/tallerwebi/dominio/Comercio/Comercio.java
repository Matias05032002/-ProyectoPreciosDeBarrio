package com.tallerwebi.dominio.Comercio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
  name = "Comercio",
  uniqueConstraints = { @UniqueConstraint(columnNames = { "direccion", "localidad" }) }
)
@SuppressWarnings("PMD.TooManyFields")
public class Comercio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 150)
  private String nombre;

  @Column(length = 100, nullable = false)
  private String localidad;

  @Column(length = 200, nullable = false)
  private String direccion;

  @Column(length = 500)
  private String descripcion;

  @Column(length = 50)
  private String tipo;

  @Lob
  private byte[] foto;

  @Column(name = "fotoTipo", length = 50)
  private String fotoTipo;

  private Boolean activo;
  private LocalDateTime fechaRegistro;
  private Double latitud;
  private Double longitud;
  private Long usuarioId;

  public Comercio() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Double getLatitud() {
    return latitud;
  }

  public void setLatitud(Double latitud) {
    this.latitud = latitud;
  }

  public Double getLongitud() {
    return longitud;
  }

  public void setLongitud(Double longitud) {
    this.longitud = longitud;
  }

  public String getLocalidad() {
    return localidad;
  }

  public void setLocalidad(String localidad) {
    this.localidad = localidad;
  }

  public String getDireccion() {
    return direccion;
  }

  public void setDireccion(String direccion) {
    this.direccion = direccion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public LocalDateTime getFechaRegistro() {
    return fechaRegistro;
  }

  public void setFechaRegistro(LocalDateTime fechaRegistro) {
    this.fechaRegistro = fechaRegistro;
  }

  public Long getUsuarioId() {
    return usuarioId;
  }

  public void setUsuarioId(Long usuarioId) {
    this.usuarioId = usuarioId;
  }

  public byte[] getFoto() {
    if (foto == null) {
      return new byte[0];
    }
    return foto.clone();
  }

  public void setFoto(byte[] fotoNueva) {
    if (fotoNueva == null) {
      this.foto = new byte[0];
    } else {
      this.foto = fotoNueva.clone();
    }
  }

  public String getFotoTipo() {
    return fotoTipo;
  }

  public void setFotoTipo(String fotoTipo) {
    this.fotoTipo = fotoTipo;
  }
}
