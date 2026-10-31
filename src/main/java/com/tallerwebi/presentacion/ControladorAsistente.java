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

  public ControladorAsistente(ServicioAsistente servicioAsistente) {
    this.servicioAsistente = servicioAsistente;
  }

  @PostMapping("/consultar")
  public ResponseEntity<?> consultar(@RequestBody Map<String, Object> body, HttpSession session) {
    try {
      String pregunta = (String) body.get("pregunta");
      Double latitud = body.get("latitud") != null
        ? ((Number) body.get("latitud")).doubleValue()
        : null;
      Double longitud = body.get("longitud") != null
        ? ((Number) body.get("longitud")).doubleValue()
        : null;

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
      return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
    }
  }
}
