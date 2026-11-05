package com.tallerwebi.dominio.Asistente;

import com.tallerwebi.dominio.Comercio.Promocion;
import com.tallerwebi.dominio.Comercio.TipoPromocion;
import com.tallerwebi.dominio.Lista.ItemLista;
import com.tallerwebi.dominio.Lista.Lista;
import com.tallerwebi.dominio.Lista.RepositorioLista;
import com.tallerwebi.dominio.Reporte.Reporte;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// Listas de compras del vecino, con dónde le conviene comprar cada producto
class SeccionListas {

  private static final int MAX_LISTAS = 5;
  private static final int MAX_ITEMS_POR_LISTA = 15;
  private static final double CENTAVO = 0.005;

  private final RepositorioLista repositorioLista;

  SeccionListas(RepositorioLista repositorioLista) {
    this.repositorioLista = repositorioLista;
  }

  String construir(Long usuarioId, List<Reporte> recientes, List<Promocion> promociones) {
    if (usuarioId == null) {
      return "";
    }
    List<Lista> listas = repositorioLista.buscarPorUsuarioId(usuarioId);
    if (listas == null || listas.isEmpty()) {
      return "\nEl vecino todavía no armó listas de compras.\n";
    }
    StringBuilder sb = new StringBuilder("\nListas de compras del vecino:\n");
    listas
      .stream()
      .limit(MAX_LISTAS)
      .forEach(lista -> sb.append(describirLista(lista, recientes, promociones)));
    return sb.toString();
  }

  private String describirLista(Lista lista, List<Reporte> recientes, List<Promocion> promociones) {
    StringBuilder sb = new StringBuilder(
      String.format(
        Formato.NUMEROS,
        "- \"%s\" (%d productos, total %s)%n",
        lista.getNombre(),
        lista.getCantidadProductos(),
        Formato.dinero(lista.getTotal())
      )
    );
    double ahorroTotal = 0;
    for (ItemLista item : lista.getItems().stream().limit(MAX_ITEMS_POR_LISTA).toList()) {
      if (item.getReporte() == null || item.getReporte().getProducto() == null) {
        continue;
      }
      sb.append(
        String.format(
          Formato.NUMEROS,
          "  • %d x %s en %s a %s",
          item.getCantidad(),
          item.getNombreProducto(),
          item.getNombreComercio(),
          Formato.dinero(item.getPrecio())
        )
      );
      Optional<Reporte> alternativa = alternativaMasBarata(item, recientes);
      if (alternativa.isPresent()) {
        double ahorro = (item.getPrecio() - alternativa.get().getPrecio()) * item.getCantidad();
        ahorroTotal += ahorro;
        sb.append(
          String.format(
            Formato.NUMEROS,
            " → más barato: %s en %s (ahorrás %s)",
            Formato.dinero(alternativa.get().getPrecio()),
            alternativa.get().getComercio().getNombre(),
            Formato.dinero(ahorro)
          )
        );
      }
      promocionMasBarata(item, promociones)
        .ifPresent(p ->
          sb.append(
            String.format(
              Formato.NUMEROS,
              " → en promoción: %s en %s%s",
              Formato.dinero(p.getPrecioPromocional()),
              p.getComercio() == null ? "otro comercio" : p.getComercio().getNombre(),
              Formato.hasta(p)
            )
          )
        );
      sb.append("\n");
    }
    if (ahorroTotal > CENTAVO) {
      sb
        .append("  Ahorro posible comprando cada producto en el comercio más barato: ")
        .append(Formato.dinero(ahorroTotal))
        .append("\n");
    }
    return sb.toString();
  }

  // Una promo de producto que le gana al precio que el vecino tiene anotado en su lista
  private Optional<Promocion> promocionMasBarata(ItemLista item, List<Promocion> promociones) {
    String nombre = PalabrasClave.normalizar(item.getReporte().getProducto().getNombre());
    return promociones
      .stream()
      .filter(p -> p.getTipo() != TipoPromocion.METODO_PAGO)
      .filter(p -> p.getPrecioPromocional() != null && p.getProductoNombre() != null)
      .filter(p -> PalabrasClave.normalizar(p.getProductoNombre()).contains(nombre))
      .filter(p -> p.getPrecioPromocional() < item.getPrecio() - CENTAVO)
      .min(Comparator.comparingDouble(Promocion::getPrecioPromocional));
  }

  private Optional<Reporte> alternativaMasBarata(ItemLista item, List<Reporte> recientes) {
    String claveItem = Formato.clave(item.getReporte().getProducto());
    return recientes
      .stream()
      .filter(r -> Formato.clave(r.getProducto()).equals(claveItem))
      .filter(r -> r.getPrecio() < item.getPrecio() - CENTAVO)
      .min(Formato.porPrecio());
  }
}
