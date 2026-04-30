package com.medicore.system.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medicore.system.dto.request.CitaRequest;
import com.medicore.system.dto.response.CitaResponse;
import com.medicore.system.dto.response.ErrorResponse;
import com.medicore.system.dto.response.MessageResponse;
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
@Tag(name = "Citas medicas", description = "Programacion y consulta de citas medicas entre pacientes y medicos.")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @Operation(summary = "Listar citas", description = "Retorna todas las citas medicas registradas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = CitaResponse.class),
                            examples = @ExampleObject(value = """
                                    [{"id":1,"fechaHora":"2026-05-15T09:30:00","motivo":"Control general","observaciones":"Paciente estable","pacienteNumeroDocumento":"1020304050","pacienteNombreCompleto":"Laura Gomez","medicoNumeroDocumento":"79998887","medicoNombreCompleto":"Carlos Rojas","especialidadNombre":"Cardiologia"}]
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<CitaResponse>> listarCitas() {
        return ResponseEntity.ok(citaService.listarCitas());
    }

    @Operation(summary = "Consultar cita", description = "Obtiene una cita medica por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cita encontrada",
                    content = @Content(schema = @Schema(implementation = CitaResponse.class),
                            examples = @ExampleObject(value = """
                                    {"id":1,"fechaHora":"2026-05-15T09:30:00","motivo":"Control general","observaciones":"Paciente estable","pacienteNumeroDocumento":"1020304050","pacienteNombreCompleto":"Laura Gomez","medicoNumeroDocumento":"79998887","medicoNombreCompleto":"Carlos Rojas","especialidadNombre":"Cardiologia"}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Identificador invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("{id}")
    public ResponseEntity<CitaResponse> verCita(
            @Parameter(description = "Identificador de la cita", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(citaService.verCita(id));
    }

    @Operation(
            summary = "Crear cita medica",
            description = "Programa una cita para un paciente y un medico existentes y activos. No permite fechas pasadas ni dos citas del mismo medico en la misma fecha y hora.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Datos de la cita a programar",
            content = @Content(schema = @Schema(implementation = CitaRequest.class),
                    examples = @ExampleObject(value = """
                            {"numeroDocumentoPaciente":"1020304050","numeroDocumentoMedico":"79998887","fechaHora":"2026-05-15T09:30:00","motivo":"Control general","observaciones":"Paciente refiere dolor toracico ocasional"}
                            """)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cita creada correctamente",
                    content = @Content(schema = @Schema(implementation = CitaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o fecha pasada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Paciente o medico no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Paciente/medico inactivo o medico ocupado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {"timestamp":"2026-04-29T16:30:00","status":409,"error":"Conflict","message":"El medico ya tiene una cita programada en esa fecha y hora.","path":"/cita"}
                                    """)))
    })
    @PostMapping
    public ResponseEntity<CitaResponse> crearCita(@Valid @RequestBody CitaRequest request) {
        return ResponseEntity.ok(citaService.crearCita(request));
    }

    @Operation(summary = "Editar cita medica", description = "Actualiza la fecha, medico, paciente, motivo u observaciones de una cita existente.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Datos actualizados de la cita",
            content = @Content(schema = @Schema(implementation = CitaRequest.class),
                    examples = @ExampleObject(value = """
                            {"numeroDocumentoPaciente":"1020304050","numeroDocumentoMedico":"79998887","fechaHora":"2026-05-15T10:30:00","motivo":"Seguimiento","observaciones":"Control posterior a tratamiento"}
                            """)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cita actualizada correctamente",
                    content = @Content(schema = @Schema(implementation = CitaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cita, paciente o medico no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Regla de negocio incumplida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("{id}")
    public ResponseEntity<CitaResponse> editarCita(
            @Parameter(description = "Identificador de la cita", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CitaRequest request) {
        return ResponseEntity.ok(citaService.editarCita(id, request));
    }

    @Operation(summary = "Eliminar cita", description = "Elimina una cita medica existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cita eliminada correctamente",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(value = """
                                    {"message":"Cita eliminada exitosamente"}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Identificador invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("{id}")
    public ResponseEntity<MessageResponse> eliminarCita(
            @Parameter(description = "Identificador de la cita", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(citaService.eliminarCita(id));
    }

    @Operation(summary = "Listar citas por paciente", description = "Retorna todas las citas asociadas a un paciente por numero de documento.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = CitaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Documento invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/paciente/{numeroDocumento}")
    public ResponseEntity<List<CitaResponse>> listarCitasPorPaciente(
            @Parameter(description = "Numero de documento del paciente", example = "1020304050")
            @PathVariable String numeroDocumento) {
        return ResponseEntity.ok(citaService.listarCitasPorPaciente(numeroDocumento));
    }

    @Operation(summary = "Listar citas por medico", description = "Retorna todas las citas asociadas a un medico por numero de documento.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = CitaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Documento invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/medico/{numeroDocumento}")
    public ResponseEntity<List<CitaResponse>> listarCitasPorMedico(
            @Parameter(description = "Numero de documento del medico", example = "79998887")
            @PathVariable String numeroDocumento) {
        return ResponseEntity.ok(citaService.listarCitasPorMedico(numeroDocumento));
    }
}
