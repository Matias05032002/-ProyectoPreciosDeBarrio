package com.tallerwebi.dominio.Comercio;

import com.tallerwebi.dominio.Imagen.TipoDeImagen;
import com.tallerwebi.dominio.excepcion.FotoInvalida;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ServicioComercio")
@Transactional
public class ServicioComercioImpl implements ServicioComercio {

  private static final int MAX_FOTO_BYTES = 5 * 1024 * 1024;

  // Rectángulo que contiene a la Argentina continental: una coordenada afuera es un error
  private static final double LATITUD_MINIMA = -55.1;
  private static final double LATITUD_MAXIMA = -21.7;
  private static final double LONGITUD_MINIMA = -73.6;
  private static final double LONGITUD_MAXIMA = -53.5;

  private RepositorioComercio repositorioComercio;
  private final RepositorioHorario repositorioHorario;
  private final ServicioGeolocalizacion servicioGeolocalizacion;

  @Autowired
  public ServicioComercioImpl(
    RepositorioComercio repositorioComercio,
    RepositorioHorario repositorioHorario,
    ServicioGeolocalizacion servicioGeolocalizacion
  ) {
    this.repositorioComercio = repositorioComercio;
    this.repositorioHorario = repositorioHorario;
    this.servicioGeolocalizacion = servicioGeolocalizacion;
  }

  @Override
  public Comercio guardarComercio(Comercio comercio) {
    Comercio existente = repositorioComercio.buscarComercioPorDireccionYLocalidad(
      comercio.getDireccion(),
      comercio.getLocalidad()
    );
    if (existente != null && existente.getUsuarioId() != null) {
      return existente;
    }
    // Si el vecino ya confirmó el lugar en el mapa se respeta; si no, se busca por la dirección
    if (!esCoordenadaValida(comercio.getLatitud(), comercio.getLongitud())) {
      ubicarSegunLaDireccion(comercio);
    }
    // Con la dirección corregida ("crhue 1526" -> "Carhue 1526") puede aparecer un comercio
    // que ya estaba cargado: se usa ese en lugar de crear uno repetido
    Comercio mismaDireccion = repositorioComercio.buscarComercioPorDireccionYLocalidad(
      comercio.getDireccion(),
      comercio.getLocalidad()
    );
    if (mismaDireccion != null) {
      return completarExistente(mismaDireccion, comercio);
    }
    comercio.setTipo(TipoDeComercio.paraGuardar(comercio.getTipo(), comercio.getNombre()));
    return repositorioComercio.guardarComercio(comercio);
  }

  private Comercio completarExistente(Comercio existente, Comercio nuevo) {
    boolean cambio = false;
    if (existente.getUsuarioId() == null && nuevo.getUsuarioId() != null) {
      existente.setUsuarioId(nuevo.getUsuarioId());
      cambio = true;
    }
    if (existente.getLatitud() == null && nuevo.getLatitud() != null) {
      existente.setLatitud(nuevo.getLatitud());
      existente.setLongitud(nuevo.getLongitud());
      cambio = true;
    }
    if (TipoDeComercio.normalizar(existente.getTipo()) == null) {
      existente.setTipo(TipoDeComercio.paraGuardar(nuevo.getTipo(), existente.getNombre()));
      cambio = true;
    }
    if (cambio) {
      repositorioComercio.actualizarComercio(existente);
    }
    return existente;
  }

  @Override
  public Comercio vincularComercioAUsuario(Comercio comercio, Long usuarioId) {
    // Cada cuenta de comercio administra un solo local: no puede reclamar otro
    Comercio propio = repositorioComercio.buscarComercioPorUsuarioId(usuarioId);
    if (propio != null) {
      return propio;
    }

    Comercio existente = repositorioComercio.buscarComercioPorNombreDireccionYLocalidad(
      comercio.getNombre(),
      comercio.getDireccion(),
      comercio.getLocalidad()
    );

    if (existente != null) {
      if (existente.getUsuarioId() != null) {
        return existente;
      }
      existente.setUsuarioId(usuarioId);
      repositorioComercio.actualizarComercio(existente);
      return existente;
    }

    comercio.setUsuarioId(usuarioId);
    return guardarComercio(comercio);
  }

  @Override
  public List<Comercio> listarTodos() {
    // Los comercios de cuentas eliminadas se desactivan y dejan de verse en la app
    return repositorioComercio
            .listarTodos()
            .stream()
            .filter(comercio -> !Boolean.FALSE.equals(comercio.getActivo()))
            .toList();
  }

  @Override
  public Comercio buscarComercio(Long id) {
    return repositorioComercio.buscarComercio(id);
  }

  @Override
  public List<Comercio> buscarComercioPorNombre(String nombre) {
    return repositorioComercio.buscarComercioPorNombre(nombre);
  }

  @Override
  public Comercio buscarComercioPorUsuarioId(Long usuarioId) {
    return repositorioComercio.buscarComercioPorUsuarioId(usuarioId);
  }

  @Override
  public void actualizarComercio(Comercio comercio) {
    repositorioComercio.actualizarComercio(comercio);
  }

  @Override
  public int completarTiposFaltantes() {
    int completados = 0;
    for (Comercio comercio : repositorioComercio.listarTodos()) {
      if (TipoDeComercio.normalizar(comercio.getTipo()) == null) {
        comercio.setTipo(TipoDeComercio.inferirDe(comercio.getNombre()));
        repositorioComercio.actualizarComercio(comercio);
        completados++;
      }
    }
    return completados;
  }

  @Override
  public void solicitarVerificacion(Long comercioId) {
    Comercio comercio = repositorioComercio.buscarComercio(comercioId);

    if (comercio == null) {
      return;
    }

    if (Boolean.TRUE.equals(comercio.getVerificado())) {
      return;
    }

    if (Boolean.TRUE.equals(comercio.getSolicitudVerificacion())) {
      return;
    }

    comercio.setSolicitudVerificacion(true);
    repositorioComercio.actualizarComercio(comercio);
  }

  @Override
  public void actualizarDatosDelComercio(
    Comercio existente,
    Comercio datos,
    Double latitudConfirmada,
    Double longitudConfirmada
  ) {
    boolean cambioLaDireccion =
      !mismoTexto(existente.getDireccion(), datos.getDireccion()) ||
      !mismoTexto(existente.getLocalidad(), datos.getLocalidad());

    existente.setNombre(datos.getNombre());
    existente.setTipo(TipoDeComercio.paraGuardar(datos.getTipo(), datos.getNombre()));
    existente.setDireccion(datos.getDireccion());
    existente.setLocalidad(datos.getLocalidad());
    existente.setDescripcion(datos.getDescripcion());

    if (esCoordenadaValida(latitudConfirmada, longitudConfirmada)) {
      existente.setLatitud(latitudConfirmada);
      existente.setLongitud(longitudConfirmada);
    } else if (cambioLaDireccion || existente.getLatitud() == null) {
      ubicarSegunLaDireccion(existente);
    }
    repositorioComercio.actualizarComercio(existente);
  }

  @Override
  public List<UbicacionSugerida> sugerirUbicaciones(String direccion, String localidad) {
    if (direccion == null || direccion.isBlank()) return Collections.emptyList();
    return servicioGeolocalizacion.buscar(direccion, localidad);
  }

  @Override
  public void ubicarSiFalta(Comercio comercio) {
    if (comercio == null || comercio.getLatitud() != null) return;
    if (comercio.getDireccion() == null || comercio.getDireccion().isBlank()) return;
    ubicarSegunLaDireccion(comercio);
    if (comercio.getLatitud() != null) {
      repositorioComercio.actualizarComercio(comercio);
    }
  }

  // Se queda con la opción más probable; sin opciones, el comercio queda sin ubicación
  // y el perfil le pide que lo marque en el mapa
  private void ubicarSegunLaDireccion(Comercio comercio) {
    List<UbicacionSugerida> sugerencias = servicioGeolocalizacion.buscar(
      comercio.getDireccion(),
      comercio.getLocalidad()
    );
    if (sugerencias.isEmpty()) {
      comercio.setLatitud(null);
      comercio.setLongitud(null);
    } else {
      UbicacionSugerida elegida = sugerencias.get(0);
      comercio.setLatitud(elegida.latitud());
      comercio.setLongitud(elegida.longitud());
      // Se guarda la calle con su nombre oficial: corrige errores de tipeo como "Crhue"
      if (elegida.direccion() != null && !elegida.direccion().isBlank()) {
        comercio.setDireccion(elegida.direccion());
      }
    }
  }

  static boolean esCoordenadaValida(Double latitud, Double longitud) {
    return (
      latitud != null &&
      longitud != null &&
      latitud >= LATITUD_MINIMA &&
      latitud <= LATITUD_MAXIMA &&
      longitud >= LONGITUD_MINIMA &&
      longitud <= LONGITUD_MAXIMA
    );
  }

  private static boolean mismoTexto(String uno, String otro) {
    return Objects.equals(normalizar(uno), normalizar(otro));
  }

  private static String normalizar(String texto) {
    return texto == null ? null : texto.trim().toLowerCase(Locale.ROOT);
  }

  @Override
  public Comercio buscarPorDireccionYLocalidad(String direccion, String localidad) {
    return repositorioComercio.buscarComercioPorDireccionYLocalidad(direccion, localidad);
  }

  @Override
  public void guardarHorarios(Long comercioId, List<HorarioComercio> horarios) {
    repositorioHorario.eliminarPorComercioId(comercioId);
    for (HorarioComercio horario : horarios) {
      horario.setComercioId(comercioId);
    }
    repositorioHorario.guardarHorarios(horarios);
  }

  @Override
  public List<HorarioComercio> obtenerHorarios(Long comercioId) {
    return repositorioHorario.buscarPorComercioId(comercioId);
  }

  @Override
  public Boolean estaAbierto(Long comercioId) {
    List<HorarioComercio> horarios = repositorioHorario.buscarPorComercioId(comercioId);

    // Sin horarios cargados no se sabe si está abierto: la vista muestra "Sin horario cargado"
    if (horarios.isEmpty()) {
      return null;
    }

    int diaHoy = java.time.LocalDate.now().getDayOfWeek().getValue();
    java.time.LocalTime ahora = java.time.LocalTime.now();

    for (HorarioComercio horario : horarios) {
      if (
        horario.getDiaSemana().equals(diaHoy) &&
        Boolean.TRUE.equals(horario.getAbierto()) &&
        horario.getHoraApertura() != null &&
        horario.getHoraCierre() != null
      ) {
        if (horario.getHoraCierre().isBefore(horario.getHoraApertura())) {
          return (
            !ahora.isBefore(horario.getHoraApertura()) || !ahora.isAfter(horario.getHoraCierre())
          );
        }
        return (
          !ahora.isBefore(horario.getHoraApertura()) && !ahora.isAfter(horario.getHoraCierre())
        );
      }
    }
    return false;
  }

  // El tipo se saca del contenido del archivo, no del que declara el navegador
  @Override
  public void guardarFoto(Long comercioId, byte[] foto) throws FotoInvalida {
    String tipo = TipoDeImagen.detectar(foto);
    if (tipo == null || foto.length > MAX_FOTO_BYTES) {
      throw new FotoInvalida();
    }
    Comercio comercio = repositorioComercio.buscarComercio(comercioId);
    if (comercio == null) return;
    comercio.setFoto(foto);
    comercio.setFotoTipo(tipo);
    repositorioComercio.actualizarComercio(comercio);
  }

  @Override
  public List<Comercio> listarSolicitudesVerificacion() {
    return repositorioComercio.listarSolicitudesVerificacion();
  }

  @Override
  public void aprobarVerificacion(Long comercioId) {
    Comercio comercio = repositorioComercio.buscarComercio(comercioId);

    if (comercio == null) {
      return;
    }

    if (!Boolean.TRUE.equals(comercio.getSolicitudVerificacion())) {
      return;
    }

    comercio.setVerificado(true);
    comercio.setSolicitudVerificacion(false);

    repositorioComercio.actualizarComercio(comercio);
  }
}
