package com.medicore.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medicore.system.model.entity.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, String> {

    Paciente findByNumeroDocumento(String numeroDocumento);

    Paciente findByNumeroDocumentoAndTipoDocumento(String numeroDocumento, String tipoDocumento);
}
