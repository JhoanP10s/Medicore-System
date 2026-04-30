package com.medicore.system.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.medicore.system.model.entity.Cita;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.model.entity.Paciente;

@DataJpaTest
@ActiveProfiles("test")
class CitaRepositoryTest {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Test
    void findByPacienteNumeroDocumentoRetornaCitasDelPaciente() {
        LocalDateTime fechaHora = LocalDateTime.now().plusDays(1);
        Paciente paciente = pacienteRepository.save(paciente("1020304050"));
        Medico medico = medicoRepository.save(medico("79998887"));
        citaRepository.save(cita(fechaHora, paciente, medico));

        assertThat(citaRepository.findByPacienteNumeroDocumento("1020304050"))
                .hasSize(1)
                .first()
                .extracting(cita -> cita.getPaciente().getNumeroDocumento())
                .isEqualTo("1020304050");
    }

    @Test
    void existsByMedicoNumeroDocumentoAndFechaHoraDetectaMedicoOcupado() {
        LocalDateTime fechaHora = LocalDateTime.now().plusDays(2).withNano(0);
        Paciente paciente = pacienteRepository.save(paciente("1020304050"));
        Medico medico = medicoRepository.save(medico("79998887"));
        citaRepository.save(cita(fechaHora, paciente, medico));

        boolean existe = citaRepository.existsByMedicoNumeroDocumentoAndFechaHora("79998887", fechaHora);
        boolean noExiste = citaRepository.existsByMedicoNumeroDocumentoAndFechaHora(
                "79998887",
                fechaHora.plusHours(1));

        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }

    private Paciente paciente(String documento) {
        Paciente paciente = new Paciente();
        paciente.setNumeroDocumento(documento);
        paciente.setPrimerNombre("Laura");
        paciente.setPrimerApellido("Gomez");
        paciente.setTipoDocumento("CC");
        paciente.setActivo(true);
        return paciente;
    }

    private Medico medico(String documento) {
        Medico medico = new Medico();
        medico.setNumeroDocumento(documento);
        medico.setPrimerNombre("Carlos");
        medico.setPrimerApellido("Rojas");
        medico.setTipoDocumento("CC");
        medico.setActivo(true);
        return medico;
    }

    private Cita cita(LocalDateTime fechaHora, Paciente paciente, Medico medico) {
        Cita cita = new Cita();
        cita.setFechaHora(fechaHora);
        cita.setMotivo("Control general");
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        return cita;
    }
}
