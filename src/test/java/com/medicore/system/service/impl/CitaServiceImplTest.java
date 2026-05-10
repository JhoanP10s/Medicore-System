package com.medicore.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
import com.medicore.system.model.entity.BloqueoAgenda;
import com.medicore.system.model.entity.Cita;
import com.medicore.system.model.entity.DiaSemana;
import com.medicore.system.model.entity.DisponibilidadMedica;
import com.medicore.system.model.entity.EstadoCita;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.model.entity.Paciente;
import com.medicore.system.repository.BloqueoAgendaRepository;
import com.medicore.system.repository.CitaRepository;
import com.medicore.system.repository.DisponibilidadMedicaRepository;
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

    @Mock
    private DisponibilidadMedicaRepository disponibilidadRepository;

    @Mock
    private BloqueoAgendaRepository bloqueoRepository;

    private CitaServiceImpl citaService;

    @BeforeEach
    void setUp() {
        citaService = new CitaServiceImpl(citaRepository, pacienteRepository, medicoRepository, disponibilidadRepository, bloqueoRepository, new CitaMapper());
    }

    @Test
    void crearCitaCuandoDatosSonValidosGuardaYRetornaResponse() {
        CitaRequest request = citaRequest(LocalDateTime.now().plusDays(1));
        Paciente paciente = pacienteActivo();
        Medico medico = medicoActivo();

        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(paciente);
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico);
        stubDisponibilidad(request.getFechaHora());
        when(bloqueoRepository.findByMedicoNumeroDocumentoAndActivoTrueAndFechaInicioLessThanAndFechaFinGreaterThan(any(), any(), any()))
                .thenReturn(List.of());
        when(citaRepository.findByMedicoNumeroDocumentoAndEstadoInAndFechaHoraBetween(any(), any(), any(), any()))
                .thenReturn(List.of());
        when(citaRepository.save(any(Cita.class))).thenAnswer(invocation -> {
            Cita cita = invocation.getArgument(0);
            cita.setId(1L);
            return cita;
        });

        CitaResponse response = citaService.crearCita(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEstado()).isEqualTo(EstadoCita.PROGRAMADA);
        assertThat(response.getDuracionMinutos()).isEqualTo(30);
        verify(citaRepository).save(any(Cita.class));
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
    void crearCitaCuandoMedicoInactivoLanzaBusinessException() {
        CitaRequest request = citaRequest(LocalDateTime.now().plusDays(1));
        Medico medico = medicoActivo();
        medico.setActivo(false);

        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(pacienteActivo());
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico);

        assertThatThrownBy(() -> citaService.crearCita(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("No se pueden crear citas con medicos inactivos.");
    }

    @Test
    void crearCitaCuandoPacienteInactivoLanzaBusinessException() {
        CitaRequest request = citaRequest(LocalDateTime.now().plusDays(1));
        Paciente paciente = pacienteActivo();
        paciente.setActivo(false);

        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(paciente);
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medicoActivo());

        assertThatThrownBy(() -> citaService.crearCita(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("No se pueden crear citas con pacientes inactivos.");
    }

    @Test
    void crearCitaCuandoExisteSolapamientoLanzaBusinessException() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);
        CitaRequest request = citaRequest(fecha);
        Cita existente = cita(fecha.plusMinutes(15), pacienteActivo(), medicoActivo());
        existente.setId(7L);
        existente.setEstado(EstadoCita.CONFIRMADA);
        existente.setDuracionMinutos(30);

        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(pacienteActivo());
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medicoActivo());
        stubDisponibilidad(request.getFechaHora());
        when(citaRepository.findByMedicoNumeroDocumentoAndEstadoInAndFechaHoraBetween(any(), any(), any(), any()))
                .thenReturn(List.of(existente));

        assertThatThrownBy(() -> citaService.crearCita(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El medico ya tiene una cita que se solapa con esa fecha y duracion.");
    }

    @Test
    void cambiarEstadoProgramadaAConfirmadaActualizaEstado() {
        Cita cita = cita(LocalDateTime.now().plusDays(1), pacienteActivo(), medicoActivo());
        cita.setId(1L);
        cita.setEstado(EstadoCita.PROGRAMADA);

        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(cita)).thenReturn(cita);

        CitaResponse response = citaService.cambiarEstado(1L, EstadoCita.CONFIRMADA);

        assertThat(response.getEstado()).isEqualTo(EstadoCita.CONFIRMADA);
    }

    @Test
    void cambiarEstadoCanceladaAConfirmadaLanzaBusinessException() {
        Cita cita = cita(LocalDateTime.now().plusDays(1), pacienteActivo(), medicoActivo());
        cita.setId(1L);
        cita.setEstado(EstadoCita.CANCELADA);

        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));

        assertThatThrownBy(() -> citaService.cambiarEstado(1L, EstadoCita.CONFIRMADA))
                .isInstanceOf(BusinessException.class)
                .hasMessage("No se puede cambiar el estado de una cita cancelada.");
    }

    @Test
    void verCitaCuandoNoExisteLanzaResourceNotFound() {
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> citaService.verCita(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No existe cita registrada");
    }


    @Test
    void editarCitaConfirmadaPreservaEstadoConfirmada() {
        Cita cita = cita(LocalDateTime.now().plusDays(1), pacienteActivo(), medicoActivo());
        cita.setId(1L);
        cita.setEstado(EstadoCita.CONFIRMADA);
        CitaRequest request = citaRequest(LocalDateTime.now().plusDays(2));

        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(pacienteActivo());
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medicoActivo());
        stubDisponibilidad(request.getFechaHora());
        when(bloqueoRepository.findByMedicoNumeroDocumentoAndActivoTrueAndFechaInicioLessThanAndFechaFinGreaterThan(any(), any(), any()))
                .thenReturn(List.of());
        when(citaRepository.findByMedicoNumeroDocumentoAndEstadoInAndFechaHoraBetween(any(), any(), any(), any()))
                .thenReturn(List.of());
        when(citaRepository.save(cita)).thenReturn(cita);

        CitaResponse response = citaService.editarCita(1L, request);

        assertThat(response.getEstado()).isEqualTo(EstadoCita.CONFIRMADA);
        assertThat(cita.getEstado()).isEqualTo(EstadoCita.CONFIRMADA);
        assertThat(response.getFechaHora()).isEqualTo(request.getFechaHora());
    }



    @Test
    void crearCitaFueraDeDisponibilidadLanzaBusinessException() {
        CitaRequest request = citaRequest(LocalDateTime.now().plusDays(1));
        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(pacienteActivo());
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medicoActivo());
        when(disponibilidadRepository.findByMedicoNumeroDocumentoAndDiaSemanaAndActivoTrue(any(), any())).thenReturn(List.of());

        assertThatThrownBy(() -> citaService.crearCita(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La cita debe estar completamente dentro de una disponibilidad activa del medico.");
    }

    @Test
    void crearCitaQueTerminaFueraDeDisponibilidadLanzaBusinessException() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(1).withHour(11).withMinute(45).withSecond(0).withNano(0);
        CitaRequest request = citaRequest(fecha);
        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(pacienteActivo());
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medicoActivo());
        stubDisponibilidad(fecha, LocalTime.of(8, 0), LocalTime.of(12, 0));

        assertThatThrownBy(() -> citaService.crearCita(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La cita debe estar completamente dentro de una disponibilidad activa del medico.");
    }

    @Test
    void crearCitaDentroDeBloqueoActivoLanzaBusinessException() {
        CitaRequest request = citaRequest(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
        BloqueoAgenda bloqueo = new BloqueoAgenda();
        bloqueo.setFechaInicio(request.getFechaHora().minusMinutes(15));
        bloqueo.setFechaFin(request.getFechaHora().plusMinutes(15));
        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(pacienteActivo());
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medicoActivo());
        stubDisponibilidad(request.getFechaHora());
        when(citaRepository.findByMedicoNumeroDocumentoAndEstadoInAndFechaHoraBetween(any(), any(), any(), any())).thenReturn(List.of());
        when(bloqueoRepository.findByMedicoNumeroDocumentoAndActivoTrueAndFechaInicioLessThanAndFechaFinGreaterThan(any(), any(), any()))
                .thenReturn(List.of(bloqueo));

        assertThatThrownBy(() -> citaService.crearCita(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La cita se solapa con un bloqueo activo de la agenda del medico.");
    }

    @Test
    void crearCitaPermiteSolapamientoConCitaCancelada() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
        CitaRequest request = citaRequest(fecha);
        Cita cancelada = cita(fecha, pacienteActivo(), medicoActivo());
        cancelada.setId(2L);
        cancelada.setEstado(EstadoCita.CANCELADA);
        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(pacienteActivo());
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medicoActivo());
        stubDisponibilidad(fecha);
        when(citaRepository.findByMedicoNumeroDocumentoAndEstadoInAndFechaHoraBetween(any(), any(), any(), any())).thenReturn(List.of());
        when(bloqueoRepository.findByMedicoNumeroDocumentoAndActivoTrueAndFechaInicioLessThanAndFechaFinGreaterThan(any(), any(), any())).thenReturn(List.of());
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(citaService.crearCita(request).getFechaHora()).isEqualTo(fecha);
    }


    @Test
    void bloqueoInactivoNoImpideCrearCita() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        CitaRequest request = citaRequest(fecha);
        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(pacienteActivo());
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medicoActivo());
        stubDisponibilidad(fecha);
        when(citaRepository.findByMedicoNumeroDocumentoAndEstadoInAndFechaHoraBetween(any(), any(), any(), any())).thenReturn(List.of());
        when(bloqueoRepository.findByMedicoNumeroDocumentoAndActivoTrueAndFechaInicioLessThanAndFechaFinGreaterThan(any(), any(), any())).thenReturn(List.of());
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(citaService.crearCita(request).getFechaHora()).isEqualTo(fecha);
    }


    @Test
    void citaExistenteSinDuracionUsaDuracionLegacyParaSolapamiento() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(1).withHour(9).withMinute(15).withSecond(0).withNano(0);
        CitaRequest request = citaRequest(fecha);
        Cita existenteLegacy = cita(fecha.minusMinutes(15), pacienteActivo(), medicoActivo());
        existenteLegacy.setId(11L);
        existenteLegacy.setDuracionMinutos(null);
        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(pacienteActivo());
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medicoActivo());
        stubDisponibilidad(fecha);
        when(citaRepository.findByMedicoNumeroDocumentoAndEstadoInAndFechaHoraBetween(any(), any(), any(), any())).thenReturn(List.of(existenteLegacy));

        assertThatThrownBy(() -> citaService.crearCita(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El medico ya tiene una cita que se solapa con esa fecha y duracion.");
    }

    private void stubDisponibilidad(LocalDateTime fechaHora) {
        stubDisponibilidad(fechaHora, LocalTime.MIN, LocalTime.MAX);
    }

    private void stubDisponibilidad(LocalDateTime fechaHora, LocalTime horaInicio, LocalTime horaFin) {
        DisponibilidadMedica disponibilidad = new DisponibilidadMedica();
        disponibilidad.setDiaSemana(DiaSemana.from(fechaHora.getDayOfWeek()));
        disponibilidad.setHoraInicio(horaInicio);
        disponibilidad.setHoraFin(horaFin);
        disponibilidad.setActivo(true);
        disponibilidad.setMedico(medicoActivo());
        when(disponibilidadRepository.findByMedicoNumeroDocumentoAndDiaSemanaAndActivoTrue(
                "79998887",
                DiaSemana.from(fechaHora.getDayOfWeek())))
                .thenReturn(List.of(disponibilidad));
    }

    private CitaRequest citaRequest(LocalDateTime fechaHora) {
        CitaRequest request = new CitaRequest();
        request.setNumeroDocumentoPaciente("1020304050");
        request.setNumeroDocumentoMedico("79998887");
        request.setFechaHora(fechaHora);
        request.setMotivo("Control general");
        request.setDuracionMinutos(30);
        return request;
    }

    private Cita cita(LocalDateTime fechaHora, Paciente paciente, Medico medico) {
        Cita cita = new Cita();
        cita.setFechaHora(fechaHora);
        cita.setMotivo("Control general");
        cita.setDuracionMinutos(30);
        cita.setEstado(EstadoCita.PROGRAMADA);
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        return cita;
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
