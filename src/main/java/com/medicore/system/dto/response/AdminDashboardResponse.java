package com.medicore.system.dto.response;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardResponse {
    private long totalPacientesActivos;
    private long totalMedicosActivos;
    private long totalEspecialidadesActivas;
    private long citasHoy;
    private long historiasClinicasRegistradas;
    private List<EstadoCitaCountResponse> citasPorEstado = new ArrayList<>();
    private List<CitaResumenResponse> proximasCitas = new ArrayList<>();
    private List<AlertaDashboardResponse> alertas = new ArrayList<>();

    public long getTotalPacientesActivos() { return totalPacientesActivos; }
    public void setTotalPacientesActivos(long totalPacientesActivos) { this.totalPacientesActivos = totalPacientesActivos; }
    public long getTotalMedicosActivos() { return totalMedicosActivos; }
    public void setTotalMedicosActivos(long totalMedicosActivos) { this.totalMedicosActivos = totalMedicosActivos; }
    public long getTotalEspecialidadesActivas() { return totalEspecialidadesActivas; }
    public void setTotalEspecialidadesActivas(long totalEspecialidadesActivas) { this.totalEspecialidadesActivas = totalEspecialidadesActivas; }
    public long getCitasHoy() { return citasHoy; }
    public void setCitasHoy(long citasHoy) { this.citasHoy = citasHoy; }
    public long getHistoriasClinicasRegistradas() { return historiasClinicasRegistradas; }
    public void setHistoriasClinicasRegistradas(long historiasClinicasRegistradas) { this.historiasClinicasRegistradas = historiasClinicasRegistradas; }
    public List<EstadoCitaCountResponse> getCitasPorEstado() { return citasPorEstado; }
    public void setCitasPorEstado(List<EstadoCitaCountResponse> citasPorEstado) { this.citasPorEstado = citasPorEstado; }
    public List<CitaResumenResponse> getProximasCitas() { return proximasCitas; }
    public void setProximasCitas(List<CitaResumenResponse> proximasCitas) { this.proximasCitas = proximasCitas; }
    public List<AlertaDashboardResponse> getAlertas() { return alertas; }
    public void setAlertas(List<AlertaDashboardResponse> alertas) { this.alertas = alertas; }
}
