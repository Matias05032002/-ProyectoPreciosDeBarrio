package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Asistente.ServicioAsistente;
import com.tallerwebi.dominio.Asistente.ServicioLimiteConsultas;
import com.tallerwebi.dominio.excepcion.LimiteDeConsultasExcedido;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/asistente")
public class ControladorAsistente {

  private final ServicioAsistente servicioAsistente;
  private final ServicioLimiteConsultas servicioLimiteConsultas;
  private static final int MAX_HISTORIAL = 20;
  private static final int MAX_PREGUNTA = 300;
  private static final double LATITUD_MAXIMA = 90.0;
  private static final double LONGITUD_MAXIMA = 180.0;
  private static final String CLAVE_ERROR = "error";
  private static final String ATRIBUTO_USUARIO_ID = "ID";
  private static final String ATRIBUTO_HISTORIAL = "donPesoHistorial";
  private static final String MENSAJE_PREGUNTA_INVALIDA =
    "Escribí una pregunta de hasta 300 caracteres.";
  private static final String MENSAJE_ERROR_GENERICO =
    "Don Peso no pudo responder en este momento. Probá de nuevo en un rato.";

  public ControladorAsistente(
    ServicioAsistente servicioAsistente,
    ServicioLimiteConsultas servicioLimiteConsultas
  ) {
    this.servicioAsistente = servicioAsistente;
    this.servicioLimiteConsultas = servicioLimiteConsultas;
  }

  @PostMapping("/consultar")
  public ResponseEntity<?> consultar(@RequestBody Map<String, Object> body, HttpSession session) {
    String pregunta = leerPregunta(body.get("pregunta"));
    if (pregunta.isEmpty() || pregunta.length() > MAX_PREGUNTA) {
      return ResponseEntity.badRequest().body(Map.of(CLAVE_ERROR, MENSAJE_PREGUNTA_INVALIDA));
    }
    try {
      servicioLimiteConsultas.registrarConsulta(claveDelLimite(session));
    } catch (LimiteDeConsultasExcedido e) {
      return ResponseEntity
        .status(HttpStatus.TOO_MANY_REQUESTS)
        .body(Map.of(CLAVE_ERROR, e.getMessage()));
    }
    Double latitud = leerCoordenada(body.get("latitud"), LATITUD_MAXIMA);
    Double longitud = leerCoordenada(body.get("longitud"), LONGITUD_MAXIMA);

    try {
      @SuppressWarnings("unchecked")
      List<Map<String, String>> historial = (List<Map<String, String>>) session.getAttribute(
        ATRIBUTO_HISTORIAL
      );
      if (historial == null) {
        historial = new ArrayList<>();
      }

      String respuesta = servicioAsistente.consultar(
        pregunta,
        latitud,
        longitud,
        leerUsuarioId(session),
        historial
      );

      historial.add(Map.of("role", "user", "content", pregunta));
      historial.add(Map.of("role", "assistant", "content", respuesta));

      if (historial.size() > MAX_HISTORIAL) {
        historial =
          new ArrayList<>(historial.subList(historial.size() - MAX_HISTORIAL, historial.size()));
      }
      session.setAttribute(ATRIBUTO_HISTORIAL, historial);

      return ResponseEntity.ok(Map.of("respuesta", respuesta));
    } catch (Exception e) {
      // El mensaje de la excepción puede traer datos internos (API, SQL): no se le muestra al usuario
      return ResponseEntity.internalServerError().body(Map.of(CLAVE_ERROR, MENSAJE_ERROR_GENERICO));
    }
  }

  // La conversación vive en la sesión: el widget la pide al abrirse para seguir donde quedó
  @GetMapping("/historial")
  public ResponseEntity<List<Map<String, String>>> historial(HttpSession session) {
    @SuppressWarnings("unchecked")
    List<Map<String, String>> historial = (List<Map<String, String>>) session.getAttribute(
      ATRIBUTO_HISTORIAL
    );
    return ResponseEntity.ok(historial == null ? new ArrayList<>() : historial);
  }

  @PostMapping("/reiniciar")
  public ResponseEntity<Void> reiniciar(HttpSession session) {
    session.removeAttribute(ATRIBUTO_HISTORIAL);
    return ResponseEntity.noContent().build();
  }

  // Si no llega un texto, devuelve vacío para que se rechace como pregunta inválida
  private String leerPregunta(Object valor) {
    return valor instanceof String texto ? texto.trim() : "";
  }

  // Con sesión iniciada el cupo es del usuario, así no se esquiva abriendo otra sesión
  private String claveDelLimite(HttpSession session) {
    Long usuarioId = leerUsuarioId(session);
    return usuarioId != null ? "usuario-" + usuarioId : "sesion-" + session.getId();
  }

  // Sin sesión iniciada no hay listas del vecino para consultar
  private Long leerUsuarioId(HttpSession session) {
    return session.getAttribute(ATRIBUTO_USUARIO_ID) instanceof Long id ? id : null;
  }

  // Una coordenada que no es un número válido dentro del rango se ignora (sin ubicación)
  private Double leerCoordenada(Object valor, double maximo) {
    if (!(valor instanceof Number numero)) {
      return null;
    }
    double coordenada = numero.doubleValue();
    return Math.abs(coordenada) <= maximo ? coordenada : null;
  }
}
