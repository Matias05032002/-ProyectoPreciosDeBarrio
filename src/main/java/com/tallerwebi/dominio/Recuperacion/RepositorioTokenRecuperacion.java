package com.tallerwebi.dominio.Recuperacion;

import java.time.LocalDateTime;
import java.util.List;

public interface RepositorioTokenRecuperacion {
  void guardar(TokenRecuperacion token);

  TokenRecuperacion buscarPorHash(String tokenHash);

  /** Tokens sin usar y sin vencer de un usuario, del más nuevo al más viejo. */
  List<TokenRecuperacion> buscarVigentesDelUsuario(Long usuarioId, LocalDateTime ahora);

  void actualizar(TokenRecuperacion token);
}
