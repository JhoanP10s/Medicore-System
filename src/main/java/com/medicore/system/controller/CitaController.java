package com.medicore.system.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medicore.system.dto.request.CambiarEstadoCitaRequest;
import com.medicore.system.dto.request.CitaRequest;
import com.medicore.system.dto.response.CitaResponse;
import com.medicore.system.dto.response.ErrorResponse;
import com.medicore.system.dto.response.MessageResponse;
import com.medicore.system.model.entity.EstadoCita;
import com.medicore.system.service.CitaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cita")
@Tag(name = "Citas medicas", description = "Agenda clinica: programacion, estados, cancelacion y consulta de citas medicas.")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @Operation(summary = "Listar citas", description = "Retorna todas las citas medicas registradas.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<CitaResponse>> listarCitas() {
        return ResponseEntity.ok(citaService.listarCitas());
    }

    @Operation(summary = "Consultar cita", description = "Obtiene una cita medica por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cita encontrada"),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("{id}")
    public ResponseEntity<CitaResponse> verCita(@Parameter(description = "Identificador de la cita", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(citaService.verCita(id));
    }

    @Operation(summary = "Crear cita medica", description = "Programa una cita para un paciente y medico activos. Valida fechas futuras, duracion y solapamientos de agenda del medico.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Datos de la cita a programar",
            content = @Content(schema = @Schema(implementation = CitaRequest.class), examples = @ExampleObject(value = """
                    {"numeroDocumentoPaciente":"1020304050","numeroDocumentoMedico":"79998887","fechaHora":"2026-05-15T09:30:00","motivo":"Control general","observaciones":"Paciente estable","duracionMinutos":30}
                    """)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cita creada correctamente", content = @Content(schema = @Schema(implementation = CitaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Paciente o medico no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Regla de negocio incumplida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<CitaResponse> crearCita(@Valid @RequestBody CitaRequest request) {
        return ResponseEntity.ok(citaService.crearCita(request));
    }

    @Operation(summary = "Editar cita medica", description = "Actualiza una cita existente si no esta completada ni cancelada.")
    @PutMapping("{id}")
    public ResponseEntity<CitaResponse> editarCita(@PathVariable Long id, @Valid @RequestBody CitaRequest request) {
        return ResponseEntity.ok(citaService.editarCita(id, request));
    }

    @Operation(summary = "Cambiar estado de cita", description = "Aplica transiciones validas: PROGRAMADA a CONFIRMADA/CANCELADA y CONFIRMADA a COMPLETADA/CANCELADA.")
    @PatchMapping("{id}/estado")
    public ResponseEntity<CitaResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoCitaRequest request) {
        return ResponseEntity.ok(citaService.cambiarEstado(id, request.getEstado()));
    }

    @Operation(summary = "Cancelar cita", description = "Cancela logicamente una cita. No elimina fisicamente el registro.")
    @PatchMapping("{id}/cancelar")
    public ResponseEntity<MessageResponse> cancelarCita(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.cancelarCita(id));
    }

    @Operation(summary = "Eliminar cita", description = "Compatibilidad con versiones previas: cancela logicamente la cita en lugar de eliminarla fisicamente.")
    @DeleteMapping("{id}")
    public ResponseEntity<MessageResponse> eliminarCita(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.eliminarCita(id));
    }

    @Operation(summary = "Listar citas por paciente", description = "Retorna las citas asociadas a un paciente por numero de documento.")
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<CitaResponse>> listarCitasPorPaciente(@PathVariable String pacienteId) {
        return ResponseEntity.ok(citaService.listarCitasPorPaciente(pacienteId));
    }

    @Operation(summary = "Listar citas por medico", description = "Retorna las citas asociadas a un medico por numero de documento.")
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<CitaResponse>> listarCitasPorMedico(@PathVariable String medicoId) {
        return ResponseEntity.ok(citaService.listarCitasPorMedico(medicoId));
    }

    @Operation(summary = "Listar citas por estado", description = "Retorna las citas filtradas por estado clinico de agenda.")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CitaResponse>> listarCitasPorEstado(@PathVariable EstadoCita estado) {
        return ResponseEntity.ok(citaService.listarCitasPorEstado(estado));
    }

    @Operation(summary = "Listar citas por rango", description = "Retorna las citas cuya fecha y hora estan dentro del rango solicitado.")
    @GetMapping("/rango")
    public ResponseEntity<List<CitaResponse>> listarCitasPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(citaService.listarCitasPorRango(inicio, fin));
    }
}
