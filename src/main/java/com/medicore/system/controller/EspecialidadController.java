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

import com.medicore.system.dto.request.EspecialidadRequest;
import com.medicore.system.dto.response.ErrorResponse;
import com.medicore.system.dto.response.EspecialidadResponse;
import com.medicore.system.dto.response.MessageResponse;
import com.medicore.system.service.EspecialidadService;
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
@RequestMapping("/especialidad")
@Tag(name = "Especialidades", description = "Administracion de especialidades medicas disponibles para asociar a medicos.")
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    public EspecialidadController(EspecialidadService especialidadService) {
        this.especialidadService = especialidadService;
    }

    @Operation(summary = "Listar especialidades", description = "Retorna todas las especialidades medicas registradas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = EspecialidadResponse.class),
                            examples = @ExampleObject(value = """
                                    [{"id":1,"nombre":"Cardiologia","descripcion":"Diagnostico y tratamiento de enfermedades cardiovasculares","activo":true}]
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<EspecialidadResponse>> listarEspecialidades() {
        return ResponseEntity.ok(especialidadService.listarEspecialidades());
    }

    @Operation(summary = "Consultar especialidad", description = "Obtiene una especialidad por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Especialidad encontrada",
                    content = @Content(schema = @Schema(implementation = EspecialidadResponse.class),
                            examples = @ExampleObject(value = """
                                    {"id":1,"nombre":"Cardiologia","descripcion":"Diagnostico y tratamiento de enfermedades cardiovasculares","activo":true}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Identificador invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Especialidad no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("{id}")
    public ResponseEntity<EspecialidadResponse> verEspecialidad(
            @Parameter(description = "Identificador de la especialidad", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(especialidadService.verEspecialidad(id));
    }

    @Operation(summary = "Crear especialidad", description = "Registra una nueva especialidad medica. El nombre no debe estar duplicado.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Datos de la especialidad a crear",
            content = @Content(schema = @Schema(implementation = EspecialidadRequest.class),
                    examples = @ExampleObject(value = """
                            {"nombre":"Cardiologia","descripcion":"Diagnostico y tratamiento de enfermedades cardiovasculares","activo":true}
                            """)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Especialidad creada correctamente",
                    content = @Content(schema = @Schema(implementation = EspecialidadResponse.class),
                            examples = @ExampleObject(value = """
                                    {"id":1,"nombre":"Cardiologia","descripcion":"Diagnostico y tratamiento de enfermedades cardiovasculares","activo":true}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Nombre de especialidad duplicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EspecialidadResponse> crearEspecialidad(
            @Valid @RequestBody EspecialidadRequest request) {
        return ResponseEntity.ok(especialidadService.crearEspecialidad(request));
    }

    @Operation(summary = "Editar especialidad", description = "Actualiza nombre, descripcion o estado de una especialidad existente.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Datos actualizados de la especialidad",
            content = @Content(schema = @Schema(implementation = EspecialidadRequest.class),
                    examples = @ExampleObject(value = """
                            {"nombre":"Cardiologia","descripcion":"Unidad especializada en salud cardiovascular","activo":true}
                            """)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Especialidad actualizada correctamente",
                    content = @Content(schema = @Schema(implementation = EspecialidadResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Especialidad no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("{id}")
    public ResponseEntity<EspecialidadResponse> editarEspecialidad(
            @Parameter(description = "Identificador de la especialidad", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EspecialidadRequest request) {
        return ResponseEntity.ok(especialidadService.editarEspecialidad(id, request));
    }

    @Operation(
            summary = "Eliminar o desactivar especialidad",
            description = "Elimina fisicamente la especialidad si no tiene medicos asociados. Si tiene relaciones, se marca como inactiva.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Especialidad eliminada o desactivada correctamente",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(value = """
                                    {"message":"Especialidad desactivada exitosamente porque tiene medicos asociados"}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Identificador invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Especialidad no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("{id}")
    public ResponseEntity<MessageResponse> eliminarEspecialidad(
            @Parameter(description = "Identificador de la especialidad", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(especialidadService.eliminarEspecialidad(id));
    }
}
