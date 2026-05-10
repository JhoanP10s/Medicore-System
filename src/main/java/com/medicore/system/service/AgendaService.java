package com.medicore.system.service;

import java.time.LocalDate;
import java.util.List;

import com.medicore.system.dto.response.HorarioDisponibleResponse;

public interface AgendaService {
    List<HorarioDisponibleResponse> buscarHorariosDisponibles(String medicoId, LocalDate fecha, Integer duracionMinutos);
}
