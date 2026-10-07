package com.tallerwebi.dominio.Reporte;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Reporte")
public class Reporte {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "fechaDeReporte")
  private LocalDateTime fechaDeReporte;

  private Integer puntuacion;
  private Double precio;
  public static final int PUNTUACION_DUDOSO = 6;
  public static final int PUNTUACION_MAXIMA = 10;

  @ManyToOne
  private Usuario usuario;

  @ManyToOne
  private Comercio comercio;

  @ManyToOne
  private Producto producto;

  @ManyToMany
  @JoinTable(
    name = "reporte_usuarios_dudoso",
    joinColumns = @JoinColumn(name = "reporte_id"),
    inverseJoinColumns = @JoinColumn(name = "usuario_id")
  )
  private List<Usuario> usuariosQueMarcaron = new ArrayList<>();

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

  public LocalDateTime getFechaDeReporte() {
    return fechaDeReporte;
  }

  public void setFechaDeReporte(LocalDateTime fechaDeReporte) {
    this.fechaDeReporte = fechaDeReporte;
  }

  public List<Usuario> getUsuariosQueMarcaron() {
    return usuariosQueMarcaron;
  }

  public void setUsuariosQueMarcaron(List<Usuario> usuariosQueMarcaron) {
    this.usuariosQueMarcaron = usuariosQueMarcaron;
  }
}
