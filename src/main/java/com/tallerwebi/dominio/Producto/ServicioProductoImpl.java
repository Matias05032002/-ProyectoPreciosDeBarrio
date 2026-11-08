package com.tallerwebi.dominio.Producto;

import com.tallerwebi.dominio.Haversine;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.presentacion.DTO.ProductoConPrecio;
import jakarta.transaction.Transactional;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioProducto")
@Transactional
public class ServicioProductoImpl implements ServicioProducto {

  private static final int MINIMO_DE_LOCALES_PARA_COMPARAR = 2;

  private RepositorioProducto repositorioProducto;
  private RepositorioReporte repositorioReporte;

  @Autowired
  public ServicioProductoImpl(
    RepositorioProducto repositorioProducto,
    RepositorioReporte repositorioReporte
  ) {
    this.repositorioProducto = repositorioProducto;
    this.repositorioReporte = repositorioReporte;
  }

  @Override
  public Producto guardarProducto(Producto producto) {
    return repositorioProducto.guardarProducto(producto);
  }

  @Override
  public List<Producto> listarTodos() {
    return repositorioProducto.listarTodos();
  }

  @Override
  public Producto buscarProductoPorId(Long id) {
    return repositorioProducto.buscarProductoPorId(id);
  }

  @Override
  public List<Producto> buscarPorNombre(String nombre) {
    return repositorioProducto.buscarPorNombre(nombre);
  }

  @Override
  public Producto buscarProductoPorNombreExacto(String nombre) {
    return repositorioProducto.buscarProductoPorNombreExacto(nombre);
  }

  @Override
  public List<ProductoConPrecio> buscarProductosConPrecio(
    String nombre,
    Usuario usuario,
    OrdenProducto orden,
    Double latUsuario,
    Double lngUsuario
  ) {
    return buscarProductosConPrecio(nombre, null, usuario, orden, latUsuario, lngUsuario);
  }

  @Override
  public List<ProductoConPrecio> buscarProductosConPrecio(
    String nombre,
    String categoria,
    Usuario usuario,
    OrdenProducto orden,
    Double latUsuario,
    Double lngUsuario
  ) {
    List<Reporte> reportes = repositorioReporte.buscarPorNombre(nombre);
    if (reportes == null) reportes = Collections.emptyList();
    if (categoria != null && !categoria.isBlank()) {
      String buscada = sinTildes(categoria);
      reportes =
        reportes
          .stream()
          .filter(r ->
            r.getProducto() != null && buscada.equals(sinTildes(r.getProducto().getCategoria()))
          )
          .toList();
    }

    Map<Object, List<ProductoConPrecio>> gruposPorProducto = new LinkedHashMap<>();
    for (Reporte reporte : quedarseConElUltimoDeCadaLocal(reportes)) {
      Producto producto = reporte.getProducto();
      if (producto == null) continue;

      ProductoConPrecio dto = crearProductoConPrecio(reporte, producto, usuario);
      asignarDistancia(dto, reporte, latUsuario, lngUsuario);
      gruposPorProducto
        .computeIfAbsent(claveDelProducto(producto), clave -> new ArrayList<>())
        .add(dto);
    }

    gruposPorProducto.values().forEach(this::marcarElMasBaratoDelGrupo);
    return ordenar(gruposPorProducto, orden);
  }

  // Las categorías se guardaron sin tildes ("Lacteos"): se comparan sin tildes ni mayúsculas
  private String sinTildes(String texto) {
    if (texto == null) return "";
    return Normalizer
      .normalize(texto.trim(), Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")
      .toLowerCase(Locale.ROOT);
  }

  // Todos los locales donde está el producto de un reporte, con el precio vigente de cada uno:
  // los confiables de menor a mayor precio y, al final, los marcados como dudosos.
  @Override
  public List<Reporte> buscarPreciosDelProductoEnCadaLocal(Long reporteId) {
    Reporte elegido = repositorioReporte.buscarReporte(reporteId);
    if (elegido == null || elegido.getProducto() == null || elegido.getProducto().getId() == null) {
      return Collections.emptyList();
    }
    List<Reporte> reportes = repositorioReporte.buscarPorProducto(elegido.getProducto().getId());
    if (reportes == null) return Collections.emptyList();

    List<Reporte> preciosPorLocal = new ArrayList<>(quedarseConElUltimoDeCadaLocal(reportes));
    preciosPorLocal.removeIf(r -> r.getComercio() == null || r.getPrecio() == null);
    preciosPorLocal.sort(
      Comparator.comparing((Reporte r) -> esDudoso(r) ? 1 : 0).thenComparing(Reporte::getPrecio)
    );
    return preciosPorLocal;
  }

  private boolean esDudoso(Reporte reporte) {
    return reporte.getPuntuacion() != null && reporte.getPuntuacion() > Reporte.PUNTUACION_DUDOSO;
  }

  private ProductoConPrecio crearProductoConPrecio(
    Reporte reporte,
    Producto producto,
    Usuario usuario
  ) {
    String nombreComercio = reporte.getComercio() != null
      ? reporte.getComercio().getNombre()
      : null;
    ProductoConPrecio dto = new ProductoConPrecio(
      producto.getNombre(),
      producto.getMarca(),
      producto.getUnidad(),
      producto.getCategoria(),
      reporte.getPrecio(),
      nombreComercio,
      reporte.getId(),
      reporte.getFechaDeReporte(),
      reporte.getPuntuacion()
    );
    if (usuario != null) {
      dto.setYaMarcoDudoso(reporte.getUsuariosQueMarcaron().contains(usuario));
    }
    return dto;
  }

  // Un mismo producto en un mismo local puede tener varios reportes de distintas fechas:
  // solo vale el último, si no un precio viejo y barato le ganaría al precio actual.
  private List<Reporte> quedarseConElUltimoDeCadaLocal(List<Reporte> reportes) {
    Map<String, Reporte> ultimoPorLocal = new HashMap<>();
    for (Reporte reporte : reportes) {
      String clave = claveDelLocal(reporte);
      if (clave != null) ultimoPorLocal.merge(clave, reporte, this::elMasReciente);
    }

    // Identidad y no equals(): lo que se compara es "este mismo reporte", no uno igual
    Set<Reporte> ultimos = Collections.newSetFromMap(new IdentityHashMap<>());
    ultimos.addAll(ultimoPorLocal.values());

    List<Reporte> resultado = new ArrayList<>();
    for (Reporte reporte : reportes) {
      if (claveDelLocal(reporte) == null || ultimos.contains(reporte)) resultado.add(reporte);
    }
    return resultado;
  }

  private String claveDelLocal(Reporte reporte) {
    if (reporte.getProducto() == null || reporte.getComercio() == null) return null;
    Long productoId = reporte.getProducto().getId();
    Long comercioId = reporte.getComercio().getId();
    if (productoId == null || comercioId == null) return null;
    return productoId + "-" + comercioId;
  }

  private Reporte elMasReciente(Reporte uno, Reporte otro) {
    LocalDateTime fechaUno = uno.getFechaDeReporte();
    LocalDateTime fechaOtro = otro.getFechaDeReporte();
    if (fechaUno == null) return otro;
    if (fechaOtro == null) return uno;
    return fechaOtro.isAfter(fechaUno) ? otro : uno;
  }

  // Los productos con id se agrupan por id; los que todavía no tienen id, cada uno por su cuenta
  private Object claveDelProducto(Producto producto) {
    return producto.getId() != null ? producto.getId() : producto;
  }

  private void marcarElMasBaratoDelGrupo(List<ProductoConPrecio> grupo) {
    if (grupo.size() < MINIMO_DE_LOCALES_PARA_COMPARAR) return;

    // Un reporte dudoso no puede ser "el más barato": podría ser un precio mal cargado
    OptionalDouble minimo = grupo
      .stream()
      .filter(dto -> !dto.isEsDudoso())
      .mapToDouble(ProductoConPrecio::getPrecioMinimo)
      .min();
    OptionalDouble maximo = grupo
      .stream()
      .filter(dto -> !dto.isEsDudoso())
      .mapToDouble(ProductoConPrecio::getPrecioMinimo)
      .max();

    for (ProductoConPrecio dto : grupo) {
      dto.setLocalesComparados(grupo.size());
      if (minimo.isEmpty() || dto.isEsDudoso()) continue;
      double diferencia = dto.getPrecioMinimo() - minimo.getAsDouble();
      if (diferencia > 0) {
        dto.setDiferenciaConElMasBarato(diferencia);
      } else {
        dto.setEsMasBarato(true);
        // Cuánto se ahorra comprando acá en vez de en el local más caro (sin contar los dudosos)
        double ahorro = maximo.getAsDouble() - minimo.getAsDouble();
        if (ahorro > 0) {
          dto.setAhorroMaximo(ahorro);
        }
      }
    }
  }

  private List<ProductoConPrecio> ordenar(
    Map<Object, List<ProductoConPrecio>> gruposPorProducto,
    OrdenProducto orden
  ) {
    if (OrdenProducto.CERCANIA.equals(orden)) {
      List<ProductoConPrecio> resultado = aplanar(gruposPorProducto);
      resultado.sort(
        Comparator.comparing(
          ProductoConPrecio::getDistanciaKm,
          Comparator.nullsLast(Comparator.naturalOrder())
        )
      );
      return resultado;
    }
    if (OrdenProducto.RECIENTE.equals(orden)) {
      List<ProductoConPrecio> resultado = aplanar(gruposPorProducto);
      resultado.sort(
        Comparator.comparing(
          ProductoConPrecio::getFechaDeReporte,
          Comparator.nullsLast(Comparator.reverseOrder())
        )
      );
      return resultado;
    }
    return ordenarPorPrecioAgrupandoPorProducto(gruposPorProducto);
  }

  // Cada producto queda junto, con sus locales de más barato a más caro,
  // y los productos se ordenan por su precio más bajo.
  private List<ProductoConPrecio> ordenarPorPrecioAgrupandoPorProducto(
    Map<Object, List<ProductoConPrecio>> gruposPorProducto
  ) {
    List<List<ProductoConPrecio>> grupos = new ArrayList<>(gruposPorProducto.values());
    for (List<ProductoConPrecio> grupo : grupos) {
      grupo.sort(Comparator.comparingDouble(ProductoConPrecio::getPrecioMinimo));
    }
    grupos.sort(Comparator.comparingDouble(grupo -> grupo.get(0).getPrecioMinimo()));

    List<ProductoConPrecio> resultado = new ArrayList<>();
    grupos.forEach(resultado::addAll);
    return resultado;
  }

  private List<ProductoConPrecio> aplanar(Map<Object, List<ProductoConPrecio>> gruposPorProducto) {
    List<ProductoConPrecio> resultado = new ArrayList<>();
    gruposPorProducto.values().forEach(resultado::addAll);
    return resultado;
  }

  private void asignarDistancia(
    ProductoConPrecio dto,
    Reporte reporte,
    Double latUsuario,
    Double lngUsuario
  ) {
    if (latUsuario == null || lngUsuario == null || reporte.getComercio() == null) {
      return;
    }
    Double latC = reporte.getComercio().getLatitud();
    Double lngC = reporte.getComercio().getLongitud();
    if (latC != null && lngC != null) {
      dto.setDistanciaKm(Haversine.km(latUsuario, lngUsuario, latC, lngC));
    }
  }
}
