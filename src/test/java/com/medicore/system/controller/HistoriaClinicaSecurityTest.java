package com.medicore.system.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class HistoriaClinicaSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        Medico medico = medicoRepository.findByNumeroDocumento("79998887");
        if (medico == null) {
            medico = new Medico("79998887", "Carlos", null, "Rojas", null, "CC", LocalDate.of(2020, 1, 1));
            medico.setActivo(true);
            medicoRepository.save(medico);
        }

        if (usuarioRepository.findByEmail("doctor@medicore.com").isEmpty()) {
            Usuario doctor = new Usuario();
            doctor.setNombre("Doctor Historia");
            doctor.setEmail("doctor@medicore.com");
            doctor.setPassword("password");
            doctor.setRol(Rol.DOCTOR);
            doctor.setActivo(true);
            doctor.setMedico(medico);
            usuarioRepository.save(doctor);
        }
    }

    @Test
    void listarHistoriasClinicasSinTokenRetornaUnauthorized() throws Exception {
        mockMvc.perform(get("/historia-clinica"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarHistoriasClinicasConRolUserRetornaForbidden() throws Exception {
        mockMvc.perform(get("/historia-clinica")
                .with(user("user@medicore.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarHistoriasClinicasConRolDoctorAsociadoRetornaOk() throws Exception {
        mockMvc.perform(get("/historia-clinica")
                .with(user("doctor@medicore.com").roles("DOCTOR")))
                .andExpect(status().isOk());
    }

    @Test
    void listarHistoriasClinicasConRolAdminRetornaOk() throws Exception {
        mockMvc.perform(get("/historia-clinica")
                .with(user("admin@medicore.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }
}
