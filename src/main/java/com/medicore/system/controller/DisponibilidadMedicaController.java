package com.medicore.system.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medicore.system.dto.request.DisponibilidadMedicaRequest;
import com.medicore.system.dto.response.DisponibilidadMedicaResponse;
import com.medicore.system.dto.response.ErrorResponse;
import com.medicore.system.dto.response.MessageResponse;
import com.medicore.system.service.DisponibilidadMedicaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/disponibilidad-medica")
@Tag(name = "Disponibilidad medica", description = "Define bloques semanales activos en los que un medico puede atender citas.")
public class DisponibilidadMedicaController {

    private final DisponibilidadMedicaService disponibilidadService;

    public DisponibilidadMedicaController(DisponibilidadMedicaService disponibilidadService) {
        this.disponibilidadService = disponibilidadService;
    }

    @Operation(summary = "Crear disponibilidad medica", description = "Crea un bloque semanal activo para un medico existente y activo. No permite bloques solapados.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = DisponibilidadMedicaRequest.class), examples = @ExampleObject(value = """
            {"numeroDocumentoMedico":"79998887","diaSemana":"VIERNES","horaInicio":"08:00:00","horaFin":"12:00:00"}
            """)))
    @ApiResponse(responseCode = "200", description = "Disponibilidad creada", content = @Content(schema = @Schema(implementation = DisponibilidadMedicaResponse.class)))
    @ApiResponse(responseCode = "409", description = "Solapamiento o medico inactivo", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<DisponibilidadMedicaResponse> crear(@Valid @RequestBody DisponibilidadMedicaRequest request) {
        return ResponseEntity.ok(disponibilidadService.crear(request));
    }

    @Operation(summary = "Listar disponibilidad medica", description = "Lista todos los bloques de disponibilidad registrados.")
    @GetMapping
    public ResponseEntity<List<DisponibilidadMedicaResponse>> listar() {
        return ResponseEntity.ok(disponibilidadService.listar());
    }

    @Operation(summary = "Consultar disponibilidad", description = "Consulta una disponibilidad por id.")
    @GetMapping("{id}")
    public ResponseEntity<DisponibilidadMedicaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(disponibilidadService.buscarPorId(id));
    }

    @Operation(summary = "Disponibilidad por medico", description = "Lista disponibilidades de un medico por numero de documento.")
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<DisponibilidadMedicaResponse>> buscarPorMedico(@PathVariable String medicoId) {
        return ResponseEntity.ok(disponibilidadService.buscarPorMedico(medicoId));
    }

    @Operation(summary = "Actualizar disponibilidad", description = "Actualiza dia y horario de una disponibilidad existente.")
    @PutMapping("{id}")
    public ResponseEntity<DisponibilidadMedicaResponse> actualizar(@PathVariable Long id, @Valid @RequestBody DisponibilidadMedicaRequest request) {
        return ResponseEntity.ok(disponibilidadService.actualizar(id, request));
    }

    @Operation(summary = "Desactivar disponibilidad", description = "Desactiva logicamente una disponibilidad. No elimina fisicamente el registro.")
    @PatchMapping("{id}/desactivar")
    public ResponseEntity<MessageResponse> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(disponibilidadService.desactivar(id));
    }
}
