package com.medicore.system.mapper;

import org.springframework.stereotype.Component;

import com.medicore.system.dto.request.CitaRequest;
import com.medicore.system.dto.response.CitaResponse;
import com.medicore.system.model.entity.Cita;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.model.entity.Paciente;

@Component
public class CitaMapper {

    public Cita toEntity(CitaRequest request, Paciente paciente, Medico medico) {
        Cita cita = new Cita();
        updateEntity(cita, request, paciente, medico);
        return cita;
    }

    public CitaResponse toResponse(Cita cita) {
        CitaResponse response = new CitaResponse();
        response.setId(cita.getId());
        response.setFechaHora(cita.getFechaHora());
        response.setMotivo(cita.getMotivo());
        response.setObservaciones(cita.getObservaciones());
        response.setEstado(cita.getEstado());
        response.setDuracionMinutos(cita.getDuracionMinutos());
        response.setPacienteNumeroDocumento(cita.getPaciente().getNumeroDocumento());
        response.setPacienteNombreCompleto(nombreCompleto(
                cita.getPaciente().getPrimerNombre(),
                cita.getPaciente().getPrimerApellido()));
        response.setPacienteActivo(cita.getPaciente().getActivo());
        response.setMedicoNumeroDocumento(cita.getMedico().getNumeroDocumento());
        response.setMedicoNombreCompleto(nombreCompleto(
                cita.getMedico().getPrimerNombre(),
                cita.getMedico().getPrimerApellido()));
        response.setMedicoActivo(cita.getMedico().getActivo());

        if (cita.getMedico().getEspecialidad() != null) {
            response.setEspecialidadNombre(cita.getMedico().getEspecialidad().getNombre());
        }

        if (cita.getHistoriaClinica() != null) {
            response.setHistoriaClinicaId(cita.getHistoriaClinica().getId());
        }

        return response;
    }

    public void updateEntity(Cita cita, CitaRequest request, Paciente paciente, Medico medico) {
        cita.setFechaHora(request.getFechaHora());
        cita.setMotivo(request.getMotivo());
        cita.setObservaciones(request.getObservaciones());
        cita.setDuracionMinutos(request.getDuracionMinutos());
        cita.setPaciente(paciente);
        cita.setMedico(medico);
    }

    private String nombreCompleto(String primerNombre, String primerApellido) {
        return primerNombre + " " + primerApellido;
    }
}
