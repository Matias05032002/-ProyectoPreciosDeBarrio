package com.tallerwebi.dominio.Comercio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.FotoInvalida;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ServicioComercioTest {

  private ServicioComercio servicioComercio;
  private RepositorioComercio repositorioComercioMock;
  private RepositorioHorario repositorioHorarioMock;
  private ServicioGeolocalizacion servicioGeolocalizacionMock;

  @BeforeEach
  public void init() {
    this.repositorioComercioMock = mock(RepositorioComercio.class);
    this.repositorioHorarioMock = mock(RepositorioHorario.class);
    this.servicioGeolocalizacionMock = mock(ServicioGeolocalizacion.class);
    this.servicioComercio =
      new ServicioComercioImpl(
        this.repositorioComercioMock,
        this.repositorioHorarioMock,
        this.servicioGeolocalizacionMock
      );
  }

  @Test
  public void guardarUnNuevoComercio() {
    Comercio comercio = new Comercio();
    comercio.setNombre("Almacen de Matias");
    when(this.repositorioComercioMock.guardarComercio(comercio)).thenReturn(comercio);

    Comercio resultado = this.servicioComercio.guardarComercio(comercio);

    assertThat(resultado, equalTo(comercio));
    verify(this.repositorioComercioMock, times(1)).guardarComercio(comercio);
  }

  @Test
  public void listarTodosLosComercios() {
    List<Comercio> comercios = Arrays.asList(new Comercio(), new Comercio());
    when(this.repositorioComercioMock.listarTodos()).thenReturn(comercios);

    List<Comercio> resultadosLista = this.servicioComercio.listarTodos();

    assertThat(resultadosLista, equalTo(comercios));
    verify(this.repositorioComercioMock, times(1)).listarTodos();
  }

  @Test
  public void buscarComercioPorId() {
    Comercio comercio = new Comercio();
    comercio.setNombre("Almacen de Matias Llanos");
    when(this.repositorioComercioMock.buscarComercio(1L)).thenReturn(comercio);

    Comercio resultadoComercio = this.servicioComercio.buscarComercio(1L);

    assertThat(resultadoComercio, equalTo(comercio));
    verify(this.repositorioComercioMock, times(1)).buscarComercio(1L);
  }

  @Test
  public void buscarComerciosPorNombre() {
    Comercio comercio = new Comercio();
    List<Comercio> listaDeComercios = Arrays.asList(comercio);
    when(this.repositorioComercioMock.buscarComercioPorNombre("Almacen Mati"))
      .thenReturn(listaDeComercios);

    List<Comercio> comercioBuscado = this.servicioComercio.buscarComercioPorNombre("Almacen Mati");

    assertThat(comercioBuscado, equalTo(listaDeComercios));
    verify(this.repositorioComercioMock, times(1)).buscarComercioPorNombre("Almacen Mati");
  }

  @Test
  public void buscarComercioPorNombreSiNoExisteRetornaNull() {
    when(this.repositorioComercioMock.buscarComercioPorNombre("ComercioInexistente"))
      .thenReturn(Collections.emptyList());

    List<Comercio> comercioBuscado =
      this.servicioComercio.buscarComercioPorNombre("ComercioInexistente");

    assertThat(comercioBuscado, equalTo(Collections.emptyList()));
    verify(this.repositorioComercioMock, times(1)).buscarComercioPorNombre("ComercioInexistente");
  }

  @Test
  public void comercioTieneGettersYSettersCorrectos() {
    Comercio comercio = new Comercio();
    comercio.setId(1L);
    comercio.setNombre("Almacen Central");
    comercio.setLocalidad("Palermo");
    comercio.setDireccion("Av. Santa Fe 1234");
    comercio.setDescripcion("Almacen de barrio");
    comercio.setTipo("Almacen");
    comercio.setActivo(true);
    java.time.LocalDateTime fecha = java.time.LocalDateTime.now();
    comercio.setFechaRegistro(fecha);

    assertThat(comercio.getId(), equalTo(1L));
    assertThat(comercio.getNombre(), equalTo("Almacen Central"));
    assertThat(comercio.getLocalidad(), equalTo("Palermo"));
    assertThat(comercio.getDireccion(), equalTo("Av. Santa Fe 1234"));
    assertThat(comercio.getDescripcion(), equalTo("Almacen de barrio"));
    assertThat(comercio.getTipo(), equalTo("Almacen"));
    assertThat(comercio.getActivo(), equalTo(true));
    assertThat(comercio.getFechaRegistro(), equalTo(fecha));
  }

  @Test
  public void buscarComercioPorUsuarioIdDeberiaRetornarElComercioDelUsuario() {
    Comercio comercio = new Comercio();
    comercio.setNombre("Mi Almacen");
    comercio.setUsuarioId(5L);
    when(this.repositorioComercioMock.buscarComercioPorUsuarioId(5L)).thenReturn(comercio);

    Comercio resultado = this.servicioComercio.buscarComercioPorUsuarioId(5L);

    assertThat(resultado, equalTo(comercio));
    verify(this.repositorioComercioMock, times(1)).buscarComercioPorUsuarioId(5L);
  }

  @Test
  public void buscarComercioPorUsuarioIdSiNoTieneComercioDeberiaRetornarNull() {
    when(this.repositorioComercioMock.buscarComercioPorUsuarioId(99L)).thenReturn(null);

    Comercio resultado = this.servicioComercio.buscarComercioPorUsuarioId(99L);

    assertThat(resultado, equalTo(null));
    verify(this.repositorioComercioMock, times(1)).buscarComercioPorUsuarioId(99L);
  }

  @Test
  public void vincularComercioAUsuarioSiExisteLoReutiliza() {
    Comercio existente = new Comercio();
    existente.setNombre("Supermercado Prueba");
    existente.setDireccion("San Martin 100");
    existente.setLocalidad("La Matanza");

    Comercio formulario = new Comercio();
    formulario.setNombre("Supermercado Prueba");
    formulario.setDireccion("San Martin 100");
    formulario.setLocalidad("La Matanza");

    when(
      repositorioComercioMock.buscarComercioPorNombreDireccionYLocalidad(
        "Supermercado Prueba",
        "San Martin 100",
        "La Matanza"
      )
    )
      .thenReturn(existente);

    Comercio resultado = servicioComercio.vincularComercioAUsuario(formulario, 7L);

    assertThat(resultado, equalTo(existente));
    assertThat(resultado.getUsuarioId(), equalTo(7L));

    verify(repositorioComercioMock, never()).guardarComercio(formulario);
  }

  @Test
  public void vincularComercioAUsuarioSiNoExisteGuardaUnoNuevo() {
    Comercio nuevo = new Comercio();
    nuevo.setNombre("Supermercado Nuevo");
    nuevo.setDireccion("Rivadavia 200");
    nuevo.setLocalidad("La Matanza");

    when(
      repositorioComercioMock.buscarComercioPorNombreDireccionYLocalidad(
        "Supermercado Nuevo",
        "Rivadavia 200",
        "La Matanza"
      )
    )
      .thenReturn(null);

    when(repositorioComercioMock.guardarComercio(nuevo)).thenReturn(nuevo);

    Comercio resultado = servicioComercio.vincularComercioAUsuario(nuevo, 8L);

    assertThat(resultado, equalTo(nuevo));
    assertThat(resultado.getUsuarioId(), equalTo(8L));

    verify(repositorioComercioMock).guardarComercio(nuevo);
  }

  @Test
  public void vincularComercioAUsuarioQueYaTieneComercioNoReclamaOtro() {
    Comercio propio = new Comercio();
    propio.setId(1L);
    propio.setUsuarioId(7L);
    when(repositorioComercioMock.buscarComercioPorUsuarioId(7L)).thenReturn(propio);

    Comercio ajeno = new Comercio();
    ajeno.setNombre("Supermercado Ajeno");
    ajeno.setDireccion("Alberdi 5925");
    ajeno.setLocalidad("Buenos Aires");

    Comercio resultado = servicioComercio.vincularComercioAUsuario(ajeno, 7L);

    assertThat(resultado, equalTo(propio));
    verify(repositorioComercioMock, never())
      .buscarComercioPorNombreDireccionYLocalidad(anyString(), anyString(), anyString());
    verify(repositorioComercioMock, never()).actualizarComercio(any());
    verify(repositorioComercioMock, never()).guardarComercio(any());
  }

  @Test
  public void guardarHorariosDeberiaEliminarYReemplazarLosExistentes() {
    Long comercioId = 1L;
    List<HorarioComercio> horarios = Arrays.asList(new HorarioComercio(), new HorarioComercio());

    servicioComercio.guardarHorarios(comercioId, horarios);

    verify(repositorioHorarioMock, times(1)).eliminarPorComercioId(comercioId);
    verify(repositorioHorarioMock, times(1)).guardarHorarios(horarios);
    horarios.forEach(h -> assertThat(h.getComercioId(), equalTo(comercioId)));
  }

  @Test
  public void obtenerHorariosDeberiaRetornarLosDelComercio() {
    Long comercioId = 2L;
    List<HorarioComercio> esperados = Arrays.asList(new HorarioComercio());
    when(repositorioHorarioMock.buscarPorComercioId(comercioId)).thenReturn(esperados);

    List<HorarioComercio> resultado = servicioComercio.obtenerHorarios(comercioId);

    assertThat(resultado, equalTo(esperados));
    verify(repositorioHorarioMock, times(1)).buscarPorComercioId(comercioId);
  }

  @Test
  public void estaAbiertoDeberiaRetornarNullSiNoHayHorariosCargados() {
    Long comercioId = 3L;
    when(repositorioHorarioMock.buscarPorComercioId(comercioId))
      .thenReturn(Collections.emptyList());

    Boolean resultado = servicioComercio.estaAbierto(comercioId);

    assertThat(resultado, equalTo(null));
  }

  @Test
  public void estaAbiertoDeberiaRetornarFalseSiElDiaEstaMarcarComoNoAbierto() {
    Long comercioId = 4L;
    HorarioComercio horario = new HorarioComercio();
    // Ponemos todos los días como cerrados
    for (int dia = 1; dia <= 7; dia++) {
      horario.setDiaSemana(dia);
      horario.setAbierto(false);
    }
    when(repositorioHorarioMock.buscarPorComercioId(comercioId)).thenReturn(Arrays.asList(horario));

    boolean resultado = servicioComercio.estaAbierto(comercioId);

    assertThat(resultado, equalTo(false));
  }

  private static byte[] jpegDe(int largo) {
    byte[] bytes = new byte[largo];
    bytes[0] = (byte) 0xFF;
    bytes[1] = (byte) 0xD8;
    bytes[2] = (byte) 0xFF;
    return bytes;
  }

  @Test
  public void guardarFotoDeberiaActualizarElComercioConElTipoRealDelArchivo() throws Exception {
    Comercio comercio = new Comercio();
    comercio.setId(1L);
    when(repositorioComercioMock.buscarComercio(1L)).thenReturn(comercio);

    byte[] bytes = jpegDe(100);
    servicioComercio.guardarFoto(1L, bytes);

    assertThat(comercio.getFoto(), equalTo(bytes));
    assertThat(comercio.getFotoTipo(), equalTo("image/jpeg"));
    verify(repositorioComercioMock, times(1)).actualizarComercio(comercio);
  }

  @Test
  public void guardarFotoQueNoEsUnaImagenPermitidaLanzaFotoInvalida() {
    byte[] noEsImagen = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

    assertThrows(FotoInvalida.class, () -> servicioComercio.guardarFoto(1L, noEsImagen));

    verify(repositorioComercioMock, never()).actualizarComercio(any());
  }

  @Test
  public void guardarFotoMayorACincoMegasLanzaFotoInvalida() {
    byte[] enorme = jpegDe(5 * 1024 * 1024 + 1);

    assertThrows(FotoInvalida.class, () -> servicioComercio.guardarFoto(1L, enorme));

    verify(repositorioComercioMock, never()).actualizarComercio(any());
  }

  @Test
  public void guardarFotoDeExactamenteCincoMegasSeAcepta() throws Exception {
    Comercio comercio = new Comercio();
    when(repositorioComercioMock.buscarComercio(1L)).thenReturn(comercio);

    servicioComercio.guardarFoto(1L, jpegDe(5 * 1024 * 1024));

    verify(repositorioComercioMock).actualizarComercio(comercio);
  }

  @Test
  public void guardarFotoCuandoElComercioNoExisteNoDeberiaHacerNada() throws Exception {
    when(repositorioComercioMock.buscarComercio(99L)).thenReturn(null);

    servicioComercio.guardarFoto(99L, jpegDe(100));

    verify(repositorioComercioMock, never()).actualizarComercio(any());
  }

  // ── geolocalización ───────────────────────────────────────────────────────

  private static final UbicacionSugerida SAN_JUSTO = new UbicacionSugerida(
    "DR IGNACIO ARIETA 3000, San Justo, La Matanza, Buenos Aires",
    "Dr Ignacio Arieta 3000",
    "San Justo",
    -34.6879,
    -58.5482,
    "georef"
  );

  private Comercio comercioEn(String direccion, String localidad) {
    Comercio comercio = new Comercio();
    comercio.setNombre("Almacén Doña Rosa");
    comercio.setTipo("Almacen");
    comercio.setDireccion(direccion);
    comercio.setLocalidad(localidad);
    return comercio;
  }

  @Test
  public void guardarUnComercioLoUbicaConLaOpcionMasProbable() {
    Comercio comercio = comercioEn("arrieta 3000", "La Matanza");
    when(servicioGeolocalizacionMock.buscar("arrieta 3000", "La Matanza"))
      .thenReturn(List.of(SAN_JUSTO));
    when(repositorioComercioMock.guardarComercio(comercio)).thenReturn(comercio);

    servicioComercio.guardarComercio(comercio);

    assertThat(comercio.getLatitud(), equalTo(-34.6879));
    assertThat(comercio.getLongitud(), equalTo(-58.5482));
  }

  @Test
  public void guardarUnComercioConUbicacionConfirmadaNoLaBuscaDeNuevo() {
    Comercio comercio = comercioEn("Carhue 1526", "Buenos Aires");
    comercio.setLatitud(-34.6595);
    comercio.setLongitud(-58.5003);

    servicioComercio.guardarComercio(comercio);

    assertThat(comercio.getLatitud(), equalTo(-34.6595));
    assertThat(comercio.getLongitud(), equalTo(-58.5003));
    assertThat(comercio.getDireccion(), equalTo("Carhue 1526"));
    verifyNoInteractions(servicioGeolocalizacionMock);
    verify(repositorioComercioMock).guardarComercio(comercio);
  }

  @Test
  public void guardarUnComercioConCoordenadasFueraDeArgentinaLasDescartaYLoUbicaPorLaDireccion() {
    Comercio comercio = comercioEn("arrieta 3000", "La Matanza");
    comercio.setLatitud(40.7128);
    comercio.setLongitud(-74.006);
    when(servicioGeolocalizacionMock.buscar("arrieta 3000", "La Matanza"))
      .thenReturn(List.of(SAN_JUSTO));

    servicioComercio.guardarComercio(comercio);

    assertThat(comercio.getLatitud(), equalTo(-34.6879));
    assertThat(comercio.getLongitud(), equalTo(-58.5482));
  }

  @Test
  public void guardarUnComercioConUnaSolaCoordenadaLaDescartaYLoUbicaPorLaDireccion() {
    Comercio comercio = comercioEn("arrieta 3000", "La Matanza");
    comercio.setLatitud(-34.6595);
    when(servicioGeolocalizacionMock.buscar("arrieta 3000", "La Matanza"))
      .thenReturn(List.of(SAN_JUSTO));

    servicioComercio.guardarComercio(comercio);

    assertThat(comercio.getLatitud(), equalTo(-34.6879));
    assertThat(comercio.getLongitud(), equalTo(-58.5482));
  }

  @Test
  public void siLaDireccionNoSeEncuentraElComercioSeGuardaSinUbicacion() {
    Comercio comercio = comercioEn("calle que no existe 1", "La Matanza");
    when(servicioGeolocalizacionMock.buscar(any(), any())).thenReturn(Collections.emptyList());

    servicioComercio.guardarComercio(comercio);

    assertThat(comercio.getLatitud() == null, equalTo(true));
    verify(repositorioComercioMock).guardarComercio(comercio);
  }

  @Test
  public void editarConUbicacionConfirmadaEnElMapaUsaEsasCoordenadas() {
    Comercio existente = comercioEn("Arieta 3000", "La Matanza");
    Comercio datos = comercioEn("Arieta 3000", "La Matanza");

    servicioComercio.actualizarDatosDelComercio(existente, datos, -34.6880, -58.5490);

    assertThat(existente.getLatitud(), equalTo(-34.6880));
    assertThat(existente.getLongitud(), equalTo(-58.5490));
    verifyNoInteractions(servicioGeolocalizacionMock);
    verify(repositorioComercioMock).actualizarComercio(existente);
  }

  @Test
  public void editarLaDireccionSinConfirmarEnElMapaVuelveAUbicarElComercio() {
    Comercio existente = comercioEn("Florencio Varela 1900", "La Matanza");
    existente.setLatitud(-34.67);
    existente.setLongitud(-58.56);
    Comercio datos = comercioEn("arrieta 3000", "La Matanza");
    when(servicioGeolocalizacionMock.buscar("arrieta 3000", "La Matanza"))
      .thenReturn(List.of(SAN_JUSTO));

    servicioComercio.actualizarDatosDelComercio(existente, datos, null, null);

    assertThat(existente.getDireccion(), equalTo("Dr Ignacio Arieta 3000"));
    assertThat(existente.getLatitud(), equalTo(-34.6879));
  }

  @Test
  public void editarOtrosDatosSinTocarLaDireccionNoVuelveAUbicar() {
    Comercio existente = comercioEn("Arieta 3000", "La Matanza");
    existente.setLatitud(-34.6879);
    existente.setLongitud(-58.5482);
    Comercio datos = comercioEn(" arieta 3000 ", "La Matanza");
    datos.setNombre("Nuevo nombre");

    servicioComercio.actualizarDatosDelComercio(existente, datos, null, null);

    assertThat(existente.getNombre(), equalTo("Nuevo nombre"));
    assertThat(existente.getLatitud(), equalTo(-34.6879));
    verifyNoInteractions(servicioGeolocalizacionMock);
  }

  @Test
  public void unComercioSinUbicacionSeIntentaUbicarAlEditar() {
    Comercio existente = comercioEn("arrieta 3000", "La Matanza");
    Comercio datos = comercioEn("arrieta 3000", "La Matanza");
    when(servicioGeolocalizacionMock.buscar("arrieta 3000", "La Matanza"))
      .thenReturn(List.of(SAN_JUSTO));

    servicioComercio.actualizarDatosDelComercio(existente, datos, null, null);

    assertThat(existente.getLatitud(), equalTo(-34.6879));
  }

  @Test
  public void coordenadasFueraDeArgentinaSeIgnoranYSeUbicaPorDireccion() {
    Comercio existente = comercioEn("Arieta 3000", "La Matanza");
    Comercio datos = comercioEn("arrieta 3000", "La Matanza");
    when(servicioGeolocalizacionMock.buscar("arrieta 3000", "La Matanza"))
      .thenReturn(List.of(SAN_JUSTO));

    servicioComercio.actualizarDatosDelComercio(existente, datos, 48.8584, 2.2945);

    assertThat(existente.getLatitud(), equalTo(-34.6879));
  }

  @Test
  public void sugerirUbicacionesDelegaEnLaGeolocalizacion() {
    when(servicioGeolocalizacionMock.buscar("arrieta 3000", "La Matanza"))
      .thenReturn(List.of(SAN_JUSTO));

    assertThat(
      servicioComercio.sugerirUbicaciones("arrieta 3000", "La Matanza"),
      equalTo(List.of(SAN_JUSTO))
    );
  }

  @Test
  public void sugerirUbicacionesSinDireccionDevuelveListaVacia() {
    assertThat(servicioComercio.sugerirUbicaciones("  ", "La Matanza").isEmpty(), equalTo(true));
    verifyNoInteractions(servicioGeolocalizacionMock);
  }

  // ── reintento de ubicación ────────────────────────────────────────────────

  @Test
  public void ubicarSiFaltaBuscaYGuardaLaUbicacionDeUnComercioSinCoordenadas() {
    Comercio comercio = comercioEn("crhue 1526", "Buenos Aires");
    when(servicioGeolocalizacionMock.buscar("crhue 1526", "Buenos Aires"))
      .thenReturn(List.of(SAN_JUSTO));

    servicioComercio.ubicarSiFalta(comercio);

    assertThat(comercio.getLatitud(), equalTo(-34.6879));
    verify(repositorioComercioMock).actualizarComercio(comercio);
  }

  @Test
  public void ubicarSiFaltaNoConsultaNadaSiElComercioYaTieneUbicacion() {
    Comercio comercio = comercioEn("Arieta 3000", "La Matanza");
    comercio.setLatitud(-34.6879);
    comercio.setLongitud(-58.5482);

    servicioComercio.ubicarSiFalta(comercio);

    verifyNoInteractions(servicioGeolocalizacionMock);
    verify(repositorioComercioMock, never()).actualizarComercio(any());
  }

  @Test
  public void ubicarSiFaltaNoGuardaNadaSiTampocoEncuentraLaDireccion() {
    Comercio comercio = comercioEn("calle inexistente 1", "La Matanza");
    when(servicioGeolocalizacionMock.buscar(any(), any())).thenReturn(Collections.emptyList());

    servicioComercio.ubicarSiFalta(comercio);

    verify(repositorioComercioMock, never()).actualizarComercio(any());
  }

  @Test
  public void ubicarSiFaltaConComercioNuloNoHaceNada() {
    servicioComercio.ubicarSiFalta(null);

    verifyNoInteractions(servicioGeolocalizacionMock, repositorioComercioMock);
  }

  // ── dirección corregida ───────────────────────────────────────────────────

  @Test
  public void guardarUnComercioCorrigeLaDireccionConElNombreOficialDeLaCalle() {
    Comercio comercio = comercioEn("arrieta 3000", "La Matanza");
    when(servicioGeolocalizacionMock.buscar("arrieta 3000", "La Matanza"))
      .thenReturn(List.of(SAN_JUSTO));

    servicioComercio.guardarComercio(comercio);

    assertThat(comercio.getDireccion(), equalTo("Dr Ignacio Arieta 3000"));
    verify(repositorioComercioMock).guardarComercio(comercio);
  }

  @Test
  public void siLaDireccionCorregidaYaEstaCargadaSeUsaEseComercioSinDuplicarlo() {
    Comercio yaCargado = comercioEn("Dr Ignacio Arieta 3000", "La Matanza");
    Comercio nuevo = comercioEn("arrieta 3000", "La Matanza");
    when(servicioGeolocalizacionMock.buscar("arrieta 3000", "La Matanza"))
      .thenReturn(List.of(SAN_JUSTO));
    when(
      repositorioComercioMock.buscarComercioPorDireccionYLocalidad(
        "Dr Ignacio Arieta 3000",
        "La Matanza"
      )
    )
      .thenReturn(yaCargado);

    Comercio resultado = servicioComercio.guardarComercio(nuevo);

    assertThat(resultado, equalTo(yaCargado));
    assertThat(yaCargado.getLatitud(), equalTo(-34.6879));
    verify(repositorioComercioMock, never()).guardarComercio(any());
    verify(repositorioComercioMock).actualizarComercio(yaCargado);
  }

  @Test
  public void siNominatimNoDaLaCalleSeparadaSeConservaLaDireccionEscrita() {
    Comercio comercio = comercioEn("Arieta 3000", "La Matanza");
    UbicacionSugerida deNominatim = new UbicacionSugerida(
      "Arieta 3000, San Justo, Partido de La Matanza, Buenos Aires, Argentina",
      null,
      null,
      -34.67,
      -58.56,
      "nominatim"
    );
    when(servicioGeolocalizacionMock.buscar("Arieta 3000", "La Matanza"))
      .thenReturn(List.of(deNominatim));

    servicioComercio.guardarComercio(comercio);

    assertThat(comercio.getDireccion(), equalTo("Arieta 3000"));
    assertThat(comercio.getLatitud(), equalTo(-34.67));
  }

  @Test
  public void guardarUnComercioSinTipoLoDeduceDelNombre() {
    Comercio comercio = comercioEn("Calle 1", "La Matanza");
    comercio.setNombre("Verdulería Don Pepe");
    comercio.setTipo(null);

    servicioComercio.guardarComercio(comercio);

    assertThat(comercio.getTipo(), equalTo("Verduleria"));
  }

  @Test
  public void guardarUnComercioSinTipoNiPistasEnElNombreLoDejaComoOtro() {
    Comercio comercio = comercioEn("Calle 2", "La Matanza");
    comercio.setNombre("Don Pepe");
    comercio.setTipo(null);

    servicioComercio.guardarComercio(comercio);

    assertThat(comercio.getTipo(), equalTo("Otro"));
  }

  @Test
  public void guardarUnComercioConTipoValidoLoRespeta() {
    Comercio comercio = comercioEn("Calle 3", "La Matanza");
    comercio.setNombre("Verdulería Don Pepe");
    comercio.setTipo("Kiosco");

    servicioComercio.guardarComercio(comercio);

    assertThat(comercio.getTipo(), equalTo("Kiosco"));
  }

  @Test
  public void editarLosDatosConUnTipoInvalidoDeduceElTipoDelNombre() {
    Comercio existente = comercioEn("Arieta 3000", "La Matanza");
    Comercio datos = comercioEn("Arieta 3000", "La Matanza");
    datos.setNombre("Carnicería El Gaucho");
    datos.setTipo("");

    servicioComercio.actualizarDatosDelComercio(existente, datos, -34.6880, -58.5490);

    assertThat(existente.getTipo(), equalTo("Carniceria"));
  }

  @Test
  public void completarTiposFaltantesLesPoneTipoSoloALosQueNoLoTienen() {
    Comercio sinTipo = comercioEn("Calle 4", "La Matanza");
    sinTipo.setNombre("Kiosco Sol");
    sinTipo.setTipo(null);
    Comercio conTipo = comercioEn("Calle 5", "La Matanza");
    conTipo.setTipo("Almacen");
    Comercio inventado = comercioEn("Calle 6", "La Matanza");
    inventado.setNombre("Don Pepe");
    inventado.setTipo("xyz");
    when(repositorioComercioMock.listarTodos()).thenReturn(List.of(sinTipo, conTipo, inventado));

    int completados = servicioComercio.completarTiposFaltantes();

    assertThat(completados, equalTo(2));
    assertThat(sinTipo.getTipo(), equalTo("Kiosco"));
    assertThat(conTipo.getTipo(), equalTo("Almacen"));
    assertThat(inventado.getTipo(), equalTo("Otro"));
    verify(repositorioComercioMock).actualizarComercio(sinTipo);
    verify(repositorioComercioMock).actualizarComercio(inventado);
    verify(repositorioComercioMock, never()).actualizarComercio(conTipo);
  }

  // Casos de borde de ServicioComercioImpl: cada uno falla si se altera la regla que protege

  private static final Long COMERCIO_ID = 1L;
  private static final String DIRECCION = "Carhue 1526";
  private static final String LOCALIDAD = "San Justo";
  private static final String TIPO_VALIDO = TipoDeComercio.TIPOS.get(0);

  private RepositorioComercio repositorioComercio;
  private RepositorioHorario repositorioHorario;
  private ServicioGeolocalizacion servicioGeolocalizacion;
  private ServicioComercioImpl servicio;

  @BeforeEach
  public void prepararCasosDeBorde() {
    repositorioComercio = mock(RepositorioComercio.class);
    repositorioHorario = mock(RepositorioHorario.class);
    servicioGeolocalizacion = mock(ServicioGeolocalizacion.class);
    servicio =
      new ServicioComercioImpl(repositorioComercio, repositorioHorario, servicioGeolocalizacion);
  }

  // ---------- coordenadas ----------

  @Test
  public void lasCoordenadasEnLosExtremosDelRectanguloSonValidas() {
    assertThat(ServicioComercioImpl.esCoordenadaValida(-55.1, -73.6), equalTo(true));
    assertThat(ServicioComercioImpl.esCoordenadaValida(-21.7, -53.5), equalTo(true));
  }

  @Test
  public void lasCoordenadasJustoAfueraDelRectanguloSonInvalidas() {
    assertThat(ServicioComercioImpl.esCoordenadaValida(-55.2, -60.0), equalTo(false));
    assertThat(ServicioComercioImpl.esCoordenadaValida(-21.6, -60.0), equalTo(false));
    assertThat(ServicioComercioImpl.esCoordenadaValida(-40.0, -73.7), equalTo(false));
    assertThat(ServicioComercioImpl.esCoordenadaValida(-40.0, -53.4), equalTo(false));
  }

  @Test
  public void unaCoordenadaIncompletaEsInvalida() {
    assertThat(ServicioComercioImpl.esCoordenadaValida(null, -60.0), equalTo(false));
    assertThat(ServicioComercioImpl.esCoordenadaValida(-40.0, null), equalTo(false));
  }

  // ---------- guardar sobre un comercio ya cargado ----------

  @Test
  public void guardarUnComercioQueYaTieneDuenoLoDevuelveSinTocarlo() {
    Comercio existente = comercio(4L, -34.6, TIPO_VALIDO);
    when(repositorioComercio.buscarComercioPorDireccionYLocalidad(DIRECCION, LOCALIDAD))
      .thenReturn(existente);

    Comercio resultado = servicio.guardarComercio(nuevo(9L, null));

    assertThat(resultado, equalTo(existente));
    verify(servicioGeolocalizacion, never()).buscar(anyString(), anyString());
    verify(repositorioComercio, never()).actualizarComercio(any());
    verify(repositorioComercio, never()).guardarComercio(any());
  }

  @Test
  public void completarUnComercioSinCambiosNoLoActualiza() {
    Comercio existente = comercio(4L, -34.6, TIPO_VALIDO);
    existenteEnLaSegundaBusqueda(existente);

    Comercio resultado = servicio.guardarComercio(nuevo(null, null));

    assertThat(resultado, equalTo(existente));
    verify(repositorioComercio, never()).actualizarComercio(any());
  }

  @Test
  public void completarUnComercioSinDuenoLeAsignaElDuenoYLoActualiza() {
    Comercio existente = comercio(null, -34.6, TIPO_VALIDO);
    existenteEnLaSegundaBusqueda(existente);

    servicio.guardarComercio(nuevo(9L, null));

    assertThat(existente.getUsuarioId(), equalTo(9L));
    verify(repositorioComercio, times(1)).actualizarComercio(existente);
  }

  @Test
  public void completarUnComercioConDuenoNoLeCambiaElDueno() {
    Comercio existente = comercio(4L, -34.6, TIPO_VALIDO);
    existenteEnLaSegundaBusqueda(existente);

    servicio.guardarComercio(nuevo(9L, null));

    assertThat(existente.getUsuarioId(), equalTo(4L));
    verify(repositorioComercio, never()).actualizarComercio(any());
  }

  @Test
  public void completarUnComercioSinDuenoConUnaSolicitudSinDuenoNoLoActualiza() {
    Comercio existente = comercio(null, -34.6, TIPO_VALIDO);
    existenteEnLaSegundaBusqueda(existente);

    servicio.guardarComercio(nuevo(null, null));

    assertThat(existente.getUsuarioId(), nullValue());
    verify(repositorioComercio, never()).actualizarComercio(any());
  }

  @Test
  public void completarUnComercioSinUbicacionLeCopiaLaUbicacionNueva() {
    Comercio existente = comercio(4L, null, TIPO_VALIDO);
    existenteEnLaSegundaBusqueda(existente);

    servicio.guardarComercio(nuevo(null, -34.7));

    assertThat(existente.getLatitud(), equalTo(-34.7));
    assertThat(existente.getLongitud(), equalTo(-58.5));
    verify(repositorioComercio, times(1)).actualizarComercio(existente);
  }

  @Test
  public void completarUnComercioConUbicacionNoLaPisa() {
    Comercio existente = comercio(4L, -34.6, TIPO_VALIDO);
    existenteEnLaSegundaBusqueda(existente);

    servicio.guardarComercio(nuevo(null, -34.7));

    assertThat(existente.getLatitud(), equalTo(-34.6));
    verify(repositorioComercio, never()).actualizarComercio(any());
  }

  @Test
  public void completarUnComercioSinTipoValidoLeCompletaElTipo() {
    Comercio existente = comercio(4L, -34.6, null);
    existenteEnLaSegundaBusqueda(existente);

    servicio.guardarComercio(nuevo(null, null));

    assertThat(TipoDeComercio.normalizar(existente.getTipo()) != null, equalTo(true));
    verify(repositorioComercio, times(1)).actualizarComercio(existente);
  }

  // ---------- ubicar si falta ----------

  @Test
  public void ubicarSiFaltaSinDireccionNoBuscaNada() {
    Comercio sinDireccion = new Comercio();
    Comercio direccionEnBlanco = new Comercio();
    direccionEnBlanco.setDireccion("   ");

    servicio.ubicarSiFalta(sinDireccion);
    servicio.ubicarSiFalta(direccionEnBlanco);

    verify(servicioGeolocalizacion, never()).buscar(any(), any());
    verify(repositorioComercio, never()).actualizarComercio(any());
  }

  // ---------- verificación ----------

  @Test
  public void solicitarVerificacionDeUnComercioInexistenteNoHaceNada() {
    when(repositorioComercio.buscarComercio(COMERCIO_ID)).thenReturn(null);

    servicio.solicitarVerificacion(COMERCIO_ID);

    verify(repositorioComercio, never()).actualizarComercio(any());
  }

  @Test
  public void solicitarVerificacionMarcaLaSolicitudYGuarda() {
    Comercio comercio = new Comercio();
    comercio.setVerificado(false);
    comercio.setSolicitudVerificacion(false);
    when(repositorioComercio.buscarComercio(COMERCIO_ID)).thenReturn(comercio);

    servicio.solicitarVerificacion(COMERCIO_ID);

    assertThat(comercio.getSolicitudVerificacion(), equalTo(true));
    verify(repositorioComercio, times(1)).actualizarComercio(comercio);
  }

  @Test
  public void aprobarVerificacionDeUnComercioInexistenteNoHaceNada() {
    when(repositorioComercio.buscarComercio(COMERCIO_ID)).thenReturn(null);

    servicio.aprobarVerificacion(COMERCIO_ID);

    verify(repositorioComercio, never()).actualizarComercio(any());
  }

  @Test
  public void aprobarVerificacionSinSolicitudNoLaAprueba() {
    Comercio comercio = new Comercio();
    comercio.setVerificado(false);
    comercio.setSolicitudVerificacion(false);
    when(repositorioComercio.buscarComercio(COMERCIO_ID)).thenReturn(comercio);

    servicio.aprobarVerificacion(COMERCIO_ID);

    assertThat(comercio.getVerificado(), equalTo(false));
    verify(repositorioComercio, never()).actualizarComercio(any());
  }

  @Test
  public void aprobarVerificacionVerificaElComercioYCierraLaSolicitud() {
    Comercio comercio = new Comercio();
    comercio.setVerificado(false);
    comercio.setSolicitudVerificacion(true);
    when(repositorioComercio.buscarComercio(COMERCIO_ID)).thenReturn(comercio);

    servicio.aprobarVerificacion(COMERCIO_ID);

    assertThat(comercio.getVerificado(), equalTo(true));
    assertThat(comercio.getSolicitudVerificacion(), equalTo(false));
    verify(repositorioComercio, times(1)).actualizarComercio(comercio);
  }

  // ---------- horario: está abierto ahora ----------

  @Test
  public void estaAbiertoDentroDelHorarioDeHoy() {
    LocalTime ahora = ahoraSinRiesgoDeMedianoche();
    horarios(horario(hoy(), true, ahora.minusHours(1), ahora.plusHours(1)));

    assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(true));
  }

  @Test
  public void estaCerradoAntesDeLaApertura() {
    LocalTime ahora = ahoraSinRiesgoDeMedianoche();
    horarios(horario(hoy(), true, ahora.plusHours(1), ahora.plusHours(2)));

    assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(false));
  }

  @Test
  public void estaCerradoDespuesDelCierre() {
    LocalTime ahora = ahoraSinRiesgoDeMedianoche();
    horarios(horario(hoy(), true, ahora.minusHours(2), ahora.minusHours(1)));

    assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(false));
  }

  @Test
  public void estaCerradoSiElHorarioEsDeOtroDia() {
    LocalTime ahora = ahoraSinRiesgoDeMedianoche();
    int otroDia = (hoy() % 7) + 1;
    horarios(horario(otroDia, true, ahora.minusHours(1), ahora.plusHours(1)));

    assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(false));
  }

  @Test
  public void estaCerradoSiHoyNoAbreAunqueLaHoraCoincida() {
    LocalTime ahora = ahoraSinRiesgoDeMedianoche();
    horarios(horario(hoy(), false, ahora.minusHours(1), ahora.plusHours(1)));

    assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(false));
  }

  @Test
  public void estaCerradoSiFaltaLaHoraDeApertura() {
    LocalTime ahora = ahoraSinRiesgoDeMedianoche();
    horarios(horario(hoy(), true, null, ahora.plusHours(1)));

    assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(false));
  }

  @Test
  public void estaCerradoSiFaltaLaHoraDeCierre() {
    LocalTime ahora = ahoraSinRiesgoDeMedianoche();
    horarios(horario(hoy(), true, ahora.minusHours(1), null));

    assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(false));
  }

  @Test
  public void conHorarioNocturnoEstaAbiertoDespuesDeLaApertura() {
    LocalTime ahora = ahoraSinRiesgoDeMedianoche();
    horarios(horario(hoy(), true, ahora.minusHours(1), ahora.minusHours(2)));

    assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(true));
  }

  @Test
  public void conHorarioNocturnoEstaAbiertoAntesDelCierre() {
    LocalTime ahora = ahoraSinRiesgoDeMedianoche();
    horarios(horario(hoy(), true, ahora.plusHours(2), ahora.plusHours(1)));

    assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(true));
  }

  @Test
  public void conHorarioNocturnoEstaCerradoEntreElCierreYLaApertura() {
    LocalTime ahora = ahoraSinRiesgoDeMedianoche();
    horarios(horario(hoy(), true, ahora.plusHours(1), ahora.minusHours(1)));

    assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(false));
  }

  // ---------- armado de datos ----------

  private void existenteEnLaSegundaBusqueda(Comercio existente) {
    when(repositorioComercio.buscarComercioPorDireccionYLocalidad(DIRECCION, LOCALIDAD))
      .thenReturn(null, existente);
  }

  private Comercio comercio(Long usuarioId, Double latitud, String tipo) {
    Comercio comercio = new Comercio();
    comercio.setNombre("Almacén de Don Pepe");
    comercio.setDireccion(DIRECCION);
    comercio.setLocalidad(LOCALIDAD);
    comercio.setUsuarioId(usuarioId);
    comercio.setLatitud(latitud);
    comercio.setLongitud(latitud == null ? null : -58.4);
    comercio.setTipo(tipo);
    return comercio;
  }

  private Comercio nuevo(Long usuarioId, Double latitud) {
    Comercio comercio = comercio(usuarioId, latitud, TIPO_VALIDO);
    if (latitud != null) {
      comercio.setLongitud(-58.5);
    }
    return comercio;
  }

  private HorarioComercio horario(int dia, boolean abierto, LocalTime apertura, LocalTime cierre) {
    HorarioComercio horario = new HorarioComercio();
    horario.setDiaSemana(dia);
    horario.setAbierto(abierto);
    horario.setHoraApertura(apertura);
    horario.setHoraCierre(cierre);
    return horario;
  }

  private void horarios(HorarioComercio... horarios) {
    when(repositorioHorario.buscarPorComercioId(COMERCIO_ID)).thenReturn(List.of(horarios));
  }

  private int hoy() {
    return LocalDate.now().getDayOfWeek().getValue();
  }

  // Los horarios se arman con +/- 2 horas: cerca de la medianoche cruzarían de día
  private LocalTime ahoraSinRiesgoDeMedianoche() {
    LocalTime ahora = LocalTime.now();
    assumeTrue(ahora.isAfter(LocalTime.of(2, 30)) && ahora.isBefore(LocalTime.of(21, 30)));
    return ahora;
  }

  @Nested
  class CasosDeBordeDeComercio {

    private static final Long COMERCIO_ID = 1L;
    private static final String DIRECCION = "Carhue 1526";
    private static final String LOCALIDAD = "San Justo";
    private static final String TIPO_VALIDO = TipoDeComercio.TIPOS.get(0);

    private RepositorioComercio repositorioComercio;
    private RepositorioHorario repositorioHorario;
    private ServicioGeolocalizacion servicioGeolocalizacion;
    private ServicioComercioImpl servicio;

    @BeforeEach
    public void init() {
      repositorioComercio = mock(RepositorioComercio.class);
      repositorioHorario = mock(RepositorioHorario.class);
      servicioGeolocalizacion = mock(ServicioGeolocalizacion.class);
      servicio =
        new ServicioComercioImpl(repositorioComercio, repositorioHorario, servicioGeolocalizacion);
    }

    // ---------- coordenadas ----------

    @Test
    public void lasCoordenadasEnLosExtremosDelRectanguloSonValidas() {
      assertThat(ServicioComercioImpl.esCoordenadaValida(-55.1, -73.6), equalTo(true));
      assertThat(ServicioComercioImpl.esCoordenadaValida(-21.7, -53.5), equalTo(true));
    }

    @Test
    public void lasCoordenadasJustoAfueraDelRectanguloSonInvalidas() {
      assertThat(ServicioComercioImpl.esCoordenadaValida(-55.2, -60.0), equalTo(false));
      assertThat(ServicioComercioImpl.esCoordenadaValida(-21.6, -60.0), equalTo(false));
      assertThat(ServicioComercioImpl.esCoordenadaValida(-40.0, -73.7), equalTo(false));
      assertThat(ServicioComercioImpl.esCoordenadaValida(-40.0, -53.4), equalTo(false));
    }

    @Test
    public void unaCoordenadaIncompletaEsInvalida() {
      assertThat(ServicioComercioImpl.esCoordenadaValida(null, -60.0), equalTo(false));
      assertThat(ServicioComercioImpl.esCoordenadaValida(-40.0, null), equalTo(false));
    }

    // ---------- guardar sobre un comercio ya cargado ----------

    @Test
    public void guardarUnComercioQueYaTieneDuenoLoDevuelveSinTocarlo() {
      Comercio existente = comercio(4L, -34.6, TIPO_VALIDO);
      when(repositorioComercio.buscarComercioPorDireccionYLocalidad(DIRECCION, LOCALIDAD))
        .thenReturn(existente);

      Comercio resultado = servicio.guardarComercio(nuevo(9L, null));

      assertThat(resultado, equalTo(existente));
      verify(servicioGeolocalizacion, never()).buscar(anyString(), anyString());
      verify(repositorioComercio, never()).actualizarComercio(any());
      verify(repositorioComercio, never()).guardarComercio(any());
    }

    @Test
    public void completarUnComercioSinCambiosNoLoActualiza() {
      Comercio existente = comercio(4L, -34.6, TIPO_VALIDO);
      existenteEnLaSegundaBusqueda(existente);

      Comercio resultado = servicio.guardarComercio(nuevo(null, null));

      assertThat(resultado, equalTo(existente));
      verify(repositorioComercio, never()).actualizarComercio(any());
    }

    @Test
    public void completarUnComercioSinDuenoLeAsignaElDuenoYLoActualiza() {
      Comercio existente = comercio(null, -34.6, TIPO_VALIDO);
      existenteEnLaSegundaBusqueda(existente);

      servicio.guardarComercio(nuevo(9L, null));

      assertThat(existente.getUsuarioId(), equalTo(9L));
      verify(repositorioComercio, times(1)).actualizarComercio(existente);
    }

    @Test
    public void completarUnComercioConDuenoNoLeCambiaElDueno() {
      Comercio existente = comercio(4L, -34.6, TIPO_VALIDO);
      existenteEnLaSegundaBusqueda(existente);

      servicio.guardarComercio(nuevo(9L, null));

      assertThat(existente.getUsuarioId(), equalTo(4L));
      verify(repositorioComercio, never()).actualizarComercio(any());
    }

    @Test
    public void completarUnComercioSinDuenoConUnaSolicitudSinDuenoNoLoActualiza() {
      Comercio existente = comercio(null, -34.6, TIPO_VALIDO);
      existenteEnLaSegundaBusqueda(existente);

      servicio.guardarComercio(nuevo(null, null));

      assertThat(existente.getUsuarioId(), nullValue());
      verify(repositorioComercio, never()).actualizarComercio(any());
    }

    @Test
    public void completarUnComercioSinUbicacionLeCopiaLaUbicacionNueva() {
      Comercio existente = comercio(4L, null, TIPO_VALIDO);
      existenteEnLaSegundaBusqueda(existente);

      servicio.guardarComercio(nuevo(null, -34.7));

      assertThat(existente.getLatitud(), equalTo(-34.7));
      assertThat(existente.getLongitud(), equalTo(-58.5));
      verify(repositorioComercio, times(1)).actualizarComercio(existente);
    }

    @Test
    public void completarUnComercioConUbicacionNoLaPisa() {
      Comercio existente = comercio(4L, -34.6, TIPO_VALIDO);
      existenteEnLaSegundaBusqueda(existente);

      servicio.guardarComercio(nuevo(null, -34.7));

      assertThat(existente.getLatitud(), equalTo(-34.6));
      verify(repositorioComercio, never()).actualizarComercio(any());
    }

    @Test
    public void completarUnComercioSinTipoValidoLeCompletaElTipo() {
      Comercio existente = comercio(4L, -34.6, null);
      existenteEnLaSegundaBusqueda(existente);

      servicio.guardarComercio(nuevo(null, null));

      assertThat(TipoDeComercio.normalizar(existente.getTipo()) != null, equalTo(true));
      verify(repositorioComercio, times(1)).actualizarComercio(existente);
    }

    // ---------- ubicar si falta ----------

    @Test
    public void ubicarSiFaltaSinDireccionNoBuscaNada() {
      Comercio sinDireccion = new Comercio();
      Comercio direccionEnBlanco = new Comercio();
      direccionEnBlanco.setDireccion("   ");

      servicio.ubicarSiFalta(sinDireccion);
      servicio.ubicarSiFalta(direccionEnBlanco);

      verify(servicioGeolocalizacion, never()).buscar(any(), any());
      verify(repositorioComercio, never()).actualizarComercio(any());
    }

    // ---------- verificación ----------

    @Test
    public void solicitarVerificacionDeUnComercioInexistenteNoHaceNada() {
      when(repositorioComercio.buscarComercio(COMERCIO_ID)).thenReturn(null);

      servicio.solicitarVerificacion(COMERCIO_ID);

      verify(repositorioComercio, never()).actualizarComercio(any());
    }

    @Test
    public void solicitarVerificacionMarcaLaSolicitudYGuarda() {
      Comercio comercio = new Comercio();
      comercio.setVerificado(false);
      comercio.setSolicitudVerificacion(false);
      when(repositorioComercio.buscarComercio(COMERCIO_ID)).thenReturn(comercio);

      servicio.solicitarVerificacion(COMERCIO_ID);

      assertThat(comercio.getSolicitudVerificacion(), equalTo(true));
      verify(repositorioComercio, times(1)).actualizarComercio(comercio);
    }

    @Test
    public void aprobarVerificacionDeUnComercioInexistenteNoHaceNada() {
      when(repositorioComercio.buscarComercio(COMERCIO_ID)).thenReturn(null);

      servicio.aprobarVerificacion(COMERCIO_ID);

      verify(repositorioComercio, never()).actualizarComercio(any());
    }

    @Test
    public void aprobarVerificacionSinSolicitudNoLaAprueba() {
      Comercio comercio = new Comercio();
      comercio.setVerificado(false);
      comercio.setSolicitudVerificacion(false);
      when(repositorioComercio.buscarComercio(COMERCIO_ID)).thenReturn(comercio);

      servicio.aprobarVerificacion(COMERCIO_ID);

      assertThat(comercio.getVerificado(), equalTo(false));
      verify(repositorioComercio, never()).actualizarComercio(any());
    }

    @Test
    public void aprobarVerificacionVerificaElComercioYCierraLaSolicitud() {
      Comercio comercio = new Comercio();
      comercio.setVerificado(false);
      comercio.setSolicitudVerificacion(true);
      when(repositorioComercio.buscarComercio(COMERCIO_ID)).thenReturn(comercio);

      servicio.aprobarVerificacion(COMERCIO_ID);

      assertThat(comercio.getVerificado(), equalTo(true));
      assertThat(comercio.getSolicitudVerificacion(), equalTo(false));
      verify(repositorioComercio, times(1)).actualizarComercio(comercio);
    }

    // ---------- horario: está abierto ahora ----------

    @Test
    public void estaAbiertoDentroDelHorarioDeHoy() {
      LocalTime ahora = ahoraSinRiesgoDeMedianoche();
      horarios(horario(hoy(), true, ahora.minusHours(1), ahora.plusHours(1)));

      assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(true));
    }

    @Test
    public void estaCerradoAntesDeLaApertura() {
      LocalTime ahora = ahoraSinRiesgoDeMedianoche();
      horarios(horario(hoy(), true, ahora.plusHours(1), ahora.plusHours(2)));

      assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(false));
    }

    @Test
    public void estaCerradoDespuesDelCierre() {
      LocalTime ahora = ahoraSinRiesgoDeMedianoche();
      horarios(horario(hoy(), true, ahora.minusHours(2), ahora.minusHours(1)));

      assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(false));
    }

    @Test
    public void estaCerradoSiElHorarioEsDeOtroDia() {
      LocalTime ahora = ahoraSinRiesgoDeMedianoche();
      int otroDia = (hoy() % 7) + 1;
      horarios(horario(otroDia, true, ahora.minusHours(1), ahora.plusHours(1)));

      assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(false));
    }

    @Test
    public void estaCerradoSiHoyNoAbreAunqueLaHoraCoincida() {
      LocalTime ahora = ahoraSinRiesgoDeMedianoche();
      horarios(horario(hoy(), false, ahora.minusHours(1), ahora.plusHours(1)));

      assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(false));
    }

    @Test
    public void estaCerradoSiFaltaLaHoraDeApertura() {
      LocalTime ahora = ahoraSinRiesgoDeMedianoche();
      horarios(horario(hoy(), true, null, ahora.plusHours(1)));

      assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(false));
    }

    @Test
    public void estaCerradoSiFaltaLaHoraDeCierre() {
      LocalTime ahora = ahoraSinRiesgoDeMedianoche();
      horarios(horario(hoy(), true, ahora.minusHours(1), null));

      assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(false));
    }

    @Test
    public void conHorarioNocturnoEstaAbiertoDespuesDeLaApertura() {
      LocalTime ahora = ahoraSinRiesgoDeMedianoche();
      horarios(horario(hoy(), true, ahora.minusHours(1), ahora.minusHours(2)));

      assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(true));
    }

    @Test
    public void conHorarioNocturnoEstaAbiertoAntesDelCierre() {
      LocalTime ahora = ahoraSinRiesgoDeMedianoche();
      horarios(horario(hoy(), true, ahora.plusHours(2), ahora.plusHours(1)));

      assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(true));
    }

    @Test
    public void conHorarioNocturnoEstaCerradoEntreElCierreYLaApertura() {
      LocalTime ahora = ahoraSinRiesgoDeMedianoche();
      horarios(horario(hoy(), true, ahora.plusHours(1), ahora.minusHours(1)));

      assertThat(servicio.estaAbierto(COMERCIO_ID), equalTo(false));
    }

    // ---------- armado de datos ----------

    private void existenteEnLaSegundaBusqueda(Comercio existente) {
      when(repositorioComercio.buscarComercioPorDireccionYLocalidad(DIRECCION, LOCALIDAD))
        .thenReturn(null, existente);
    }

    private Comercio comercio(Long usuarioId, Double latitud, String tipo) {
      Comercio comercio = new Comercio();
      comercio.setNombre("Almacén de Don Pepe");
      comercio.setDireccion(DIRECCION);
      comercio.setLocalidad(LOCALIDAD);
      comercio.setUsuarioId(usuarioId);
      comercio.setLatitud(latitud);
      comercio.setLongitud(latitud == null ? null : -58.4);
      comercio.setTipo(tipo);
      return comercio;
    }

    private Comercio nuevo(Long usuarioId, Double latitud) {
      Comercio comercio = comercio(usuarioId, latitud, TIPO_VALIDO);
      if (latitud != null) {
        comercio.setLongitud(-58.5);
      }
      return comercio;
    }

    private HorarioComercio horario(
      int dia,
      boolean abierto,
      LocalTime apertura,
      LocalTime cierre
    ) {
      HorarioComercio horario = new HorarioComercio();
      horario.setDiaSemana(dia);
      horario.setAbierto(abierto);
      horario.setHoraApertura(apertura);
      horario.setHoraCierre(cierre);
      return horario;
    }

    private void horarios(HorarioComercio... horarios) {
      when(repositorioHorario.buscarPorComercioId(COMERCIO_ID)).thenReturn(List.of(horarios));
    }

    private int hoy() {
      return LocalDate.now().getDayOfWeek().getValue();
    }

    // Los horarios se arman con +/- 2 horas: cerca de la medianoche cruzarían de día
    private LocalTime ahoraSinRiesgoDeMedianoche() {
      LocalTime ahora = LocalTime.now();
      assumeTrue(ahora.isAfter(LocalTime.of(2, 30)) && ahora.isBefore(LocalTime.of(21, 30)));
      return ahora;
    }
  }

  @Nested
  class ComercioEnElMismoLugar {

    private static final String LOCALIDAD = "Buenos Aires";

    private UbicacionSugerida lugar(String direccion, double latitud, double longitud) {
      return new UbicacionSugerida(direccion, direccion, LOCALIDAD, latitud, longitud, "georef");
    }

    private Comercio comercioEn(String direccion, double latitud, double longitud, Long dueno) {
      Comercio comercio = new Comercio();
      comercio.setDireccion(direccion);
      comercio.setLocalidad(LOCALIDAD);
      comercio.setLatitud(latitud);
      comercio.setLongitud(longitud);
      comercio.setUsuarioId(dueno);
      return comercio;
    }

    @Test
    public void conElMismoTextoDeDireccionDevuelveEseComercioSinConsultarElMapa() {
      Comercio cargado = comercioEn("Carhue 1526", -34.65, -58.51, null);
      when(repositorioComercioMock.buscarComercioPorDireccionYLocalidad("Carhue 1526", LOCALIDAD))
        .thenReturn(cargado);

      Comercio resultado = servicioComercio.buscarEnElMismoLugar("Carhue 1526", LOCALIDAD);

      assertThat(resultado, equalTo(cargado));
      verify(servicioGeolocalizacionMock, never()).buscar(anyString(), anyString());
    }

    @Test
    public void conOtraGrafiaDeLaCalleLoEncuentraPorLaDireccionOficial() {
      Comercio cargado = comercioEn("Avenida de los corrales 7535", -34.66, -58.50, null);
      when(servicioGeolocalizacionMock.buscar("Av de los corrales 7535", LOCALIDAD))
        .thenReturn(List.of(lugar("Avenida de los corrales 7535", -34.66, -58.50)));
      when(
        repositorioComercioMock.buscarComercioPorDireccionYLocalidad(
          "Avenida de los corrales 7535",
          LOCALIDAD
        )
      )
        .thenReturn(cargado);

      Comercio resultado = servicioComercio.buscarEnElMismoLugar(
        "Av de los corrales 7535",
        LOCALIDAD
      );

      assertThat(resultado, equalTo(cargado));
    }

    @Test
    public void conUnComercioCargadoEnElMismoPuntoDelMapaDevuelveEse() {
      Comercio cargado = comercioEn("Av de los corrales 7535", -34.6678494, -58.5064855, null);
      when(servicioGeolocalizacionMock.buscar("Avenida de los corrales 7535", LOCALIDAD))
        .thenReturn(List.of(lugar("Avenida de los corrales 7535", -34.6678494, -58.5064855)));
      when(repositorioComercioMock.listarTodos()).thenReturn(List.of(cargado));

      Comercio resultado = servicioComercio.buscarEnElMismoLugar(
        "Avenida de los corrales 7535",
        LOCALIDAD
      );

      assertThat(resultado, equalTo(cargado));
    }

    @Test
    public void unComercioAUnos110MetrosEsOtroLocal() {
      Comercio otro = comercioEn("Rivadavia 1334", -34.6010, -58.5000, null);
      when(servicioGeolocalizacionMock.buscar("Rivadavia 1234", LOCALIDAD))
        .thenReturn(List.of(lugar("Rivadavia 1234", -34.6000, -58.5000)));
      when(repositorioComercioMock.listarTodos()).thenReturn(List.of(otro));

      assertThat(servicioComercio.buscarEnElMismoLugar("Rivadavia 1234", LOCALIDAD), nullValue());
    }

    @Test
    public void unComercioDeOtraLocalidadEnElMismoPuntoNoCuenta() {
      Comercio deOtraLocalidad = comercioEn("Rivadavia 1234", -34.6000, -58.5000, null);
      deOtraLocalidad.setLocalidad("La Matanza");
      when(servicioGeolocalizacionMock.buscar("Rivadavia 1234", LOCALIDAD))
        .thenReturn(List.of(lugar("Rivadavia 1234", -34.6000, -58.5000)));
      when(repositorioComercioMock.listarTodos()).thenReturn(List.of(deOtraLocalidad));

      assertThat(servicioComercio.buscarEnElMismoLugar("Rivadavia 1234", LOCALIDAD), nullValue());
    }

    @Test
    public void sinResultadosDelMapaYSinTextoCoincidenteDevuelveNull() {
      when(servicioGeolocalizacionMock.buscar("Calle inventada 1", LOCALIDAD))
        .thenReturn(Collections.emptyList());

      assertThat(
        servicioComercio.buscarEnElMismoLugar("Calle inventada 1", LOCALIDAD),
        nullValue()
      );
    }

    @Test
    public void conDireccionVaciaODatosNulosDevuelveNullSinConsultar() {
      assertThat(servicioComercio.buscarEnElMismoLugar(" ", LOCALIDAD), nullValue());
      assertThat(servicioComercio.buscarEnElMismoLugar(null, LOCALIDAD), nullValue());
      assertThat(servicioComercio.buscarEnElMismoLugar("Carhue 1526", null), nullValue());
      verifyNoInteractions(repositorioComercioMock, servicioGeolocalizacionMock);
    }

    @Test
    public void alGuardarUnComercioEnUnLugarYaCargadoConOtraGrafiaNoCreaOtro() {
      Comercio cargado = comercioEn("Av de los corrales 7535", -34.6678494, -58.5064855, null);
      when(servicioGeolocalizacionMock.buscar("Avenida de los corrales 7535", LOCALIDAD))
        .thenReturn(List.of(lugar("Avenida de los corrales 7535", -34.6678494, -58.5064855)));
      when(repositorioComercioMock.listarTodos()).thenReturn(List.of(cargado));
      Comercio nuevo = new Comercio();
      nuevo.setNombre("Dia");
      nuevo.setDireccion("Avenida de los corrales 7535");
      nuevo.setLocalidad(LOCALIDAD);
      nuevo.setUsuarioId(5L);

      Comercio resultado = servicioComercio.guardarComercio(nuevo);

      assertThat(resultado, equalTo(cargado));
      assertThat(cargado.getUsuarioId(), equalTo(5L));
      verify(repositorioComercioMock, never()).guardarComercio(any());
      verify(repositorioComercioMock).actualizarComercio(cargado);
    }

    @Test
    public void alGuardarUnComercioEnElLugarDeOtroConDuenoDevuelveElQueYaTieneDueno() {
      Comercio conDueno = comercioEn("Av de los corrales 7535", -34.6678494, -58.5064855, 3L);
      when(servicioGeolocalizacionMock.buscar("Avenida de los corrales 7535", LOCALIDAD))
        .thenReturn(List.of(lugar("Avenida de los corrales 7535", -34.6678494, -58.5064855)));
      when(repositorioComercioMock.listarTodos()).thenReturn(List.of(conDueno));
      Comercio nuevo = new Comercio();
      nuevo.setNombre("Dia");
      nuevo.setDireccion("Avenida de los corrales 7535");
      nuevo.setLocalidad(LOCALIDAD);
      nuevo.setUsuarioId(9L);

      Comercio resultado = servicioComercio.guardarComercio(nuevo);

      assertThat(resultado, equalTo(conDueno));
      assertThat(conDueno.getUsuarioId(), equalTo(3L));
      verify(repositorioComercioMock, never()).guardarComercio(any());
    }

    @Test
    public void unaDireccionConEspaciosDeMasEsLaMismaDireccion() {
      Comercio cargado = comercioEn("Av  de los corrales 7535", -34.6644774, -58.5025962, 5L);
      when(repositorioComercioMock.listarTodos()).thenReturn(List.of(cargado));

      Comercio resultado = servicioComercio.buscarEnElMismoLugar(
        "Av de los corrales 7535",
        LOCALIDAD
      );

      assertThat(resultado, equalTo(cargado));
      verify(servicioGeolocalizacionMock, never()).buscar(anyString(), anyString());
    }

    @Test
    public void mayusculasTildesYPuntosNoCambianLaDireccion() {
      Comercio cargado = comercioEn("Avenida de los Córrales 7535", -34.66, -58.50, null);
      when(repositorioComercioMock.listarTodos()).thenReturn(List.of(cargado));

      assertThat(
        servicioComercio.buscarEnElMismoLugar("AV. DE LOS CORRALES, 7535", LOCALIDAD),
        equalTo(cargado)
      );
    }

    @Test
    public void otroNumeroDeLaMismaCalleEsOtraDireccion() {
      Comercio cargado = comercioEn("Av de los corrales 7535", -34.66, -58.50, null);
      when(repositorioComercioMock.listarTodos()).thenReturn(List.of(cargado));

      assertThat(
        servicioComercio.buscarEnElMismoLugar("Av de los corrales 7536", LOCALIDAD),
        nullValue()
      );
    }

    @Test
    public void laMismaDireccionEnOtraLocalidadEsOtroComercio() {
      Comercio cargado = comercioEn("Av de los corrales 7535", -34.66, -58.50, null);
      cargado.setLocalidad("La Matanza");
      when(repositorioComercioMock.listarTodos()).thenReturn(List.of(cargado));

      assertThat(
        servicioComercio.buscarEnElMismoLugar("Av de los corrales 7535", LOCALIDAD),
        nullValue()
      );
    }

    @Test
    public void unComercioDadoDeBajaNoCuentaComoElMismoLugar() {
      Comercio dadoDeBaja = comercioEn("Av de los corrales 7535", -34.66, -58.50, null);
      dadoDeBaja.setActivo(false);
      when(repositorioComercioMock.listarTodos()).thenReturn(List.of(dadoDeBaja));

      assertThat(
        servicioComercio.buscarEnElMismoLugar("Av de los corrales 7535", LOCALIDAD),
        nullValue()
      );
    }

    @Test
    public void siHayDosConLaMismaDireccionPrefiereElQueTieneDueno() {
      Comercio sinDueno = comercioEn("Av de los corrales 7535", -34.66, -58.50, null);
      Comercio conDueno = comercioEn("Av  de los corrales 7535", -34.66, -58.50, 5L);
      when(repositorioComercioMock.listarTodos()).thenReturn(List.of(sinDueno, conDueno));

      assertThat(
        servicioComercio.buscarEnElMismoLugar("AV DE LOS CORRALES 7535", LOCALIDAD),
        equalTo(conDueno)
      );
    }

    @Test
    public void guardarUnComercioConLaMismaDireccionEscritaDistintoNoLoDuplicaAunqueElMapaLoUbiqueLejos() {
      Comercio conDueno = comercioEn("Av  de los corrales 7535", -34.6644774, -58.5025962, 5L);
      when(repositorioComercioMock.listarTodos()).thenReturn(List.of(conDueno));
      Comercio nuevo = new Comercio();
      nuevo.setNombre("Dia");
      nuevo.setDireccion("Av de los corrales 7535");
      nuevo.setLocalidad(LOCALIDAD);
      nuevo.setLatitud(-34.6638589);
      nuevo.setLongitud(-58.5016684);

      Comercio resultado = servicioComercio.guardarComercio(nuevo);

      assertThat(resultado, equalTo(conDueno));
      verify(repositorioComercioMock, never()).guardarComercio(any());
    }

    @Test
    public void direccionNormalizadaUnificaEscrituras() {
      assertThat(
        BuscadorDeComercioEnElMismoLugar.direccionNormalizada("  Av.  de los Córrales  7535 "),
        equalTo("avenida de los corrales 7535")
      );
      assertThat(
        BuscadorDeComercioEnElMismoLugar.direccionNormalizada("Avda de los corrales 7535"),
        equalTo("avenida de los corrales 7535")
      );
      assertThat(BuscadorDeComercioEnElMismoLugar.direccionNormalizada(null), equalTo(""));
    }
  }
}
