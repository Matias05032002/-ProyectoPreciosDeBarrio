package com.tallerwebi.dominio.Lista;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LISTA")
public class Lista {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String nombre;

  @Column(nullable = false)
  private Long usuarioId;

  @Column(nullable = false)
  private LocalDateTime fechaCreacion;

  @OneToMany(
    mappedBy = "lista",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.EAGER
  )
  private List<ItemLista> items = new ArrayList<>();

  public Lista() {}

  public Lista(String nombre, Long usuarioId) {
    this.nombre = nombre;
    this.usuarioId = usuarioId;
    this.fechaCreacion = LocalDateTime.now();
  }

  // Getters y setters
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

  public Long getUsuarioId() {
    return usuarioId;
  }

  public void setUsuarioId(Long usuarioId) {
    this.usuarioId = usuarioId;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  public List<ItemLista> getItems() {
    return items;
  }

  public void setItems(List<ItemLista> items) {
    this.items = items;
  }

  public int getCantidadProductos() {
    return items.size();
  }

  public double getTotal() {
    return items.stream().mapToDouble(item -> item.getPrecio() * item.getCantidad()).sum();
  }
}
