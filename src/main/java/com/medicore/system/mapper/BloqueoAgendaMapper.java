package com.medicore.system.mapper;

import org.springframework.stereotype.Component;

import com.medicore.system.dto.request.BloqueoAgendaRequest;
import com.medicore.system.dto.response.BloqueoAgendaResponse;
import com.medicore.system.model.entity.BloqueoAgenda;
import com.medicore.system.model.entity.Medico;

@Component
public class BloqueoAgendaMapper {

    public BloqueoAgenda toEntity(BloqueoAgendaRequest request, Medico medico) {
        BloqueoAgenda bloqueo = new BloqueoAgenda();
        updateEntity(bloqueo, request, medico);
        return bloqueo;
    }

    public void updateEntity(BloqueoAgenda bloqueo, BloqueoAgendaRequest request, Medico medico) {
        bloqueo.setMedico(medico);
        bloqueo.setFechaInicio(request.getFechaInicio());
        bloqueo.setFechaFin(request.getFechaFin());
        bloqueo.setMotivo(request.getMotivo());
    }

    public BloqueoAgendaResponse toResponse(BloqueoAgenda bloqueo) {
        BloqueoAgendaResponse response = new BloqueoAgendaResponse();
        response.setId(bloqueo.getId());
        response.setMedicoNumeroDocumento(bloqueo.getMedico().getNumeroDocumento());
        response.setMedicoNombreCompleto(nombreCompleto(bloqueo.getMedico().getPrimerNombre(), bloqueo.getMedico().getPrimerApellido()));
        response.setFechaInicio(bloqueo.getFechaInicio());
        response.setFechaFin(bloqueo.getFechaFin());
        response.setMotivo(bloqueo.getMotivo());
        response.setActivo(bloqueo.getActivo());
        response.setCreatedAt(bloqueo.getCreatedAt());
        response.setUpdatedAt(bloqueo.getUpdatedAt());
        return response;
    }

    private String nombreCompleto(String primerNombre, String primerApellido) {
        return primerNombre + " " + primerApellido;
    }
}
