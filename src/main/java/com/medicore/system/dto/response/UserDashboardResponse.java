package com.medicore.system.dto.response;

import java.util.ArrayList;
import java.util.List;

public class UserDashboardResponse {
    private String mensaje;
    private List<String> accesosDisponibles = new ArrayList<>();

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public List<String> getAccesosDisponibles() { return accesosDisponibles; }
    public void setAccesosDisponibles(List<String> accesosDisponibles) { this.accesosDisponibles = accesosDisponibles; }
}
