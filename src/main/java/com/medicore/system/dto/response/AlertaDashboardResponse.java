package com.medicore.system.dto.response;

public class AlertaDashboardResponse {
    private String tipo;
    private String mensaje;
    private String severidad;

    public AlertaDashboardResponse() { }

    public AlertaDashboardResponse(String tipo, String mensaje, String severidad) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.severidad = severidad;
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getSeveridad() { return severidad; }
    public void setSeveridad(String severidad) { this.severidad = severidad; }
}
