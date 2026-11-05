package com.tallerwebi.dominio.Reporte;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.ServicioComercio;
import com.tallerwebi.dominio.Producto.Producto;
import com.tallerwebi.dominio.Producto.ServicioProducto;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.*;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioReporte")
@Transactional
public class ServicioReporteImpl implements ServicioReporte {

  // Número + unidad de medida. "u" es para productos que se venden por unidad (1u, 6u).
  // Lo usa también ServicioOcr para normalizar lo que lee de los tickets.
  static final String FORMATO_UNIDAD = "^[0-9]+(\\.[0-9]+)?(?i)(g|kg|gr|ml|l|u)$";

  private RepositorioReporte repositorioReporte;
  private ServicioComercio servicioComercio;
  private ServicioProducto servicioProducto;
  private static final int DIAS_PLAZO_EDICION = 2;
  private static final double PRECIO_MAXIMO = 10_000_000.0;
  private static final int LARGO_MAXIMO_CORTO = 100;

  @Autowired
  public ServicioReporteImpl(
    ServicioComercio servicioComercio,
    ServicioProducto servicioProducto,
    RepositorioReporte repositorioReporte
  ) {
    this.servicioComercio = servicioComercio;
    this.servicioProducto = servicioProducto;
    this.repositorioReporte = repositorioReporte;
  }

  @Override
  public Reporte guardarReporte(Reporte reporte)
    throws ReporteExistente, CamposObligatoriosVacios, UnidadInvalida, PrecioIncorrecto {
    validarCamposObligatorios(reporte);
    normalizarTextos(reporte);
    validarUnidad(reporte.getProducto().getUnidad());
    validarPrecio(reporte.getPrecio());
    reporte.setProducto(resolverProducto(reporte.getProducto()));
    reporte.setComercio(resolverComercio(reporte.getComercio()));

    reporte.setFechaDeReporte(LocalDateTime.now());

    if (reporte.getUsuario() != null) {
      if (
        repositorioReporte.buscarReporteDuplicado(
          reporte.getUsuario().getId(),
          reporte.getProducto().getId(),
          reporte.getComercio().getId(),
          reporte.getFechaDeReporte().toLocalDate()
        ) !=
        null
      ) {
        throw new ReporteExistente();
      }
    }
    return repositorioReporte.guardarReporte(reporte);
  }

  @Override
  public Reporte buscarReporte(Long id) {
    return repositorioReporte.buscarReporte(id);
  }

  @Override
  public List<Reporte> buscarPorProducto(Long productoId) {
    return repositorioReporte.buscarPorProducto(productoId);
  }

  @Override
  public Reporte marcarDudoso(Long id, Usuario usuario) {
    Reporte reporte = repositorioReporte.buscarReporte(id);
    if (reporte == null) {
      return null;
    }
    // No puede marcar su propio reporte: se avisa en vez de ignorarlo en silencio
    if (MarcasDeDudoso.esElAutor(reporte, usuario)) {
      throw new ReportePropio();
    }
    // No puede marcar dos veces el mismo reporte
    if (MarcasDeDudoso.yaLoMarco(reporte, usuario)) {
      return reporte;
    }
    return repositorioReporte.marcarDudoso(id, usuario);
  }

  @Override
  public List<Reporte> listarTodos() {
    return repositorioReporte.listarTodos();
  }

  @Override
  public Reporte buscarReporteDuplicado(
    Long usuarioId,
    Long productoId,
    Long comercioId,
    LocalDate fecha
  ) {
    return repositorioReporte.buscarReporteDuplicado(usuarioId, productoId, comercioId, fecha);
  }

  @Override
  public List<Reporte> buscarPorNombre(String nombreProducto) {
    return repositorioReporte.buscarPorNombre(nombreProducto);
  }

  @Override
  public List<Reporte> buscarPorComercio(Long comercioId) {
    return repositorioReporte.buscarPorComercio(comercioId);
  }

  private void validarCamposObligatorios(Reporte reporte) throws CamposObligatoriosVacios {
    if (reporte.getProducto() == null || reporte.getComercio() == null) {
      throw new CamposObligatoriosVacios("Todos los campos son obligatorios");
    }
    List<String> campos = Arrays.asList(
      reporte.getProducto().getNombre(),
      reporte.getProducto().getMarca(),
      reporte.getProducto().getCategoria(),
      reporte.getProducto().getUnidad(),
      reporte.getComercio().getNombre(),
      reporte.getComercio().getDireccion(),
      reporte.getComercio().getLocalidad()
    );
    if (campos.stream().anyMatch(v -> v == null || v.trim().isEmpty())) {
      throw new CamposObligatoriosVacios("Todos los campos son obligatorios");
    }
    validarLargos(reporte);
  }

  // Coinciden con el largo de las columnas: si se pasan, la base falla con un error 500
  private void validarLargos(Reporte reporte) throws CamposObligatoriosVacios {
    validarLargo(reporte.getProducto().getNombre(), "El nombre del producto", 150);
    validarLargo(reporte.getProducto().getMarca(), "La marca", LARGO_MAXIMO_CORTO);
    validarLargo(reporte.getProducto().getCategoria(), "La categoría", LARGO_MAXIMO_CORTO);
    validarLargo(reporte.getComercio().getNombre(), "El nombre del comercio", 150);
    validarLargo(reporte.getComercio().getDireccion(), "La dirección", 200);
    validarLargo(reporte.getComercio().getLocalidad(), "La localidad", LARGO_MAXIMO_CORTO);
  }

  private void validarLargo(String valor, String campo, int maximo)
    throws CamposObligatoriosVacios {
    if (valor.trim().length() > maximo) {
      throw new CamposObligatoriosVacios(campo + " no puede superar los " + maximo + " caracteres");
    }
  }

  @Override
  public List<Reporte> buscarPorUsuarioId(Long usuarioId) {
    return repositorioReporte.buscarPorUsuarioId(usuarioId);
  }

  private void validarUnidad(String unidad) throws UnidadInvalida {
    if (!unidad.matches(FORMATO_UNIDAD)) {
      throw new UnidadInvalida(
        "La unidad debe ser un número seguido de g, kg, gr, ml, L o u " +
        "(ej: 500g, 1kg, 250ml, 1u)"
      );
    }
  }

  private void validarPrecio(Double precio) throws PrecioIncorrecto {
    if (!esPrecioPositivo(precio)) {
      throw new PrecioIncorrecto("El precio debe ser mayor a cero");
    }
    if (precio > PRECIO_MAXIMO) {
      throw new PrecioIncorrecto("El precio no puede superar $10.000.000");
    }
    if (Math.round(precio * 100) != (long) (precio * 100)) {
      throw new PrecioIncorrecto("El precio no puede tener más de 2 decimales");
    }
  }

  // NaN e infinito no son <= 0 pero tampoco son precios: se rechazan junto con el nulo
  private boolean esPrecioPositivo(Double precio) {
    return precio != null && !precio.isNaN() && !precio.isInfinite() && precio > 0;
  }

  @Override
  public boolean puedeEditar(Long reporteId, Long usuarioId) {
    Reporte reporte = repositorioReporte.buscarReporte(reporteId);
    if (reporte == null || reporte.getUsuario() == null) return false;
    if (!reporte.getUsuario().getId().equals(usuarioId)) return false;
    if (reporte.getFechaDeReporte() == null) return false;
    return reporte.getFechaDeReporte().isAfter(LocalDateTime.now().minusDays(DIAS_PLAZO_EDICION));
  }

  // El producto y el comercio son compartidos por todos los reportes: editar un reporte nunca los
  // modifica. Si el vecino cambia el producto o el comercio, el reporte pasa a apuntar al que
  // corresponda (existente o nuevo) y los demás reportes quedan como estaban.
  @Override
  public void editarReporte(Long id, Reporte datos, Long usuarioId)
    throws CamposObligatoriosVacios, UnidadInvalida, PrecioIncorrecto, FueraDePlazoDeEdicion {
    Reporte reporte = repositorioReporte.buscarReporte(id);
    validarPermisoEdicion(reporte, usuarioId);
    validarCamposObligatorios(datos);
    normalizarTextos(datos);
    validarUnidad(datos.getProducto().getUnidad());
    validarPrecio(datos.getPrecio());
    reporte.setProducto(resolverProducto(datos.getProducto()));
    reporte.setComercio(resolverComercio(datos.getComercio()));
    reporte.setPrecio(datos.getPrecio());
    if (datos.getFoto() != null) {
      reporte.setFoto(datos.getFoto());
    }
    repositorioReporte.guardarReporte(reporte);
  }

  private void normalizarTextos(Reporte reporte) {
    Producto producto = reporte.getProducto();
    producto.setNombre(producto.getNombre().trim());
    producto.setMarca(producto.getMarca().trim());
    producto.setCategoria(producto.getCategoria().trim());
    producto.setUnidad(producto.getUnidad().trim());
    Comercio comercio = reporte.getComercio();
    comercio.setNombre(comercio.getNombre().trim());
    comercio.setDireccion(comercio.getDireccion().trim());
    comercio.setLocalidad(comercio.getLocalidad().trim());
  }

  // Un producto es el mismo si coinciden nombre, marca y unidad (la clave única de la tabla).
  // Antes se buscaba solo por nombre y se tomaba el primero: con dos "Leche" de distinta marca
  // o tamaño podía elegir el equivocado o intentar crear uno repetido.
  private Producto resolverProducto(Producto datos) {
    List<Producto> mismoNombre = servicioProducto.buscarPorNombre(datos.getNombre());
    if (mismoNombre != null) {
      for (Producto existente : mismoNombre) {
        if (
          mismoTexto(existente.getNombre(), datos.getNombre()) &&
          mismoTexto(existente.getMarca(), datos.getMarca()) &&
          mismoTexto(existente.getUnidad(), datos.getUnidad())
        ) {
          return existente;
        }
      }
    }
    servicioProducto.guardarProducto(datos);
    return datos;
  }

  // Un comercio es el mismo si coinciden dirección y localidad (la clave única de la tabla),
  // aunque el vecino haya escrito el nombre distinto. Así nadie le cambia el nombre a un local.
  private Comercio resolverComercio(Comercio datos) {
    Comercio existente = buscarPorNombreYDireccion(datos);
    if (existente == null) {
      existente =
        servicioComercio.buscarPorDireccionYLocalidad(datos.getDireccion(), datos.getLocalidad());
    }
    if (existente == null) {
      return servicioComercio.guardarComercio(datos);
    }
    servicioComercio.ubicarSiFalta(existente);
    return existente;
  }

  private Comercio buscarPorNombreYDireccion(Comercio datos) {
    List<Comercio> mismoNombre = servicioComercio.buscarComercioPorNombre(datos.getNombre());
    if (mismoNombre == null) return null;
    return mismoNombre
      .stream()
      .filter(comercio ->
        mismoTexto(comercio.getDireccion(), datos.getDireccion()) &&
        mismoTexto(comercio.getLocalidad(), datos.getLocalidad())
      )
      .findFirst()
      .orElse(null);
  }

  private static boolean mismoTexto(String uno, String otro) {
    return uno != null && otro != null && uno.trim().equalsIgnoreCase(otro.trim());
  }

  private void validarPermisoEdicion(Reporte reporte, Long usuarioId) {
    if (reporte == null || reporte.getUsuario() == null) {
      throw new FueraDePlazoDeEdicion("El reporte no existe o no tiene usuario asignado");
    }
    if (!reporte.getUsuario().getId().equals(usuarioId)) {
      throw new FueraDePlazoDeEdicion("No tenés permiso para editar este reporte");
    }
    if (
      reporte.getFechaDeReporte() == null ||
      reporte.getFechaDeReporte().isBefore(LocalDateTime.now().minusDays(DIAS_PLAZO_EDICION))
    ) {
      throw new FueraDePlazoDeEdicion(
        "El reporte ya no puede editarse porque superó el plazo de " + DIAS_PLAZO_EDICION + " días"
      );
    }
  }

  @Override
  public List<Object[]> contarUsuariosDistintosPorComercio(Long comercioId) {
    return repositorioReporte.contarUsuariosDistintosPorComercio(comercioId);
  }

  @Override
  public Long contarReportesDudososPorComercio(Long comercioId) {
    return repositorioReporte.contarReportesDudososPorComercio(comercioId);
  }

  @Override
  public List<Object[]> rangoEtariosVisitantes(Long comercioId) {
    return repositorioReporte.rangoEtariosVisitantes(comercioId);
  }

  @Override
  public List<Object[]> productosMasBuscadosPorLocalidad(String localidad, int limite) {
    return repositorioReporte.productosMasBuscadosPorLocalidad(localidad, limite);
  }

  @Override
  public List<Reporte> ultimosReportesVisibles(int limite) {
    return MetricasDeReportes.masRecientes(
      MetricasDeReportes.visibles(repositorioReporte.listarTodos()),
      limite
    );
  }

  @Override
  public ResumenDeActividad resumenDeActividad() {
    return MetricasDeReportes.resumen(
      MetricasDeReportes.visibles(repositorioReporte.listarTodos())
    );
  }

  @Override
  public void validarFoto(byte[] foto) throws FotoInvalida {
    FotoDeReporte.validar(foto);
  }

  @Override
  public double precioPromedioDelComercio(Long comercioId) {
    return MetricasDeReportes.precioPromedio(reportesDelComercio(comercioId));
  }

  @Override
  public String productoEstrellaDelComercio(Long comercioId) {
    return MetricasDeReportes.productoEstrella(reportesDelComercio(comercioId));
  }

  private List<Reporte> reportesDelComercio(Long comercioId) {
    return repositorioReporte.buscarPorComercio(comercioId);
  }
}
