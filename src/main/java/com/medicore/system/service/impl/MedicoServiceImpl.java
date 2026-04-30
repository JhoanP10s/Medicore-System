package com.medicore.system.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medicore.system.dto.request.MedicoRequest;
import com.medicore.system.dto.response.MedicoResponse;
import com.medicore.system.dto.response.MessageResponse;
import com.medicore.system.exception.BadRequestException;
import com.medicore.system.exception.BusinessException;
import com.medicore.system.exception.ResourceNotFoundException;
import com.medicore.system.mapper.MedicoMapper;
import com.medicore.system.model.entity.Especialidad;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.repository.CitaRepository;
import com.medicore.system.repository.EspecialidadRepository;
import com.medicore.system.repository.MedicoRepository;
import com.medicore.system.service.MedicoService;

@Service
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository;
    private final CitaRepository citaRepository;
    private final MedicoMapper medicoMapper;

    public MedicoServiceImpl(
            MedicoRepository medicoRepository,
            EspecialidadRepository especialidadRepository,
            CitaRepository citaRepository,
            MedicoMapper medicoMapper) {
        this.medicoRepository = medicoRepository;
        this.especialidadRepository = especialidadRepository;
        this.citaRepository = citaRepository;
        this.medicoMapper = medicoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponse> listarMedicos() {
        return medicoRepository.findAll()
                .stream()
                .map(medicoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse verMedico(String numeroDocumento) {
        return medicoMapper.toResponse(buscarMedico(numeroDocumento, "No existe medico registrado"));
    }

    @Override
    @Transactional
    public MessageResponse crearMedico(MedicoRequest request) {
        validarRequest(request);

        if (medicoRepository.findByNumeroDocumentoAndTipoDocumento(
                request.getNumeroDocumento(), request.getTipoDocumento()) != null) {
            throw new BusinessException("Ya existe un medico con el mismo tipo y numero de documento.");
        }

        if (medicoRepository.findByNumeroDocumento(request.getNumeroDocumento()) != null) {
            throw new BusinessException("Ya existe un medico con el mismo numero de documento.");
        }

        Especialidad especialidad = buscarEspecialidad(request.getEspecialidadId());
        medicoRepository.save(medicoMapper.toEntity(request, especialidad));
        return new MessageResponse("Medico creado exitosamente");
    }

    @Override
    @Transactional
    public MessageResponse editarMedico(String numeroDocumento, MedicoRequest request) {
        validarNumeroDocumento(numeroDocumento, "El numero de documento del medico es obligatorio.");
        validarRequest(request);

        Medico medico = buscarMedico(numeroDocumento, "Medico no existe");
        Especialidad especialidad = buscarEspecialidad(request.getEspecialidadId());
        medicoMapper.updateEntity(medico, request, especialidad);
        medicoRepository.save(medico);
        return new MessageResponse("Medico editado exitosamente");
    }

    @Override
    @Transactional
    public MessageResponse eliminarMedico(String numeroDocumento) {
        validarNumeroDocumento(numeroDocumento, "El numero de documento del medico es obligatorio.");
        Medico medico = buscarMedico(numeroDocumento, "Error, no se puede eliminar Medico.");

        if (citaRepository.existsByMedicoNumeroDocumento(numeroDocumento)) {
            medico.setActivo(false);
            medicoRepository.save(medico);
            return new MessageResponse("Medico desactivado exitosamente porque tiene citas asociadas");
        }

        medicoRepository.delete(medico);
        return new MessageResponse("Medico eliminado exitosamente.");
    }

    private Medico buscarMedico(String numeroDocumento, String mensajeNoEncontrado) {
        validarNumeroDocumento(numeroDocumento, "El numero de documento del medico es obligatorio.");
        Medico medico = medicoRepository.findByNumeroDocumento(numeroDocumento);

        if (medico == null) {
            throw new ResourceNotFoundException(mensajeNoEncontrado);
        }

        return medico;
    }

    private void validarRequest(MedicoRequest request) {
        if (request == null) {
            throw new BadRequestException("La informacion del medico es obligatoria.");
        }
    }

    private void validarNumeroDocumento(String numeroDocumento, String mensaje) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new BadRequestException(mensaje);
        }
    }

    private Especialidad buscarEspecialidad(Long especialidadId) {
        if (especialidadId == null) {
            return null;
        }

        return especialidadRepository.findById(especialidadId)
                .map(especialidad -> {
                    if (Boolean.FALSE.equals(especialidad.getActivo())) {
                        throw new BusinessException("No se puede asignar una especialidad inactiva al medico.");
                    }

                    return especialidad;
                })
                .orElseThrow(() -> new ResourceNotFoundException("No existe especialidad registrada"));
    }
}
