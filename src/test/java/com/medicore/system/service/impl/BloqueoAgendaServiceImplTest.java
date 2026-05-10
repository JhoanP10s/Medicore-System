package com.medicore.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medicore.system.dto.request.BloqueoAgendaRequest;
import com.medicore.system.exception.BadRequestException;
import com.medicore.system.exception.BusinessException;
import com.medicore.system.mapper.BloqueoAgendaMapper;
import com.medicore.system.model.entity.BloqueoAgenda;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.repository.BloqueoAgendaRepository;
import com.medicore.system.repository.MedicoRepository;

@ExtendWith(MockitoExtension.class)
class BloqueoAgendaServiceImplTest {

    @Mock private BloqueoAgendaRepository bloqueoRepository;
    @Mock private MedicoRepository medicoRepository;
    private BloqueoAgendaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new BloqueoAgendaServiceImpl(bloqueoRepository, medicoRepository, new BloqueoAgendaMapper());
    }

    @Test
    void crearBloqueoValidoGuarda() {
        BloqueoAgendaRequest request = request(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1));
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico(true));
        when(bloqueoRepository.save(any(BloqueoAgenda.class))).thenAnswer(inv -> {
            BloqueoAgenda b = inv.getArgument(0);
            b.setId(1L);
            return b;
        });
        assertThat(service.crear(request).getId()).isEqualTo(1L);
    }

    @Test
    void crearBloqueoConMedicoInactivoLanzaBusinessException() {
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico(false));
        assertThatThrownBy(() -> service.crear(request(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1))))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void crearBloqueoConRangoInvalidoLanzaBadRequest() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(1);
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico(true));
        assertThatThrownBy(() -> service.crear(request(fecha, fecha)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void desactivarBloqueoMarcaInactivo() {
        BloqueoAgenda bloqueo = new BloqueoAgenda();
        bloqueo.setId(1L);
        bloqueo.setActivo(true);
        bloqueo.setMedico(medico(true));
        when(bloqueoRepository.findById(1L)).thenReturn(Optional.of(bloqueo));
        when(bloqueoRepository.save(bloqueo)).thenReturn(bloqueo);
        service.desactivar(1L);
        assertThat(bloqueo.getActivo()).isFalse();
    }

    private BloqueoAgendaRequest request(LocalDateTime inicio, LocalDateTime fin) {
        BloqueoAgendaRequest request = new BloqueoAgendaRequest();
        request.setNumeroDocumentoMedico("79998887");
        request.setFechaInicio(inicio);
        request.setFechaFin(fin);
        request.setMotivo("Capacitacion");
        return request;
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
