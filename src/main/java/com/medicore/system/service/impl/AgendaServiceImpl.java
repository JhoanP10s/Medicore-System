package com.medicore.system.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.medicore.system.security.AuthenticatedUserService;
import com.medicore.system.service.AgendaService;

@Service
public class AgendaServiceImpl implements AgendaService {

    private static final Set<EstadoCita> ESTADOS_OCUPADOS = EnumSet.of(EstadoCita.PROGRAMADA, EstadoCita.CONFIRMADA, EstadoCita.COMPLETADA);
    private static final int DURACION_LEGACY_MINUTOS = 30;

    private final MedicoRepository medicoRepository;
    private final DisponibilidadMedicaRepository disponibilidadRepository;
    private final CitaRepository citaRepository;
    private final BloqueoAgendaRepository bloqueoRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public AgendaServiceImpl(
            MedicoRepository medicoRepository,
            DisponibilidadMedicaRepository disponibilidadRepository,
            CitaRepository citaRepository,
            BloqueoAgendaRepository bloqueoRepository) {
        this(medicoRepository, disponibilidadRepository, citaRepository, bloqueoRepository, null);
    }

    
    @Autowired
    public AgendaServiceImpl(
            MedicoRepository medicoRepository,
            DisponibilidadMedicaRepository disponibilidadRepository,
            CitaRepository citaRepository,
            BloqueoAgendaRepository bloqueoRepository,
            AuthenticatedUserService authenticatedUserService) {
        this.medicoRepository = medicoRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.citaRepository = citaRepository;
        this.bloqueoRepository = bloqueoRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioDisponibleResponse> buscarHorariosDisponibles(String medicoId, LocalDate fecha, Integer duracionMinutos) {
        if (authenticatedUserService != null) {
            authenticatedUserService.validateDoctorOwnsMedico(medicoId);
        }
        validarMedicoActivo(medicoId);
        if (fecha == null) {
            throw new BadRequestException("La fecha es obligatoria.");
        }
        validarDuracion(duracionMinutos);

        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.plusDays(1).atStartOfDay();
        List<Cita> citas = citaRepository.findByMedicoNumeroDocumentoAndEstadoInAndFechaHoraBetween(medicoId, ESTADOS_OCUPADOS, inicioDia, finDia);
        List<BloqueoAgenda> bloqueos = bloqueoRepository.findByMedicoNumeroDocumentoAndActivoTrueAndFechaInicioLessThanAndFechaFinGreaterThan(medicoId, finDia, inicioDia);

        return disponibilidadRepository.findByMedicoNumeroDocumentoAndDiaSemanaAndActivoTrue(medicoId, DiaSemana.from(fecha.getDayOfWeek()))
                .stream()
                .flatMap(disponibilidad -> horariosDeDisponibilidad(fecha, disponibilidad, duracionMinutos, citas, bloqueos).stream())
                .sorted(Comparator.comparing(HorarioDisponibleResponse::getInicio))
                .toList();
    }

    private List<HorarioDisponibleResponse> horariosDeDisponibilidad(
            LocalDate fecha,
            DisponibilidadMedica disponibilidad,
            Integer duracionMinutos,
            List<Cita> citas,
            List<BloqueoAgenda> bloqueos) {
        java.util.ArrayList<HorarioDisponibleResponse> horarios = new java.util.ArrayList<>();
        LocalDateTime cursor = fecha.atTime(disponibilidad.getHoraInicio());
        LocalDateTime finDisponibilidad = fecha.atTime(disponibilidad.getHoraFin());

        while (!cursor.plusMinutes(duracionMinutos).isAfter(finDisponibilidad)) {
            LocalDateTime fin = cursor.plusMinutes(duracionMinutos);
            if (!estaOcupado(cursor, fin, citas, bloqueos)) {
                horarios.add(new HorarioDisponibleResponse(cursor, fin));
            }
            cursor = fin;
        }
        return horarios;
    }

    private boolean estaOcupado(LocalDateTime inicio, LocalDateTime fin, List<Cita> citas, List<BloqueoAgenda> bloqueos) {
        boolean ocupadoPorCita = citas.stream()
                .anyMatch(cita -> haySolapamiento(inicio, fin, cita.getFechaHora(), calcularFinCita(cita)));
        boolean ocupadoPorBloqueo = bloqueos.stream()
                .anyMatch(bloqueo -> haySolapamiento(inicio, fin, bloqueo.getFechaInicio(), bloqueo.getFechaFin()));
        return ocupadoPorCita || ocupadoPorBloqueo;
    }

    private boolean haySolapamiento(LocalDateTime inicioNuevo, LocalDateTime finNuevo, LocalDateTime inicioExistente, LocalDateTime finExistente) {
        return inicioNuevo.isBefore(finExistente) && finNuevo.isAfter(inicioExistente);
    }

    private LocalDateTime calcularFinCita(Cita cita) {
        int duracion = cita.getDuracionMinutos() != null ? cita.getDuracionMinutos() : DURACION_LEGACY_MINUTOS;
        return cita.getFechaHora().plusMinutes(duracion);
    }

    private void validarDuracion(Integer duracionMinutos) {
        if (duracionMinutos == null) {
            throw new BadRequestException("La duracion es obligatoria.");
        }
        if (duracionMinutos < 15 || duracionMinutos > 240) {
            throw new BadRequestException("La duracion debe estar entre 15 y 240 minutos.");
        }
    }

    private void validarMedicoActivo(String medicoId) {
        if (medicoId == null || medicoId.isBlank()) {
            throw new BadRequestException("El documento del medico es obligatorio.");
        }
        Medico medico = medicoRepository.findByNumeroDocumento(medicoId);
        if (medico == null) {
            throw new ResourceNotFoundException("No existe medico registrado");
        }
        if (Boolean.FALSE.equals(medico.getActivo())) {
            throw new BusinessException("No se puede consultar agenda para medicos inactivos.");
        }
    }
}
