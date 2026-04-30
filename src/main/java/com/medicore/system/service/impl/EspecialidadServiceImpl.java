package com.medicore.system.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medicore.system.dto.request.EspecialidadRequest;
import com.medicore.system.dto.response.EspecialidadResponse;
import com.medicore.system.dto.response.MessageResponse;
import com.medicore.system.exception.BadRequestException;
import com.medicore.system.exception.BusinessException;
import com.medicore.system.exception.ResourceNotFoundException;
import com.medicore.system.mapper.EspecialidadMapper;
import com.medicore.system.model.entity.Especialidad;
import com.medicore.system.repository.EspecialidadRepository;
import com.medicore.system.repository.MedicoRepository;
import com.medicore.system.service.EspecialidadService;

@Service
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;
    private final MedicoRepository medicoRepository;
    private final EspecialidadMapper especialidadMapper;

    public EspecialidadServiceImpl(
            EspecialidadRepository especialidadRepository,
            MedicoRepository medicoRepository,
            EspecialidadMapper especialidadMapper) {
        this.especialidadRepository = especialidadRepository;
        this.medicoRepository = medicoRepository;
        this.especialidadMapper = especialidadMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspecialidadResponse> listarEspecialidades() {
        return especialidadRepository.findAll()
                .stream()
                .map(especialidadMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EspecialidadResponse verEspecialidad(Long id) {
        return especialidadMapper.toResponse(buscarEspecialidad(id));
    }

    @Override
    @Transactional
    public EspecialidadResponse crearEspecialidad(EspecialidadRequest request) {
        validarRequest(request);

        if (especialidadRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe una especialidad con el mismo nombre.");
        }

        Especialidad especialidad = especialidadRepository.save(especialidadMapper.toEntity(request));
        return especialidadMapper.toResponse(especialidad);
    }

    @Override
    @Transactional
    public EspecialidadResponse editarEspecialidad(Long id, EspecialidadRequest request) {
        validarId(id);
        validarRequest(request);

        Especialidad especialidad = buscarEspecialidad(id);
        especialidadMapper.updateEntity(especialidad, request);
        return especialidadMapper.toResponse(especialidadRepository.save(especialidad));
    }

    @Override
    @Transactional
    public MessageResponse eliminarEspecialidad(Long id) {
        validarId(id);
        Especialidad especialidad = buscarEspecialidad(id);

        if (medicoRepository.existsByEspecialidadId(id)) {
            especialidad.setActivo(false);
            especialidadRepository.save(especialidad);
            return new MessageResponse("Especialidad desactivada exitosamente porque tiene medicos asociados");
        }

        especialidadRepository.delete(especialidad);
        return new MessageResponse("Especialidad eliminada exitosamente");
    }

    private Especialidad buscarEspecialidad(Long id) {
        validarId(id);
        return especialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe especialidad registrada"));
    }

    private void validarRequest(EspecialidadRequest request) {
        if (request == null) {
            throw new BadRequestException("La informacion de la especialidad es obligatoria.");
        }
    }

    private void validarId(Long id) {
        if (id == null) {
            throw new BadRequestException("El id de la especialidad es obligatorio.");
        }
    }
}
