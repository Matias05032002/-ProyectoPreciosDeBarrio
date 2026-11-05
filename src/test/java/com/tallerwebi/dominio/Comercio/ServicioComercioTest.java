package com.tallerwebi.dominio.Comercio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.FotoInvalida;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
}
