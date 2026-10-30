package com.tallerwebi.dominio.Comercio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ServicioComercio")
@Transactional
public class ServicioComercioImpl implements ServicioComercio {

  private RepositorioComercio repositorioComercio;
  private final RepositorioHorario repositorioHorario;
  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  public ServicioComercioImpl(
    RepositorioComercio repositorioComercio,
    RepositorioHorario repositorioHorario
  ) {
    this.repositorioComercio = repositorioComercio;
    this.repositorioHorario = repositorioHorario;
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
    try {
      String direccionCompleta =
        comercio.getDireccion() + ", " + comercio.getLocalidad() + ", Buenos Aires, Argentina";
      String url =
        "https://nominatim.openstreetmap.org/search?format=json&limit=1&q=" +
        java.net.URLEncoder.encode(direccionCompleta, "UTF-8");

      java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url)
        .openConnection();
      conn.setRequestProperty("User-Agent", "TallerWebi/1.0");

      String response;
      try (
        java.util.Scanner scanner = new java.util.Scanner(
          conn.getInputStream(),
          java.nio.charset.StandardCharsets.UTF_8
        )
      ) {
        response = scanner.useDelimiter("\\A").next();
      }

      JsonNode results = objectMapper.readTree(response);
      if (results.isArray() && results.size() > 0) {
        JsonNode primero = results.get(0);
        comercio.setLatitud(primero.get("lat").asDouble());
        comercio.setLongitud(primero.get("lon").asDouble());
      } else {
        comercio.setLatitud(null);
        comercio.setLongitud(null);
      }
    } catch (Exception e) {
      comercio.setLatitud(null);
      comercio.setLongitud(null);
    }
    return repositorioComercio.guardarComercio(comercio);
  }

  @Override
  public Comercio vincularComercioAUsuario(Comercio comercio, Long usuarioId) {
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
    return repositorioComercio.listarTodos();
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

    if (horarios.isEmpty()) {
      return false;
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

  @Override
  public void guardarFoto(Long comercioId, byte[] foto, String fotoTipo) {
    Comercio comercio = repositorioComercio.buscarComercio(comercioId);
    if (comercio == null) return;
    comercio.setFoto(foto);
    comercio.setFotoTipo(fotoTipo);
    repositorioComercio.actualizarComercio(comercio);
  }
}
