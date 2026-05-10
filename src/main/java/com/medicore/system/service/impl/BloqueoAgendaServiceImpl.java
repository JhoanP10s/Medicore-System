package com.medicore.system.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medicore.system.dto.request.BloqueoAgendaRequest;
import com.medicore.system.dto.response.BloqueoAgendaResponse;
import com.medicore.system.dto.response.MessageResponse;
import com.medicore.system.exception.BadRequestException;
import com.medicore.system.exception.BusinessException;
import com.medicore.system.exception.ResourceNotFoundException;
import com.medicore.system.mapper.BloqueoAgendaMapper;
import com.medicore.system.model.entity.BloqueoAgenda;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.repository.BloqueoAgendaRepository;
import com.medicore.system.repository.MedicoRepository;
import com.medicore.system.security.AuthenticatedUserService;
import com.medicore.system.service.BloqueoAgendaService;

@Service
public class BloqueoAgendaServiceImpl implements BloqueoAgendaService {

    private final BloqueoAgendaRepository bloqueoRepository;
    private final MedicoRepository medicoRepository;
    private final BloqueoAgendaMapper bloqueoMapper;
    private final AuthenticatedUserService authenticatedUserService;

    public BloqueoAgendaServiceImpl(BloqueoAgendaRepository bloqueoRepository, MedicoRepository medicoRepository, BloqueoAgendaMapper bloqueoMapper) {
        this(bloqueoRepository, medicoRepository, bloqueoMapper, null);
    }


    
    @Autowired
    public BloqueoAgendaServiceImpl(
            BloqueoAgendaRepository bloqueoRepository,
            MedicoRepository medicoRepository,
            BloqueoAgendaMapper bloqueoMapper,
            AuthenticatedUserService authenticatedUserService) {
        this.bloqueoRepository = bloqueoRepository;
        this.medicoRepository = medicoRepository;
        this.bloqueoMapper = bloqueoMapper;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Override
    @Transactional
    public BloqueoAgendaResponse crear(BloqueoAgendaRequest request) {
        validarGestionSoloAdmin();
        validarRequest(request);
        Medico medico = buscarMedicoActivo(request.getNumeroDocumentoMedico());
        validarRango(request.getFechaInicio(), request.getFechaFin());
        return bloqueoMapper.toResponse(bloqueoRepository.save(bloqueoMapper.toEntity(request, medico)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloqueoAgendaResponse> listar() {
        if (esDoctorAutenticado()) {
            return bloqueoRepository.findByMedicoNumeroDocumento(authenticatedUserService.getCurrentDoctorDocumentoOrThrow())
                    .stream()
                    .map(bloqueoMapper::toResponse)
                    .toList();
        }
        return bloqueoRepository.findAll().stream().map(bloqueoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BloqueoAgendaResponse buscarPorId(Long id) {
        BloqueoAgenda bloqueo = buscarBloqueo(id);
        validarOwnershipMedico(bloqueo.getMedico().getNumeroDocumento());
        return bloqueoMapper.toResponse(bloqueo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloqueoAgendaResponse> buscarPorMedico(String medicoId) {
        validarDocumento(medicoId, "El documento del medico es obligatorio.");
        validarOwnershipMedico(medicoId);
        return bloqueoRepository.findByMedicoNumeroDocumento(medicoId).stream().map(bloqueoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloqueoAgendaResponse> buscarPorRango(LocalDateTime inicio, LocalDateTime fin) {
        validarRango(inicio, fin);
        if (esDoctorAutenticado()) {
            return bloqueoRepository.findByMedicoNumeroDocumentoAndFechaInicioLessThanAndFechaFinGreaterThan(authenticatedUserService.getCurrentDoctorDocumentoOrThrow(), fin, inicio)
                    .stream()
                    .map(bloqueoMapper::toResponse)
                    .toList();
        }
        return bloqueoRepository.findByFechaInicioLessThanAndFechaFinGreaterThan(fin, inicio)
                .stream()
                .map(bloqueoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloqueoAgendaResponse> buscarActivosPorRango(LocalDateTime inicio, LocalDateTime fin) {
        validarRango(inicio, fin);
        if (esDoctorAutenticado()) {
            return bloqueoRepository.findByMedicoNumeroDocumentoAndActivoTrueAndFechaInicioLessThanAndFechaFinGreaterThan(authenticatedUserService.getCurrentDoctorDocumentoOrThrow(), fin, inicio)
                    .stream()
                    .map(bloqueoMapper::toResponse)
                    .toList();
        }
        return bloqueoRepository.findByActivoTrueAndFechaInicioLessThanAndFechaFinGreaterThan(fin, inicio)
                .stream()
                .map(bloqueoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public BloqueoAgendaResponse actualizar(Long id, BloqueoAgendaRequest request) {
        validarGestionSoloAdmin();
        validarRequest(request);
        BloqueoAgenda bloqueo = buscarBloqueo(id);
        Medico medico = buscarMedicoActivo(request.getNumeroDocumentoMedico());
        validarRango(request.getFechaInicio(), request.getFechaFin());
        bloqueoMapper.updateEntity(bloqueo, request, medico);
        return bloqueoMapper.toResponse(bloqueoRepository.save(bloqueo));
    }

    @Override
    @Transactional
    public MessageResponse desactivar(Long id) {
        validarGestionSoloAdmin();
        BloqueoAgenda bloqueo = buscarBloqueo(id);
        bloqueo.setActivo(false);
        bloqueoRepository.save(bloqueo);
        return new MessageResponse("Bloqueo de agenda desactivado exitosamente");
    }

    private BloqueoAgenda buscarBloqueo(Long id) {
        if (id == null) {
            throw new BadRequestException("El id del bloqueo es obligatorio.");
        }
        return bloqueoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe bloqueo de agenda registrado"));
    }

    private Medico buscarMedicoActivo(String numeroDocumento) {
        validarDocumento(numeroDocumento, "El documento del medico es obligatorio.");
        Medico medico = medicoRepository.findByNumeroDocumento(numeroDocumento);
        if (medico == null) {
            throw new ResourceNotFoundException("No existe medico registrado");
        }
        if (Boolean.FALSE.equals(medico.getActivo())) {
            throw new BusinessException("No se puede crear bloqueo para medicos inactivos.");
        }
        return medico;
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
            throw new AccessDeniedException("Solo ADMIN puede gestionar bloqueos de agenda.");
        }
    }

    private void validarRequest(BloqueoAgendaRequest request) {
        if (request == null) {
            throw new BadRequestException("La informacion del bloqueo de agenda es obligatoria.");
        }
    }

    private void validarRango(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) {
            throw new BadRequestException("La fecha de inicio y fin son obligatorias.");
        }
        if (!inicio.isBefore(fin)) {
            throw new BadRequestException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
    }

    private void validarDocumento(String numeroDocumento, String mensaje) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new BadRequestException(mensaje);
        }
    }
}
