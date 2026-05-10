package com.medicore.system.dto.request;

import java.time.LocalTime;

import com.medicore.system.model.entity.DiaSemana;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DisponibilidadMedicaRequest {

    @Schema(example = "79998887")
    @NotBlank(message = "El documento del medico es obligatorio")
    @Size(max = 30, message = "El documento del medico no puede superar 30 caracteres")
    private String numeroDocumentoMedico;

    @Schema(example = "LUNES")
    @NotNull(message = "El dia de la semana es obligatorio")
    private DiaSemana diaSemana;

    @Schema(example = "08:00:00")
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @Schema(example = "12:00:00")
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    public String getNumeroDocumentoMedico() { return numeroDocumentoMedico; }
    public void setNumeroDocumentoMedico(String numeroDocumentoMedico) { this.numeroDocumentoMedico = numeroDocumentoMedico; }
    public DiaSemana getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DiaSemana diaSemana) { this.diaSemana = diaSemana; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
}
