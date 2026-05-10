package com.medicore.system.service.impl;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medicore.system.dto.request.CitaRequest;
import com.medicore.system.dto.response.CitaResponse;
import com.medicore.system.dto.response.MessageResponse;
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
import com.medicore.system.security.AuthenticatedUserService;
import com.medicore.system.service.CitaService;

@Service
public class CitaServiceImpl implements CitaService {

    private static final int MAXIMA_DURACION_MINUTOS = 240;
    private static final int DURACION_LEGACY_MINUTOS = 30;
    private static final Set<EstadoCita> ESTADOS_AGENDA_ACTIVA = EnumSet.of(
            EstadoCita.PROGRAMADA,
            EstadoCita.CONFIRMADA,
            EstadoCita.COMPLETADA);

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final DisponibilidadMedicaRepository disponibilidadRepository;
    private final BloqueoAgendaRepository bloqueoRepository;
    private final CitaMapper citaMapper;
    private final AuthenticatedUserService authenticatedUserService;

    public CitaServiceImpl(
            CitaRepository citaRepository,
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository,
            DisponibilidadMedicaRepository disponibilidadRepository,
            BloqueoAgendaRepository bloqueoRepository,
            CitaMapper citaMapper) {
        this(citaRepository, pacienteRepository, medicoRepository, disponibilidadRepository, bloqueoRepository, citaMapper, null);
    }



    
    @Autowired
    public CitaServiceImpl(
            CitaRepository citaRepository,
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository,
            DisponibilidadMedicaRepository disponibilidadRepository,
            BloqueoAgendaRepository bloqueoRepository,
            CitaMapper citaMapper,
            AuthenticatedUserService authenticatedUserService) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.bloqueoRepository = bloqueoRepository;
        this.citaMapper = citaMapper;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listarCitas() {
        if (esDoctorAutenticado()) {
            return citaRepository.findByMedicoNumeroDocumento(authenticatedUserService.getCurrentDoctorDocumentoOrThrow())
                    .stream()
                    .map(citaMapper::toResponse)
                    .toList();
        }
        return citaRepository.findAll().stream().map(citaMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse verCita(Long id) {
        Cita cita = buscarCita(id);
        validarOwnershipCita(cita);
        return citaMapper.toResponse(cita);
    }

    @Override
    @Transactional
    public CitaResponse crearCita(CitaRequest request) {
        validarRequest(request);
        validarOwnershipMedico(request.getNumeroDocumentoMedico());
        Paciente paciente = buscarPaciente(request.getNumeroDocumentoPaciente());
        Medico medico = buscarMedico(request.getNumeroDocumentoMedico());
        validarCitaNuevaOEditable(request, paciente, medico, null);
        Cita cita = citaRepository.save(citaMapper.toEntity(request, paciente, medico));
        return citaMapper.toResponse(cita);
    }

    @Override
    @Transactional
    public CitaResponse editarCita(Long id, CitaRequest request) {
        validarId(id);
        validarRequest(request);
        Cita cita = buscarCita(id);
        validarOwnershipCita(cita);
        validarOwnershipMedico(request.getNumeroDocumentoMedico());

        if (EstadoCita.COMPLETADA.equals(cita.getEstado())) {
            throw new BusinessException("No se puede modificar una cita completada.");
        }
        if (EstadoCita.CANCELADA.equals(cita.getEstado())) {
            throw new BusinessException("No se puede modificar una cita cancelada.");
        }

        Paciente paciente = buscarPaciente(request.getNumeroDocumentoPaciente());
        Medico medico = buscarMedico(request.getNumeroDocumentoMedico());
        validarCitaNuevaOEditable(request, paciente, medico, id);

        EstadoCita estadoActual = cita.getEstado();
        citaMapper.updateEntity(cita, request, paciente, medico);
        cita.setEstado(estadoActual);
        return citaMapper.toResponse(citaRepository.save(cita));
    }

    @Override
    @Transactional
    public MessageResponse eliminarCita(Long id) {
        cancelarCita(id);
        return new MessageResponse("Cita cancelada exitosamente");
    }

    @Override
    @Transactional
    public MessageResponse cancelarCita(Long id) {
        Cita cita = buscarCita(id);
        validarOwnershipCita(cita);
        if (EstadoCita.CANCELADA.equals(cita.getEstado())) {
            return new MessageResponse("La cita ya se encontraba cancelada");
        }
        if (EstadoCita.COMPLETADA.equals(cita.getEstado())) {
            throw new BusinessException("No se puede cancelar una cita completada.");
        }
        cita.setEstado(EstadoCita.CANCELADA);
        citaRepository.save(cita);
        return new MessageResponse("Cita cancelada exitosamente");
    }

    @Override
    @Transactional
    public CitaResponse cambiarEstado(Long id, EstadoCita estado) {
        if (estado == null) {
            throw new BadRequestException("El estado de la cita es obligatorio.");
        }
        Cita cita = buscarCita(id);
        validarOwnershipCita(cita);
        validarTransicionEstado(cita.getEstado(), estado);
        cita.setEstado(estado);
        return citaMapper.toResponse(citaRepository.save(cita));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listarCitasPorPaciente(String numeroDocumento) {
        validarNumeroDocumento(numeroDocumento, "El numero de documento del paciente es obligatorio.");
        String doctorDocumento = esDoctorAutenticado() ? authenticatedUserService.getCurrentDoctorDocumentoOrThrow() : null;
        return citaRepository.findByPacienteNumeroDocumento(numeroDocumento)
                .stream()
                .filter(cita -> doctorDocumento == null || cita.getMedico().getNumeroDocumento().equals(doctorDocumento))
                .map(citaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listarCitasPorMedico(String numeroDocumento) {
        validarNumeroDocumento(numeroDocumento, "El numero de documento del medico es obligatorio.");
        validarOwnershipMedico(numeroDocumento);
        return citaRepository.findByMedicoNumeroDocumento(numeroDocumento).stream().map(citaMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listarCitasPorEstado(EstadoCita estado) {
        if (estado == null) {
            throw new BadRequestException("El estado de la cita es obligatorio.");
        }
        if (esDoctorAutenticado()) {
            return citaRepository.findByMedicoNumeroDocumentoAndEstado(authenticatedUserService.getCurrentDoctorDocumentoOrThrow(), estado)
                    .stream()
                    .map(citaMapper::toResponse)
                    .toList();
        }
        return citaRepository.findByEstado(estado).stream().map(citaMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listarCitasPorRango(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) {
            throw new BadRequestException("El inicio y fin del rango son obligatorios.");
        }
        if (!inicio.isBefore(fin)) {
            throw new BadRequestException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
        if (esDoctorAutenticado()) {
            return citaRepository.findByMedicoNumeroDocumentoAndFechaHoraBetween(authenticatedUserService.getCurrentDoctorDocumentoOrThrow(), inicio, fin)
                    .stream()
                    .map(citaMapper::toResponse)
                    .toList();
        }
        return citaRepository.findByFechaHoraBetween(inicio, fin).stream().map(citaMapper::toResponse).toList();
    }

    private Cita buscarCita(Long id) {
        validarId(id);
        return citaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe cita registrada"));
    }

    private Paciente buscarPaciente(String numeroDocumento) {
        validarNumeroDocumento(numeroDocumento, "El numero de documento del paciente es obligatorio.");
        Paciente paciente = pacienteRepository.findByNumeroDocumento(numeroDocumento);
        if (paciente == null) {
            throw new ResourceNotFoundException("No existe paciente registrado");
        }
        return paciente;
    }

    private Medico buscarMedico(String numeroDocumento) {
        validarNumeroDocumento(numeroDocumento, "El numero de documento del medico es obligatorio.");
        Medico medico = medicoRepository.findByNumeroDocumento(numeroDocumento);
        if (medico == null) {
            throw new ResourceNotFoundException("No existe medico registrado");
        }
        return medico;
    }

    private void validarCitaNuevaOEditable(CitaRequest request, Paciente paciente, Medico medico, Long citaId) {
        if (request.getFechaHora() == null) {
            throw new BadRequestException("La fecha y hora de la cita es obligatoria.");
        }
        if (request.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("No se permiten citas en fechas pasadas.");
        }
        if (request.getDuracionMinutos() == null) {
            throw new BadRequestException("La duracion de la cita es obligatoria.");
        }
        if (request.getDuracionMinutos() < 15 || request.getDuracionMinutos() > MAXIMA_DURACION_MINUTOS) {
            throw new BadRequestException("La duracion de la cita debe estar entre 15 y 240 minutos.");
        }
        if (Boolean.FALSE.equals(paciente.getActivo())) {
            throw new BusinessException("No se pueden crear citas con pacientes inactivos.");
        }
        if (Boolean.FALSE.equals(medico.getActivo())) {
            throw new BusinessException("No se pueden crear citas con medicos inactivos.");
        }
        validarDentroDeDisponibilidad(medico.getNumeroDocumento(), request.getFechaHora(), request.getDuracionMinutos());
        validarSolapamientoCitas(medico.getNumeroDocumento(), request.getFechaHora(), request.getDuracionMinutos(), citaId);
        validarBloqueos(medico.getNumeroDocumento(), request.getFechaHora(), request.getDuracionMinutos());
    }

    private void validarDentroDeDisponibilidad(String medicoDocumento, LocalDateTime fechaHora, Integer duracionMinutos) {
        LocalDateTime fin = fechaHora.plusMinutes(duracionMinutos);
        List<DisponibilidadMedica> disponibilidades = disponibilidadRepository.findByMedicoNumeroDocumentoAndDiaSemanaAndActivoTrue(
                medicoDocumento,
                DiaSemana.from(fechaHora.getDayOfWeek()));
        boolean dentro = disponibilidades.stream().anyMatch(disponibilidad ->
                !fechaHora.toLocalTime().isBefore(disponibilidad.getHoraInicio())
                        && !fin.toLocalTime().isAfter(disponibilidad.getHoraFin())
                        && fechaHora.toLocalDate().equals(fin.toLocalDate()));
        if (!dentro) {
            throw new BusinessException("La cita debe estar completamente dentro de una disponibilidad activa del medico.");
        }
    }

    private void validarSolapamientoCitas(String medicoDocumento, LocalDateTime fechaHora, Integer duracionMinutos, Long citaId) {
        LocalDateTime fin = fechaHora.plusMinutes(duracionMinutos);
        LocalDateTime inicioBusqueda = fechaHora.minusMinutes(MAXIMA_DURACION_MINUTOS);
        List<Cita> candidatas = citaRepository.findByMedicoNumeroDocumentoAndEstadoInAndFechaHoraBetween(
                medicoDocumento,
                ESTADOS_AGENDA_ACTIVA,
                inicioBusqueda,
                fin);
        boolean existeSolapamiento = candidatas.stream()
                .filter(cita -> citaId == null || !cita.getId().equals(citaId))
                .anyMatch(cita -> haySolapamiento(fechaHora, fin, cita.getFechaHora(), calcularFinCita(cita)));
        if (existeSolapamiento) {
            throw new BusinessException("El medico ya tiene una cita que se solapa con esa fecha y duracion.");
        }
    }

    private void validarBloqueos(String medicoDocumento, LocalDateTime fechaHora, Integer duracionMinutos) {
        LocalDateTime fin = fechaHora.plusMinutes(duracionMinutos);
        boolean bloqueado = bloqueoRepository.findByMedicoNumeroDocumentoAndActivoTrueAndFechaInicioLessThanAndFechaFinGreaterThan(
                medicoDocumento,
                fin,
                fechaHora)
                .stream()
                .anyMatch(bloqueo -> haySolapamiento(fechaHora, fin, bloqueo.getFechaInicio(), bloqueo.getFechaFin()));
        if (bloqueado) {
            throw new BusinessException("La cita se solapa con un bloqueo activo de la agenda del medico.");
        }
    }

    private boolean haySolapamiento(LocalDateTime inicioNueva, LocalDateTime finNueva, LocalDateTime inicioExistente, LocalDateTime finExistente) {
        return inicioNueva.isBefore(finExistente) && finNueva.isAfter(inicioExistente);
    }

    private LocalDateTime calcularFinCita(Cita cita) {
        int duracion = cita.getDuracionMinutos() != null ? cita.getDuracionMinutos() : DURACION_LEGACY_MINUTOS;
        return cita.getFechaHora().plusMinutes(duracion);
    }

    private void validarTransicionEstado(EstadoCita estadoActual, EstadoCita estadoNuevo) {
        if (estadoActual.equals(estadoNuevo)) {
            return;
        }
        if (EstadoCita.CANCELADA.equals(estadoActual)) {
            throw new BusinessException("No se puede cambiar el estado de una cita cancelada.");
        }
        if (EstadoCita.COMPLETADA.equals(estadoActual)) {
            throw new BusinessException("No se puede cambiar el estado de una cita completada.");
        }
        boolean transicionValida = EstadoCita.PROGRAMADA.equals(estadoActual)
                && (EstadoCita.CONFIRMADA.equals(estadoNuevo) || EstadoCita.CANCELADA.equals(estadoNuevo))
                || EstadoCita.CONFIRMADA.equals(estadoActual)
                        && (EstadoCita.COMPLETADA.equals(estadoNuevo) || EstadoCita.CANCELADA.equals(estadoNuevo));
        if (!transicionValida) {
            throw new BusinessException("Transicion de estado de cita no permitida.");
        }
    }

    private boolean esDoctorAutenticado() {
        return authenticatedUserService != null && authenticatedUserService.isDoctor();
    }

    private void validarOwnershipMedico(String medicoDocumento) {
        if (authenticatedUserService != null) {
            authenticatedUserService.validateDoctorOwnsMedico(medicoDocumento);
        }
    }

    private void validarOwnershipCita(Cita cita) {
        if (authenticatedUserService != null) {
            authenticatedUserService.validateDoctorOwnsCita(cita);
        }
    }

    private void validarRequest(CitaRequest request) {
        if (request == null) {
            throw new BadRequestException("La informacion de la cita es obligatoria.");
        }
    }

    private void validarId(Long id) {
        if (id == null) {
            throw new BadRequestException("El id de la cita es obligatorio.");
        }
    }

    private void validarNumeroDocumento(String numeroDocumento, String mensaje) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new BadRequestException(mensaje);
        }
    }
}
