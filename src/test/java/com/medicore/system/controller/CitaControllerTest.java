package com.medicore.system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.medicore.system.config.GlobalExceptionHandler;
import com.medicore.system.dto.request.CitaRequest;
import com.medicore.system.dto.response.CitaResponse;
import com.medicore.system.exception.BusinessException;
import com.medicore.system.exception.ResourceNotFoundException;
import com.medicore.system.security.CustomUserDetailsService;
import com.medicore.system.security.JwtService;
import com.medicore.system.service.CitaService;

@WebMvcTest(CitaController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class CitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CitaService citaService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void verCitaCuandoExisteRetornaOk() throws Exception {
        CitaResponse response = new CitaResponse();
        response.setId(1L);
        response.setMotivo("Control general");
        response.setPacienteNumeroDocumento("1020304050");
        response.setMedicoNumeroDocumento("79998887");

        when(citaService.verCita(1L)).thenReturn(response);

        mockMvc.perform(get("/cita/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.motivo").value("Control general"))
                .andExpect(jsonPath("$.pacienteNumeroDocumento").value("1020304050"));
    }

    @Test
    void verCitaCuandoNoExisteRetornaNotFoundConErrorResponse() throws Exception {
        when(citaService.verCita(99L)).thenThrow(new ResourceNotFoundException("No existe cita registrada"));

        mockMvc.perform(get("/cita/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("No existe cita registrada"))
                .andExpect(jsonPath("$.path").value("/cita/99"));
    }

    @Test
    void crearCitaConBodyValidoRetornaOk() throws Exception {
        CitaResponse response = new CitaResponse();
        response.setId(1L);
        response.setFechaHora(LocalDateTime.now().plusDays(1));
        response.setMotivo("Control general");

        when(citaService.crearCita(any(CitaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/cita")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"numeroDocumentoPaciente":"1020304050","numeroDocumentoMedico":"79998887","fechaHora":"2030-05-15T09:30:00","motivo":"Control general"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.motivo").value("Control general"));
    }

    @Test
    void crearCitaConBodyInvalidoRetornaBadRequest() throws Exception {
        mockMvc.perform(post("/cita")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"numeroDocumentoPaciente":"","numeroDocumentoMedico":"79998887","fechaHora":"2030-05-15T09:30:00","motivo":""}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void crearCitaCuandoMedicoEstaOcupadoRetornaConflict() throws Exception {
        when(citaService.crearCita(any(CitaRequest.class)))
                .thenThrow(new BusinessException("El medico ya tiene una cita programada en esa fecha y hora."));

        mockMvc.perform(post("/cita")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"numeroDocumentoPaciente":"1020304050","numeroDocumentoMedico":"79998887","fechaHora":"2030-05-15T09:30:00","motivo":"Control general"}
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("El medico ya tiene una cita programada en esa fecha y hora."));
    }
}
