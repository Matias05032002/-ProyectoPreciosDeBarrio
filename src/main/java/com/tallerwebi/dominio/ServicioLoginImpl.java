package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioLogin")
@Transactional
public class ServicioLoginImpl implements ServicioLogin {

  private RepositorioUsuario repositorioUsuario;

  @Autowired
  public ServicioLoginImpl(RepositorioUsuario repositorioUsuario) {
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public Usuario consultarUsuario(String email, String password) {
    return repositorioUsuario.buscarUsuario(email, password);
  }

  @Override
  public void registrar(Usuario usuario)
    throws UsuarioExistente, ContrasenaInvalida, EmailInvalido {
    validarContrasena(usuario.getPassword());
    validarEmail(usuario.getEmail());
    Usuario usuarioEncontrado = repositorioUsuario.buscar(usuario.getEmail());
    if (usuarioEncontrado != null) {
      throw new UsuarioExistente();
    }
    usuario.setRol("USER");
    repositorioUsuario.guardar(usuario);
  }

  @Override
  public Usuario buscarPorEmail(String email) {
    return repositorioUsuario.buscar(email);
  }

  private void validarEmail(String email) throws EmailInvalido {
    if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
      throw new EmailInvalido("El email ingresado no es válido");
    }
  }

  private void validarContrasena(String contrasena) throws ContrasenaInvalida {
    if (contrasena == null || contrasena.length() < 8) {
      throw new ContrasenaInvalida("La contraseña debe tener al menos 8 caracteres");
    }
    if (!contrasena.matches(".*[A-Z].*")) {
      throw new ContrasenaInvalida("La contraseña debe contener al menos una mayúscula");
    }
  }
}
