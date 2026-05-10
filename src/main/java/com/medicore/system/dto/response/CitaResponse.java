package com.medicore.system.dto.response;

import java.time.LocalDateTime;

import com.medicore.system.model.entity.EstadoCita;

public class CitaResponse {

    private Long id;
    private LocalDateTime fechaHora;
    private String motivo;
    private String observaciones;
    private EstadoCita estado;
    private Integer duracionMinutos;
    private String pacienteNumeroDocumento;
    private String pacienteNombreCompleto;
    private Boolean pacienteActivo;
    private String medicoNumeroDocumento;
    private String medicoNombreCompleto;
    private Boolean medicoActivo;
    private String especialidadNombre;
    private Long historiaClinicaId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public EstadoCita getEstado() { return estado; }
    public void setEstado(EstadoCita estado) { this.estado = estado; }
    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public String getPacienteNumeroDocumento() { return pacienteNumeroDocumento; }
    public void setPacienteNumeroDocumento(String pacienteNumeroDocumento) { this.pacienteNumeroDocumento = pacienteNumeroDocumento; }
    public String getPacienteNombreCompleto() { return pacienteNombreCompleto; }
    public void setPacienteNombreCompleto(String pacienteNombreCompleto) { this.pacienteNombreCompleto = pacienteNombreCompleto; }
    public Boolean getPacienteActivo() { return pacienteActivo; }
    public void setPacienteActivo(Boolean pacienteActivo) { this.pacienteActivo = pacienteActivo; }
    public String getMedicoNumeroDocumento() { return medicoNumeroDocumento; }
    public void setMedicoNumeroDocumento(String medicoNumeroDocumento) { this.medicoNumeroDocumento = medicoNumeroDocumento; }
    public String getMedicoNombreCompleto() { return medicoNombreCompleto; }
    public void setMedicoNombreCompleto(String medicoNombreCompleto) { this.medicoNombreCompleto = medicoNombreCompleto; }
    public Boolean getMedicoActivo() { return medicoActivo; }
    public void setMedicoActivo(Boolean medicoActivo) { this.medicoActivo = medicoActivo; }
    public String getEspecialidadNombre() { return especialidadNombre; }
    public void setEspecialidadNombre(String especialidadNombre) { this.especialidadNombre = especialidadNombre; }
    public Long getHistoriaClinicaId() { return historiaClinicaId; }
    public void setHistoriaClinicaId(Long historiaClinicaId) { this.historiaClinicaId = historiaClinicaId; }
}
