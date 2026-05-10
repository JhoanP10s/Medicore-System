package com.medicore.system.service;

import java.util.List;

import com.medicore.system.dto.request.DisponibilidadMedicaRequest;
import com.medicore.system.dto.response.DisponibilidadMedicaResponse;
import com.medicore.system.dto.response.MessageResponse;

public interface DisponibilidadMedicaService {
    DisponibilidadMedicaResponse crear(DisponibilidadMedicaRequest request);
    List<DisponibilidadMedicaResponse> listar();
    DisponibilidadMedicaResponse buscarPorId(Long id);
    List<DisponibilidadMedicaResponse> buscarPorMedico(String medicoId);
    DisponibilidadMedicaResponse actualizar(Long id, DisponibilidadMedicaRequest request);
    MessageResponse desactivar(Long id);
}
