package com.tallerwebi.dominio.Producto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Reporte.Reporte;
import com.tallerwebi.dominio.Reporte.RepositorioReporte;
import com.tallerwebi.presentacion.DTO.ProductoConPrecio;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioProductoTendenciaTest {

  private RepositorioReporte repositorioReporte;
  private ServicioProducto servicioProducto;

  @BeforeEach
  public void init() {
    repositorioReporte = mock(RepositorioReporte.class);
    servicioProducto =
      new ServicioProductoImpl(
        mock(RepositorioProducto.class),
        repositorioReporte,
        mock(ServicioImagenProducto.class),
        mock(ServicioFotoGenerica.class)
      );
  }

  @Test
  public void laTarjetaDeberiaMostrarQueElPrecioDeEseComercioBajo() {
    Producto leche = unProducto(1L, "Leche");
    Comercio almacen = unComercio(1L, "Don Pepe");
    when(repositorioReporte.buscarPorNombre("Leche"))
      .thenReturn(List.of(unReporte(leche, almacen, 1000, 20), unReporte(leche, almacen, 900, 1)));

    List<ProductoConPrecio> tarjetas = servicioProducto.buscarProductosConPrecio(
      "Leche",
      null,
      List.of(),
      null,
      null
    );

    assertThat(tarjetas, hasSize(1));
    assertThat(tarjetas.get(0).getTendencia().porcentaje(), is(-10));
    assertThat(tarjetas.get(0).getTendencia().desde(), is(LocalDate.now().minusDays(20)));
  }

  @Test
  public void cadaTarjetaDeberiaTenerLaTendenciaDeSuPropioComercio() {
    Producto leche = unProducto(1L, "Leche");
    Comercio donPepe = unComercio(1L, "Don Pepe");
    Comercio hermanos = unComercio(2L, "Los Hermanos");
    when(repositorioReporte.buscarPorNombre("Leche"))
      .thenReturn(
        List.of(
          unReporte(leche, donPepe, 1000, 20),
          unReporte(leche, donPepe, 1000, 1),
          unReporte(leche, hermanos, 800, 15),
          unReporte(leche, hermanos, 1000, 2)
        )
      );

    List<ProductoConPrecio> tarjetas = servicioProducto.buscarProductosConPrecio(
      "Leche",
      null,
      List.of(),
      null,
      null
    );

    ProductoConPrecio deDonPepe = tarjetaDe(tarjetas, "Don Pepe");
    ProductoConPrecio deHermanos = tarjetaDe(tarjetas, "Los Hermanos");
    assertThat(deDonPepe.getTendencia(), is(nullValue()));
    assertThat(deHermanos.getTendencia().porcentaje(), is(25));
  }

  @Test
  public void sinReporteAnteriorEnElComercioLaTarjetaNoDeberiaTenerTendencia() {
    Producto leche = unProducto(1L, "Leche");
    when(repositorioReporte.buscarPorNombre("Leche"))
      .thenReturn(
        List.of(
          unReporte(leche, unComercio(1L, "Don Pepe"), 1000, 1),
          unReporte(leche, unComercio(2L, "Los Hermanos"), 800, 15),
          unReporte(leche, unComercio(2L, "Los Hermanos"), 900, 14)
        )
      );

    List<ProductoConPrecio> tarjetas = servicioProducto.buscarProductosConPrecio(
      "Leche",
      null,
      List.of(),
      null,
      null
    );

    assertThat(tarjetaDe(tarjetas, "Don Pepe").getTendencia(), is(nullValue()));
  }

  @Test
  public void unaTarjetaDudosaNoDeberiaTenerTendencia() {
    Producto leche = unProducto(1L, "Leche");
    Comercio almacen = unComercio(1L, "Don Pepe");
    Reporte dudoso = unReporte(leche, almacen, 100, 1);
    dudoso.setPuntuacion(Reporte.PUNTUACION_DUDOSO + 1);
    when(repositorioReporte.buscarPorNombre("Leche"))
      .thenReturn(
        List.of(unReporte(leche, almacen, 1000, 20), unReporte(leche, almacen, 900, 5), dudoso)
      );

    List<ProductoConPrecio> tarjetas = servicioProducto.buscarProductosConPrecio(
      "Leche",
      null,
      List.of(),
      null,
      null
    );

    assertThat(tarjetas.get(0).getTendencia(), is(nullValue()));
  }

  private ProductoConPrecio tarjetaDe(List<ProductoConPrecio> tarjetas, String comercio) {
    return tarjetas
      .stream()
      .filter(t -> comercio.equals(t.getComercio()))
      .findFirst()
      .orElseThrow();
  }

  private Producto unProducto(Long id, String nombre) {
    Producto producto = new Producto();
    producto.setId(id);
    producto.setNombre(nombre);
    return producto;
  }

  private Comercio unComercio(Long id, String nombre) {
    Comercio comercio = new Comercio();
    comercio.setId(id);
    comercio.setNombre(nombre);
    return comercio;
  }

  private Reporte unReporte(Producto producto, Comercio comercio, double precio, int haceDias) {
    Reporte reporte = new Reporte();
    reporte.setProducto(producto);
    reporte.setComercio(comercio);
    reporte.setPrecio(precio);
    reporte.setFechaDeReporte(LocalDateTime.now().minusDays(haceDias));
    return reporte;
  }
}
