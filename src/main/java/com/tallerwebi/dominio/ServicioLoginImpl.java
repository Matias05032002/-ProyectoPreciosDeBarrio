package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.RolInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service("servicioLogin")
@Transactional
public class ServicioLoginImpl implements ServicioLogin {

  private static final String ROL_COMERCIO = "COMERCIO";
  private static final String ROL_USUARIO = "USER";

  private RepositorioUsuario repositorioUsuario;
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  public ServicioLoginImpl(RepositorioUsuario repositorioUsuario) {
    this.repositorioUsuario = repositorioUsuario;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  @Override
  public Usuario consultarUsuario(String email, String password) {
    Usuario usuario = repositorioUsuario.buscar(email);
    if (usuario == null || !usuario.getActivo()) return null;
    if (!passwordEncoder.matches(password, usuario.getPassword())) return null;
    return usuario;
  }

  @Override
  public void registrar(Usuario usuario)
    throws UsuarioExistente, ContrasenaInvalida, EmailInvalido {
    if (usuario.getRol() == null || usuario.getRol().isBlank()) {
      throw new RolInvalido();
    }
    validarContrasena(usuario.getPassword());
    validarEmail(usuario.getEmail());
    Usuario usuarioEncontrado = repositorioUsuario.buscar(usuario.getEmail());
    if (usuarioEncontrado != null) {
      throw new UsuarioExistente();
    }
    if (ROL_COMERCIO.equals(usuario.getRol())) {
      usuario.setRol(ROL_COMERCIO);
    } else {
      usuario.setRol(ROL_USUARIO);
    }
    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
    usuario.activar();
    repositorioUsuario.guardar(usuario);
  }

  @Override
  public Usuario buscarPorId(Long id) {
    return repositorioUsuario.buscarPorId(id);
  }

  @Override
  public Usuario buscarPorEmail(String email) {
    return repositorioUsuario.buscar(email);
  }

  @Override
  public boolean actualizarEmail(Long usuarioId, String nuevoEmail) {
    if (nuevoEmail == null || !nuevoEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
      return false;
    }

    Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
    if (usuario == null) {
      return false;
    }

    Usuario existente = repositorioUsuario.buscar(nuevoEmail.trim());
    if (existente != null && !existente.getId().equals(usuarioId)) {
      return false;
    }

    usuario.setEmail(nuevoEmail.trim());
    repositorioUsuario.modificar(usuario);
    return true;
  }

  @Override
  public boolean cambiarContrasena(
    Long usuarioId,
    String contrasenaActual,
    String contrasenaNueva
  ) {
    Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
    if (
      usuario == null ||
      contrasenaActual == null ||
      !passwordEncoder.matches(contrasenaActual, usuario.getPassword())
    ) {
      return false;
    }

    try {
      validarContrasena(contrasenaNueva);
    } catch (ContrasenaInvalida e) {
      return false;
    }

    usuario.setPassword(passwordEncoder.encode(contrasenaNueva));
    repositorioUsuario.modificar(usuario);
    return true;
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
