package com.medicore.system.dto.response;

import java.time.LocalDateTime;

import com.medicore.system.model.entity.EstadoCita;

public class CitaResumenResponse {
    private Long id;
    private LocalDateTime fechaHora;
    private EstadoCita estado;
    private Integer duracionMinutos;
    private String motivo;
    private String pacienteNumeroDocumento;
    private String pacienteNombreCompleto;
    private String medicoNumeroDocumento;
    private String medicoNombreCompleto;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public EstadoCita getEstado() { return estado; }
    public void setEstado(EstadoCita estado) { this.estado = estado; }
    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getPacienteNumeroDocumento() { return pacienteNumeroDocumento; }
    public void setPacienteNumeroDocumento(String pacienteNumeroDocumento) { this.pacienteNumeroDocumento = pacienteNumeroDocumento; }
    public String getPacienteNombreCompleto() { return pacienteNombreCompleto; }
    public void setPacienteNombreCompleto(String pacienteNombreCompleto) { this.pacienteNombreCompleto = pacienteNombreCompleto; }
    public String getMedicoNumeroDocumento() { return medicoNumeroDocumento; }
    public void setMedicoNumeroDocumento(String medicoNumeroDocumento) { this.medicoNumeroDocumento = medicoNumeroDocumento; }
    public String getMedicoNombreCompleto() { return medicoNombreCompleto; }
    public void setMedicoNombreCompleto(String medicoNombreCompleto) { this.medicoNombreCompleto = medicoNombreCompleto; }
}
