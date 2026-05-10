package com.medicore.system.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medicore.system.dto.request.HistoriaClinicaRequest;
import com.medicore.system.dto.response.ErrorResponse;
import com.medicore.system.dto.response.HistoriaClinicaResponse;
import com.medicore.system.service.HistoriaClinicaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/historia-clinica")
@Tag(name = "Historia clinica", description = "Registro clinico derivado de citas confirmadas o completadas.")
public class HistoriaClinicaController {

    private final HistoriaClinicaService historiaClinicaService;

    public HistoriaClinicaController(HistoriaClinicaService historiaClinicaService) {
        this.historiaClinicaService = historiaClinicaService;
    }

    @Operation(summary = "Crear historia clinica", description = "Crea una historia clinica para una cita existente confirmada o completada. Copia paciente y medico desde la cita.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = HistoriaClinicaRequest.class), examples = @ExampleObject(value = """
                    {"citaId":1,"sintomas":"Dolor toracico leve","diagnostico":"Hipertension en seguimiento","tratamiento":"Control de presion y seguimiento","observaciones":"Paciente estable"}
                    """)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historia clinica creada", content = @Content(schema = @Schema(implementation = HistoriaClinicaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Regla de negocio incumplida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<HistoriaClinicaResponse> crearHistoriaClinica(@Valid @RequestBody HistoriaClinicaRequest request) {
        return ResponseEntity.ok(historiaClinicaService.crearHistoriaClinica(request));
    }

    @Operation(summary = "Listar historias clinicas", description = "Retorna todas las historias clinicas registradas.")
    @GetMapping
    public ResponseEntity<List<HistoriaClinicaResponse>> listarHistoriasClinicas() {
        return ResponseEntity.ok(historiaClinicaService.listarHistoriasClinicas());
    }

    @Operation(summary = "Consultar historia clinica", description = "Obtiene una historia clinica por su identificador.")
    @GetMapping("{id}")
    public ResponseEntity<HistoriaClinicaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(historiaClinicaService.buscarPorId(id));
    }

    @Operation(summary = "Historias clinicas por paciente", description = "Retorna historias clinicas asociadas a un paciente por numero de documento.")
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<HistoriaClinicaResponse>> buscarPorPaciente(@PathVariable String pacienteId) {
        return ResponseEntity.ok(historiaClinicaService.buscarPorPaciente(pacienteId));
    }

    @Operation(summary = "Historias clinicas por medico", description = "Retorna historias clinicas asociadas a un medico por numero de documento.")
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<HistoriaClinicaResponse>> buscarPorMedico(@PathVariable String medicoId) {
        return ResponseEntity.ok(historiaClinicaService.buscarPorMedico(medicoId));
    }

    @Operation(summary = "Historia clinica por cita", description = "Obtiene la historia clinica asociada a una cita.")
    @GetMapping("/cita/{citaId}")
    public ResponseEntity<HistoriaClinicaResponse> buscarPorCita(@PathVariable Long citaId) {
        return ResponseEntity.ok(historiaClinicaService.buscarPorCita(citaId));
    }

    @Operation(summary = "Actualizar historia clinica", description = "Actualiza los datos clinicos de una historia clinica sin cambiar su cita, paciente ni medico.")
    @PutMapping("{id}")
    public ResponseEntity<HistoriaClinicaResponse> actualizarHistoriaClinica(
            @PathVariable Long id,
            @Valid @RequestBody HistoriaClinicaRequest request) {
        return ResponseEntity.ok(historiaClinicaService.actualizarHistoriaClinica(id, request));
    }
}
