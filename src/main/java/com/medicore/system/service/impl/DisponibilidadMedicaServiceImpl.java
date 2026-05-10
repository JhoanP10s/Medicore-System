package com.medicore.system.service.impl;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medicore.system.dto.request.DisponibilidadMedicaRequest;
import com.medicore.system.dto.response.DisponibilidadMedicaResponse;
import com.medicore.system.dto.response.MessageResponse;
import com.medicore.system.exception.BadRequestException;
import com.medicore.system.exception.BusinessException;
import com.medicore.system.exception.ResourceNotFoundException;
import com.medicore.system.mapper.DisponibilidadMedicaMapper;
import com.medicore.system.model.entity.DisponibilidadMedica;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.repository.DisponibilidadMedicaRepository;
import com.medicore.system.repository.MedicoRepository;
import com.medicore.system.security.AuthenticatedUserService;
import com.medicore.system.service.DisponibilidadMedicaService;

@Service
public class DisponibilidadMedicaServiceImpl implements DisponibilidadMedicaService {

    private final DisponibilidadMedicaRepository disponibilidadRepository;
    private final MedicoRepository medicoRepository;
    private final DisponibilidadMedicaMapper disponibilidadMapper;
    private final AuthenticatedUserService authenticatedUserService;

    public DisponibilidadMedicaServiceImpl(
            DisponibilidadMedicaRepository disponibilidadRepository,
            MedicoRepository medicoRepository,
            DisponibilidadMedicaMapper disponibilidadMapper) {
        this(disponibilidadRepository, medicoRepository, disponibilidadMapper, null);
    }

    
    @Autowired
    public DisponibilidadMedicaServiceImpl(
            DisponibilidadMedicaRepository disponibilidadRepository,
            MedicoRepository medicoRepository,
            DisponibilidadMedicaMapper disponibilidadMapper,
            AuthenticatedUserService authenticatedUserService) {
        this.disponibilidadRepository = disponibilidadRepository;
        this.medicoRepository = medicoRepository;
        this.disponibilidadMapper = disponibilidadMapper;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Override
    @Transactional
    public DisponibilidadMedicaResponse crear(DisponibilidadMedicaRequest request) {
        validarGestionSoloAdmin();
        validarRequest(request);
        Medico medico = buscarMedicoActivo(request.getNumeroDocumentoMedico());
        validarRangoHorario(request.getHoraInicio(), request.getHoraFin());
        validarSolapamiento(request, null);
        return disponibilidadMapper.toResponse(disponibilidadRepository.save(disponibilidadMapper.toEntity(request, medico)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadMedicaResponse> listar() {
        if (esDoctorAutenticado()) {
            return disponibilidadRepository.findByMedicoNumeroDocumento(authenticatedUserService.getCurrentDoctorDocumentoOrThrow())
                    .stream()
                    .map(disponibilidadMapper::toResponse)
                    .toList();
        }
        return disponibilidadRepository.findAll().stream().map(disponibilidadMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DisponibilidadMedicaResponse buscarPorId(Long id) {
        DisponibilidadMedica disponibilidad = buscarDisponibilidad(id);
        validarOwnershipMedico(disponibilidad.getMedico().getNumeroDocumento());
        return disponibilidadMapper.toResponse(disponibilidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadMedicaResponse> buscarPorMedico(String medicoId) {
        validarDocumento(medicoId, "El documento del medico es obligatorio.");
        validarOwnershipMedico(medicoId);
        return disponibilidadRepository.findByMedicoNumeroDocumento(medicoId).stream().map(disponibilidadMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public DisponibilidadMedicaResponse actualizar(Long id, DisponibilidadMedicaRequest request) {
        validarGestionSoloAdmin();
        validarRequest(request);
        DisponibilidadMedica disponibilidad = buscarDisponibilidad(id);
        Medico medico = buscarMedicoActivo(request.getNumeroDocumentoMedico());
        validarRangoHorario(request.getHoraInicio(), request.getHoraFin());
        validarSolapamiento(request, id);
        disponibilidadMapper.updateEntity(disponibilidad, request, medico);
        return disponibilidadMapper.toResponse(disponibilidadRepository.save(disponibilidad));
    }

    @Override
    @Transactional
    public MessageResponse desactivar(Long id) {
        validarGestionSoloAdmin();
        DisponibilidadMedica disponibilidad = buscarDisponibilidad(id);
        disponibilidad.setActivo(false);
        disponibilidadRepository.save(disponibilidad);
        return new MessageResponse("Disponibilidad medica desactivada exitosamente");
    }

    private DisponibilidadMedica buscarDisponibilidad(Long id) {
        if (id == null) {
            throw new BadRequestException("El id de la disponibilidad es obligatorio.");
        }
        return disponibilidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe disponibilidad medica registrada"));
    }

    private Medico buscarMedicoActivo(String numeroDocumento) {
        validarDocumento(numeroDocumento, "El documento del medico es obligatorio.");
        Medico medico = medicoRepository.findByNumeroDocumento(numeroDocumento);
        if (medico == null) {
            throw new ResourceNotFoundException("No existe medico registrado");
        }
        if (Boolean.FALSE.equals(medico.getActivo())) {
            throw new BusinessException("No se puede crear agenda para medicos inactivos.");
        }
        return medico;
    }

    private void validarSolapamiento(DisponibilidadMedicaRequest request, Long id) {
        List<DisponibilidadMedica> candidatas = id == null
                ? disponibilidadRepository.findByMedicoNumeroDocumentoAndDiaSemanaAndActivoTrue(request.getNumeroDocumentoMedico(), request.getDiaSemana())
                : disponibilidadRepository.findByMedicoNumeroDocumentoAndDiaSemanaAndActivoTrueAndIdNot(request.getNumeroDocumentoMedico(), request.getDiaSemana(), id);
        boolean solapada = candidatas.stream()
                .anyMatch(disponibilidad -> haySolapamiento(request.getHoraInicio(), request.getHoraFin(), disponibilidad.getHoraInicio(), disponibilidad.getHoraFin()));
        if (solapada) {
            throw new BusinessException("Ya existe una disponibilidad activa que se solapa para ese medico y dia.");
        }
    }

    private boolean haySolapamiento(LocalTime inicioNuevo, LocalTime finNuevo, LocalTime inicioExistente, LocalTime finExistente) {
        return inicioNuevo.isBefore(finExistente) && finNuevo.isAfter(inicioExistente);
    }

    private void validarRangoHorario(LocalTime inicio, LocalTime fin) {
        if (inicio == null || fin == null) {
            throw new BadRequestException("La hora de inicio y fin son obligatorias.");
        }
        if (!inicio.isBefore(fin)) {
            throw new BadRequestException("La hora de inicio debe ser anterior a la hora de fin.");
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

    private void validarGestionSoloAdmin() {
        if (authenticatedUserService == null) {
            return;
        }
        if (!authenticatedUserService.isAdmin()) {
            throw new AccessDeniedException("Solo ADMIN puede gestionar disponibilidad medica.");
        }
    }

    private void validarRequest(DisponibilidadMedicaRequest request) {
        if (request == null) {
            throw new BadRequestException("La informacion de disponibilidad medica es obligatoria.");
        }
        if (request.getDiaSemana() == null) {
            throw new BadRequestException("El dia de la semana es obligatorio.");
        }
    }

    private void validarDocumento(String numeroDocumento, String mensaje) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new BadRequestException(mensaje);
        }
    }
}
