package com.medicore.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medicore.system.dto.response.HorarioDisponibleResponse;
import com.medicore.system.exception.BadRequestException;
import com.medicore.system.exception.BusinessException;
import com.medicore.system.exception.ResourceNotFoundException;
import com.medicore.system.model.entity.BloqueoAgenda;
import com.medicore.system.model.entity.Cita;
import com.medicore.system.model.entity.DiaSemana;
import com.medicore.system.model.entity.DisponibilidadMedica;
import com.medicore.system.model.entity.EstadoCita;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.repository.BloqueoAgendaRepository;
import com.medicore.system.repository.CitaRepository;
import com.medicore.system.repository.DisponibilidadMedicaRepository;
import com.medicore.system.repository.MedicoRepository;

@ExtendWith(MockitoExtension.class)
class AgendaServiceImplTest {

    @Mock private MedicoRepository medicoRepository;
    @Mock private DisponibilidadMedicaRepository disponibilidadRepository;
    @Mock private CitaRepository citaRepository;
    @Mock private BloqueoAgendaRepository bloqueoRepository;
    private AgendaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AgendaServiceImpl(medicoRepository, disponibilidadRepository, citaRepository, bloqueoRepository);
    }

    @Test
    void retornaHorariosDisponiblesSinCitasNiBloqueos() {
        LocalDate fecha = LocalDate.of(2026, 5, 15);
        stubBase(fecha, List.of(disponibilidad(fecha, LocalTime.of(8, 0), LocalTime.of(10, 0))), List.of(), List.of());
        List<HorarioDisponibleResponse> horarios = service.buscarHorariosDisponibles("79998887", fecha, 30);
        assertThat(horarios).hasSize(4);
        assertThat(horarios.get(0).getInicio()).isEqualTo(LocalDateTime.of(2026, 5, 15, 8, 0));
    }

    @Test
    void excluyeHorariosOcupadosPorCitas() {
        LocalDate fecha = LocalDate.of(2026, 5, 15);
        Cita cita = new Cita();
        cita.setFechaHora(LocalDateTime.of(2026, 5, 15, 8, 30));
        cita.setDuracionMinutos(30);
        cita.setEstado(EstadoCita.PROGRAMADA);
        stubBase(fecha, List.of(disponibilidad(fecha, LocalTime.of(8, 0), LocalTime.of(9, 0))), List.of(cita), List.of());
        assertThat(service.buscarHorariosDisponibles("79998887", fecha, 30))
                .extracting(HorarioDisponibleResponse::getInicio)
                .containsExactly(LocalDateTime.of(2026, 5, 15, 8, 0));
    }

    @Test
    void excluyeHorariosBloqueados() {
        LocalDate fecha = LocalDate.of(2026, 5, 15);
        BloqueoAgenda bloqueo = new BloqueoAgenda();
        bloqueo.setFechaInicio(LocalDateTime.of(2026, 5, 15, 8, 0));
        bloqueo.setFechaFin(LocalDateTime.of(2026, 5, 15, 8, 30));
        stubBase(fecha, List.of(disponibilidad(fecha, LocalTime.of(8, 0), LocalTime.of(9, 0))), List.of(), List.of(bloqueo));
        assertThat(service.buscarHorariosDisponibles("79998887", fecha, 30))
                .extracting(HorarioDisponibleResponse::getInicio)
                .containsExactly(LocalDateTime.of(2026, 5, 15, 8, 30));
    }

    @Test
    void retornaListaVaciaSiNoTieneDisponibilidad() {
        LocalDate fecha = LocalDate.of(2026, 5, 15);
        stubBase(fecha, List.of(), List.of(), List.of());
        assertThat(service.buscarHorariosDisponibles("79998887", fecha, 30)).isEmpty();
    }

    @Test
    void retornaHorariosConVariosBloquesDeDisponibilidad() {
        LocalDate fecha = LocalDate.of(2026, 5, 15);
        stubBase(fecha, List.of(
                disponibilidad(fecha, LocalTime.of(8, 0), LocalTime.of(9, 0)),
                disponibilidad(fecha, LocalTime.of(10, 0), LocalTime.of(11, 0))), List.of(), List.of());

        assertThat(service.buscarHorariosDisponibles("79998887", fecha, 30))
                .extracting(HorarioDisponibleResponse::getInicio)
                .containsExactly(
                        LocalDateTime.of(2026, 5, 15, 8, 0),
                        LocalDateTime.of(2026, 5, 15, 8, 30),
                        LocalDateTime.of(2026, 5, 15, 10, 0),
                        LocalDateTime.of(2026, 5, 15, 10, 30));
    }

    @Test
    void bloqueoParcialExcluyeSlotCompleto() {
        LocalDate fecha = LocalDate.of(2026, 5, 15);
        BloqueoAgenda bloqueo = new BloqueoAgenda();
        bloqueo.setFechaInicio(LocalDateTime.of(2026, 5, 15, 8, 10));
        bloqueo.setFechaFin(LocalDateTime.of(2026, 5, 15, 8, 20));
        stubBase(fecha, List.of(disponibilidad(fecha, LocalTime.of(8, 0), LocalTime.of(9, 0))), List.of(), List.of(bloqueo));

        assertThat(service.buscarHorariosDisponibles("79998887", fecha, 30))
                .extracting(HorarioDisponibleResponse::getInicio)
                .containsExactly(LocalDateTime.of(2026, 5, 15, 8, 30));
    }

    @Test
    void medicoInexistenteLanzaResourceNotFound() {
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(null);

        assertThatThrownBy(() -> service.buscarHorariosDisponibles("79998887", LocalDate.of(2026, 5, 15), 30))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void medicoInactivoLanzaBusinessException() {
        Medico medico = medico();
        medico.setActivo(false);
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico);

        assertThatThrownBy(() -> service.buscarHorariosDisponibles("79998887", LocalDate.of(2026, 5, 15), 30))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void duracionInvalidaLanzaBadRequest() {
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico());

        assertThatThrownBy(() -> service.buscarHorariosDisponibles("79998887", LocalDate.of(2026, 5, 15), 10))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void citaCompletadaOcupaHorario() {
        LocalDate fecha = LocalDate.of(2026, 5, 15);
        Cita cita = new Cita();
        cita.setFechaHora(LocalDateTime.of(2026, 5, 15, 8, 0));
        cita.setDuracionMinutos(30);
        cita.setEstado(EstadoCita.COMPLETADA);
        stubBase(fecha, List.of(disponibilidad(fecha, LocalTime.of(8, 0), LocalTime.of(9, 0))), List.of(cita), List.of());

        assertThat(service.buscarHorariosDisponibles("79998887", fecha, 30))
                .extracting(HorarioDisponibleResponse::getInicio)
                .containsExactly(LocalDateTime.of(2026, 5, 15, 8, 30));
    }

    @Test
    void citaCanceladaNoOcupaHorarioDesdeAgenda() {
        LocalDate fecha = LocalDate.of(2026, 5, 15);
        stubBase(fecha, List.of(disponibilidad(fecha, LocalTime.of(8, 0), LocalTime.of(9, 0))), List.of(), List.of());

        assertThat(service.buscarHorariosDisponibles("79998887", fecha, 30))
                .extracting(HorarioDisponibleResponse::getInicio)
                .containsExactly(
                        LocalDateTime.of(2026, 5, 15, 8, 0),
                        LocalDateTime.of(2026, 5, 15, 8, 30));
    }

    private void stubBase(LocalDate fecha, List<DisponibilidadMedica> disponibilidades, List<Cita> citas, List<BloqueoAgenda> bloqueos) {
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico());
        when(disponibilidadRepository.findByMedicoNumeroDocumentoAndDiaSemanaAndActivoTrue("79998887", DiaSemana.from(fecha.getDayOfWeek()))).thenReturn(disponibilidades);
        when(citaRepository.findByMedicoNumeroDocumentoAndEstadoInAndFechaHoraBetween(any(), any(), any(), any())).thenReturn(citas);
        when(bloqueoRepository.findByMedicoNumeroDocumentoAndActivoTrueAndFechaInicioLessThanAndFechaFinGreaterThan(any(), any(), any())).thenReturn(bloqueos);
    }

    private DisponibilidadMedica disponibilidad(LocalDate fecha, LocalTime inicio, LocalTime fin) {
        DisponibilidadMedica disponibilidad = new DisponibilidadMedica();
        disponibilidad.setDiaSemana(DiaSemana.from(fecha.getDayOfWeek()));
        disponibilidad.setHoraInicio(inicio);
        disponibilidad.setHoraFin(fin);
        disponibilidad.setActivo(true);
        disponibilidad.setMedico(medico());
        return disponibilidad;
    }

    private Medico medico() {
        Medico medico = new Medico();
        medico.setNumeroDocumento("79998887");
        medico.setPrimerNombre("Carlos");
        medico.setPrimerApellido("Rojas");
        medico.setActivo(true);
        return medico;
    }
}
