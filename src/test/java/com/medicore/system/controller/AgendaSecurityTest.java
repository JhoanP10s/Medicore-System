package com.medicore.system.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
class AgendaSecurityTest {

    private static final String MEDICO_ID = "79998887";
    private static final String DISPONIBILIDAD_BODY = """
            {"numeroDocumentoMedico":"79998887","diaSemana":"LUNES","horaInicio":"08:00:00","horaFin":"12:00:00"}
            """;

    private static final String BLOQUEO_BODY = """
            {"numeroDocumentoMedico":"79998887","fechaInicio":"2026-05-15T10:00:00","fechaFin":"2026-05-15T11:00:00","motivo":"Capacitacion"}
            """;

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

        if (usuarioRepository.findByEmail("doctor@medicore.com").isEmpty()) {
            Usuario doctor = new Usuario();
            doctor.setNombre("Doctor Agenda");
            doctor.setEmail("doctor@medicore.com");
            doctor.setPassword("password");
            doctor.setRol(Rol.DOCTOR);
            doctor.setActivo(true);
            doctor.setMedico(medico);
            usuarioRepository.save(doctor);
        }
    }

    @Test
    void disponibilidadSinTokenRetornaUnauthorized() throws Exception {
        mockMvc.perform(get("/disponibilidad-medica"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userNoPuedeGestionarNiConsultarDisponibilidad() throws Exception {
        mockMvc.perform(get("/disponibilidad-medica").with(user("user@medicore.com").roles("USER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/disponibilidad-medica")
                .with(user("user@medicore.com").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(DISPONIBILIDAD_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void doctorPuedeConsultarDisponibilidadPropia() throws Exception {
        mockMvc.perform(get("/disponibilidad-medica").with(user("doctor@medicore.com").roles("DOCTOR")))
                .andExpect(status().isOk());
    }

    @Test
    void adminPuedeGestionarDisponibilidad() throws Exception {
        mockMvc.perform(post("/disponibilidad-medica")
                .with(user("admin@medicore.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/disponibilidad-medica/999")
                .with(user("admin@medicore.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(DISPONIBILIDAD_BODY))
                .andExpect(status().isNotFound());
        mockMvc.perform(patch("/disponibilidad-medica/999/desactivar")
                .with(user("admin@medicore.com").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void bloqueoSinTokenRetornaUnauthorized() throws Exception {
        mockMvc.perform(get("/bloqueo-agenda"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userNoPuedeGestionarNiConsultarBloqueos() throws Exception {
        mockMvc.perform(get("/bloqueo-agenda").with(user("user@medicore.com").roles("USER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/bloqueo-agenda")
                .with(user("user@medicore.com").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(BLOQUEO_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void doctorPuedeConsultarBloqueosPropios() throws Exception {
        mockMvc.perform(get("/bloqueo-agenda").with(user("doctor@medicore.com").roles("DOCTOR")))
                .andExpect(status().isOk());
    }

    @Test
    void adminPuedeGestionarBloqueos() throws Exception {
        mockMvc.perform(post("/bloqueo-agenda")
                .with(user("admin@medicore.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/bloqueo-agenda/999")
                .with(user("admin@medicore.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(BLOQUEO_BODY))
                .andExpect(status().isNotFound());
        mockMvc.perform(patch("/bloqueo-agenda/999/desactivar")
                .with(user("admin@medicore.com").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void agendaSinTokenRetornaUnauthorized() throws Exception {
        mockMvc.perform(get("/agenda/medico/79998887/disponibles?fecha=2026-05-15&duracionMinutos=30"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userNoPuedeConsultarAgenda() throws Exception {
        mockMvc.perform(get("/agenda/medico/79998887/disponibles?fecha=2026-05-15&duracionMinutos=30")
                .with(user("user@medicore.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void doctorYAdminPuedenConsultarAgendaPermitida() throws Exception {
        mockMvc.perform(get("/agenda/medico/79998887/disponibles?fecha=2026-05-15&duracionMinutos=30")
                .with(user("doctor@medicore.com").roles("DOCTOR")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/agenda/medico/79998887/disponibles?fecha=2026-05-15&duracionMinutos=30")
                .with(user("admin@medicore.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }
}
