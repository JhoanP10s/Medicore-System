package com.medicore.system.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.medicore.system.model.entity.Medico;
import com.medicore.system.model.entity.Rol;
import com.medicore.system.model.entity.Usuario;
import com.medicore.system.repository.MedicoRepository;
import com.medicore.system.repository.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerSecurityTest {

    private static final String MEDICO_ID = "79998889";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        Medico medico = medicoRepository.findByNumeroDocumento(MEDICO_ID);
        if (medico == null) {
            medico = new Medico(MEDICO_ID, "Carlos", null, "Rojas", null, "CC", LocalDate.of(2020, 1, 1));
            medico.setActivo(true);
            medicoRepository.save(medico);
        }

        if (usuarioRepository.findByEmail("doctor.dashboard@medicore.com").isEmpty()) {
            Usuario doctor = new Usuario();
            doctor.setNombre("Doctor Dashboard");
            doctor.setEmail("doctor.dashboard@medicore.com");
            doctor.setPassword("password");
            doctor.setRol(Rol.DOCTOR);
            doctor.setActivo(true);
            doctor.setMedico(medico);
            usuarioRepository.save(doctor);
        }
    }

    @Test
    void resumenSinTokenRetornaUnauthorized() throws Exception {
        mockMvc.perform(get("/dashboard/resumen"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminAutenticadoPuedeConsultarResumen() throws Exception {
        mockMvc.perform(get("/dashboard/resumen")
                .with(user("admin@medicore.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("ADMIN"))
                .andExpect(jsonPath("$.admin").exists())
                .andExpect(jsonPath("$.doctor").doesNotExist());
    }

    @Test
    void doctorAutenticadoPuedeConsultarResumenPropio() throws Exception {
        mockMvc.perform(get("/dashboard/resumen")
                .with(user("doctor.dashboard@medicore.com").roles("DOCTOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("DOCTOR"))
                .andExpect(jsonPath("$.doctor").exists())
                .andExpect(jsonPath("$.admin").doesNotExist());
    }

    @Test
    void userAutenticadoPuedeConsultarResumenLimitado() throws Exception {
        mockMvc.perform(get("/dashboard/resumen")
                .with(user("user@medicore.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("USER"))
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.admin").doesNotExist())
                .andExpect(jsonPath("$.doctor").doesNotExist());
    }
}
