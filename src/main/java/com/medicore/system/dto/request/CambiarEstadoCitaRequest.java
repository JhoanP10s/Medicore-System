package com.medicore.system.dto.request;

import com.medicore.system.model.entity.EstadoCita;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class CambiarEstadoCitaRequest {

    @Schema(description = "Nuevo estado de la cita", example = "CONFIRMADA", allowableValues = {
            "PROGRAMADA", "CONFIRMADA", "CANCELADA", "COMPLETADA" })
    @NotNull(message = "El estado de la cita es obligatorio")
    private EstadoCita estado;

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }
}
