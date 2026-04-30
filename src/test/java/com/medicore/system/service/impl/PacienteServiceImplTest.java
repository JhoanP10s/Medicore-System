package com.medicore.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medicore.system.dto.response.MessageResponse;
import com.medicore.system.mapper.PacienteMapper;
import com.medicore.system.model.entity.Paciente;
import com.medicore.system.repository.CitaRepository;
import com.medicore.system.repository.PacienteRepository;

@ExtendWith(MockitoExtension.class)
class PacienteServiceImplTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private CitaRepository citaRepository;

    private PacienteServiceImpl pacienteService;

    @BeforeEach
    void setUp() {
        pacienteService = new PacienteServiceImpl(
                pacienteRepository,
                citaRepository,
                new PacienteMapper());
    }

    @Test
    void eliminarPacienteConCitasAsociadasAplicaEliminacionLogica() {
        Paciente paciente = new Paciente();
        paciente.setNumeroDocumento("1020304050");
        paciente.setActivo(true);

        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(paciente);
        when(citaRepository.existsByPacienteNumeroDocumento("1020304050")).thenReturn(true);

        MessageResponse response = pacienteService.eliminarPaciente("1020304050");

        assertThat(response.getMessage()).contains("desactivado");
        assertThat(paciente.getActivo()).isFalse();
        verify(pacienteRepository).save(paciente);
        verify(pacienteRepository, never()).delete(paciente);
    }

    @Test
    void eliminarPacienteSinCitasAsociadasEliminaFisicamente() {
        Paciente paciente = new Paciente();
        paciente.setNumeroDocumento("1020304050");
        paciente.setActivo(true);

        when(pacienteRepository.findByNumeroDocumento("1020304050")).thenReturn(paciente);
        when(citaRepository.existsByPacienteNumeroDocumento("1020304050")).thenReturn(false);

        MessageResponse response = pacienteService.eliminarPaciente("1020304050");

        assertThat(response.getMessage()).isEqualTo("Paciente eliminado exitosamente");
        verify(pacienteRepository).delete(paciente);
        verify(pacienteRepository, never()).save(paciente);
    }
}
