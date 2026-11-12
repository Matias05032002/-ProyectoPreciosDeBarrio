package com.tallerwebi.dominio.Cuenta;

import com.tallerwebi.dominio.Comercio.Comercio;
import com.tallerwebi.dominio.Comercio.Promocion;
import com.tallerwebi.dominio.Comercio.RepositorioComercio;
import com.tallerwebi.dominio.Comercio.RepositorioPromocion;
import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioEliminacionDeCuentaImpl implements ServicioEliminacionDeCuenta {

    static final String NOMBRE_ELIMINADO = "Usuario eliminado";
    private static final String ROL_ADMIN = "ADMIN";
    private static final String ROL_COMERCIO = "COMERCIO";

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioComercio repositorioComercio;
    private final RepositorioPromocion repositorioPromocion;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public ServicioEliminacionDeCuentaImpl(
            RepositorioUsuario repositorioUsuario,
            RepositorioComercio repositorioComercio,
            RepositorioPromocion repositorioPromocion
    ) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioComercio = repositorioComercio;
        this.repositorioPromocion = repositorioPromocion;
    }

    @Override
    public boolean eliminarCuenta(Long usuarioId, String contrasena) {
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        if (usuario == null || !Boolean.TRUE.equals(usuario.getActivo())) {
            return false;
        }
        if (ROL_ADMIN.equals(usuario.getRol())) {
            throw new AccesoDenegado("La cuenta del administrador no se puede eliminar.");
        }
        if (contrasena == null || !passwordEncoder.matches(contrasena, usuario.getPassword())) {
            return false;
        }
        if (ROL_COMERCIO.equals(usuario.getRol())) {
            desactivarComercioDe(usuarioId);
        }
        anonimizar(usuario);
        repositorioUsuario.modificar(usuario);
        return true;
    }

    private void desactivarComercioDe(Long usuarioId) {
        Comercio comercio = repositorioComercio.buscarComercioPorUsuarioId(usuarioId);
        if (comercio == null) {
            return;
        }
        comercio.setActivo(false);
        repositorioComercio.actualizarComercio(comercio);
        for (Promocion promocion : repositorioPromocion.listarPorComercio(comercio.getId())) {
            promocion.setActiva(false);
            repositorioPromocion.actualizar(promocion);
        }
    }

    // La fila se conserva porque los reportes de precios dependen de ella; solo se vacía de datos
    private void anonimizar(Usuario usuario) {
        usuario.setActivo(false);
        usuario.setVerificado(false);
        usuario.setNombre(NOMBRE_ELIMINADO);
        usuario.setApellido(null);
        usuario.setFechaNacimiento(null);
        usuario.setLocalidad(null);
        // El email queda libre para que la persona pueda volver a registrarse
        usuario.setEmail("cuenta-eliminada-" + usuario.getId() + "@eliminada.invalid");
        // Una contraseña al azar que nadie conoce: la cuenta no vuelve a abrirse
        usuario.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
    }
}