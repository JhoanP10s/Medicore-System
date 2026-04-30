package com.medicore.system.dto.response;

import java.time.LocalDateTime;

public class CitaResponse {

    private Long id;
    private LocalDateTime fechaHora;
    private String motivo;
    private String observaciones;
    private String pacienteNumeroDocumento;
    private String pacienteNombreCompleto;
    private String medicoNumeroDocumento;
    private String medicoNombreCompleto;
    private String especialidadNombre;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPacienteNumeroDocumento() {
        return pacienteNumeroDocumento;
    }

    public void setPacienteNumeroDocumento(String pacienteNumeroDocumento) {
        this.pacienteNumeroDocumento = pacienteNumeroDocumento;
    }

    public String getPacienteNombreCompleto() {
        return pacienteNombreCompleto;
    }

    public void setPacienteNombreCompleto(String pacienteNombreCompleto) {
        this.pacienteNombreCompleto = pacienteNombreCompleto;
    }

    public String getMedicoNumeroDocumento() {
        return medicoNumeroDocumento;
    }

    public void setMedicoNumeroDocumento(String medicoNumeroDocumento) {
        this.medicoNumeroDocumento = medicoNumeroDocumento;
    }

    public String getMedicoNombreCompleto() {
        return medicoNombreCompleto;
    }

    public void setMedicoNombreCompleto(String medicoNombreCompleto) {
        this.medicoNombreCompleto = medicoNombreCompleto;
    }

    public String getEspecialidadNombre() {
        return especialidadNombre;
    }

    public void setEspecialidadNombre(String especialidadNombre) {
        this.especialidadNombre = especialidadNombre;
    }
}
