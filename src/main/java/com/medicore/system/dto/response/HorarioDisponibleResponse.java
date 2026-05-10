package com.medicore.system.dto.response;

import java.time.LocalDateTime;

public class HorarioDisponibleResponse {
    private LocalDateTime inicio;
    private LocalDateTime fin;

    public HorarioDisponibleResponse(LocalDateTime inicio, LocalDateTime fin) {
        this.inicio = inicio;
        this.fin = fin;
    }

    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }
    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }
}
