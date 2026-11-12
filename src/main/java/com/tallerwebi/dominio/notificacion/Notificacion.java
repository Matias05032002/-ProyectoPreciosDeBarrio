package com.tallerwebi.dominio.notificacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

// Aviso que la app le deja a un usuario. La clave evita avisar dos veces lo mismo (por ejemplo,
// "dudoso:15" es el aviso del reporte 15): la base garantiza que no se repite por usuario.
@Entity
@Table(
  name = "Notificacion",
  uniqueConstraints = @UniqueConstraint(columnNames = { "usuarioId", "clave" })
)
public class Notificacion {

  public static final int LARGO_MAXIMO_MENSAJE = 300;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "usuarioId", nullable = false)
  private Long usuarioId;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo", length = 40, nullable = false)
  private TipoNotificacion tipo;

  @Column(name = "mensaje", length = LARGO_MAXIMO_MENSAJE, nullable = false)
  private String mensaje;

  @Column(name = "enlace", length = 200)
  private String enlace;

  @Column(name = "clave", length = 100)
  private String clave;

  @Column(name = "fechaCreacion", nullable = false)
  private LocalDateTime fechaCreacion;

  @Column(name = "leida", nullable = false)
  private boolean leida;

  public Long getId() {
    return id;
  }

  public Long getUsuarioId() {
    return usuarioId;
  }

  public void setUsuarioId(Long usuarioId) {
    this.usuarioId = usuarioId;
  }

  public TipoNotificacion getTipo() {
    return tipo;
  }

  public void setTipo(TipoNotificacion tipo) {
    this.tipo = tipo;
  }

  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  public String getEnlace() {
    return enlace;
  }

  public void setEnlace(String enlace) {
    this.enlace = enlace;
  }

  public String getClave() {
    return clave;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  public boolean isLeida() {
    return leida;
  }

  public void setLeida(boolean leida) {
    this.leida = leida;
  }
}
