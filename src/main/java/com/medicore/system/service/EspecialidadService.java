package com.medicore.system.service;

import java.util.List;

import com.medicore.system.dto.request.EspecialidadRequest;
import com.medicore.system.dto.response.EspecialidadResponse;
import com.medicore.system.dto.response.MessageResponse;

public interface EspecialidadService {

    List<EspecialidadResponse> listarEspecialidades();

    EspecialidadResponse verEspecialidad(Long id);

    EspecialidadResponse crearEspecialidad(EspecialidadRequest request);

    EspecialidadResponse editarEspecialidad(Long id, EspecialidadRequest request);

    MessageResponse eliminarEspecialidad(Long id);
}
