package com.tallerwebi.dominio.Asistente;

import com.tallerwebi.dominio.excepcion.LimiteDeConsultasExcedido;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;
import org.springframework.stereotype.Service;

// Ventana deslizante en memoria: cuida la cuota de la API de IA de que alguien dispare
// consultas en bucle. Guarda solo los instantes de las consultas aceptadas.
@Service
public class ServicioLimiteConsultasImpl implements ServicioLimiteConsultas {

  static final int MAXIMO_POR_VENTANA = 20;
  static final long VENTANA_EN_MS = 3_600_000L;
  private static final int CLAVES_PARA_LIMPIAR = 5_000;

  private final Map<String, Deque<Long>> consultasPorClave = new ConcurrentHashMap<>();
  private final int maximo;
  private final long ventanaEnMs;
  private final LongSupplier reloj;

  public ServicioLimiteConsultasImpl() {
    this(MAXIMO_POR_VENTANA, VENTANA_EN_MS, System::currentTimeMillis);
  }

  ServicioLimiteConsultasImpl(int maximo, long ventanaEnMs, LongSupplier reloj) {
    this.maximo = maximo;
    this.ventanaEnMs = ventanaEnMs;
    this.reloj = reloj;
  }

  @Override
  public void registrarConsulta(String clave) throws LimiteDeConsultasExcedido {
    long ahora = reloj.getAsLong();
    if (consultasPorClave.size() > CLAVES_PARA_LIMPIAR) {
      olvidarVencidas(ahora);
    }
    Deque<Long> instantes = consultasPorClave.computeIfAbsent(clave, k -> new ArrayDeque<>());
    synchronized (instantes) {
      while (!instantes.isEmpty() && ahora - instantes.peekFirst() >= ventanaEnMs) {
        instantes.pollFirst();
      }
      if (instantes.size() >= maximo) {
        throw new LimiteDeConsultasExcedido();
      }
      instantes.addLast(ahora);
    }
  }

  // Evita que el mapa crezca sin fin con visitantes que no vuelven
  private void olvidarVencidas(long ahora) {
    consultasPorClave
      .entrySet()
      .removeIf(entrada -> {
        synchronized (entrada.getValue()) {
          Long ultimo = entrada.getValue().peekLast();
          return ultimo == null || ahora - ultimo >= ventanaEnMs;
        }
      });
  }
}
