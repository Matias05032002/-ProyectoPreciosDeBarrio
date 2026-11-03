package com.tallerwebi.dominio.Recuperacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Pedido de recuperación de contraseña. Se guarda el hash SHA-256 del token y no el token: si
 * alguien lee la base, no puede usar los links que están vigentes.
 */
@Entity
@Table(name = "TokenRecuperacion")
public class TokenRecuperacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "tokenHash", length = 64, nullable = false, unique = true)
  private String tokenHash;

  @Column(name = "usuarioId", nullable = false)
  private Long usuarioId;

  @Column(name = "fechaCreacion", nullable = false)
  private LocalDateTime fechaCreacion;

  @Column(name = "fechaVencimiento", nullable = false)
  private LocalDateTime fechaVencimiento;

  @Column(name = "usado", nullable = false)
  private Boolean usado = false;

  public TokenRecuperacion() {}

  public TokenRecuperacion(
    String tokenHash,
    Long usuarioId,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaVencimiento
  ) {
    this.tokenHash = tokenHash;
    this.usuarioId = usuarioId;
    this.fechaCreacion = fechaCreacion;
    this.fechaVencimiento = fechaVencimiento;
    this.usado = false;
  }

  public boolean estaVigente(LocalDateTime ahora) {
    return (
      !Boolean.TRUE.equals(usado) && fechaVencimiento != null && ahora.isBefore(fechaVencimiento)
    );
  }

  public void marcarUsado() {
    this.usado = true;
  }

  public Long getId() {
    return id;
  }

  public String getTokenHash() {
    return tokenHash;
  }

  public Long getUsuarioId() {
    return usuarioId;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public LocalDateTime getFechaVencimiento() {
    return fechaVencimiento;
  }

  public Boolean getUsado() {
    return usado;
  }
}
