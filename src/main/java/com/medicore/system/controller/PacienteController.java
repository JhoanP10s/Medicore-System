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

import com.medicore.system.dto.request.PacienteRequest;
import com.medicore.system.dto.response.ErrorResponse;
import com.medicore.system.dto.response.MessageResponse;
import com.medicore.system.dto.response.PacienteResponse;
import com.medicore.system.service.PacienteService;
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
@RequestMapping("/paciente")
@Tag(name = "Pacientes", description = "Gestion de pacientes, datos de contacto, estado activo y eliminacion logica cuando existen citas asociadas.")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @Operation(
            summary = "Listar pacientes",
            description = "Retorna todos los pacientes registrados en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = PacienteResponse.class),
                            examples = @ExampleObject(value = """
                                    [{"numeroDocumento":"1020304050","primerNombre":"Laura","primerApellido":"Gomez","tipoDocumento":"CC","email":"laura.gomez@example.com","telefono":"+573001112233","activo":true}]
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<PacienteResponse>> getAllPaciente() {
        return ResponseEntity.ok(pacienteService.listarPacientes());
    }

    @Operation(
            summary = "Consultar paciente por documento",
            description = "Busca un paciente usando su numero de documento.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente encontrado",
                    content = @Content(schema = @Schema(implementation = PacienteResponse.class),
                            examples = @ExampleObject(value = """
                                    {"numeroDocumento":"1020304050","primerNombre":"Laura","primerApellido":"Gomez","tipoDocumento":"CC","email":"laura.gomez@example.com","telefono":"+573001112233","activo":true}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Documento invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {"timestamp":"2026-04-29T16:30:00","status":404,"error":"Not Found","message":"No existe paciente registrado","path":"/paciente/1020304050"}
                                    """)))
    })
    @GetMapping("{cc}")
    public ResponseEntity<PacienteResponse> getPaciente(
            @Parameter(description = "Numero de documento del paciente", example = "1020304050")
            @PathVariable("cc") String pacienteCC) {
        return ResponseEntity.ok(pacienteService.verPaciente(pacienteCC));
    }

    @Operation(
            summary = "Crear paciente",
            description = "Registra un paciente nuevo. No permite documentos duplicados.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Datos del paciente a crear",
            content = @Content(schema = @Schema(implementation = PacienteRequest.class),
                    examples = @ExampleObject(value = """
                            {"numeroDocumento":"1020304050","primerNombre":"Laura","segundoNombre":"Marcela","primerApellido":"Gomez","segundoApellido":"Perez","tipoDocumento":"CC","fechaExpedicionDoc":"2020-05-10","email":"laura.gomez@example.com","telefono":"+573001112233","activo":true}
                            """)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente creado correctamente",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(value = """
                                    {"message":"Paciente creado exitosamente"}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Documento duplicado o regla de negocio incumplida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/crearPaciente")
    public ResponseEntity<MessageResponse> crearPaciente(@Valid @RequestBody PacienteRequest paciente) {
        return ResponseEntity.ok(pacienteService.crearPaciente(paciente));
    }

    @Operation(
            summary = "Editar paciente",
            description = "Actualiza los datos de un paciente existente usando su numero de documento.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Datos actualizados del paciente",
            content = @Content(schema = @Schema(implementation = PacienteRequest.class),
                    examples = @ExampleObject(value = """
                            {"numeroDocumento":"1020304050","primerNombre":"Laura","primerApellido":"Gomez","tipoDocumento":"CC","fechaExpedicionDoc":"2020-05-10","email":"laura.gomez@example.com","telefono":"+573001112233","activo":true}
                            """)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(value = """
                                    {"message":"Paciente editado exitosamente"}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("{cc}")
    public ResponseEntity<MessageResponse> editarPaciente(
            @Parameter(description = "Numero de documento del paciente", example = "1020304050")
            @PathVariable("cc") String pacienteCC,
            @Valid @RequestBody PacienteRequest paciente) {
        return ResponseEntity.ok(pacienteService.editarPaciente(pacienteCC, paciente));
    }

    @Operation(
            summary = "Eliminar o desactivar paciente",
            description = "Elimina fisicamente el paciente si no tiene citas. Si tiene citas asociadas, aplica eliminacion logica marcandolo como inactivo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente eliminado o desactivado correctamente",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(value = """
                                    {"message":"Paciente desactivado exitosamente porque tiene citas asociadas"}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Documento invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/eliminarPaciente/{cc}")
    public ResponseEntity<MessageResponse> eliminarPaciente(
            @Parameter(description = "Numero de documento del paciente", example = "1020304050")
            @PathVariable("cc") String pacienteCC) {
        return ResponseEntity.ok(pacienteService.eliminarPaciente(pacienteCC));
    }
}
