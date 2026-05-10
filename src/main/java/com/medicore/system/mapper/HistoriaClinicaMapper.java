package com.medicore.system.mapper;

import org.springframework.stereotype.Component;

import com.medicore.system.dto.request.HistoriaClinicaRequest;
import com.medicore.system.dto.response.HistoriaClinicaResponse;
import com.medicore.system.model.entity.Cita;
import com.medicore.system.model.entity.HistoriaClinica;

@Component
public class HistoriaClinicaMapper {

    public HistoriaClinica toEntity(HistoriaClinicaRequest request, Cita cita) {
        HistoriaClinica historiaClinica = new HistoriaClinica();
        updateEntity(historiaClinica, request);
        historiaClinica.setCita(cita);
        historiaClinica.setPaciente(cita.getPaciente());
        historiaClinica.setMedico(cita.getMedico());
        return historiaClinica;
    }

    public HistoriaClinicaResponse toResponse(HistoriaClinica historiaClinica) {
        HistoriaClinicaResponse response = new HistoriaClinicaResponse();
        response.setId(historiaClinica.getId());
        response.setSintomas(historiaClinica.getSintomas());
        response.setDiagnostico(historiaClinica.getDiagnostico());
        response.setTratamiento(historiaClinica.getTratamiento());
        response.setObservaciones(historiaClinica.getObservaciones());
        response.setFechaRegistro(historiaClinica.getFechaRegistro());
        response.setPacienteNumeroDocumento(historiaClinica.getPaciente().getNumeroDocumento());
        response.setPacienteNombreCompleto(nombreCompleto(
                historiaClinica.getPaciente().getPrimerNombre(),
                historiaClinica.getPaciente().getPrimerApellido()));
        response.setMedicoNumeroDocumento(historiaClinica.getMedico().getNumeroDocumento());
        response.setMedicoNombreCompleto(nombreCompleto(
                historiaClinica.getMedico().getPrimerNombre(),
                historiaClinica.getMedico().getPrimerApellido()));
        response.setCitaId(historiaClinica.getCita().getId());
        response.setCitaFechaHora(historiaClinica.getCita().getFechaHora());
        response.setCitaEstado(historiaClinica.getCita().getEstado());
        response.setCreatedAt(historiaClinica.getCreatedAt());
        response.setUpdatedAt(historiaClinica.getUpdatedAt());
        return response;
    }

    public void updateEntity(HistoriaClinica historiaClinica, HistoriaClinicaRequest request) {
        historiaClinica.setSintomas(request.getSintomas());
        historiaClinica.setDiagnostico(request.getDiagnostico());
        historiaClinica.setTratamiento(request.getTratamiento());
        historiaClinica.setObservaciones(request.getObservaciones());
    }

    private String nombreCompleto(String primerNombre, String primerApellido) {
        return primerNombre + " " + primerApellido;
    }
}
