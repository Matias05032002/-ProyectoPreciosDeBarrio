package com.tallerwebi.dominio.Email;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class ServicioEmailImplTest {

  @Test
  public void sinUsuarioOClaveNoEstaConfigurado() {
    assertThat(new ServicioEmailImpl(null, "clave").estaConfigurado(), equalTo(false));
    assertThat(new ServicioEmailImpl("pdb@gmail.com", null).estaConfigurado(), equalTo(false));
    assertThat(new ServicioEmailImpl("pdb@gmail.com", "   ").estaConfigurado(), equalTo(false));
  }

  @Test
  public void conUsuarioYClaveEstaConfiguradoAunqueLaClaveTengaEspacios() {
    assertThat(
      new ServicioEmailImpl("pdb@gmail.com", "abcd efgh ijkl mnop").estaConfigurado(),
      equalTo(true)
    );
  }

  @Test
  public void sinConfiguracionEnviarNoFallaYSoloDejaElMailEnElLog() {
    ServicioEmailImpl servicio = new ServicioEmailImpl(null, null);

    assertDoesNotThrow(() -> servicio.enviar("vecina@test.com", "Asunto", "Cuerpo"));
  }

  @Test
  public void laClaveCopiadaConEspaciosDurosOComillasTambienSirve() {
    assertThat(
      new ServicioEmailImpl("pdb@gmail.com", "\"abcd\u00A0efgh ijkl\tmnop\"").estaConfigurado(),
      equalTo(true)
    );
    assertThat(
      new ServicioEmailImpl("pdb@gmail.com", "\u00A0 \u00A0").estaConfigurado(),
      equalTo(false)
    );
  }
}
