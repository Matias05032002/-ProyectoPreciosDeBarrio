package com.tallerwebi.dominio.Comercio;

import com.tallerwebi.dominio.Localidades;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.ComercioYaRegistrado;
import com.tallerwebi.dominio.excepcion.ContrasenaInvalida;
import com.tallerwebi.dominio.excepcion.DatosDeComercioInvalidos;
import com.tallerwebi.dominio.excepcion.EmailInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ServicioRegistroComercio")
@Transactional
public class ServicioRegistroComercioImpl implements ServicioRegistroComercio {

  private static final String ROL_COMERCIO = "COMERCIO";
  private static final int LARGO_MAXIMO_NOMBRE = 150;
  private static final int LARGO_MAXIMO_DIRECCION = 200;
  private static final int LARGO_MAXIMO_TIPO = 50;

  private final ServicioLogin servicioLogin;
  private final ServicioComercio servicioComercio;

  @Autowired
  public ServicioRegistroComercioImpl(
    ServicioLogin servicioLogin,
    ServicioComercio servicioComercio
  ) {
    this.servicioLogin = servicioLogin;
    this.servicioComercio = servicioComercio;
  }

  @Override
  public void registrar(
    String email,
    String password,
    String nombre,
    String tipo,
    String direccion,
    String localidad
  )
    throws DatosDeComercioInvalidos, ComercioYaRegistrado, UsuarioExistente, EmailInvalido, ContrasenaInvalida {
    // Todo se valida antes de crear el usuario: si no, quedaría una cuenta sin comercio
    validarDatos(nombre, tipo, direccion, localidad);
    String nombreLimpio = nombre.trim();
    String tipoLimpio = TipoDeComercio.normalizar(tipo);
    String direccionLimpia = direccion.trim();
    String localidadLimpia = localidad.trim();

    Comercio existente = servicioComercio.buscarEnElMismoLugar(direccionLimpia, localidadLimpia);
    if (existente != null && existente.getUsuarioId() != null) {
      throw new ComercioYaRegistrado();
    }

    Usuario usuario = new Usuario();
    usuario.setEmail(email);
    usuario.setPassword(password);
    usuario.setNombre(nombreLimpio);
    usuario.setRol(ROL_COMERCIO);
    servicioLogin.registrar(usuario);

    Usuario usuarioCreado = servicioLogin.buscarPorEmail(email);
    if (usuarioCreado == null) {
      return;
    }
    if (existente != null) {
      existente.setUsuarioId(usuarioCreado.getId());
      existente.setTipo(tipoLimpio);
      servicioComercio.ubicarSiFalta(existente);
      servicioComercio.actualizarComercio(existente);
    } else {
      servicioComercio.guardarComercio(
        nuevoComercio(nombreLimpio, tipoLimpio, direccionLimpia, localidadLimpia, usuarioCreado)
      );
    }
  }

  private Comercio nuevoComercio(
    String nombre,
    String tipo,
    String direccion,
    String localidad,
    Usuario dueno
  ) {
    Comercio comercio = new Comercio();
    comercio.setNombre(nombre);
    comercio.setTipo(tipo);
    comercio.setDireccion(direccion);
    comercio.setLocalidad(localidad);
    comercio.setActivo(true);
    comercio.setFechaRegistro(LocalDateTime.now());
    comercio.setUsuarioId(dueno.getId());
    return comercio;
  }

  private void validarDatos(String nombre, String tipo, String direccion, String localidad)
    throws DatosDeComercioInvalidos {
    validarQueEstenCompletos(nombre, tipo, direccion);
    validarLargos(nombre, tipo, direccion);
    if (TipoDeComercio.normalizar(tipo) == null) {
      throw new DatosDeComercioInvalidos("Elegí un tipo de comercio de la lista");
    }
    if (!Localidades.esValida(localidad)) {
      throw new DatosDeComercioInvalidos("Elegí una localidad de la lista");
    }
  }

  private void validarQueEstenCompletos(String nombre, String tipo, String direccion)
    throws DatosDeComercioInvalidos {
    if (estaVacio(nombre) || estaVacio(direccion) || estaVacio(tipo)) {
      throw new DatosDeComercioInvalidos("Completá el nombre, el tipo y la dirección del comercio");
    }
  }

  private void validarLargos(String nombre, String tipo, String direccion)
    throws DatosDeComercioInvalidos {
    if (
      superaElLargo(nombre, LARGO_MAXIMO_NOMBRE) ||
      superaElLargo(direccion, LARGO_MAXIMO_DIRECCION) ||
      superaElLargo(tipo, LARGO_MAXIMO_TIPO)
    ) {
      throw new DatosDeComercioInvalidos(
        "El nombre, el tipo o la dirección del comercio son demasiado largos"
      );
    }
  }

  private boolean superaElLargo(String valor, int maximo) {
    return valor.trim().length() > maximo;
  }

  private boolean estaVacio(String valor) {
    return valor == null || valor.isBlank();
  }
}
