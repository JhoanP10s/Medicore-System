package com.medicore.system.config;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.medicore.system.model.entity.BloqueoAgenda;
import com.medicore.system.model.entity.Cita;
import com.medicore.system.model.entity.DiaSemana;
import com.medicore.system.model.entity.DisponibilidadMedica;
import com.medicore.system.model.entity.Especialidad;
import com.medicore.system.model.entity.EstadoCita;
import com.medicore.system.model.entity.HistoriaClinica;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.model.entity.Paciente;
import com.medicore.system.model.entity.Rol;
import com.medicore.system.model.entity.Usuario;
import com.medicore.system.repository.BloqueoAgendaRepository;
import com.medicore.system.repository.CitaRepository;
import com.medicore.system.repository.DisponibilidadMedicaRepository;
import com.medicore.system.repository.EspecialidadRepository;
import com.medicore.system.repository.HistoriaClinicaRepository;
import com.medicore.system.repository.MedicoRepository;
import com.medicore.system.repository.PacienteRepository;
import com.medicore.system.repository.UsuarioRepository;

@Configuration
@Profile("demo")
public class DemoDataSeeder {

    @Bean
    CommandLineRunner seedDemoData(DemoSeedService demoSeedService) {
        return args -> demoSeedService.seed();
    }

    @Configuration
    static class DemoSeedService {
        private final EspecialidadRepository especialidadRepository;
        private final MedicoRepository medicoRepository;
        private final PacienteRepository pacienteRepository;
        private final DisponibilidadMedicaRepository disponibilidadRepository;
        private final BloqueoAgendaRepository bloqueoRepository;
        private final CitaRepository citaRepository;
        private final HistoriaClinicaRepository historiaRepository;
        private final UsuarioRepository usuarioRepository;
        private final PasswordEncoder passwordEncoder;

        DemoSeedService(
                EspecialidadRepository especialidadRepository,
                MedicoRepository medicoRepository,
                PacienteRepository pacienteRepository,
                DisponibilidadMedicaRepository disponibilidadRepository,
                BloqueoAgendaRepository bloqueoRepository,
                CitaRepository citaRepository,
                HistoriaClinicaRepository historiaRepository,
                UsuarioRepository usuarioRepository,
                PasswordEncoder passwordEncoder) {
            this.especialidadRepository = especialidadRepository;
            this.medicoRepository = medicoRepository;
            this.pacienteRepository = pacienteRepository;
            this.disponibilidadRepository = disponibilidadRepository;
            this.bloqueoRepository = bloqueoRepository;
            this.citaRepository = citaRepository;
            this.historiaRepository = historiaRepository;
            this.usuarioRepository = usuarioRepository;
            this.passwordEncoder = passwordEncoder;
        }

        @Transactional
        public void seed() {
            Especialidad medicinaGeneral = especialidad("Medicina General", "Atencion primaria y controles generales.");
            Especialidad cardiologia = especialidad("Cardiologia", "Diagnostico y seguimiento cardiovascular.");
            Especialidad pediatria = especialidad("Pediatria", "Atencion clinica de ninos y adolescentes.");

            Medico laura = medico("79998887", "Laura", null, "Rojas", "Martinez", "CC", "laura.rojas@medicore.com", "3001112233", medicinaGeneral, true);
            Medico andres = medico("79998888", "Andres", null, "Perez", "Gomez", "CC", "andres.perez@medicore.com", "3002223344", cardiologia, true);
            Medico camila = medico("79998889", "Camila", null, "Torres", "Diaz", "CC", "camila.torres@medicore.com", "3003334455", pediatria, true);

            Paciente ana = paciente("1020304050", "Ana", "Maria", "Gomez", "Lopez", "CC", "ana.gomez@example.com", "3101002000", true);
            Paciente juan = paciente("1020304051", "Juan", null, "Martinez", "Ruiz", "CC", "juan.martinez@example.com", "3101002001", true);
            Paciente sofia = paciente("1020304052", "Sofia", null, "Ramirez", "Castro", "CC", "sofia.ramirez@example.com", "3101002002", true);
            Paciente mateo = paciente("1020304053", "Mateo", null, "Herrera", "Vega", "CC", "mateo.herrera@example.com", "3101002003", true);
            Paciente valentina = paciente("1020304054", "Valentina", null, "Moreno", "Silva", "CC", "valentina.moreno@example.com", "3101002004", true);
            paciente("1020304055", "Ricardo", null, "Inactivo", "Demo", "CC", "ricardo.inactivo@example.com", "3101002005", false);

            usuario("Admin Clinica", "admin@medicore.com", "Admin12345", Rol.ADMIN, null);
            usuario("Dra. Laura Rojas", "laura.doctor@medicore.com", "Doctor12345", Rol.DOCTOR, laura);
            usuario("Usuario Demo", "user@medicore.com", "User12345", Rol.USER, null);

            disponibilidadSemanal(laura, List.of(DiaSemana.LUNES, DiaSemana.MARTES, DiaSemana.MIERCOLES, DiaSemana.JUEVES, DiaSemana.VIERNES), LocalTime.of(8, 0), LocalTime.of(12, 0));
            disponibilidadSemanal(laura, List.of(DiaSemana.LUNES, DiaSemana.MARTES, DiaSemana.MIERCOLES, DiaSemana.JUEVES, DiaSemana.VIERNES), LocalTime.of(14, 0), LocalTime.of(17, 0));
            disponibilidadSemanal(andres, List.of(DiaSemana.LUNES, DiaSemana.MIERCOLES, DiaSemana.VIERNES), LocalTime.of(9, 0), LocalTime.of(13, 0));
            disponibilidadSemanal(camila, List.of(DiaSemana.MARTES, DiaSemana.JUEVES), LocalTime.of(8, 0), LocalTime.of(12, 0));

            LocalDate proximoLunes = proximaFecha(DayOfWeek.MONDAY);
            LocalDate proximoMartes = proximaFecha(DayOfWeek.TUESDAY);
            LocalDate proximoMiercoles = proximaFecha(DayOfWeek.WEDNESDAY);
            LocalDate proximoViernes = proximaFecha(DayOfWeek.FRIDAY);

            bloqueo("DEMO - Capacitacion medica", andres, proximoViernes.atTime(11, 0), proximoViernes.atTime(12, 0), true);
            bloqueo("DEMO - Reunion de comite clinico", laura, proximoMiercoles.atTime(15, 0), proximoMiercoles.atTime(16, 0), true);
            bloqueo("DEMO - Bloqueo historico inactivo", camila, proximoMartes.atTime(10, 0), proximoMartes.atTime(11, 0), false);

            Cita programada = cita("DEMO - Control general programado", ana, laura, proximoLunes.atTime(8, 30), 30, EstadoCita.PROGRAMADA, "Paciente asiste a control preventivo.");
            Cita confirmada = cita("DEMO - Seguimiento de presion arterial", juan, laura, proximoLunes.atTime(9, 30), 30, EstadoCita.CONFIRMADA, "Cita confirmada para seguimiento.");
            Cita completadaConHistoria = cita("DEMO - Evaluacion cardiovascular completada", sofia, andres, proximoMiercoles.atTime(9, 30), 30, EstadoCita.COMPLETADA, "Atencion completada con registro clinico.");
            Cita completadaLaura = cita("DEMO - Control general completado con historia", valentina, laura, proximoLunes.atTime(11, 30), 30, EstadoCita.COMPLETADA, "Atencion finalizada por medicina general.");
            Cita completadaSinHistoria = cita("DEMO - Consulta pediatrica pendiente de historia", mateo, camila, proximoMartes.atTime(8, 30), 30, EstadoCita.COMPLETADA, "Pendiente por registrar historia clinica.");
            cita("DEMO - Cita cancelada por paciente", valentina, laura, proximoLunes.atTime(10, 30), 30, EstadoCita.CANCELADA, "Cancelada logicamente para demostrar estados.");

            historia(completadaConHistoria, "Dolor toracico leve ya resuelto.", "Control general sin hallazgos de alarma.", "Educacion en signos de alarma y control en tres meses.", "Paciente estable durante la valoracion.");
            historia(completadaLaura, "Cefalea ocasional y registros tensionales elevados.", "Hipertension en seguimiento.", "Monitoreo de presion arterial, dieta baja en sodio y control medico.", "Registro demo asociado a cita completada.");
        }

        private Especialidad especialidad(String nombre, String descripcion) {
            Optional<Especialidad> existente = especialidadRepository.findAll().stream()
                    .filter(item -> nombre.equalsIgnoreCase(item.getNombre()))
                    .findFirst();
            if (existente.isPresent()) {
                return existente.get();
            }
            Especialidad especialidad = new Especialidad();
            especialidad.setNombre(nombre);
            especialidad.setDescripcion(descripcion);
            especialidad.setActivo(true);
            return especialidadRepository.save(especialidad);
        }

        private Medico medico(String documento, String primerNombre, String segundoNombre, String primerApellido,
                String segundoApellido, String tipoDocumento, String email, String telefono, Especialidad especialidad, boolean activo) {
            Medico medico = medicoRepository.findByNumeroDocumento(documento);
            if (medico == null) {
                medico = new Medico(documento, primerNombre, segundoNombre, primerApellido, segundoApellido, tipoDocumento, LocalDate.of(2020, 1, 1));
            }
            medico.setPrimerNombre(primerNombre);
            medico.setSegundoNombre(segundoNombre);
            medico.setPrimerApellido(primerApellido);
            medico.setSegundoApellido(segundoApellido);
            medico.setTipoDocumento(tipoDocumento);
            medico.setEmail(email);
            medico.setTelefono(telefono);
            medico.setEspecialidad(especialidad);
            medico.setActivo(activo);
            return medicoRepository.save(medico);
        }

        private Paciente paciente(String documento, String primerNombre, String segundoNombre, String primerApellido,
                String segundoApellido, String tipoDocumento, String email, String telefono, boolean activo) {
            Paciente paciente = pacienteRepository.findByNumeroDocumento(documento);
            if (paciente == null) {
                paciente = new Paciente(documento, primerNombre, segundoNombre, primerApellido, segundoApellido, tipoDocumento, LocalDate.of(2020, 1, 1));
            }
            paciente.setPrimerNombre(primerNombre);
            paciente.setSegundoNombre(segundoNombre);
            paciente.setPrimerApellido(primerApellido);
            paciente.setSegundoApellido(segundoApellido);
            paciente.setTipoDocumento(tipoDocumento);
            paciente.setEmail(email);
            paciente.setTelefono(telefono);
            paciente.setActivo(activo);
            return pacienteRepository.save(paciente);
        }

        private Usuario usuario(String nombre, String email, String password, Rol rol, Medico medico) {
            return usuarioRepository.findByEmail(email).orElseGet(() -> {
                Usuario usuario = new Usuario();
                usuario.setNombre(nombre);
                usuario.setEmail(email);
                usuario.setPassword(passwordEncoder.encode(password));
                usuario.setRol(rol);
                usuario.setActivo(true);
                usuario.setMedico(medico);
                return usuarioRepository.save(usuario);
            });
        }

        private void disponibilidadSemanal(Medico medico, List<DiaSemana> dias, LocalTime inicio, LocalTime fin) {
            for (DiaSemana dia : dias) {
                boolean existe = disponibilidadRepository.findByMedicoNumeroDocumentoAndDiaSemanaAndActivoTrue(medico.getNumeroDocumento(), dia).stream()
                        .anyMatch(item -> inicio.equals(item.getHoraInicio()) && fin.equals(item.getHoraFin()));
                if (!existe) {
                    DisponibilidadMedica disponibilidad = new DisponibilidadMedica();
                    disponibilidad.setMedico(medico);
                    disponibilidad.setDiaSemana(dia);
                    disponibilidad.setHoraInicio(inicio);
                    disponibilidad.setHoraFin(fin);
                    disponibilidad.setActivo(true);
                    disponibilidadRepository.save(disponibilidad);
                }
            }
        }

        private BloqueoAgenda bloqueo(String motivo, Medico medico, LocalDateTime inicio, LocalDateTime fin, boolean activo) {
            Optional<BloqueoAgenda> existente = bloqueoRepository.findByMedicoNumeroDocumento(medico.getNumeroDocumento()).stream()
                    .filter(item -> motivo.equals(item.getMotivo()))
                    .findFirst();
            if (existente.isPresent()) {
                BloqueoAgenda bloqueo = existente.get();
                bloqueo.setFechaInicio(inicio);
                bloqueo.setFechaFin(fin);
                bloqueo.setActivo(activo);
                return bloqueoRepository.save(bloqueo);
            }
            BloqueoAgenda bloqueo = new BloqueoAgenda();
            bloqueo.setMedico(medico);
            bloqueo.setFechaInicio(inicio);
            bloqueo.setFechaFin(fin);
            bloqueo.setMotivo(motivo);
            bloqueo.setActivo(activo);
            return bloqueoRepository.save(bloqueo);
        }

        private Cita cita(String motivo, Paciente paciente, Medico medico, LocalDateTime fechaHora, Integer duracion,
                EstadoCita estado, String observaciones) {
            Optional<Cita> existente = citaRepository.findAll().stream()
                    .filter(item -> motivo.equals(item.getMotivo()))
                    .findFirst();
            if (existente.isPresent()) {
                Cita cita = existente.get();
                cita.setPaciente(paciente);
                cita.setMedico(medico);
                cita.setFechaHora(fechaHora);
                cita.setDuracionMinutos(duracion);
                cita.setEstado(estado);
                cita.setObservaciones(observaciones);
                return citaRepository.save(cita);
            }
            Cita cita = new Cita();
            cita.setPaciente(paciente);
            cita.setMedico(medico);
            cita.setFechaHora(fechaHora);
            cita.setDuracionMinutos(duracion);
            cita.setEstado(estado);
            cita.setMotivo(motivo);
            cita.setObservaciones(observaciones);
            return citaRepository.save(cita);
        }

        private void historia(Cita cita, String sintomas, String diagnostico, String tratamiento, String observaciones) {
            if (historiaRepository.existsByCitaId(cita.getId())) {
                return;
            }
            HistoriaClinica historia = new HistoriaClinica();
            historia.setCita(cita);
            historia.setPaciente(cita.getPaciente());
            historia.setMedico(cita.getMedico());
            historia.setFechaRegistro(LocalDateTime.now());
            historia.setSintomas(sintomas);
            historia.setDiagnostico(diagnostico);
            historia.setTratamiento(tratamiento);
            historia.setObservaciones(observaciones);
            historiaRepository.save(historia);
        }

        private LocalDate proximaFecha(DayOfWeek dia) {
            LocalDate hoy = LocalDate.now();
            LocalDate fecha = hoy.with(TemporalAdjusters.nextOrSame(dia));
            if (!fecha.isAfter(hoy)) {
                fecha = fecha.plusWeeks(1);
            }
            return fecha;
        }
    }
}
