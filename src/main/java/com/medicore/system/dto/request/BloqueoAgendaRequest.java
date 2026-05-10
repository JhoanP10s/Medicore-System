package com.medicore.system.dto.request;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BloqueoAgendaRequest {

    @Schema(example = "79998887")
    @NotBlank(message = "El documento del medico es obligatorio")
    @Size(max = 30, message = "El documento del medico no puede superar 30 caracteres")
    private String numeroDocumentoMedico;

    @Schema(example = "2026-05-15T10:00:00")
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @Schema(example = "2026-05-15T11:00:00")
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    @Schema(example = "Capacitacion medica")
    @NotBlank(message = "El motivo del bloqueo es obligatorio")
    @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
    private String motivo;

    public String getNumeroDocumentoMedico() { return numeroDocumentoMedico; }
    public void setNumeroDocumentoMedico(String numeroDocumentoMedico) { this.numeroDocumentoMedico = numeroDocumentoMedico; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
