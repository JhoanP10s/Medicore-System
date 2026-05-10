package com.medicore.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medicore.system.dto.request.HistoriaClinicaRequest;
import com.medicore.system.dto.response.HistoriaClinicaResponse;
import com.medicore.system.exception.BusinessException;
import com.medicore.system.mapper.HistoriaClinicaMapper;
import com.medicore.system.model.entity.Cita;
import com.medicore.system.model.entity.EstadoCita;
import com.medicore.system.model.entity.HistoriaClinica;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.model.entity.Paciente;
import com.medicore.system.repository.CitaRepository;
import com.medicore.system.repository.HistoriaClinicaRepository;

@ExtendWith(MockitoExtension.class)
class HistoriaClinicaServiceImplTest {

    @Mock
    private HistoriaClinicaRepository historiaClinicaRepository;

    @Mock
    private CitaRepository citaRepository;

    private HistoriaClinicaServiceImpl historiaClinicaService;

    @BeforeEach
    void setUp() {
        historiaClinicaService = new HistoriaClinicaServiceImpl(
                historiaClinicaRepository,
                citaRepository,
                new HistoriaClinicaMapper());
    }

    @Test
    void crearHistoriaClinicaDesdeCitaConfirmadaCambiaCitaACompletada() {
        Cita cita = cita(EstadoCita.CONFIRMADA);
        HistoriaClinicaRequest request = request();

        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(historiaClinicaRepository.existsByCitaId(1L)).thenReturn(false);
        when(historiaClinicaRepository.save(any(HistoriaClinica.class))).thenAnswer(invocation -> {
            HistoriaClinica historia = invocation.getArgument(0);
            historia.setId(10L);
            historia.setCreatedAt(LocalDateTime.now());
            historia.setUpdatedAt(LocalDateTime.now());
            return historia;
        });

        HistoriaClinicaResponse response = historiaClinicaService.crearHistoriaClinica(request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getCitaId()).isEqualTo(1L);
        assertThat(cita.getEstado()).isEqualTo(EstadoCita.COMPLETADA);
    }

    @Test
    void crearHistoriaClinicaDuplicadaLanzaBusinessException() {
        Cita cita = cita(EstadoCita.CONFIRMADA);

        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(historiaClinicaRepository.existsByCitaId(1L)).thenReturn(true);

        assertThatThrownBy(() -> historiaClinicaService.crearHistoriaClinica(request()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Ya existe una historia clinica para esta cita.");
    }

    @Test
    void crearHistoriaClinicaParaCitaCanceladaLanzaBusinessException() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita(EstadoCita.CANCELADA)));

        assertThatThrownBy(() -> historiaClinicaService.crearHistoriaClinica(request()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("No se puede crear historia clinica para una cita cancelada.");
    }

    @Test
    void crearHistoriaClinicaParaCitaProgramadaLanzaBusinessException() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita(EstadoCita.PROGRAMADA)));

        assertThatThrownBy(() -> historiaClinicaService.crearHistoriaClinica(request()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("No se puede crear historia clinica para una cita sin confirmar.");
    }

    private HistoriaClinicaRequest request() {
        HistoriaClinicaRequest request = new HistoriaClinicaRequest();
        request.setCitaId(1L);
        request.setSintomas("Dolor toracico leve");
        request.setDiagnostico("Hipertension en seguimiento");
        request.setTratamiento("Control de presion");
        return request;
    }

    private Cita cita(EstadoCita estado) {
        Paciente paciente = new Paciente();
        paciente.setNumeroDocumento("1020304050");
        paciente.setPrimerNombre("Laura");
        paciente.setPrimerApellido("Gomez");

        Medico medico = new Medico();
        medico.setNumeroDocumento("79998887");
        medico.setPrimerNombre("Carlos");
        medico.setPrimerApellido("Rojas");

        Cita cita = new Cita();
        cita.setId(1L);
        cita.setFechaHora(LocalDateTime.now().plusDays(1));
        cita.setMotivo("Control general");
        cita.setDuracionMinutos(30);
        cita.setEstado(estado);
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        return cita;
    }
}
