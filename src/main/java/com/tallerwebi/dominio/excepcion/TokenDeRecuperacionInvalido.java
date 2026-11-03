package com.tallerwebi.dominio.excepcion;

public class TokenDeRecuperacionInvalido extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TokenDeRecuperacionInvalido() {
    super("El link para restablecer la contraseña venció o ya se usó. Pedí uno nuevo.");
  }
}
