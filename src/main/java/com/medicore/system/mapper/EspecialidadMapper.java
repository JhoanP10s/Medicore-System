package com.medicore.system.mapper;

import org.springframework.stereotype.Component;

import com.medicore.system.dto.request.EspecialidadRequest;
import com.medicore.system.dto.response.EspecialidadResponse;
import com.medicore.system.model.entity.Especialidad;

@Component
public class EspecialidadMapper {

    public Especialidad toEntity(EspecialidadRequest request) {
        Especialidad especialidad = new Especialidad();
        updateEntity(especialidad, request);
        return especialidad;
    }

    public EspecialidadResponse toResponse(Especialidad especialidad) {
        EspecialidadResponse response = new EspecialidadResponse();
        response.setId(especialidad.getId());
        response.setNombre(especialidad.getNombre());
        response.setDescripcion(especialidad.getDescripcion());
        response.setActivo(especialidad.getActivo());
        return response;
    }

    public void updateEntity(Especialidad especialidad, EspecialidadRequest request) {
        especialidad.setNombre(request.getNombre());
        especialidad.setDescripcion(request.getDescripcion());
        if (request.getActivo() != null) {
            especialidad.setActivo(request.getActivo());
        }
    }
}
