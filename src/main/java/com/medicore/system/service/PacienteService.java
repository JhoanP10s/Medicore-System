package com.medicore.system.service;

import java.util.List;

import com.medicore.system.dto.request.PacienteRequest;
import com.medicore.system.dto.response.MessageResponse;
import com.medicore.system.dto.response.PacienteResponse;

public interface PacienteService {

    List<PacienteResponse> listarPacientes();

    PacienteResponse verPaciente(String numeroDocumento);

    MessageResponse crearPaciente(PacienteRequest request);

    MessageResponse editarPaciente(String numeroDocumento, PacienteRequest request);

    MessageResponse eliminarPaciente(String numeroDocumento);
}
