package com.medicore.system.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medicore.system.dto.request.CitaRequest;
import com.medicore.system.dto.response.CitaResponse;
import com.medicore.system.dto.response.MessageResponse;
import com.medicore.system.exception.BadRequestException;
import com.medicore.system.exception.BusinessException;
import com.medicore.system.exception.ResourceNotFoundException;
import com.medicore.system.mapper.CitaMapper;
import com.medicore.system.model.entity.Cita;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.model.entity.Paciente;
import com.medicore.system.repository.CitaRepository;
import com.medicore.system.repository.MedicoRepository;
import com.medicore.system.repository.PacienteRepository;
import com.medicore.system.service.CitaService;

@Service
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final CitaMapper citaMapper;

    public CitaServiceImpl(
            CitaRepository citaRepository,
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository,
            CitaMapper citaMapper) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.citaMapper = citaMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listarCitas() {
        return citaRepository.findAll()
                .stream()
                .map(citaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse verCita(Long id) {
        return citaMapper.toResponse(buscarCita(id));
    }

    @Override
    @Transactional
    public CitaResponse crearCita(CitaRequest request) {
        validarRequest(request);

        Paciente paciente = buscarPaciente(request.getNumeroDocumentoPaciente());
        Medico medico = buscarMedico(request.getNumeroDocumentoMedico());
        validarCita(request, paciente, medico, null);

        Cita cita = citaRepository.save(citaMapper.toEntity(request, paciente, medico));
        return citaMapper.toResponse(cita);
    }

    @Override
    @Transactional
    public CitaResponse editarCita(Long id, CitaRequest request) {
        validarId(id);
        validarRequest(request);

        Cita cita = buscarCita(id);
        Paciente paciente = buscarPaciente(request.getNumeroDocumentoPaciente());
        Medico medico = buscarMedico(request.getNumeroDocumentoMedico());
        validarCita(request, paciente, medico, id);

        citaMapper.updateEntity(cita, request, paciente, medico);
        return citaMapper.toResponse(citaRepository.save(cita));
    }

    @Override
    @Transactional
    public MessageResponse eliminarCita(Long id) {
        validarId(id);
        Cita cita = buscarCita(id);
        citaRepository.delete(cita);
        return new MessageResponse("Cita eliminada exitosamente");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listarCitasPorPaciente(String numeroDocumento) {
        validarNumeroDocumento(numeroDocumento, "El numero de documento del paciente es obligatorio.");
        return citaRepository.findByPacienteNumeroDocumento(numeroDocumento)
                .stream()
                .map(citaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listarCitasPorMedico(String numeroDocumento) {
        validarNumeroDocumento(numeroDocumento, "El numero de documento del medico es obligatorio.");
        return citaRepository.findByMedicoNumeroDocumento(numeroDocumento)
                .stream()
                .map(citaMapper::toResponse)
                .toList();
    }

    private Cita buscarCita(Long id) {
        validarId(id);
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe cita registrada"));
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

    private void validarCita(CitaRequest request, Paciente paciente, Medico medico, Long citaId) {
        if (request.getFechaHora() == null) {
            throw new BadRequestException("La fecha y hora de la cita es obligatoria.");
        }

        if (request.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("No se permiten citas en fechas pasadas.");
        }

        if (Boolean.FALSE.equals(paciente.getActivo())) {
            throw new BusinessException("No se pueden crear citas con pacientes inactivos.");
        }

        if (Boolean.FALSE.equals(medico.getActivo())) {
            throw new BusinessException("No se pueden crear citas con medicos inactivos.");
        }

        boolean medicoOcupado = citaId == null
                ? citaRepository.existsByMedicoNumeroDocumentoAndFechaHora(
                        medico.getNumeroDocumento(),
                        request.getFechaHora())
                : citaRepository.existsByMedicoNumeroDocumentoAndFechaHoraAndIdNot(
                        medico.getNumeroDocumento(),
                        request.getFechaHora(),
                        citaId);

        if (medicoOcupado) {
            throw new BusinessException("El medico ya tiene una cita programada en esa fecha y hora.");
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
