package com.medicore.system.dto.response;

import java.time.LocalDateTime;

public class HistoriaClinicaResumenResponse {
    private Long id;
    private LocalDateTime fechaRegistro;
    private String pacienteNumeroDocumento;
    private String pacienteNombreCompleto;
    private String diagnostico;
    private Long citaId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public String getPacienteNumeroDocumento() { return pacienteNumeroDocumento; }
    public void setPacienteNumeroDocumento(String pacienteNumeroDocumento) { this.pacienteNumeroDocumento = pacienteNumeroDocumento; }
    public String getPacienteNombreCompleto() { return pacienteNombreCompleto; }
    public void setPacienteNombreCompleto(String pacienteNombreCompleto) { this.pacienteNombreCompleto = pacienteNombreCompleto; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public Long getCitaId() { return citaId; }
    public void setCitaId(Long citaId) { this.citaId = citaId; }
}
