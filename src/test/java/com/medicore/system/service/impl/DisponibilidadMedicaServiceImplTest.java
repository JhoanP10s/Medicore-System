package com.medicore.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medicore.system.dto.request.DisponibilidadMedicaRequest;
import com.medicore.system.exception.BadRequestException;
import com.medicore.system.exception.BusinessException;
import com.medicore.system.mapper.DisponibilidadMedicaMapper;
import com.medicore.system.model.entity.DiaSemana;
import com.medicore.system.model.entity.DisponibilidadMedica;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.repository.DisponibilidadMedicaRepository;
import com.medicore.system.repository.MedicoRepository;

@ExtendWith(MockitoExtension.class)
class DisponibilidadMedicaServiceImplTest {

    @Mock private DisponibilidadMedicaRepository disponibilidadRepository;
    @Mock private MedicoRepository medicoRepository;
    private DisponibilidadMedicaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DisponibilidadMedicaServiceImpl(disponibilidadRepository, medicoRepository, new DisponibilidadMedicaMapper());
    }

    @Test
    void crearDisponibilidadValidaGuarda() {
        DisponibilidadMedicaRequest request = request(LocalTime.of(8, 0), LocalTime.of(12, 0));
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico(true));
        when(disponibilidadRepository.findByMedicoNumeroDocumentoAndDiaSemanaAndActivoTrue("79998887", DiaSemana.LUNES)).thenReturn(List.of());
        when(disponibilidadRepository.save(any(DisponibilidadMedica.class))).thenAnswer(inv -> {
            DisponibilidadMedica d = inv.getArgument(0);
            d.setId(1L);
            return d;
        });
        assertThat(service.crear(request).getId()).isEqualTo(1L);
    }

    @Test
    void crearDisponibilidadConMedicoInactivoLanzaBusinessException() {
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico(false));
        assertThatThrownBy(() -> service.crear(request(LocalTime.of(8, 0), LocalTime.of(12, 0))))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void crearDisponibilidadConRangoInvalidoLanzaBadRequest() {
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico(true));
        assertThatThrownBy(() -> service.crear(request(LocalTime.of(12, 0), LocalTime.of(8, 0))))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void crearDisponibilidadSolapadaLanzaBusinessException() {
        DisponibilidadMedica existente = disponibilidad(LocalTime.of(9, 0), LocalTime.of(11, 0));
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico(true));
        when(disponibilidadRepository.findByMedicoNumeroDocumentoAndDiaSemanaAndActivoTrue("79998887", DiaSemana.LUNES)).thenReturn(List.of(existente));
        assertThatThrownBy(() -> service.crear(request(LocalTime.of(8, 0), LocalTime.of(10, 0))))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void desactivarDisponibilidadMarcaInactiva() {
        DisponibilidadMedica disponibilidad = disponibilidad(LocalTime.of(8, 0), LocalTime.of(12, 0));
        disponibilidad.setId(1L);
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(disponibilidad));
        when(disponibilidadRepository.save(disponibilidad)).thenReturn(disponibilidad);
        service.desactivar(1L);
        assertThat(disponibilidad.getActivo()).isFalse();
    }

    @Test
    void actualizarDisponibilidadNoSeComparaContraSiMisma() {
        DisponibilidadMedica disponibilidad = disponibilidad(LocalTime.of(8, 0), LocalTime.of(12, 0));
        disponibilidad.setId(1L);
        DisponibilidadMedicaRequest request = request(LocalTime.of(8, 0), LocalTime.of(12, 0));
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(disponibilidad));
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico(true));
        when(disponibilidadRepository.findByMedicoNumeroDocumentoAndDiaSemanaAndActivoTrueAndIdNot("79998887", DiaSemana.LUNES, 1L))
                .thenReturn(List.of());
        when(disponibilidadRepository.save(disponibilidad)).thenReturn(disponibilidad);

        assertThat(service.actualizar(1L, request).getHoraInicio()).isEqualTo(LocalTime.of(8, 0));
    }

    @Test
    void permiteVariosBloquesNoSolapadosMismoDia() {
        DisponibilidadMedica existente = disponibilidad(LocalTime.of(8, 0), LocalTime.of(10, 0));
        DisponibilidadMedicaRequest request = request(LocalTime.of(10, 0), LocalTime.of(12, 0));
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico(true));
        when(disponibilidadRepository.findByMedicoNumeroDocumentoAndDiaSemanaAndActivoTrue("79998887", DiaSemana.LUNES))
                .thenReturn(List.of(existente));
        when(disponibilidadRepository.save(any(DisponibilidadMedica.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(service.crear(request).getHoraInicio()).isEqualTo(LocalTime.of(10, 0));
    }

    private DisponibilidadMedicaRequest request(LocalTime inicio, LocalTime fin) {
        DisponibilidadMedicaRequest request = new DisponibilidadMedicaRequest();
        request.setNumeroDocumentoMedico("79998887");
        request.setDiaSemana(DiaSemana.LUNES);
        request.setHoraInicio(inicio);
        request.setHoraFin(fin);
        return request;
    }

    private DisponibilidadMedica disponibilidad(LocalTime inicio, LocalTime fin) {
        DisponibilidadMedica disponibilidad = new DisponibilidadMedica();
        disponibilidad.setMedico(medico(true));
        disponibilidad.setDiaSemana(DiaSemana.LUNES);
        disponibilidad.setHoraInicio(inicio);
        disponibilidad.setHoraFin(fin);
        disponibilidad.setActivo(true);
        return disponibilidad;
    }

    private Medico medico(boolean activo) {
        Medico medico = new Medico();
        medico.setNumeroDocumento("79998887");
        medico.setPrimerNombre("Carlos");
        medico.setPrimerApellido("Rojas");
        medico.setActivo(activo);
        return medico;
    }
}
