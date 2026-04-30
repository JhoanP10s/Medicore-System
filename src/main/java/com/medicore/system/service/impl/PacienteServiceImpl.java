package com.medicore.system.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medicore.system.dto.request.PacienteRequest;
import com.medicore.system.dto.response.MessageResponse;
import com.medicore.system.dto.response.PacienteResponse;
import com.medicore.system.exception.BadRequestException;
import com.medicore.system.exception.BusinessException;
import com.medicore.system.exception.ResourceNotFoundException;
import com.medicore.system.mapper.PacienteMapper;
import com.medicore.system.model.entity.Paciente;
import com.medicore.system.repository.CitaRepository;
import com.medicore.system.repository.PacienteRepository;
import com.medicore.system.service.PacienteService;

@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final CitaRepository citaRepository;
    private final PacienteMapper pacienteMapper;

    public PacienteServiceImpl(
            PacienteRepository pacienteRepository,
            CitaRepository citaRepository,
            PacienteMapper pacienteMapper) {
        this.pacienteRepository = pacienteRepository;
        this.citaRepository = citaRepository;
        this.pacienteMapper = pacienteMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listarPacientes() {
        return pacienteRepository.findAll()
                .stream()
                .map(pacienteMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse verPaciente(String numeroDocumento) {
        return pacienteMapper.toResponse(buscarPaciente(numeroDocumento, "No existe paciente registrado"));
    }

    @Override
    @Transactional
    public MessageResponse crearPaciente(PacienteRequest request) {
        validarRequest(request);

        if (pacienteRepository.findByNumeroDocumentoAndTipoDocumento(
                request.getNumeroDocumento(), request.getTipoDocumento()) != null) {
            throw new BusinessException("Ya existe un paciente con el mismo tipo y numero de documento.");
        }

        if (pacienteRepository.findByNumeroDocumento(request.getNumeroDocumento()) != null) {
            throw new BusinessException("Ya existe un paciente con el mismo numero de documento.");
        }

        pacienteRepository.save(pacienteMapper.toEntity(request));
        return new MessageResponse("Paciente creado exitosamente");
    }

    @Override
    @Transactional
    public MessageResponse editarPaciente(String numeroDocumento, PacienteRequest request) {
        validarNumeroDocumento(numeroDocumento, "El numero de documento del paciente es obligatorio.");
        validarRequest(request);

        Paciente paciente = buscarPaciente(numeroDocumento, "Paciente no existe");
        pacienteMapper.updateEntity(paciente, request);
        pacienteRepository.save(paciente);
        return new MessageResponse("Paciente editado exitosamente");
    }

    @Override
    @Transactional
    public MessageResponse eliminarPaciente(String numeroDocumento) {
        validarNumeroDocumento(numeroDocumento, "El numero de documento del paciente es obligatorio.");
        Paciente paciente = buscarPaciente(numeroDocumento, "Error, no se puede eliminar Paciente.");

        if (citaRepository.existsByPacienteNumeroDocumento(numeroDocumento)) {
            paciente.setActivo(false);
            pacienteRepository.save(paciente);
            return new MessageResponse("Paciente desactivado exitosamente porque tiene citas asociadas");
        }

        pacienteRepository.delete(paciente);
        return new MessageResponse("Paciente eliminado exitosamente");
    }

    private Paciente buscarPaciente(String numeroDocumento, String mensajeNoEncontrado) {
        validarNumeroDocumento(numeroDocumento, "El numero de documento del paciente es obligatorio.");
        Paciente paciente = pacienteRepository.findByNumeroDocumento(numeroDocumento);

        if (paciente == null) {
            throw new ResourceNotFoundException(mensajeNoEncontrado);
        }

        return paciente;
    }

    private void validarRequest(PacienteRequest request) {
        if (request == null) {
            throw new BadRequestException("La informacion del paciente es obligatoria.");
        }
    }

    private void validarNumeroDocumento(String numeroDocumento, String mensaje) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new BadRequestException(mensaje);
        }
    }
}
