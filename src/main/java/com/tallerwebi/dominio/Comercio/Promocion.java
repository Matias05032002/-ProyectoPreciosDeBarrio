package com.tallerwebi.dominio.Comercio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Promocion")
@SuppressWarnings("PMD.TooManyFields")
public class Promocion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 100, nullable = false)
  private String titulo;

  @Column(length = 500)
  private String descripcion;

  // Las promociones viejas no tienen tipo: se leen como promociones de producto
  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private TipoPromocion tipo = TipoPromocion.PRODUCTO;

  // Solo las de tipo PRODUCTO llevan producto y precio
  @Column(length = 150)
  private String productoNombre;

  private Double precioPromocional;

  // Solo las de tipo METODO_PAGO llevan medio de pago y porcentaje
  @Enumerated(EnumType.STRING)
  @Column(length = 30)
  private MetodoPago metodoPago;

  private Integer porcentajeDescuento;

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

  public TipoPromocion getTipo() {
    return tipo == null ? TipoPromocion.PRODUCTO : tipo;
  }

  public void setTipo(TipoPromocion tipo) {
    this.tipo = tipo;
  }

  public String getProductoNombre() {
    return productoNombre;
  }

  public void setProductoNombre(String productoNombre) {
    this.productoNombre = productoNombre;
  }

  public MetodoPago getMetodoPago() {
    return metodoPago;
  }

  public void setMetodoPago(MetodoPago metodoPago) {
    this.metodoPago = metodoPago;
  }

  public Integer getPorcentajeDescuento() {
    return porcentajeDescuento;
  }

  public void setPorcentajeDescuento(Integer porcentajeDescuento) {
    this.porcentajeDescuento = porcentajeDescuento;
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
