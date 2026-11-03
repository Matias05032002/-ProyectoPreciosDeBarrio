package com.tallerwebi.dominio.Consulta;

import com.tallerwebi.dominio.excepcion.DatosInvalidos;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioConsulta")
@Transactional
public class ServicioConsultaImpl implements ServicioConsulta {

  private static final int LARGO_MAXIMO_NOMBRE = 100;
  private static final int LARGO_MAXIMO_EMAIL = 150;
  private static final int LARGO_MAXIMO_MENSAJE = 2000;
  private static final String FORMATO_EMAIL = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
  // Tienen que coincidir con las opciones del select de feedback.html
  private static final Set<String> ASUNTOS_VALIDOS = Set.of(
    "Sugerencia",
    "Consulta",
    "Problema",
    "Otro"
  );

  private final RepositorioConsulta repositorioConsulta;

  @Autowired
  public ServicioConsultaImpl(RepositorioConsulta repositorioConsulta) {
    this.repositorioConsulta = repositorioConsulta;
  }

  @Override
  public void guardar(Consulta consulta) {
    validar(consulta);
    consulta.setNombre(consulta.getNombre().trim());
    consulta.setEmail(consulta.getEmail().trim());
    consulta.setMensaje(consulta.getMensaje().trim());
    consulta.setFecha(LocalDateTime.now());
    consulta.setEstado("Pendiente");
    repositorioConsulta.guardar(consulta);
  }

  @Override
  public List<Consulta> listarTodas() {
    return repositorioConsulta.listarTodas();
  }

  private void validar(Consulta consulta) {
    validarTexto(consulta.getNombre(), "El nombre", LARGO_MAXIMO_NOMBRE);
    validarTexto(consulta.getEmail(), "El correo electrónico", LARGO_MAXIMO_EMAIL);
    if (!consulta.getEmail().trim().matches(FORMATO_EMAIL)) {
      throw new DatosInvalidos("El correo electrónico no es válido");
    }
    if (consulta.getAsunto() == null || !ASUNTOS_VALIDOS.contains(consulta.getAsunto())) {
      throw new DatosInvalidos("Elegí un asunto de la lista");
    }
    validarTexto(consulta.getMensaje(), "El mensaje", LARGO_MAXIMO_MENSAJE);
  }

  private void validarTexto(String valor, String campo, int largoMaximo) {
    if (valor == null || valor.isBlank()) {
      throw new DatosInvalidos(campo + " es obligatorio");
    }
    if (valor.trim().length() > largoMaximo) {
      throw new DatosInvalidos(campo + " no puede superar los " + largoMaximo + " caracteres");
    }
  }
}
