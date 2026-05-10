package com.medicore.system.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.medicore.system.model.entity.Cita;
import com.medicore.system.model.entity.EstadoCita;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    @Override
    @EntityGraph(attributePaths = { "paciente", "medico", "medico.especialidad", "historiaClinica" })
    List<Cita> findAll();

    @Override
    @EntityGraph(attributePaths = { "paciente", "medico", "medico.especialidad", "historiaClinica" })
    Optional<Cita> findById(Long id);

    @EntityGraph(attributePaths = { "paciente", "medico", "medico.especialidad", "historiaClinica" })
    List<Cita> findByPacienteNumeroDocumento(String numeroDocumento);

    @EntityGraph(attributePaths = { "paciente", "medico", "medico.especialidad", "historiaClinica" })
    List<Cita> findByMedicoNumeroDocumento(String numeroDocumento);

    @EntityGraph(attributePaths = { "paciente", "medico", "medico.especialidad", "historiaClinica" })
    List<Cita> findByMedicoNumeroDocumentoAndEstado(String numeroDocumento, EstadoCita estado);

    @EntityGraph(attributePaths = { "paciente", "medico", "medico.especialidad", "historiaClinica" })
    List<Cita> findByMedicoNumeroDocumentoAndFechaHoraBetween(String numeroDocumento, LocalDateTime inicio, LocalDateTime fin);

    @EntityGraph(attributePaths = { "paciente", "medico", "medico.especialidad", "historiaClinica" })
    List<Cita> findByEstado(EstadoCita estado);

    @EntityGraph(attributePaths = { "paciente", "medico", "medico.especialidad", "historiaClinica" })
    List<Cita> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Cita> findByMedicoNumeroDocumentoAndEstadoInAndFechaHoraBetween(
            String numeroDocumento,
            Collection<EstadoCita> estados,
            LocalDateTime inicio,
            LocalDateTime fin);


    long countByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    long countByMedicoNumeroDocumentoAndFechaHoraBetween(String numeroDocumento, LocalDateTime inicio, LocalDateTime fin);

    long countByEstado(EstadoCita estado);

    long countByMedicoNumeroDocumentoAndEstado(String numeroDocumento, EstadoCita estado);

    long countByEstadoInAndHistoriaClinicaIsNull(Collection<EstadoCita> estados);

    long countByMedicoNumeroDocumentoAndEstadoInAndHistoriaClinicaIsNull(String numeroDocumento, Collection<EstadoCita> estados);

    @EntityGraph(attributePaths = { "paciente", "medico", "medico.especialidad", "historiaClinica" })
    List<Cita> findTop5ByEstadoInAndFechaHoraGreaterThanEqualOrderByFechaHoraAsc(Collection<EstadoCita> estados, LocalDateTime fechaHora);

    @EntityGraph(attributePaths = { "paciente", "medico", "medico.especialidad", "historiaClinica" })
    List<Cita> findTop5ByMedicoNumeroDocumentoAndEstadoInAndFechaHoraGreaterThanEqualOrderByFechaHoraAsc(String numeroDocumento, Collection<EstadoCita> estados, LocalDateTime fechaHora);

    boolean existsByPacienteNumeroDocumento(String numeroDocumento);

    boolean existsByMedicoNumeroDocumento(String numeroDocumento);

    boolean existsByMedicoNumeroDocumentoAndFechaHora(String numeroDocumento, LocalDateTime fechaHora);

    boolean existsByMedicoNumeroDocumentoAndFechaHoraAndIdNot(
            String numeroDocumento,
            LocalDateTime fechaHora,
            Long id);
}
