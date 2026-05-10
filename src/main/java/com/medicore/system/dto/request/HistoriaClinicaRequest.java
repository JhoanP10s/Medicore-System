package com.medicore.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class HistoriaClinicaRequest {

    @Schema(description = "Identificador de la cita asociada", example = "1")
    @NotNull(message = "El id de la cita es obligatorio")
    private Long citaId;

    @Schema(description = "Sintomas reportados por el paciente", example = "Dolor toracico leve y fatiga")
    @NotBlank(message = "Los sintomas son obligatorios")
    @Size(max = 1000, message = "Los sintomas no pueden superar 1000 caracteres")
    private String sintomas;

    @Schema(description = "Diagnostico medico", example = "Hipertension arterial en seguimiento")
    @NotBlank(message = "El diagnostico es obligatorio")
    @Size(max = 1000, message = "El diagnostico no puede superar 1000 caracteres")
    private String diagnostico;

    @Schema(description = "Tratamiento indicado", example = "Control de presion, dieta baja en sodio y seguimiento en 30 dias")
    @NotBlank(message = "El tratamiento es obligatorio")
    @Size(max = 1000, message = "El tratamiento no puede superar 1000 caracteres")
    private String tratamiento;

    @Schema(description = "Observaciones clinicas adicionales", example = "Paciente entiende signos de alarma")
    @Size(max = 1000, message = "Las observaciones no pueden superar 1000 caracteres")
    private String observaciones;

    public Long getCitaId() { return citaId; }
    public void setCitaId(Long citaId) { this.citaId = citaId; }
    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
