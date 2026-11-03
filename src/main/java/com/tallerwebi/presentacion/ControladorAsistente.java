package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Asistente.ServicioAsistente;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/asistente")
public class ControladorAsistente {

  private final ServicioAsistente servicioAsistente;
  private static final int MAX_HISTORIAL = 20;
  private static final int MAX_PREGUNTA = 300;
  private static final double LATITUD_MAXIMA = 90.0;
  private static final double LONGITUD_MAXIMA = 180.0;
  private static final String CLAVE_ERROR = "error";
  private static final String MENSAJE_PREGUNTA_INVALIDA =
    "Escribí una pregunta de hasta 300 caracteres.";
  private static final String MENSAJE_ERROR_GENERICO =
    "Don Peso no pudo responder en este momento. Probá de nuevo en un rato.";

  public ControladorAsistente(ServicioAsistente servicioAsistente) {
    this.servicioAsistente = servicioAsistente;
  }

  @PostMapping("/consultar")
  public ResponseEntity<?> consultar(@RequestBody Map<String, Object> body, HttpSession session) {
    String pregunta = leerPregunta(body.get("pregunta"));
    if (pregunta.isEmpty() || pregunta.length() > MAX_PREGUNTA) {
      return ResponseEntity.badRequest().body(Map.of(CLAVE_ERROR, MENSAJE_PREGUNTA_INVALIDA));
    }
    Double latitud = leerCoordenada(body.get("latitud"), LATITUD_MAXIMA);
    Double longitud = leerCoordenada(body.get("longitud"), LONGITUD_MAXIMA);

    try {
      @SuppressWarnings("unchecked")
      List<Map<String, String>> historial = (List<Map<String, String>>) session.getAttribute(
        "donPesoHistorial"
      );
      if (historial == null) {
        historial = new ArrayList<>();
      }

      String respuesta = servicioAsistente.consultar(pregunta, latitud, longitud, historial);

      historial.add(Map.of("role", "user", "content", pregunta));
      historial.add(Map.of("role", "assistant", "content", respuesta));

      if (historial.size() > MAX_HISTORIAL) {
        historial =
          new ArrayList<>(historial.subList(historial.size() - MAX_HISTORIAL, historial.size()));
      }
      session.setAttribute("donPesoHistorial", historial);

      return ResponseEntity.ok(Map.of("respuesta", respuesta));
    } catch (Exception e) {
      // El mensaje de la excepción puede traer datos internos (API, SQL): no se le muestra al usuario
      return ResponseEntity.internalServerError().body(Map.of(CLAVE_ERROR, MENSAJE_ERROR_GENERICO));
    }
  }

  // Si no llega un texto, devuelve vacío para que se rechace como pregunta inválida
  private String leerPregunta(Object valor) {
    return valor instanceof String texto ? texto.trim() : "";
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
