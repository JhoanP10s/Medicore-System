package com.medicore.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EspecialidadRequest {

    @Schema(description = "Nombre de la especialidad", example = "Cardiologia")
    @NotBlank(message = "El nombre de la especialidad es obligatorio")
    @Size(max = 100, message = "El nombre de la especialidad no puede superar 100 caracteres")
    private String nombre;

    @Schema(description = "Descripcion de la especialidad", example = "Diagnostico y tratamiento de enfermedades cardiovasculares")
    @Size(max = 255, message = "La descripcion no puede superar 255 caracteres")
    private String descripcion;

    @Schema(description = "Indica si la especialidad esta activa", example = "true")
    private Boolean activo;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
