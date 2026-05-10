package com.medicore.system.dto.response;

import java.util.ArrayList;
import java.util.List;

public class DoctorDashboardResponse {
    private long citasHoy;
    private List<CitaResumenResponse> proximasCitas = new ArrayList<>();
    private List<EstadoCitaCountResponse> citasPorEstado = new ArrayList<>();
    private long citasPendientesHistoria;
    private List<HistoriaClinicaResumenResponse> historiasRecientes = new ArrayList<>();
    private List<AlertaDashboardResponse> alertas = new ArrayList<>();

    public long getCitasHoy() { return citasHoy; }
    public void setCitasHoy(long citasHoy) { this.citasHoy = citasHoy; }
    public List<CitaResumenResponse> getProximasCitas() { return proximasCitas; }
    public void setProximasCitas(List<CitaResumenResponse> proximasCitas) { this.proximasCitas = proximasCitas; }
    public List<EstadoCitaCountResponse> getCitasPorEstado() { return citasPorEstado; }
    public void setCitasPorEstado(List<EstadoCitaCountResponse> citasPorEstado) { this.citasPorEstado = citasPorEstado; }
    public long getCitasPendientesHistoria() { return citasPendientesHistoria; }
    public void setCitasPendientesHistoria(long citasPendientesHistoria) { this.citasPendientesHistoria = citasPendientesHistoria; }
    public List<HistoriaClinicaResumenResponse> getHistoriasRecientes() { return historiasRecientes; }
    public void setHistoriasRecientes(List<HistoriaClinicaResumenResponse> historiasRecientes) { this.historiasRecientes = historiasRecientes; }
    public List<AlertaDashboardResponse> getAlertas() { return alertas; }
    public void setAlertas(List<AlertaDashboardResponse> alertas) { this.alertas = alertas; }
}
