package com.medicore.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.medicore.system.dto.response.DashboardResponse;
import com.medicore.system.model.entity.Cita;
import com.medicore.system.model.entity.EstadoCita;
import com.medicore.system.model.entity.HistoriaClinica;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.model.entity.Paciente;
import com.medicore.system.repository.CitaRepository;
import com.medicore.system.repository.DisponibilidadMedicaRepository;
import com.medicore.system.repository.EspecialidadRepository;
import com.medicore.system.repository.HistoriaClinicaRepository;
import com.medicore.system.repository.MedicoRepository;
import com.medicore.system.repository.PacienteRepository;
import com.medicore.system.security.AuthenticatedUserService;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private PacienteRepository pacienteRepository;
    @Mock
    private MedicoRepository medicoRepository;
    @Mock
    private EspecialidadRepository especialidadRepository;
    @Mock
    private CitaRepository citaRepository;
    @Mock
    private HistoriaClinicaRepository historiaClinicaRepository;
    @Mock
    private DisponibilidadMedicaRepository disponibilidadMedicaRepository;
    @Mock
    private AuthenticatedUserService authenticatedUserService;

    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardServiceImpl(
                pacienteRepository,
                medicoRepository,
                especialidadRepository,
                citaRepository,
                historiaClinicaRepository,
                disponibilidadMedicaRepository,
                authenticatedUserService);
    }

    @Test
    void adminObtieneDashboardGlobal() {
        when(authenticatedUserService.isAdmin()).thenReturn(true);
        when(pacienteRepository.countByActivoTrue()).thenReturn(10L);
        when(medicoRepository.countByActivoTrue()).thenReturn(3L);
        when(especialidadRepository.countByActivoTrue()).thenReturn(4L);
        when(citaRepository.countByFechaHoraBetween(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(2L);
        when(historiaClinicaRepository.count()).thenReturn(8L);
        when(citaRepository.findTop5ByEstadoInAndFechaHoraGreaterThanEqualOrderByFechaHoraAsc(org.mockito.ArgumentMatchers.anyCollection(), org.mockito.ArgumentMatchers.any())).thenReturn(List.of(cita(EstadoCita.PROGRAMADA), cita(EstadoCita.CONFIRMADA)));
        when(medicoRepository.findByActivoTrue()).thenReturn(List.of(medico()));
        when(disponibilidadMedicaRepository.existsByMedicoNumeroDocumentoAndActivoTrue("79998887")).thenReturn(true);

        DashboardResponse response = dashboardService.obtenerResumen();

        assertThat(response.getTipo()).isEqualTo("ADMIN");
        assertThat(response.getAdmin()).isNotNull();
        assertThat(response.getDoctor()).isNull();
        assertThat(response.getAdmin().getTotalPacientesActivos()).isEqualTo(10L);
        assertThat(response.getAdmin().getProximasCitas()).extracting("estado")
                .containsExactly(EstadoCita.PROGRAMADA, EstadoCita.CONFIRMADA);
    }

    @Test
    void doctorObtieneSoloDashboardPropio() {
        when(authenticatedUserService.isAdmin()).thenReturn(false);
        when(authenticatedUserService.isDoctor()).thenReturn(true);
        when(authenticatedUserService.getCurrentDoctorDocumentoOrThrow()).thenReturn("79998887");
        when(citaRepository.findTop5ByMedicoNumeroDocumentoAndEstadoInAndFechaHoraGreaterThanEqualOrderByFechaHoraAsc(org.mockito.ArgumentMatchers.eq("79998887"), org.mockito.ArgumentMatchers.anyCollection(), org.mockito.ArgumentMatchers.any())).thenReturn(List.of(cita(EstadoCita.CONFIRMADA)));
        when(historiaClinicaRepository.findTop5ByMedicoNumeroDocumentoOrderByFechaRegistroDesc("79998887")).thenReturn(List.of(historia()));
        when(disponibilidadMedicaRepository.existsByMedicoNumeroDocumentoAndActivoTrue("79998887")).thenReturn(true);

        DashboardResponse response = dashboardService.obtenerResumen();

        assertThat(response.getTipo()).isEqualTo("DOCTOR");
        assertThat(response.getDoctor()).isNotNull();
        assertThat(response.getAdmin()).isNull();
        assertThat(response.getDoctor().getProximasCitas()).hasSize(1);
        assertThat(response.getDoctor().getHistoriasRecientes()).hasSize(1);
    }

    @Test
    void doctorSinMedicoAsociadoRecibeErrorClaro() {
        when(authenticatedUserService.isAdmin()).thenReturn(false);
        when(authenticatedUserService.isDoctor()).thenReturn(true);
        when(authenticatedUserService.getCurrentDoctorDocumentoOrThrow())
                .thenThrow(new AccessDeniedException("El usuario doctor no tiene un medico asociado."));

        assertThatThrownBy(() -> dashboardService.obtenerResumen())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("El usuario doctor no tiene un medico asociado.");
    }

    @Test
    void adminConsultaProximasCitasSoloProgramadasYConfirmadas() {
        when(authenticatedUserService.isAdmin()).thenReturn(true);
        when(citaRepository.findTop5ByEstadoInAndFechaHoraGreaterThanEqualOrderByFechaHoraAsc(org.mockito.ArgumentMatchers.anyCollection(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(cita(EstadoCita.PROGRAMADA), cita(EstadoCita.CONFIRMADA)));
        when(medicoRepository.findByActivoTrue()).thenReturn(List.of());

        DashboardResponse response = dashboardService.obtenerResumen();

        assertThat(response.getAdmin().getProximasCitas())
                .extracting("estado")
                .containsExactly(EstadoCita.PROGRAMADA, EstadoCita.CONFIRMADA)
                .doesNotContain(EstadoCita.CANCELADA, EstadoCita.COMPLETADA);
        verify(citaRepository).findTop5ByEstadoInAndFechaHoraGreaterThanEqualOrderByFechaHoraAsc(
                org.mockito.ArgumentMatchers.argThat((Collection<EstadoCita> estados) -> estados.contains(EstadoCita.PROGRAMADA)
                        && estados.contains(EstadoCita.CONFIRMADA)
                        && !estados.contains(EstadoCita.CANCELADA)
                        && !estados.contains(EstadoCita.COMPLETADA)),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    void doctorConsultaProximasCitasSoloPropiasProgramadasYConfirmadas() {
        when(authenticatedUserService.isAdmin()).thenReturn(false);
        when(authenticatedUserService.isDoctor()).thenReturn(true);
        when(authenticatedUserService.getCurrentDoctorDocumentoOrThrow()).thenReturn("79998887");
        when(citaRepository.findTop5ByMedicoNumeroDocumentoAndEstadoInAndFechaHoraGreaterThanEqualOrderByFechaHoraAsc(org.mockito.ArgumentMatchers.eq("79998887"), org.mockito.ArgumentMatchers.anyCollection(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(cita(EstadoCita.PROGRAMADA), cita(EstadoCita.CONFIRMADA)));
        when(historiaClinicaRepository.findTop5ByMedicoNumeroDocumentoOrderByFechaRegistroDesc("79998887")).thenReturn(List.of());
        when(disponibilidadMedicaRepository.existsByMedicoNumeroDocumentoAndActivoTrue("79998887")).thenReturn(true);

        DashboardResponse response = dashboardService.obtenerResumen();

        assertThat(response.getDoctor().getProximasCitas())
                .extracting("estado")
                .containsExactly(EstadoCita.PROGRAMADA, EstadoCita.CONFIRMADA)
                .doesNotContain(EstadoCita.CANCELADA, EstadoCita.COMPLETADA);
        verify(citaRepository).findTop5ByMedicoNumeroDocumentoAndEstadoInAndFechaHoraGreaterThanEqualOrderByFechaHoraAsc(
                org.mockito.ArgumentMatchers.eq("79998887"),
                org.mockito.ArgumentMatchers.argThat((Collection<EstadoCita> estados) -> estados.contains(EstadoCita.PROGRAMADA)
                        && estados.contains(EstadoCita.CONFIRMADA)
                        && !estados.contains(EstadoCita.CANCELADA)
                        && !estados.contains(EstadoCita.COMPLETADA)),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    void doctorConMedicoInactivoRecibeErrorClaro() {
        when(authenticatedUserService.isAdmin()).thenReturn(false);
        when(authenticatedUserService.isDoctor()).thenReturn(true);
        when(authenticatedUserService.getCurrentDoctorDocumentoOrThrow())
                .thenThrow(new AccessDeniedException("El medico asociado al usuario esta inactivo."));

        assertThatThrownBy(() -> dashboardService.obtenerResumen())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("El medico asociado al usuario esta inactivo.");
    }

    @Test
    void userObtieneDashboardLimitado() {
        when(authenticatedUserService.isAdmin()).thenReturn(false);
        when(authenticatedUserService.isDoctor()).thenReturn(false);

        DashboardResponse response = dashboardService.obtenerResumen();

        assertThat(response.getTipo()).isEqualTo("USER");
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getAdmin()).isNull();
        assertThat(response.getDoctor()).isNull();
        assertThat(response.getUser().getAccesosDisponibles()).isNotEmpty();
    }

    private Cita cita(EstadoCita estado) {
        Cita cita = new Cita();
        cita.setId(1L);
        cita.setFechaHora(LocalDateTime.now().plusHours(2));
        cita.setEstado(estado);
        cita.setDuracionMinutos(30);
        cita.setMotivo("Control general");
        cita.setPaciente(paciente());
        cita.setMedico(medico());
        return cita;
    }

    private HistoriaClinica historia() {
        HistoriaClinica historia = new HistoriaClinica();
        historia.setId(5L);
        historia.setFechaRegistro(LocalDateTime.now());
        historia.setDiagnostico("Diagnostico de prueba");
        historia.setPaciente(paciente());
        historia.setMedico(medico());
        historia.setCita(cita(EstadoCita.CONFIRMADA));
        return historia;
    }

    private Paciente paciente() {
        Paciente paciente = new Paciente();
        paciente.setNumeroDocumento("1020304050");
        paciente.setPrimerNombre("Laura");
        paciente.setPrimerApellido("Gomez");
        return paciente;
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
