package com.medicore.system.service;

import java.util.List;

import com.medicore.system.dto.request.HistoriaClinicaRequest;
import com.medicore.system.dto.response.HistoriaClinicaResponse;

public interface HistoriaClinicaService {

    HistoriaClinicaResponse crearHistoriaClinica(HistoriaClinicaRequest request);

    List<HistoriaClinicaResponse> listarHistoriasClinicas();

    HistoriaClinicaResponse buscarPorId(Long id);

    List<HistoriaClinicaResponse> buscarPorPaciente(String numeroDocumento);

    List<HistoriaClinicaResponse> buscarPorMedico(String numeroDocumento);

    HistoriaClinicaResponse buscarPorCita(Long citaId);

    HistoriaClinicaResponse actualizarHistoriaClinica(Long id, HistoriaClinicaRequest request);
}
