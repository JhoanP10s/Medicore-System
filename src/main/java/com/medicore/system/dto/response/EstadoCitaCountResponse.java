package com.medicore.system.dto.response;

import com.medicore.system.model.entity.EstadoCita;

public class EstadoCitaCountResponse {
    private EstadoCita estado;
    private long total;

    public EstadoCitaCountResponse() { }

    public EstadoCitaCountResponse(EstadoCita estado, long total) {
        this.estado = estado;
        this.total = total;
    }

    public EstadoCita getEstado() { return estado; }
    public void setEstado(EstadoCita estado) { this.estado = estado; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
}
