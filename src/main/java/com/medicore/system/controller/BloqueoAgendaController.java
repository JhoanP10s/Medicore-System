package com.medicore.system.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medicore.system.dto.request.BloqueoAgendaRequest;
import com.medicore.system.dto.response.BloqueoAgendaResponse;
import com.medicore.system.dto.response.ErrorResponse;
import com.medicore.system.dto.response.MessageResponse;
import com.medicore.system.service.BloqueoAgendaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/bloqueo-agenda")
@Tag(name = "Bloqueos de agenda", description = "Administra rangos de tiempo en los que un medico no puede recibir citas.")
public class BloqueoAgendaController {

    private final BloqueoAgendaService bloqueoService;

    public BloqueoAgendaController(BloqueoAgendaService bloqueoService) {
        this.bloqueoService = bloqueoService;
    }

    @Operation(summary = "Crear bloqueo de agenda", description = "Crea un bloqueo activo para un medico existente y activo.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = BloqueoAgendaRequest.class), examples = @ExampleObject(value = """
            {"numeroDocumentoMedico":"79998887","fechaInicio":"2026-05-15T10:00:00","fechaFin":"2026-05-15T11:00:00","motivo":"Capacitacion medica"}
            """)))
    @ApiResponse(responseCode = "200", description = "Bloqueo creado", content = @Content(schema = @Schema(implementation = BloqueoAgendaResponse.class)))
    @ApiResponse(responseCode = "409", description = "Medico inactivo", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<BloqueoAgendaResponse> crear(@Valid @RequestBody BloqueoAgendaRequest request) {
        return ResponseEntity.ok(bloqueoService.crear(request));
    }

    @Operation(summary = "Listar bloqueos", description = "Lista todos los bloqueos de agenda registrados.")
    @GetMapping
    public ResponseEntity<List<BloqueoAgendaResponse>> listar() {
        return ResponseEntity.ok(bloqueoService.listar());
    }

    @Operation(summary = "Consultar bloqueo", description = "Consulta un bloqueo por id.")
    @GetMapping("{id}")
    public ResponseEntity<BloqueoAgendaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(bloqueoService.buscarPorId(id));
    }

    @Operation(summary = "Bloqueos por medico", description = "Lista bloqueos de un medico por numero de documento.")
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<BloqueoAgendaResponse>> buscarPorMedico(@PathVariable String medicoId) {
        return ResponseEntity.ok(bloqueoService.buscarPorMedico(medicoId));
    }

    @Operation(summary = "Bloqueos por rango", description = "Lista todos los bloqueos, activos e inactivos, que se solapan con el rango consultado. Este endpoint se conserva para administracion.")
    @GetMapping("/rango")
    public ResponseEntity<List<BloqueoAgendaResponse>> buscarPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(bloqueoService.buscarPorRango(inicio, fin));
    }

    @Operation(summary = "Bloqueos activos por rango", description = "Lista solo bloqueos activos que se solapan con el rango consultado. Endpoint operativo para validaciones de agenda.")
    @GetMapping("/rango/activos")
    public ResponseEntity<List<BloqueoAgendaResponse>> buscarActivosPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(bloqueoService.buscarActivosPorRango(inicio, fin));
    }

    @Operation(summary = "Actualizar bloqueo", description = "Actualiza rango, medico o motivo de un bloqueo existente.")
    @PutMapping("{id}")
    public ResponseEntity<BloqueoAgendaResponse> actualizar(@PathVariable Long id, @Valid @RequestBody BloqueoAgendaRequest request) {
        return ResponseEntity.ok(bloqueoService.actualizar(id, request));
    }

    @Operation(summary = "Desactivar bloqueo", description = "Desactiva logicamente un bloqueo. No elimina fisicamente el registro.")
    @PatchMapping("{id}/desactivar")
    public ResponseEntity<MessageResponse> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(bloqueoService.desactivar(id));
    }
}
