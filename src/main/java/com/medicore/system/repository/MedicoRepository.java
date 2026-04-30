package com.medicore.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medicore.system.model.entity.Medico;

public interface MedicoRepository extends JpaRepository<Medico, String> {

    Medico findByNumeroDocumento(String numeroDocumento);

    Medico findByNumeroDocumentoAndTipoDocumento(String numeroDocumento, String tipoDocumento);

    boolean existsByEspecialidadId(Long especialidadId);
}
