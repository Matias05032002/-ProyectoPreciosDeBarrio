package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioUsuario {
  Usuario buscarUsuario(String email, String password);
  void guardar(Usuario usuario);
  Usuario buscar(String email);
  void modificar(Usuario usuario);
  Usuario buscarPorId(Long id);
  Long contarTodos();
  List<Object[]> usuariosMasActivos(int limite);
  List<Object[]> distribucionPorRangoEtario();
}
