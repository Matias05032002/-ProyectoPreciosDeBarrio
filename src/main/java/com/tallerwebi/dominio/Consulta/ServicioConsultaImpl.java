package com.tallerwebi.dominio.Consulta;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioConsulta")
@Transactional
public class ServicioConsultaImpl implements ServicioConsulta {

  private final RepositorioConsulta repositorioConsulta;

  @Autowired
  public ServicioConsultaImpl(RepositorioConsulta repositorioConsulta) {
    this.repositorioConsulta = repositorioConsulta;
  }

  @Override
  public void guardar(Consulta consulta) {
    consulta.setFecha(LocalDateTime.now());
    consulta.setEstado("Pendiente");
    repositorioConsulta.guardar(consulta);
  }

  @Override
  public List<Consulta> listarTodas() {
    return repositorioConsulta.listarTodas();
  }
}
