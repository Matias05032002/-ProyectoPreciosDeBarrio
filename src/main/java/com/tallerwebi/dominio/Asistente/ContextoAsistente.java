package com.tallerwebi.dominio.Asistente;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.Promocion;
import com.tallerwebi.dominio.Comercio.RepositorioComercio;
import com.tallerwebi.dominio.Comercio.RepositorioHorario;
import com.tallerwebi.dominio.Comercio.RepositorioPromocion;
import com.tallerwebi.dominio.Comercio.TipoPromocion;
import com.tallerwebi.dominio.Lista.RepositorioLista;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.RepositorioProducto;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Arma el texto con los datos reales de la app que Don Peso necesita para responder
class ContextoAsistente {

  private static final int DIAS_RECIENTES = 7;
  private static final int MAX_REPORTES = 20;
  private static final int MAX_PRODUCTOS_GENERAL = 25;
  private static final int MAX_PROMOCIONES = 10;
  private static final int MAX_PROXIMAS = 5;
  private static final int MAX_CATALOGO = 8;
  private static final int MAX_SIN_REPORTES_RECIENTES = 3;
  private static final double RADIO_KM = 2.0;
  private static final DateTimeFormatter DIA_MES = DateTimeFormatter.ofPattern("dd/MM");

  private final RepositorioReporte repositorioReporte;
  private final RepositorioPromocion repositorioPromocion;
  private final RepositorioProducto repositorioProducto;
  private final SeccionListas seccionListas;
  private final SeccionComercios seccionComercios;

  ContextoAsistente(
    RepositorioReporte repositorioReporte,
    RepositorioPromocion repositorioPromocion,
    RepositorioLista repositorioLista,
    RepositorioProducto repositorioProducto,
    RepositorioComercio repositorioComercio,
    RepositorioHorario repositorioHorario,
    Clock reloj
  ) {
    this.repositorioReporte = repositorioReporte;
    this.repositorioPromocion = repositorioPromocion;
    this.repositorioProducto = repositorioProducto;
    this.seccionListas = new SeccionListas(repositorioLista);
    this.seccionComercios = new SeccionComercios(repositorioComercio, repositorioHorario, reloj);
  }

  String construir(String pregunta, Double latitud, Double longitud, Long usuarioId) {
    List<Reporte> recientes = reportesRecientes();
    List<String> palabras = PalabrasClave.extraer(pregunta);
    List<Promocion> promociones = promocionesVigentes();
    return (
      seccionPrecios(recientes, palabras, latitud, longitud) +
      seccionCatalogo(palabras, recientes) +
      seccionPromociones(promociones) +
      seccionPromocionesProximas() +
      seccionListas.construir(usuarioId, recientes, promociones) +
      seccionComercios.construir(pregunta, latitud, longitud)
    );
  }

  // ---------- Precios ----------

  private List<Reporte> reportesRecientes() {
    return repositorioReporte
      .listarRecientes(DIAS_RECIENTES)
      .stream()
      .filter(r -> r.getComercio() != null && r.getProducto() != null && r.getPrecio() != null)
      .toList();
  }

  private String seccionPrecios(
    List<Reporte> recientes,
    List<String> palabras,
    Double latitud,
    Double longitud
  ) {
    List<Reporte> coinciden = recientes
      .stream()
      .filter(r -> PalabrasClave.coincide(textoDeBusqueda(r.getProducto()), palabras))
      .toList();
    boolean especifico = !coinciden.isEmpty();
    List<Reporte> base = especifico ? coinciden : recientes;

    boolean ubicado = latitud != null && longitud != null;
    List<Reporte> cercanos = base
      .stream()
      .filter(r -> estaCerca(r.getComercio(), latitud, longitud))
      .toList();
    boolean sinCercanos = ubicado && cercanos.isEmpty();
    List<Reporte> candidatos = sinCercanos ? base : cercanos;

    List<Reporte> elegidos = especifico
      ? candidatos.stream().sorted(Formato.porPrecio()).limit(MAX_REPORTES).toList()
      : masBaratoPorProducto(candidatos).stream().limit(MAX_PRODUCTOS_GENERAL).toList();
    if (elegidos.isEmpty()) {
      return "No hay reportes disponibles.\n";
    }

    StringBuilder sb = new StringBuilder(
      especifico
        ? "Precios reportados que coinciden con la pregunta (del más barato al más caro)"
        : "Precio más barato reportado de cada producto"
    );
    if (ubicado) {
      sb.append(sinCercanos ? " (sin filtro de distancia)" : " (a menos de 2 km)");
    }
    sb.append(":\n");
    elegidos.forEach(r -> sb.append(formatearReporte(r, latitud, longitud)));
    return sb.toString();
  }

  private String formatearReporte(Reporte reporte, Double latitud, Double longitud) {
    StringBuilder linea = new StringBuilder(
      String.format(
        Formato.NUMEROS,
        "- %s: %s en %s, %s",
        nombreCompleto(reporte.getProducto()),
        Formato.dinero(reporte.getPrecio()),
        reporte.getComercio().getNombre(),
        reporte.getComercio().getDireccion()
      )
    );
    linea.append(antiguedad(reporte));
    if (reporte.getPuntuacion() != null && reporte.getPuntuacion() > Reporte.PUNTUACION_DUDOSO) {
      linea.append(" [otros vecinos lo marcaron como dudoso]");
    }
    if (Distancia.hayDistancia(reporte.getComercio(), latitud, longitud)) {
      linea.append(
        String.format(
          Formato.NUMEROS,
          " a %.1f km",
          Distancia.km(latitud, longitud, reporte.getComercio())
        )
      );
    }
    return linea.append(System.lineSeparator()).toString();
  }

  private String antiguedad(Reporte reporte) {
    if (reporte.getFechaDeReporte() == null) {
      return "";
    }
    long dias = ChronoUnit.DAYS.between(reporte.getFechaDeReporte().toLocalDate(), LocalDate.now());
    return dias <= 0 ? " (de hoy)" : " (hace " + dias + (dias == 1 ? " día)" : " días)");
  }

  private List<Reporte> masBaratoPorProducto(List<Reporte> reportes) {
    Map<String, Reporte> mejores = new LinkedHashMap<>();
    for (Reporte reporte : reportes) {
      mejores.merge(
        Formato.clave(reporte.getProducto()),
        reporte,
        (actual, nuevo) -> nuevo.getPrecio() < actual.getPrecio() ? nuevo : actual
      );
    }
    return mejores.values().stream().sorted(Formato.porPrecio()).toList();
  }

  // ---------- Catálogo de productos ----------

  private String seccionCatalogo(List<String> palabras, List<Reporte> recientes) {
    if (palabras.isEmpty()) {
      return "";
    }
    Map<String, Producto> encontrados = new LinkedHashMap<>();
    for (String palabra : palabras) {
      for (Producto producto : repositorioProducto.buscarPorNombre(palabra)) {
        encontrados.putIfAbsent(Formato.clave(producto), producto);
      }
    }
    List<Producto> catalogo = encontrados.values().stream().limit(MAX_CATALOGO).toList();
    if (catalogo.isEmpty()) {
      return "";
    }

    StringBuilder sb = new StringBuilder(
      "\nProductos del catálogo que coinciden con la pregunta:\n"
    );
    catalogo.forEach(p -> sb.append("- ").append(descripcionCatalogo(p)).append("\n"));

    List<String> conPreciosRecientes = recientes
      .stream()
      .map(r -> Formato.clave(r.getProducto()))
      .toList();
    List<Producto> sinRecientes = catalogo
      .stream()
      .filter(p -> !conPreciosRecientes.contains(Formato.clave(p)))
      .limit(MAX_SIN_REPORTES_RECIENTES)
      .toList();
    if (!sinRecientes.isEmpty()) {
      sb.append("Sin reportes de los últimos 7 días (último precio conocido):\n");
      sinRecientes.forEach(p -> sb.append(ultimoPrecioConocido(p)));
    }
    return sb.toString();
  }

  private String descripcionCatalogo(Producto producto) {
    String categoria = producto.getCategoria() == null
      ? ""
      : " (categoría: " + producto.getCategoria() + ")";
    return nombreCompleto(producto) + categoria;
  }

  private String ultimoPrecioConocido(Producto producto) {
    Optional<Reporte> ultimo = producto.getId() == null
      ? Optional.empty()
      : repositorioReporte
        .buscarPorProducto(producto.getId())
        .stream()
        .filter(r -> r.getComercio() != null && r.getPrecio() != null)
        .max(
          Comparator.comparing(
            Reporte::getFechaDeReporte,
            Comparator.nullsFirst(Comparator.naturalOrder())
          )
        );
    if (ultimo.isEmpty()) {
      return "- " + nombreCompleto(producto) + ": todavía nadie reportó su precio\n";
    }
    Reporte reporte = ultimo.get();
    return String.format(
      Formato.NUMEROS,
      "- %s: %s en %s%s (dato viejo)%n",
      nombreCompleto(producto),
      Formato.dinero(reporte.getPrecio()),
      reporte.getComercio().getNombre(),
      antiguedad(reporte)
    );
  }

  // ---------- Promociones ----------

  private List<Promocion> promocionesVigentes() {
    List<Promocion> promociones = repositorioPromocion.listarActivas();
    return promociones == null ? List.of() : promociones;
  }

  private String seccionPromociones(List<Promocion> promociones) {
    if (promociones.isEmpty()) {
      return "";
    }
    StringBuilder sb = new StringBuilder("\nPromociones vigentes:\n");
    promociones
      .stream()
      .limit(MAX_PROMOCIONES)
      .forEach(p -> sb.append(formatearPromocion(p, Formato.hasta(p))));
    return sb.toString();
  }

  // Las que todavía no empezaron no se pueden aprovechar hoy: se avisa cuándo arrancan
  private String seccionPromocionesProximas() {
    List<Promocion> proximas = repositorioPromocion.listarProximas();
    if (proximas == null || proximas.isEmpty()) {
      return "";
    }
    StringBuilder sb = new StringBuilder(
      "\nPromociones que todavía NO empezaron (no se pueden usar hoy, avisá desde cuándo valen):\n"
    );
    proximas
      .stream()
      .limit(MAX_PROXIMAS)
      .forEach(p ->
        sb.append(
          formatearPromocion(
            p,
            p.getFechaInicio() == null ? "" : " desde el " + p.getFechaInicio().format(DIA_MES)
          )
        )
      );
    return sb.toString();
  }

  private String formatearPromocion(Promocion promocion, String hasta) {
    String comercio = promocion.getComercio() == null ? "" : promocion.getComercio().getNombre();
    if (promocion.getTipo() == TipoPromocion.METODO_PAGO && promocion.getMetodoPago() != null) {
      return String.format(
        Formato.NUMEROS,
        "- %d%% de descuento pagando con %s en %s (%s)%s%n",
        promocion.getPorcentajeDescuento(),
        promocion.getMetodoPago().getEtiqueta(),
        comercio,
        promocion.getTitulo(),
        hasta
      );
    }
    return String.format(
      Formato.NUMEROS,
      "- %s a $%.2f en %s (%s)%s%n",
      promocion.getProductoNombre(),
      promocion.getPrecioPromocional(),
      comercio,
      promocion.getTitulo(),
      hasta
    );
  }

  // ---------- Utilidades ----------

  private String nombreCompleto(Producto producto) {
    StringBuilder nombre = new StringBuilder(
      producto.getNombre() == null ? "" : producto.getNombre()
    );
    if (producto.getMarca() != null && !producto.getMarca().isBlank()) {
      nombre.append(" ").append(producto.getMarca());
    }
    if (producto.getUnidad() != null && !producto.getUnidad().isBlank()) {
      nombre.append(" ").append(producto.getUnidad());
    }
    return nombre.toString().trim();
  }

  private String textoDeBusqueda(Producto producto) {
    return (
      (producto.getNombre() == null ? "" : producto.getNombre()) +
      " " +
      (producto.getMarca() == null ? "" : producto.getMarca()) +
      " " +
      (producto.getCategoria() == null ? "" : producto.getCategoria())
    );
  }

  private boolean estaCerca(Comercio comercio, Double latitud, Double longitud) {
    if (latitud == null || longitud == null) {
      return true;
    }
    return Distancia.km(latitud, longitud, comercio) <= RADIO_KM;
  }
}
