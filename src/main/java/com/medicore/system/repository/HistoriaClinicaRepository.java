package com.medicore.system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.medicore.system.model.entity.HistoriaClinica;

public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {

    @Override
    @EntityGraph(attributePaths = { "paciente", "medico", "cita", "cita.paciente", "cita.medico" })
    List<HistoriaClinica> findAll();

    @Override
    @EntityGraph(attributePaths = { "paciente", "medico", "cita", "cita.paciente", "cita.medico" })
    Optional<HistoriaClinica> findById(Long id);

    @EntityGraph(attributePaths = { "paciente", "medico", "cita", "cita.paciente", "cita.medico" })
    List<HistoriaClinica> findByPacienteNumeroDocumento(String numeroDocumento);

    @EntityGraph(attributePaths = { "paciente", "medico", "cita", "cita.paciente", "cita.medico" })
    List<HistoriaClinica> findByMedicoNumeroDocumento(String numeroDocumento);

    @EntityGraph(attributePaths = { "paciente", "medico", "cita", "cita.paciente", "cita.medico" })
    Optional<HistoriaClinica> findByCitaId(Long citaId);


    @EntityGraph(attributePaths = { "paciente", "medico", "cita", "cita.paciente", "cita.medico" })
    List<HistoriaClinica> findTop5ByMedicoNumeroDocumentoOrderByFechaRegistroDesc(String numeroDocumento);

    @EntityGraph(attributePaths = { "paciente", "medico", "cita", "cita.paciente", "cita.medico" })
    List<HistoriaClinica> findTop5ByOrderByFechaRegistroDesc();

    long countByMedicoNumeroDocumento(String numeroDocumento);

    boolean existsByCitaId(Long citaId);
}
