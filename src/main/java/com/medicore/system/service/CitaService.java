package com.medicore.system.service;

import java.util.List;

import com.medicore.system.dto.request.CitaRequest;
import com.medicore.system.dto.response.CitaResponse;
import com.medicore.system.dto.response.MessageResponse;

public interface CitaService {

    List<CitaResponse> listarCitas();

    CitaResponse verCita(Long id);

    CitaResponse crearCita(CitaRequest request);

    CitaResponse editarCita(Long id, CitaRequest request);

    MessageResponse eliminarCita(Long id);

    List<CitaResponse> listarCitasPorPaciente(String numeroDocumento);

    List<CitaResponse> listarCitasPorMedico(String numeroDocumento);
}
