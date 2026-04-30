package com.medicore.system.mapper;

import org.springframework.stereotype.Component;

import com.medicore.system.dto.request.PacienteRequest;
import com.medicore.system.dto.response.PacienteResponse;
import com.medicore.system.model.entity.Paciente;

@Component
public class PacienteMapper {

    public Paciente toEntity(PacienteRequest request) {
        Paciente paciente = new Paciente();
        paciente.setNumeroDocumento(request.getNumeroDocumento());
        updateEntity(paciente, request);
        return paciente;
    }

    public PacienteResponse toResponse(Paciente paciente) {
        PacienteResponse response = new PacienteResponse();
        response.setNumeroDocumento(paciente.getNumeroDocumento());
        response.setPrimerNombre(paciente.getPrimerNombre());
        response.setSegundoNombre(paciente.getSegundoNombre());
        response.setPrimerApellido(paciente.getPrimerApellido());
        response.setSegundoApellido(paciente.getSegundoApellido());
        response.setTipoDocumento(paciente.getTipoDocumento());
        response.setFechaExpedicionDoc(paciente.getFechaExpedicionDoc());
        response.setEmail(paciente.getEmail());
        response.setTelefono(paciente.getTelefono());
        response.setActivo(paciente.getActivo());
        return response;
    }

    public void updateEntity(Paciente paciente, PacienteRequest request) {
        paciente.setPrimerNombre(request.getPrimerNombre());
        paciente.setSegundoNombre(request.getSegundoNombre());
        paciente.setPrimerApellido(request.getPrimerApellido());
        paciente.setSegundoApellido(request.getSegundoApellido());
        paciente.setTipoDocumento(request.getTipoDocumento());
        paciente.setFechaExpedicionDoc(request.getFechaExpedicionDoc());
        paciente.setEmail(request.getEmail());
        paciente.setTelefono(request.getTelefono());

        if (request.getActivo() != null) {
            paciente.setActivo(request.getActivo());
        }
    }
}
