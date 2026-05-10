package com.medicore.system.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.medicore.system.model.entity.BloqueoAgenda;

public interface BloqueoAgendaRepository extends JpaRepository<BloqueoAgenda, Long> {

    @Override
    @EntityGraph(attributePaths = "medico")
    List<BloqueoAgenda> findAll();

    @Override
    @EntityGraph(attributePaths = "medico")
    Optional<BloqueoAgenda> findById(Long id);

    @EntityGraph(attributePaths = "medico")
    List<BloqueoAgenda> findByMedicoNumeroDocumento(String numeroDocumento);

    @EntityGraph(attributePaths = "medico")
    List<BloqueoAgenda> findByFechaInicioLessThanAndFechaFinGreaterThan(LocalDateTime fin, LocalDateTime inicio);

    @EntityGraph(attributePaths = "medico")
    List<BloqueoAgenda> findByMedicoNumeroDocumentoAndFechaInicioLessThanAndFechaFinGreaterThan(
            String numeroDocumento,
            LocalDateTime fin,
            LocalDateTime inicio);

    @EntityGraph(attributePaths = "medico")
    List<BloqueoAgenda> findByActivoTrueAndFechaInicioLessThanAndFechaFinGreaterThan(LocalDateTime fin, LocalDateTime inicio);

    @EntityGraph(attributePaths = "medico")
    List<BloqueoAgenda> findByMedicoNumeroDocumentoAndActivoTrueAndFechaInicioLessThanAndFechaFinGreaterThan(
            String numeroDocumento,
            LocalDateTime fin,
            LocalDateTime inicio);
}
