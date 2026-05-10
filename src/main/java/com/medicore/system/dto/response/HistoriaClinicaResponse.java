package com.medicore.system.dto.response;

import java.time.LocalDateTime;

import com.medicore.system.model.entity.EstadoCita;

public class HistoriaClinicaResponse {

    private Long id;
    private String sintomas;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private LocalDateTime fechaRegistro;
    private String pacienteNumeroDocumento;
    private String pacienteNombreCompleto;
    private String medicoNumeroDocumento;
    private String medicoNombreCompleto;
    private Long citaId;
    private LocalDateTime citaFechaHora;
    private EstadoCita citaEstado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public String getPacienteNumeroDocumento() { return pacienteNumeroDocumento; }
    public void setPacienteNumeroDocumento(String pacienteNumeroDocumento) { this.pacienteNumeroDocumento = pacienteNumeroDocumento; }
    public String getPacienteNombreCompleto() { return pacienteNombreCompleto; }
    public void setPacienteNombreCompleto(String pacienteNombreCompleto) { this.pacienteNombreCompleto = pacienteNombreCompleto; }
    public String getMedicoNumeroDocumento() { return medicoNumeroDocumento; }
    public void setMedicoNumeroDocumento(String medicoNumeroDocumento) { this.medicoNumeroDocumento = medicoNumeroDocumento; }
    public String getMedicoNombreCompleto() { return medicoNombreCompleto; }
    public void setMedicoNombreCompleto(String medicoNombreCompleto) { this.medicoNombreCompleto = medicoNombreCompleto; }
    public Long getCitaId() { return citaId; }
    public void setCitaId(Long citaId) { this.citaId = citaId; }
    public LocalDateTime getCitaFechaHora() { return citaFechaHora; }
    public void setCitaFechaHora(LocalDateTime citaFechaHora) { this.citaFechaHora = citaFechaHora; }
    public EstadoCita getCitaEstado() { return citaEstado; }
    public void setCitaEstado(EstadoCita citaEstado) { this.citaEstado = citaEstado; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
