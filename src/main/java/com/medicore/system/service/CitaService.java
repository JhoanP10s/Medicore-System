package com.medicore.system.service;

import java.time.LocalDateTime;
import java.util.List;

import com.medicore.system.dto.request.CitaRequest;
import com.medicore.system.dto.response.CitaResponse;
import com.medicore.system.dto.response.MessageResponse;
import com.medicore.system.model.entity.EstadoCita;

public interface CitaService {

    List<CitaResponse> listarCitas();

    CitaResponse verCita(Long id);

    CitaResponse crearCita(CitaRequest request);

    CitaResponse editarCita(Long id, CitaRequest request);

    MessageResponse eliminarCita(Long id);

    MessageResponse cancelarCita(Long id);

    CitaResponse cambiarEstado(Long id, EstadoCita estado);

    List<CitaResponse> listarCitasPorPaciente(String numeroDocumento);

    List<CitaResponse> listarCitasPorMedico(String numeroDocumento);

    List<CitaResponse> listarCitasPorEstado(EstadoCita estado);

    List<CitaResponse> listarCitasPorRango(LocalDateTime inicio, LocalDateTime fin);
}
