package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.DatosInvalidos;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.RolInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service("servicioLogin")
@Transactional
public class ServicioLoginImpl implements ServicioLogin {

  private static final String ROL_COMERCIO = "COMERCIO";
  private static final String ROL_USUARIO = "USER";
  private static final int LARGO_MAXIMO_NOMBRE = 100;
  private static final LocalDate FECHA_NACIMIENTO_MINIMA = LocalDate.of(1900, 1, 1);

  private RepositorioUsuario repositorioUsuario;
  private BCryptPasswordEncoder passwordEncoder;
  private static final int MAX_INTENTOS = 5;
  private static final long BLOQUEO_SEGUNDOS = 300; // 5 minutos
  private final Map<String, Integer> intentosFallidos = new ConcurrentHashMap<>();
  private final Map<String, Instant> bloqueadoHasta = new ConcurrentHashMap<>();

  @Autowired
  public ServicioLoginImpl(RepositorioUsuario repositorioUsuario) {
    this.repositorioUsuario = repositorioUsuario;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  @Override
  public Usuario consultarUsuario(String email, String password) {
    if (email == null) {
      return null;
    }
    String emailNorm = email.trim().toLowerCase(java.util.Locale.ROOT);
    if (estaBloqueado(emailNorm)) {
      return null; // bloqueado temporalmente
    }
    Usuario usuario = repositorioUsuario.buscar(emailNorm);
    if (usuario == null || !Boolean.TRUE.equals(usuario.getActivo())) {
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

  @Override
  public boolean estaBloqueado(String email) {
    if (email == null) {
      return false;
    }
    Instant bloqueo = bloqueadoHasta.get(email.trim().toLowerCase(java.util.Locale.ROOT));
    return bloqueo != null && Instant.now().isBefore(bloqueo);
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
    String emailNormalizado = usuario.getEmail() == null
      ? null
      : usuario.getEmail().trim().toLowerCase(java.util.Locale.ROOT);
    usuario.setEmail(emailNormalizado);
    validarEmail(emailNormalizado);
    Usuario usuarioEncontrado = repositorioUsuario.buscar(usuario.getEmail());
    if (usuarioEncontrado != null) {
      throw new UsuarioExistente();
    }
    boolean esComercio = ROL_COMERCIO.equals(usuario.getRol());
    validarDatosDeRegistro(usuario, esComercio);
    usuario.setNombre(usuario.getNombre().trim());
    if (esComercio) {
      usuario.setRol(ROL_COMERCIO);
    } else {
      usuario.setApellido(usuario.getApellido().trim());
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

    String emailNorm = nuevoEmail.trim().toLowerCase(java.util.Locale.ROOT);
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

  @Override
  public void restablecerContrasena(Long usuarioId, String contrasenaNueva)
    throws ContrasenaInvalida {
    Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
    if (usuario == null) {
      throw new UsuarioNoEncontrado();
    }
    validarContrasena(contrasenaNueva);

    usuario.setPassword(passwordEncoder.encode(contrasenaNueva));
    repositorioUsuario.modificar(usuario);

    // Quien recuperó su cuenta no tiene por qué seguir bloqueado por los intentos de antes
    if (usuario.getEmail() != null) {
      String emailNorm = usuario.getEmail().trim().toLowerCase(java.util.Locale.ROOT);
      intentosFallidos.remove(emailNorm);
      bloqueadoHasta.remove(emailNorm);
    }
  }

  // El comercio se registra con el nombre del local: solo el vecino necesita apellido y localidad
  private void validarDatosDeRegistro(Usuario usuario, boolean esComercio) {
    validarNombreOApellido(usuario.getNombre(), "El nombre");
    if (esComercio) return;
    validarNombreOApellido(usuario.getApellido(), "El apellido");
    validarFechaDeNacimiento(usuario.getFechaNacimiento());
    if (!Localidades.esValida(usuario.getLocalidad())) {
      throw new DatosInvalidos("Elegí una localidad de la lista");
    }
  }

  private void validarNombreOApellido(String valor, String campo) {
    if (valor == null || valor.isBlank()) {
      throw new DatosInvalidos(campo + " es obligatorio");
    }
    if (valor.trim().length() > LARGO_MAXIMO_NOMBRE) {
      throw new DatosInvalidos(
        campo + " no puede superar los " + LARGO_MAXIMO_NOMBRE + " caracteres"
      );
    }
  }

  private void validarFechaDeNacimiento(LocalDate fecha) {
    if (fecha == null) {
      throw new DatosInvalidos("La fecha de nacimiento es obligatoria");
    }
    if (fecha.isAfter(LocalDate.now())) {
      throw new DatosInvalidos("La fecha de nacimiento no puede ser futura");
    }
    if (fecha.isBefore(FECHA_NACIMIENTO_MINIMA)) {
      throw new DatosInvalidos("La fecha de nacimiento no es válida");
    }
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
