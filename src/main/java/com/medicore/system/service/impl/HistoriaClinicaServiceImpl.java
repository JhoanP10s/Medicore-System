package com.medicore.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medicore.system.dto.request.HistoriaClinicaRequest;
import com.medicore.system.dto.response.HistoriaClinicaResponse;
import com.medicore.system.exception.BadRequestException;
import com.medicore.system.exception.BusinessException;
import com.medicore.system.exception.ResourceNotFoundException;
import com.medicore.system.mapper.HistoriaClinicaMapper;
import com.medicore.system.model.entity.Cita;
import com.medicore.system.model.entity.EstadoCita;
import com.medicore.system.model.entity.HistoriaClinica;
import com.medicore.system.repository.CitaRepository;
import com.medicore.system.repository.HistoriaClinicaRepository;
import com.medicore.system.security.AuthenticatedUserService;
import com.medicore.system.service.HistoriaClinicaService;

@Service
public class HistoriaClinicaServiceImpl implements HistoriaClinicaService {

    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final CitaRepository citaRepository;
    private final HistoriaClinicaMapper historiaClinicaMapper;
    private final AuthenticatedUserService authenticatedUserService;

    public HistoriaClinicaServiceImpl(
            HistoriaClinicaRepository historiaClinicaRepository,
            CitaRepository citaRepository,
            HistoriaClinicaMapper historiaClinicaMapper) {
        this(historiaClinicaRepository, citaRepository, historiaClinicaMapper, null);
    }


    
    @Autowired
    public HistoriaClinicaServiceImpl(
            HistoriaClinicaRepository historiaClinicaRepository,
            CitaRepository citaRepository,
            HistoriaClinicaMapper historiaClinicaMapper,
            AuthenticatedUserService authenticatedUserService) {
        this.historiaClinicaRepository = historiaClinicaRepository;
        this.citaRepository = citaRepository;
        this.historiaClinicaMapper = historiaClinicaMapper;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Override
    @Transactional
    public HistoriaClinicaResponse crearHistoriaClinica(HistoriaClinicaRequest request) {
        validarRequest(request);
        Cita cita = buscarCita(request.getCitaId());
        validarOwnershipCita(cita);
        validarCitaParaHistoriaClinica(cita);

        if (historiaClinicaRepository.existsByCitaId(cita.getId())) {
            throw new BusinessException("Ya existe una historia clinica para esta cita.");
        }

        HistoriaClinica historiaClinica = historiaClinicaMapper.toEntity(request, cita);
        if (EstadoCita.CONFIRMADA.equals(cita.getEstado())) {
            cita.setEstado(EstadoCita.COMPLETADA);
        }

        HistoriaClinica guardada = historiaClinicaRepository.save(historiaClinica);
        cita.setHistoriaClinica(guardada);
        return historiaClinicaMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoriaClinicaResponse> listarHistoriasClinicas() {
        if (esDoctorAutenticado()) {
            return historiaClinicaRepository.findByMedicoNumeroDocumento(authenticatedUserService.getCurrentDoctorDocumentoOrThrow())
                    .stream()
                    .map(historiaClinicaMapper::toResponse)
                    .toList();
        }
        return historiaClinicaRepository.findAll().stream().map(historiaClinicaMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HistoriaClinicaResponse buscarPorId(Long id) {
        HistoriaClinica historiaClinica = buscarHistoriaClinica(id);
        validarOwnershipHistoriaClinica(historiaClinica);
        return historiaClinicaMapper.toResponse(historiaClinica);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoriaClinicaResponse> buscarPorPaciente(String numeroDocumento) {
        validarNumeroDocumento(numeroDocumento, "El numero de documento del paciente es obligatorio.");
        String doctorDocumento = esDoctorAutenticado() ? authenticatedUserService.getCurrentDoctorDocumentoOrThrow() : null;
        return historiaClinicaRepository.findByPacienteNumeroDocumento(numeroDocumento)
                .stream()
                .filter(historiaClinica -> doctorDocumento == null || historiaClinica.getMedico().getNumeroDocumento().equals(doctorDocumento))
                .map(historiaClinicaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoriaClinicaResponse> buscarPorMedico(String numeroDocumento) {
        validarNumeroDocumento(numeroDocumento, "El numero de documento del medico es obligatorio.");
        validarOwnershipMedico(numeroDocumento);
        return historiaClinicaRepository.findByMedicoNumeroDocumento(numeroDocumento)
                .stream()
                .map(historiaClinicaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HistoriaClinicaResponse buscarPorCita(Long citaId) {
        validarId(citaId, "El id de la cita es obligatorio.");
        HistoriaClinica historiaClinica = historiaClinicaRepository.findByCitaId(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe historia clinica registrada para esta cita"));
        validarOwnershipHistoriaClinica(historiaClinica);
        return historiaClinicaMapper.toResponse(historiaClinica);
    }

    @Override
    @Transactional
    public HistoriaClinicaResponse actualizarHistoriaClinica(Long id, HistoriaClinicaRequest request) {
        validarId(id, "El id de la historia clinica es obligatorio.");
        validarRequest(request);

        HistoriaClinica historiaClinica = buscarHistoriaClinica(id);
        validarOwnershipHistoriaClinica(historiaClinica);
        if (!historiaClinica.getCita().getId().equals(request.getCitaId())) {
            throw new BusinessException("No se puede cambiar la cita asociada a una historia clinica.");
        }

        historiaClinicaMapper.updateEntity(historiaClinica, request);
        return historiaClinicaMapper.toResponse(historiaClinicaRepository.save(historiaClinica));
    }

    private HistoriaClinica buscarHistoriaClinica(Long id) {
        validarId(id, "El id de la historia clinica es obligatorio.");
        return historiaClinicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe historia clinica registrada"));
    }

    private Cita buscarCita(Long id) {
        validarId(id, "El id de la cita es obligatorio.");
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe cita registrada"));
    }

    private void validarCitaParaHistoriaClinica(Cita cita) {
        if (EstadoCita.CANCELADA.equals(cita.getEstado())) {
            throw new BusinessException("No se puede crear historia clinica para una cita cancelada.");
        }
        if (EstadoCita.PROGRAMADA.equals(cita.getEstado())) {
            throw new BusinessException("No se puede crear historia clinica para una cita sin confirmar.");
        }
        if (!EstadoCita.CONFIRMADA.equals(cita.getEstado()) && !EstadoCita.COMPLETADA.equals(cita.getEstado())) {
            throw new BusinessException("El estado de la cita no permite crear historia clinica.");
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

    private void validarOwnershipHistoriaClinica(HistoriaClinica historiaClinica) {
        if (authenticatedUserService != null) {
            authenticatedUserService.validateDoctorOwnsHistoriaClinica(historiaClinica);
        }
    }

    private void validarRequest(HistoriaClinicaRequest request) {
        if (request == null) {
            throw new BadRequestException("La informacion de la historia clinica es obligatoria.");
        }
    }

    private void validarId(Long id, String mensaje) {
        if (id == null) {
            throw new BadRequestException(mensaje);
        }
    }

    private void validarNumeroDocumento(String numeroDocumento, String mensaje) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new BadRequestException(mensaje);
        }
    }
}
