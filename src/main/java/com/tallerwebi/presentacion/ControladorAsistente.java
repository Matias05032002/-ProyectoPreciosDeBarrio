package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Asistente.ServicioAsistente;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/asistente")
public class ControladorAsistente {

  private final ServicioAsistente servicioAsistente;

  public ControladorAsistente(ServicioAsistente servicioAsistente) {
    this.servicioAsistente = servicioAsistente;
  }

  @PostMapping("/consultar")
  public ResponseEntity<?> consultar(@RequestBody Map<String, Object> body) {
    try {
      String pregunta = (String) body.get("pregunta");
      Double latitud = body.get("latitud") != null
        ? ((Number) body.get("latitud")).doubleValue()
        : null;
      Double longitud = body.get("longitud") != null
        ? ((Number) body.get("longitud")).doubleValue()
        : null;

      String respuesta = servicioAsistente.consultar(pregunta, latitud, longitud);
      return ResponseEntity.ok(Map.of("respuesta", respuesta));
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
    }
  }
}
