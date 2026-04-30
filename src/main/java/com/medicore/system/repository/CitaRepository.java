package com.medicore.system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medicore.system.model.entity.Cita;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPacienteNumeroDocumento(String numeroDocumento);

    List<Cita> findByMedicoNumeroDocumento(String numeroDocumento);

    boolean existsByPacienteNumeroDocumento(String numeroDocumento);

    boolean existsByMedicoNumeroDocumento(String numeroDocumento);

    boolean existsByMedicoNumeroDocumentoAndFechaHora(String numeroDocumento, java.time.LocalDateTime fechaHora);

    boolean existsByMedicoNumeroDocumentoAndFechaHoraAndIdNot(
            String numeroDocumento,
            java.time.LocalDateTime fechaHora,
            Long id);
}
