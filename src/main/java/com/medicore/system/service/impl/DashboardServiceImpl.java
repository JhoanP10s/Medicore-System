package com.medicore.system.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medicore.system.dto.response.AdminDashboardResponse;
import com.medicore.system.dto.response.AlertaDashboardResponse;
import com.medicore.system.dto.response.CitaResumenResponse;
import com.medicore.system.dto.response.DashboardResponse;
import com.medicore.system.dto.response.DoctorDashboardResponse;
import com.medicore.system.dto.response.EstadoCitaCountResponse;
import com.medicore.system.dto.response.HistoriaClinicaResumenResponse;
import com.medicore.system.dto.response.UserDashboardResponse;
import com.medicore.system.model.entity.Cita;
import com.medicore.system.model.entity.EstadoCita;
import com.medicore.system.model.entity.HistoriaClinica;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.repository.CitaRepository;
import com.medicore.system.repository.DisponibilidadMedicaRepository;
import com.medicore.system.repository.EspecialidadRepository;
import com.medicore.system.repository.HistoriaClinicaRepository;
import com.medicore.system.repository.MedicoRepository;
import com.medicore.system.repository.PacienteRepository;
import com.medicore.system.security.AuthenticatedUserService;
import com.medicore.system.service.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final List<EstadoCita> ESTADOS_PENDIENTES_HISTORIA = Arrays.asList(EstadoCita.CONFIRMADA, EstadoCita.COMPLETADA);
    private static final List<EstadoCita> ESTADOS_PROXIMAS_CITAS = Arrays.asList(EstadoCita.PROGRAMADA, EstadoCita.CONFIRMADA);

    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository;
    private final CitaRepository citaRepository;
    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final DisponibilidadMedicaRepository disponibilidadMedicaRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public DashboardServiceImpl(
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository,
            EspecialidadRepository especialidadRepository,
            CitaRepository citaRepository,
            HistoriaClinicaRepository historiaClinicaRepository,
            DisponibilidadMedicaRepository disponibilidadMedicaRepository,
            AuthenticatedUserService authenticatedUserService) {
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.especialidadRepository = especialidadRepository;
        this.citaRepository = citaRepository;
        this.historiaClinicaRepository = historiaClinicaRepository;
        this.disponibilidadMedicaRepository = disponibilidadMedicaRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse obtenerResumen() {
        if (authenticatedUserService.isAdmin()) {
            return adminDashboard();
        }
        if (authenticatedUserService.isDoctor()) {
            return doctorDashboard();
        }
        return userDashboard();
    }

    private DashboardResponse adminDashboard() {
        // Pendiente: hacer configurable el timezone para despliegues multi-zona.
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime ahora = LocalDateTime.now();

        AdminDashboardResponse admin = new AdminDashboardResponse();
        admin.setTotalPacientesActivos(pacienteRepository.countByActivoTrue());
        admin.setTotalMedicosActivos(medicoRepository.countByActivoTrue());
        admin.setTotalEspecialidadesActivas(especialidadRepository.countByActivoTrue());
        admin.setCitasHoy(citaRepository.countByFechaHoraBetween(inicioDia, finDia));
        admin.setHistoriasClinicasRegistradas(historiaClinicaRepository.count());
        admin.setCitasPorEstado(citasPorEstadoGlobal());
        admin.setProximasCitas(mapCitas(citaRepository.findTop5ByEstadoInAndFechaHoraGreaterThanEqualOrderByFechaHoraAsc(ESTADOS_PROXIMAS_CITAS, ahora)));
        admin.setAlertas(adminAlertas());

        DashboardResponse response = new DashboardResponse();
        response.setTipo("ADMIN");
        response.setAdmin(admin);
        return response;
    }

    private DashboardResponse doctorDashboard() {
        String medicoDocumento = authenticatedUserService.getCurrentDoctorDocumentoOrThrow();
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime ahora = LocalDateTime.now();

        long pendientesHistoria = citaRepository.countByMedicoNumeroDocumentoAndEstadoInAndHistoriaClinicaIsNull(medicoDocumento, ESTADOS_PENDIENTES_HISTORIA);

        DoctorDashboardResponse doctor = new DoctorDashboardResponse();
        doctor.setCitasHoy(citaRepository.countByMedicoNumeroDocumentoAndFechaHoraBetween(medicoDocumento, inicioDia, finDia));
        doctor.setProximasCitas(mapCitas(citaRepository.findTop5ByMedicoNumeroDocumentoAndEstadoInAndFechaHoraGreaterThanEqualOrderByFechaHoraAsc(medicoDocumento, ESTADOS_PROXIMAS_CITAS, ahora)));
        doctor.setCitasPorEstado(citasPorEstadoMedico(medicoDocumento));
        doctor.setCitasPendientesHistoria(pendientesHistoria);
        doctor.setHistoriasRecientes(mapHistorias(historiaClinicaRepository.findTop5ByMedicoNumeroDocumentoOrderByFechaRegistroDesc(medicoDocumento)));
        doctor.setAlertas(doctorAlertas(medicoDocumento, pendientesHistoria));

        DashboardResponse response = new DashboardResponse();
        response.setTipo("DOCTOR");
        response.setDoctor(doctor);
        return response;
    }

    private DashboardResponse userDashboard() {
        UserDashboardResponse user = new UserDashboardResponse();
        user.setMensaje("Bienvenido a Medicore System. Tu acceso es limitado.");
        user.setAccesosDisponibles(List.of(
                "Consulta de informacion permitida",
                "Gestion personal pendiente para futuras versiones"));

        DashboardResponse response = new DashboardResponse();
        response.setTipo("USER");
        response.setUser(user);
        return response;
    }

    private List<EstadoCitaCountResponse> citasPorEstadoGlobal() {
        List<EstadoCitaCountResponse> estados = new ArrayList<>();
        for (EstadoCita estado : EstadoCita.values()) {
            estados.add(new EstadoCitaCountResponse(estado, citaRepository.countByEstado(estado)));
        }
        return estados;
    }

    private List<EstadoCitaCountResponse> citasPorEstadoMedico(String medicoDocumento) {
        List<EstadoCitaCountResponse> estados = new ArrayList<>();
        for (EstadoCita estado : EstadoCita.values()) {
            estados.add(new EstadoCitaCountResponse(estado, citaRepository.countByMedicoNumeroDocumentoAndEstado(medicoDocumento, estado)));
        }
        return estados;
    }

    private List<AlertaDashboardResponse> adminAlertas() {
        List<AlertaDashboardResponse> alertas = new ArrayList<>();
        long pendientesHistoria = citaRepository.countByEstadoInAndHistoriaClinicaIsNull(ESTADOS_PENDIENTES_HISTORIA);
        if (pendientesHistoria > 0) {
            alertas.add(new AlertaDashboardResponse("HISTORIA_CLINICA", pendientesHistoria + " citas estan pendientes de historia clinica.", "WARNING"));
        }

        long medicosSinDisponibilidad = medicoRepository.findByActivoTrue().stream()
                .filter(medico -> !disponibilidadMedicaRepository.existsByMedicoNumeroDocumentoAndActivoTrue(medico.getNumeroDocumento()))
                .count();
        if (medicosSinDisponibilidad > 0) {
            alertas.add(new AlertaDashboardResponse("DISPONIBILIDAD", medicosSinDisponibilidad + " medicos activos no tienen disponibilidad registrada.", "INFO"));
        }
        return alertas;
    }

    private List<AlertaDashboardResponse> doctorAlertas(String medicoDocumento, long pendientesHistoria) {
        List<AlertaDashboardResponse> alertas = new ArrayList<>();
        if (pendientesHistoria > 0) {
            alertas.add(new AlertaDashboardResponse("HISTORIA_CLINICA", pendientesHistoria + " citas propias estan pendientes de historia clinica.", "WARNING"));
        }
        if (!disponibilidadMedicaRepository.existsByMedicoNumeroDocumentoAndActivoTrue(medicoDocumento)) {
            alertas.add(new AlertaDashboardResponse("DISPONIBILIDAD", "No tienes disponibilidad activa registrada.", "INFO"));
        }
        return alertas;
    }

    private List<CitaResumenResponse> mapCitas(List<Cita> citas) {
        return citas.stream().map(this::mapCita).toList();
    }

    private CitaResumenResponse mapCita(Cita cita) {
        CitaResumenResponse response = new CitaResumenResponse();
        response.setId(cita.getId());
        response.setFechaHora(cita.getFechaHora());
        response.setEstado(cita.getEstado());
        response.setDuracionMinutos(cita.getDuracionMinutos());
        response.setMotivo(cita.getMotivo());
        response.setPacienteNumeroDocumento(cita.getPaciente().getNumeroDocumento());
        response.setPacienteNombreCompleto(nombreCompleto(cita.getPaciente().getPrimerNombre(), cita.getPaciente().getPrimerApellido()));
        response.setMedicoNumeroDocumento(cita.getMedico().getNumeroDocumento());
        response.setMedicoNombreCompleto(nombreCompleto(cita.getMedico().getPrimerNombre(), cita.getMedico().getPrimerApellido()));
        return response;
    }

    private List<HistoriaClinicaResumenResponse> mapHistorias(List<HistoriaClinica> historias) {
        return historias.stream().map(this::mapHistoria).toList();
    }

    private HistoriaClinicaResumenResponse mapHistoria(HistoriaClinica historia) {
        HistoriaClinicaResumenResponse response = new HistoriaClinicaResumenResponse();
        response.setId(historia.getId());
        response.setFechaRegistro(historia.getFechaRegistro());
        response.setPacienteNumeroDocumento(historia.getPaciente().getNumeroDocumento());
        response.setPacienteNombreCompleto(nombreCompleto(historia.getPaciente().getPrimerNombre(), historia.getPaciente().getPrimerApellido()));
        response.setDiagnostico(historia.getDiagnostico());
        response.setCitaId(historia.getCita().getId());
        return response;
    }

    private String nombreCompleto(String primerNombre, String primerApellido) {
        return primerNombre + " " + primerApellido;
    }
}
