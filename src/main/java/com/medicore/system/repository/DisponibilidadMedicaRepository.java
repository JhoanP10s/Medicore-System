package com.medicore.system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.medicore.system.model.entity.DiaSemana;
import com.medicore.system.model.entity.DisponibilidadMedica;

public interface DisponibilidadMedicaRepository extends JpaRepository<DisponibilidadMedica, Long> {

    @Override
    @EntityGraph(attributePaths = "medico")
    List<DisponibilidadMedica> findAll();

    @Override
    @EntityGraph(attributePaths = "medico")
    Optional<DisponibilidadMedica> findById(Long id);

    @EntityGraph(attributePaths = "medico")
    List<DisponibilidadMedica> findByMedicoNumeroDocumento(String numeroDocumento);

    @EntityGraph(attributePaths = "medico")
    List<DisponibilidadMedica> findByMedicoNumeroDocumentoAndDiaSemanaAndActivoTrue(String numeroDocumento, DiaSemana diaSemana);

    @EntityGraph(attributePaths = "medico")
    List<DisponibilidadMedica> findByMedicoNumeroDocumentoAndDiaSemanaAndActivoTrueAndIdNot(String numeroDocumento, DiaSemana diaSemana, Long id);

    boolean existsByMedicoNumeroDocumentoAndActivoTrue(String numeroDocumento);
}
