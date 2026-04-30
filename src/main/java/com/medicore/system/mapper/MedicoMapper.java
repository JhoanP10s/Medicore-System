package com.medicore.system.mapper;

import org.springframework.stereotype.Component;

import com.medicore.system.dto.request.MedicoRequest;
import com.medicore.system.dto.response.MedicoResponse;
import com.medicore.system.model.entity.Especialidad;
import com.medicore.system.model.entity.Medico;

@Component
public class MedicoMapper {

    public Medico toEntity(MedicoRequest request, Especialidad especialidad) {
        Medico medico = new Medico();
        medico.setNumeroDocumento(request.getNumeroDocumento());
        updateEntity(medico, request, especialidad);
        return medico;
    }

    public MedicoResponse toResponse(Medico medico) {
        MedicoResponse response = new MedicoResponse();
        response.setNumeroDocumento(medico.getNumeroDocumento());
        response.setPrimerNombre(medico.getPrimerNombre());
        response.setSegundoNombre(medico.getSegundoNombre());
        response.setPrimerApellido(medico.getPrimerApellido());
        response.setSegundoApellido(medico.getSegundoApellido());
        response.setTipoDocumento(medico.getTipoDocumento());
        response.setFechaExpedicionDoc(medico.getFechaExpedicionDoc());
        response.setEmail(medico.getEmail());
        response.setTelefono(medico.getTelefono());
        response.setActivo(medico.getActivo());

        if (medico.getEspecialidad() != null) {
            response.setEspecialidadId(medico.getEspecialidad().getId());
            response.setEspecialidadNombre(medico.getEspecialidad().getNombre());
        }

        return response;
    }

    public void updateEntity(Medico medico, MedicoRequest request, Especialidad especialidad) {
        medico.setPrimerNombre(request.getPrimerNombre());
        medico.setSegundoNombre(request.getSegundoNombre());
        medico.setPrimerApellido(request.getPrimerApellido());
        medico.setSegundoApellido(request.getSegundoApellido());
        medico.setTipoDocumento(request.getTipoDocumento());
        medico.setFechaExpedicionDoc(request.getFechaExpedicionDoc());
        medico.setEmail(request.getEmail());
        medico.setTelefono(request.getTelefono());
        if (request.getActivo() != null) {
            medico.setActivo(request.getActivo());
        }
        medico.setEspecialidad(especialidad);
    }
}
