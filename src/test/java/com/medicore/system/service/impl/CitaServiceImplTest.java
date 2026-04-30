package com.medicore.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medicore.system.dto.request.CitaRequest;
import com.medicore.system.dto.response.CitaResponse;
import com.medicore.system.exception.BadRequestException;
import com.medicore.system.exception.BusinessException;
import com.medicore.system.exception.ResourceNotFoundException;
import com.medicore.system.mapper.CitaMapper;
import com.medicore.system.model.entity.Cita;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.model.entity.Paciente;
import com.medicore.system.repository.CitaRepository;
import com.medicore.system.repository.MedicoRepository;
import com.medicore.system.repository.PacienteRepository;

@ExtendWith(MockitoExtension.class)
class CitaServiceImplTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private MedicoRepository medicoRepository;

    private CitaServiceImpl citaService;

    @BeforeEach
    void setUp() {
        citaService = new CitaServiceImpl(
                citaRepository,
                pacienteRepository,
                medicoRepository,
                new CitaMapper());
    }

    @Test
    void crearCitaCuandoDatosSonValidosGuardaYRetornaResponse() {
        CitaRequest request = citaRequest(LocalDateTime.now().plusDays(1));
        Paciente paciente = pacienteActivo();
        Medico medico = medicoActivo();

        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(paciente);
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico);
        when(citaRepository.existsByMedicoNumeroDocumentoAndFechaHora("79998887", request.getFechaHora()))
                .thenReturn(false);
        when(citaRepository.save(any(Cita.class))).thenAnswer(invocation -> {
            Cita cita = invocation.getArgument(0);
            cita.setId(1L);
            return cita;
        });

        CitaResponse response = citaService.crearCita(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getPacienteNumeroDocumento()).isEqualTo("1020304050");
        assertThat(response.getMedicoNumeroDocumento()).isEqualTo("79998887");
        verify(citaRepository).save(any(Cita.class));
    }

    @Test
    void crearCitaCuandoPacienteNoExisteLanzaResourceNotFound() {
        CitaRequest request = citaRequest(LocalDateTime.now().plusDays(1));

        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(null);

        assertThatThrownBy(() -> citaService.crearCita(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No existe paciente registrado");

        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    void crearCitaCuandoPacienteEstaInactivoLanzaBusinessException() {
        CitaRequest request = citaRequest(LocalDateTime.now().plusDays(1));
        Paciente paciente = pacienteActivo();
        paciente.setActivo(false);

        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(paciente);
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medicoActivo());

        assertThatThrownBy(() -> citaService.crearCita(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("No se pueden crear citas con pacientes inactivos.");

        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    void crearCitaCuandoMedicoYaTieneCitaALaMismaHoraLanzaBusinessException() {
        CitaRequest request = citaRequest(LocalDateTime.now().plusDays(1));

        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(pacienteActivo());
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medicoActivo());
        when(citaRepository.existsByMedicoNumeroDocumentoAndFechaHora("79998887", request.getFechaHora()))
                .thenReturn(true);

        assertThatThrownBy(() -> citaService.crearCita(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El medico ya tiene una cita programada en esa fecha y hora.");

        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    void crearCitaCuandoFechaEsPasadaLanzaBadRequest() {
        CitaRequest request = citaRequest(LocalDateTime.now().minusDays(1));

        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(pacienteActivo());
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medicoActivo());

        assertThatThrownBy(() -> citaService.crearCita(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("No se permiten citas en fechas pasadas.");

        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    void verCitaCuandoNoExisteLanzaResourceNotFound() {
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> citaService.verCita(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No existe cita registrada");
    }

    private CitaRequest citaRequest(LocalDateTime fechaHora) {
        CitaRequest request = new CitaRequest();
        request.setNumeroDocumentoPaciente("1020304050");
        request.setNumeroDocumentoMedico("79998887");
        request.setFechaHora(fechaHora);
        request.setMotivo("Control general");
        return request;
    }

    private Paciente pacienteActivo() {
        Paciente paciente = new Paciente();
        paciente.setNumeroDocumento("1020304050");
        paciente.setPrimerNombre("Laura");
        paciente.setPrimerApellido("Gomez");
        paciente.setTipoDocumento("CC");
        paciente.setActivo(true);
        return paciente;
    }

    private Medico medicoActivo() {
        Medico medico = new Medico();
        medico.setNumeroDocumento("79998887");
        medico.setPrimerNombre("Carlos");
        medico.setPrimerApellido("Rojas");
        medico.setTipoDocumento("CC");
        medico.setActivo(true);
        return medico;
    }
}
