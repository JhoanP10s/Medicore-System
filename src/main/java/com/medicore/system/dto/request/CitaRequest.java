package com.medicore.system.dto.request;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CitaRequest {

    @Schema(description = "Numero de documento del paciente", example = "1020304050")
    @NotBlank(message = "El documento del paciente es obligatorio")
    @Size(max = 30, message = "El documento del paciente no puede superar 30 caracteres")
    private String numeroDocumentoPaciente;

    @Schema(description = "Numero de documento del medico", example = "79998887")
    @NotBlank(message = "El documento del medico es obligatorio")
    @Size(max = 30, message = "El documento del medico no puede superar 30 caracteres")
    private String numeroDocumentoMedico;

    @Schema(description = "Fecha y hora programada para la cita", example = "2026-05-15T09:30:00")
    @NotNull(message = "La fecha y hora de la cita es obligatoria")
    @FutureOrPresent(message = "La fecha de la cita no puede estar en el pasado")
    private LocalDateTime fechaHora;

    @Schema(description = "Motivo principal de la cita", example = "Control general")
    @NotBlank(message = "El motivo de la cita es obligatorio")
    @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
    private String motivo;

    @Schema(description = "Observaciones adicionales de la cita", example = "Paciente refiere dolor toracico ocasional")
    @Size(max = 500, message = "Las observaciones no pueden superar 500 caracteres")
    private String observaciones;

    public String getNumeroDocumentoPaciente() {
        return numeroDocumentoPaciente;
    }

    public void setNumeroDocumentoPaciente(String numeroDocumentoPaciente) {
        this.numeroDocumentoPaciente = numeroDocumentoPaciente;
    }

    public String getNumeroDocumentoMedico() {
        return numeroDocumentoMedico;
    }

    public void setNumeroDocumentoMedico(String numeroDocumentoMedico) {
        this.numeroDocumentoMedico = numeroDocumentoMedico;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
