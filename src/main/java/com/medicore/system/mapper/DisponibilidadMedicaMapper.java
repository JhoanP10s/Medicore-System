package com.medicore.system.mapper;

import org.springframework.stereotype.Component;

import com.medicore.system.dto.request.DisponibilidadMedicaRequest;
import com.medicore.system.dto.response.DisponibilidadMedicaResponse;
import com.medicore.system.model.entity.DisponibilidadMedica;
import com.medicore.system.model.entity.Medico;

@Component
public class DisponibilidadMedicaMapper {

    public DisponibilidadMedica toEntity(DisponibilidadMedicaRequest request, Medico medico) {
        DisponibilidadMedica disponibilidad = new DisponibilidadMedica();
        updateEntity(disponibilidad, request, medico);
        return disponibilidad;
    }

    public void updateEntity(DisponibilidadMedica disponibilidad, DisponibilidadMedicaRequest request, Medico medico) {
        disponibilidad.setMedico(medico);
        disponibilidad.setDiaSemana(request.getDiaSemana());
        disponibilidad.setHoraInicio(request.getHoraInicio());
        disponibilidad.setHoraFin(request.getHoraFin());
    }

    public DisponibilidadMedicaResponse toResponse(DisponibilidadMedica disponibilidad) {
        DisponibilidadMedicaResponse response = new DisponibilidadMedicaResponse();
        response.setId(disponibilidad.getId());
        response.setMedicoNumeroDocumento(disponibilidad.getMedico().getNumeroDocumento());
        response.setMedicoNombreCompleto(nombreCompleto(disponibilidad.getMedico().getPrimerNombre(), disponibilidad.getMedico().getPrimerApellido()));
        response.setDiaSemana(disponibilidad.getDiaSemana());
        response.setHoraInicio(disponibilidad.getHoraInicio());
        response.setHoraFin(disponibilidad.getHoraFin());
        response.setActivo(disponibilidad.getActivo());
        response.setCreatedAt(disponibilidad.getCreatedAt());
        response.setUpdatedAt(disponibilidad.getUpdatedAt());
        return response;
    }

    private String nombreCompleto(String primerNombre, String primerApellido) {
        return primerNombre + " " + primerApellido;
    }
}
