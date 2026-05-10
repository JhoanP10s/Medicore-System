package com.medicore.system.service;

import java.time.LocalDateTime;
import java.util.List;

import com.medicore.system.dto.request.BloqueoAgendaRequest;
import com.medicore.system.dto.response.BloqueoAgendaResponse;
import com.medicore.system.dto.response.MessageResponse;

public interface BloqueoAgendaService {
    BloqueoAgendaResponse crear(BloqueoAgendaRequest request);
    List<BloqueoAgendaResponse> listar();
    BloqueoAgendaResponse buscarPorId(Long id);
    List<BloqueoAgendaResponse> buscarPorMedico(String medicoId);
    List<BloqueoAgendaResponse> buscarPorRango(LocalDateTime inicio, LocalDateTime fin);
    List<BloqueoAgendaResponse> buscarActivosPorRango(LocalDateTime inicio, LocalDateTime fin);
    BloqueoAgendaResponse actualizar(Long id, BloqueoAgendaRequest request);
    MessageResponse desactivar(Long id);
}
