package com.tallerwebi.dominio.Comercio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPromocionImplTest {

  private static final Long USUARIO_ID = 7L;
  private static final Long COMERCIO_ID = 3L;
  private static final Long PROMOCION_ID = 10L;
  private static final String TITULO = "Oferta especial";
  private static final String DESCRIPCION = "Descuento por tiempo limitado";
  private static final double PRECIO = 1500.0;

  private ServicioPromocion servicioPromocion;
  private RepositorioPromocion repositorioPromocionMock;
  private RepositorioComercio repositorioComercioMock;
  private Comercio comercio;

  @BeforeEach
  public void init() {
    repositorioPromocionMock = mock(RepositorioPromocion.class);
    repositorioComercioMock = mock(RepositorioComercio.class);

    servicioPromocion =
      new ServicioPromocionImpl(repositorioPromocionMock, repositorioComercioMock);

    comercio = new Comercio();
    comercio.setId(COMERCIO_ID);
    comercio.setUsuarioId(USUARIO_ID);
  }

  @Test
  public void crearPromocionConDatosValidos() {
    Promocion promocion = crearPromocionValida();

    when(repositorioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(comercio);
    when(repositorioPromocionMock.guardar(promocion)).thenReturn(promocion);

    Promocion resultado = servicioPromocion.crearPromocion(promocion, USUARIO_ID);

    assertThat(resultado, equalTo(promocion));
    assertThat(resultado.getComercio(), equalTo(comercio));
    assertThat(resultado.getActiva(), equalTo(true));
    verify(repositorioPromocionMock).guardar(promocion);
  }

  @Test
  public void crearPromocionSinComercioLanzaExcepcion() {
    Promocion promocion = crearPromocionValida();

    when(repositorioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(null);

    assertThrows(
      IllegalArgumentException.class,
      () -> servicioPromocion.crearPromocion(promocion, USUARIO_ID)
    );

    verify(repositorioPromocionMock, never()).guardar(promocion);
  }

  @Test
  public void crearPromocionSinTituloLanzaExcepcion() {
    Promocion promocion = crearPromocionValida();
    promocion.setTitulo(" ");

    assertThrows(
      IllegalArgumentException.class,
      () -> servicioPromocion.crearPromocion(promocion, USUARIO_ID)
    );
  }

  @Test
  public void crearPromocionConPrecioInvalidoLanzaExcepcion() {
    Promocion promocion = crearPromocionValida();
    promocion.setPrecioPromocional(0.0);

    when(repositorioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(comercio);

    assertThrows(
      IllegalArgumentException.class,
      () -> servicioPromocion.crearPromocion(promocion, USUARIO_ID)
    );
  }

  @Test
  public void crearPromocionConFechasInvalidasLanzaExcepcion() {
    Promocion promocion = crearPromocionValida();
    promocion.setFechaFin(promocion.getFechaInicio().minusDays(1));

    when(repositorioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(comercio);

    assertThrows(
      IllegalArgumentException.class,
      () -> servicioPromocion.crearPromocion(promocion, USUARIO_ID)
    );
  }

  @Test
  public void buscarPromocionPorIdDelegaEnRepositorio() {
    Promocion promocion = crearPromocionValida();

    when(repositorioPromocionMock.buscarPorId(PROMOCION_ID)).thenReturn(promocion);

    Promocion resultado = servicioPromocion.buscarPorId(PROMOCION_ID);

    assertThat(resultado, equalTo(promocion));
    verify(repositorioPromocionMock).buscarPorId(PROMOCION_ID);
  }

  @Test
  public void listarPromocionesDeUnComercio() {
    when(repositorioPromocionMock.listarPorComercio(COMERCIO_ID))
      .thenReturn(Collections.emptyList());

    assertThat(servicioPromocion.listarPorComercio(COMERCIO_ID), equalTo(Collections.emptyList()));

    verify(repositorioPromocionMock).listarPorComercio(COMERCIO_ID);
  }

  @Test
  public void listarPromocionesActivas() {
    when(repositorioPromocionMock.listarActivas()).thenReturn(Collections.emptyList());

    assertThat(servicioPromocion.listarPromocionesActivas(), equalTo(Collections.emptyList()));

    verify(repositorioPromocionMock).listarActivas();
  }

  @Test
  public void actualizarPromocionPropia() {
    Promocion existente = crearPromocionValida();
    existente.setId(PROMOCION_ID);
    Promocion cambios = crearPromocionValida();
    cambios.setId(PROMOCION_ID);
    cambios.setTitulo("Nueva oferta");

    when(repositorioPromocionMock.buscarPorId(PROMOCION_ID)).thenReturn(existente);
    when(repositorioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(comercio);

    servicioPromocion.actualizarPromocion(cambios, USUARIO_ID);

    assertThat(existente.getTitulo(), equalTo("Nueva oferta"));
    verify(repositorioPromocionMock).actualizar(existente);
  }

  @Test
  public void actualizarPromocionInexistenteLanzaExcepcion() {
    when(repositorioPromocionMock.buscarPorId(PROMOCION_ID)).thenReturn(null);

    Promocion cambios = crearPromocionValida();
    cambios.setId(PROMOCION_ID);

    assertThrows(
      IllegalArgumentException.class,
      () -> servicioPromocion.actualizarPromocion(cambios, USUARIO_ID)
    );
  }

  @Test
  public void actualizarPromocionDeOtroComercioLanzaExcepcion() {
    Promocion existente = crearPromocionValida();
    existente.setId(PROMOCION_ID);

    Comercio otroComercio = new Comercio();
    otroComercio.setId(99L);
    existente.setComercio(otroComercio);

    when(repositorioPromocionMock.buscarPorId(PROMOCION_ID)).thenReturn(existente);
    when(repositorioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(comercio);

    Promocion cambios = crearPromocionValida();
    cambios.setId(PROMOCION_ID);

    assertThrows(
      IllegalArgumentException.class,
      () -> servicioPromocion.actualizarPromocion(cambios, USUARIO_ID)
    );
  }

  @Test
  public void desactivarPromocionPropia() {
    Promocion promocion = crearPromocionValida();
    promocion.setId(PROMOCION_ID);

    when(repositorioPromocionMock.buscarPorId(PROMOCION_ID)).thenReturn(promocion);
    when(repositorioComercioMock.buscarComercioPorUsuarioId(USUARIO_ID)).thenReturn(comercio);

    servicioPromocion.desactivarPromocion(PROMOCION_ID, USUARIO_ID);

    assertThat(promocion.getActiva(), equalTo(false));
    verify(repositorioPromocionMock).actualizar(promocion);
  }

  @Test
  public void desactivarPromocionInexistenteLanzaExcepcion() {
    when(repositorioPromocionMock.buscarPorId(PROMOCION_ID)).thenReturn(null);

    assertThrows(
      IllegalArgumentException.class,
      () -> servicioPromocion.desactivarPromocion(PROMOCION_ID, USUARIO_ID)
    );
  }

  private Promocion crearPromocionValida() {
    Promocion promocion = new Promocion();
    promocion.setTitulo(TITULO);
    promocion.setDescripcion(DESCRIPCION);
    promocion.setPrecioPromocional(PRECIO);
    promocion.setFechaInicio(LocalDateTime.now().plusDays(1));
    promocion.setFechaFin(LocalDateTime.now().plusDays(5));
    promocion.setActiva(true);
    promocion.setComercio(comercio);
    return promocion;
  }
}
