package com.tallerwebi.dominio.Cuenta;

@FunctionalInterface
public interface ServicioEliminacionDeCuenta {
    /**
     * Da de baja la cuenta si la contraseña es la correcta: queda desactivada y sin datos personales.
     * Sus reportes de precios se conservan sin su nombre. Si es un comercio, también se desactivan
     * el local y sus promociones. Devuelve false si no se pudo (contraseña incorrecta, la cuenta no
     * existe o ya estaba eliminada). Lanza AccesoDenegado si es la cuenta del administrador.
     */
    boolean eliminarCuenta(Long usuarioId, String contrasena);
}