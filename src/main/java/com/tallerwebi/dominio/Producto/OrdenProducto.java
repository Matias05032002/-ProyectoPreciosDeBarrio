package com.tallerwebi.dominio.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public enum OrdenProducto {
  PRECIO("precio"),
  CERCANIA("cercania"),
  RECIENTE("reciente");

  private final String clave;

  OrdenProducto(String clave) {
    this.clave = clave;
  }

  public String clave() {
    return clave;
  }
  public static List<OrdenProducto> desde(String texto) {
    List<String> pedidos = texto == null
            ? List.of()
            : List.of(texto.toLowerCase(Locale.ROOT).split(","));
    List<OrdenProducto> criterios = new ArrayList<>();
    for (OrdenProducto criterio : values()) {
      if (pedidos.stream().anyMatch(pedido -> pedido.trim().equals(criterio.clave))) {
        criterios.add(criterio);
      }
    }
    return criterios.isEmpty() ? List.of(PRECIO) : criterios;
  }
}