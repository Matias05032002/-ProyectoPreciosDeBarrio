package com.tallerwebi.dominio.Reporte;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Reporte")
public class Reporte {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDateTime fechaDeReporte;
  private Integer puntuacion;
  private Double precio;
  private Boolean vigente;

  @ManyToOne
  private Usuario usuario;

  @ManyToOne
  private Comercio comercio;

  @ManyToOne
  private Producto producto;

  public Reporte() {}

  public Producto getProducto() {
    return producto;
  }

  public void setProducto(Producto producto) {
    this.producto = producto;
  }

  public Comercio getComercio() {
    return comercio;
  }

  public void setComercio(Comercio comercio) {
    this.comercio = comercio;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getPuntuacion() {
    return puntuacion;
  }

  public void setPuntuacion(Integer puntuacion) {
    this.puntuacion = puntuacion;
  }

  public Double getPrecio() {
    return precio;
  }

  public void setPrecio(Double precio) {
    this.precio = precio;
  }

  public Boolean getVigente() {
    return vigente;
  }

  public void setVigente(Boolean vigente) {
    this.vigente = vigente;
  }

  public LocalDateTime getFechaDeReporte() {
    return fechaDeReporte;
  }

  public void setFechaDeReporte(LocalDateTime fechaDeReporte) {
    this.fechaDeReporte = fechaDeReporte;
  }
}
