package com.medicore.system.service;

import java.util.List;

import com.medicore.system.dto.request.MedicoRequest;
import com.medicore.system.dto.response.MedicoResponse;
import com.medicore.system.dto.response.MessageResponse;

public interface MedicoService {

    List<MedicoResponse> listarMedicos();

    MedicoResponse verMedico(String numeroDocumento);

    MessageResponse crearMedico(MedicoRequest request);

    MessageResponse editarMedico(String numeroDocumento, MedicoRequest request);

    MessageResponse eliminarMedico(String numeroDocumento);
}
