package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.RolInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service("servicioLogin")
@Transactional
public class ServicioLoginImpl implements ServicioLogin {

  private static final String ROL_COMERCIO = "COMERCIO";
  private static final String ROL_USUARIO = "USER";

  private RepositorioUsuario repositorioUsuario;
  private BCryptPasswordEncoder passwordEncoder;
  private static final int MAX_INTENTOS = 5;
  private static final long BLOQUEO_SEGUNDOS = 300; // 5 minutos
  private final ConcurrentHashMap<String, Integer> intentosFallidos = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Instant> bloqueadoHasta = new ConcurrentHashMap<>();

  @Autowired
  public ServicioLoginImpl(RepositorioUsuario repositorioUsuario) {
    this.repositorioUsuario = repositorioUsuario;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  @Override
  public Usuario consultarUsuario(String email, String password) {
    String emailNorm = (email == null) ? null : email.trim().toLowerCase();
    // Verificar bloqueo por IP/email
    Instant bloqueo = bloqueadoHasta.get(emailNorm);
    if (bloqueo != null && Instant.now().isBefore(bloqueo)) {
      return null; // bloqueado temporalmente
    }
    Usuario usuario = repositorioUsuario.buscar(emailNorm);
    if (usuario == null || !usuario.getActivo()) {
      registrarIntentoFallido(emailNorm);
      return null;
    }
    if (!passwordEncoder.matches(password, usuario.getPassword())) {
      registrarIntentoFallido(emailNorm);
      return null;
    }
    // Login exitoso: limpiar contadores
    intentosFallidos.remove(emailNorm);
    bloqueadoHasta.remove(emailNorm);
    return usuario;
  }

  private void registrarIntentoFallido(String emailNorm) {
    int intentos = intentosFallidos.merge(emailNorm, 1, Integer::sum);
    if (intentos >= MAX_INTENTOS) {
      bloqueadoHasta.put(emailNorm, Instant.now().plusSeconds(BLOQUEO_SEGUNDOS));
      intentosFallidos.remove(emailNorm);
    }
  }

  @Override
  public void registrar(Usuario usuario)
    throws UsuarioExistente, ContrasenaInvalida, EmailInvalido {
    if (usuario.getRol() == null || usuario.getRol().isBlank()) {
      throw new RolInvalido();
    }
    validarContrasena(usuario.getPassword());
    String emailNormalizado = usuario.getEmail() == null ? null : usuario.getEmail().trim().toLowerCase();
    usuario.setEmail(emailNormalizado);
    validarEmail(emailNormalizado);
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

    String emailNorm = nuevoEmail.trim().toLowerCase();
    Usuario existente = repositorioUsuario.buscar(emailNorm);
    if (existente != null && !existente.getId().equals(usuarioId)) {
      return false;
    }
    usuario.setEmail(emailNorm);
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

  @Override
  public void actualizarDatosPersonales(
    Long usuarioId,
    String nombre,
    String apellido,
    java.time.LocalDate fechaNacimiento
  ) {
    Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
    if (usuario == null) return;

    if (nombre != null) usuario.setNombre(nombre.trim());
    if (apellido != null) usuario.setApellido(apellido.trim());
    usuario.setFechaNacimiento(fechaNacimiento);

    repositorioUsuario.modificar(usuario);
  }
}
