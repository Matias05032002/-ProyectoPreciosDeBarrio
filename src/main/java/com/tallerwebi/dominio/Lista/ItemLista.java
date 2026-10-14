package com.tallerwebi.dominio.Lista;

import com.tallerwebi.dominio.Reporte.Reporte;
import jakarta.persistence.*;

@Entity
@Table(name = "ITEM_LISTA")
public class ItemLista {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "lista_id", nullable = false)
  private Lista lista;

  @ManyToOne
  @JoinColumn(name = "reporte_id", nullable = false)
  private Reporte reporte;

  @Column(nullable = false)
  private int cantidad;

  public ItemLista() {}

  public ItemLista(Lista lista, Reporte reporte, int cantidad) {
    this.lista = lista;
    this.reporte = reporte;
    this.cantidad = cantidad;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Lista getLista() {
    return lista;
  }

  public void setLista(Lista lista) {
    this.lista = lista;
  }

  public Reporte getReporte() {
    return reporte;
  }

  public void setReporte(Reporte reporte) {
    this.reporte = reporte;
  }

  public int getCantidad() {
    return cantidad;
  }

  public void setCantidad(int cantidad) {
    this.cantidad = cantidad;
  }

  public String getNombreProducto() {
    return reporte.getProducto().getNombre();
  }

  public String getNombreComercio() {
    return reporte.getComercio().getNombre();
  }

  public double getPrecio() {
    return reporte.getPrecio();
  }
}
