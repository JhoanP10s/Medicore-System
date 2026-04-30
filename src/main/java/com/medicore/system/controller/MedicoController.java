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

import com.medicore.system.dto.request.MedicoRequest;
import com.medicore.system.dto.response.ErrorResponse;
import com.medicore.system.dto.response.MedicoResponse;
import com.medicore.system.dto.response.MessageResponse;
import com.medicore.system.service.MedicoService;
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
@RequestMapping("/medico")
@Tag(name = "Medicos", description = "Gestion de medicos, datos de contacto, especialidad, estado activo y eliminacion logica cuando existen citas asociadas.")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @Operation(summary = "Listar medicos", description = "Retorna todos los medicos registrados con su especialidad asociada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = MedicoResponse.class),
                            examples = @ExampleObject(value = """
                                    [{"numeroDocumento":"79998887","primerNombre":"Carlos","primerApellido":"Rojas","tipoDocumento":"CC","email":"carlos.rojas@example.com","telefono":"+573205556677","activo":true,"especialidadId":1,"especialidadNombre":"Cardiologia"}]
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<MedicoResponse>> getAllMedico() {
        return ResponseEntity.ok(medicoService.listarMedicos());
    }

    @Operation(summary = "Consultar medico por documento", description = "Busca un medico usando su numero de documento.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medico encontrado",
                    content = @Content(schema = @Schema(implementation = MedicoResponse.class),
                            examples = @ExampleObject(value = """
                                    {"numeroDocumento":"79998887","primerNombre":"Carlos","primerApellido":"Rojas","tipoDocumento":"CC","email":"carlos.rojas@example.com","telefono":"+573205556677","activo":true,"especialidadId":1,"especialidadNombre":"Cardiologia"}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Documento invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Medico no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {"timestamp":"2026-04-29T16:30:00","status":404,"error":"Not Found","message":"No existe medico registrado","path":"/medico/79998887"}
                                    """)))
    })
    @GetMapping("{cc}")
    public ResponseEntity<MedicoResponse> getMedico(
            @Parameter(description = "Numero de documento del medico", example = "79998887")
            @PathVariable("cc") String medicoCC) {
        return ResponseEntity.ok(medicoService.verMedico(medicoCC));
    }

    @Operation(summary = "Crear medico", description = "Registra un medico nuevo y lo asocia a una especialidad activa.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Datos del medico a crear",
            content = @Content(schema = @Schema(implementation = MedicoRequest.class),
                    examples = @ExampleObject(value = """
                            {"numeroDocumento":"79998887","primerNombre":"Carlos","segundoNombre":"Andres","primerApellido":"Rojas","segundoApellido":"Diaz","tipoDocumento":"CC","fechaExpedicionDoc":"2015-08-20","email":"carlos.rojas@example.com","telefono":"+573205556677","activo":true,"especialidadId":1}
                            """)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medico creado correctamente",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(value = """
                                    {"message":"Medico creado exitosamente"}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o especialidad invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Especialidad no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Documento duplicado o especialidad inactiva",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/crearMedico")
    public ResponseEntity<MessageResponse> crearMedico(@Valid @RequestBody MedicoRequest medico) {
        return ResponseEntity.ok(medicoService.crearMedico(medico));
    }

    @Operation(summary = "Editar medico", description = "Actualiza los datos de un medico existente y su especialidad.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Datos actualizados del medico",
            content = @Content(schema = @Schema(implementation = MedicoRequest.class),
                    examples = @ExampleObject(value = """
                            {"numeroDocumento":"79998887","primerNombre":"Carlos","primerApellido":"Rojas","tipoDocumento":"CC","fechaExpedicionDoc":"2015-08-20","email":"carlos.rojas@example.com","telefono":"+573205556677","activo":true,"especialidadId":1}
                            """)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medico actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(value = """
                                    {"message":"Medico editado exitosamente"}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Medico o especialidad no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Regla de negocio incumplida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("{cc}")
    public ResponseEntity<MessageResponse> editarMedico(
            @Parameter(description = "Numero de documento del medico", example = "79998887")
            @PathVariable("cc") String medicoCC,
            @Valid @RequestBody MedicoRequest medico) {
        return ResponseEntity.ok(medicoService.editarMedico(medicoCC, medico));
    }

    @Operation(
            summary = "Eliminar o desactivar medico",
            description = "Elimina fisicamente el medico si no tiene citas. Si tiene citas asociadas, aplica eliminacion logica marcandolo como inactivo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medico eliminado o desactivado correctamente",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(value = """
                                    {"message":"Medico desactivado exitosamente porque tiene citas asociadas"}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Documento invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Medico no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/eliminarMedico/{cc}")
    public ResponseEntity<MessageResponse> eliminarMedico(
            @Parameter(description = "Numero de documento del medico", example = "79998887")
            @PathVariable("cc") String medicoCC) {
        return ResponseEntity.ok(medicoService.eliminarMedico(medicoCC));
    }
}
