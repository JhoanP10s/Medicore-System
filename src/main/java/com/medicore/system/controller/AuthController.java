package com.medicore.system.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medicore.system.dto.request.LoginRequest;
import com.medicore.system.dto.request.RegisterRequest;
import com.medicore.system.dto.response.AuthResponse;
import com.medicore.system.dto.response.ErrorResponse;
import com.medicore.system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticacion", description = "Registro, login y generacion de tokens JWT.")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Registrar usuario",
            description = """
                    Crea un usuario con rol ADMIN, DOCTOR o USER y retorna un token JWT.
                    En este proyecto academico el registro permite seleccionar rol para facilitar pruebas;
                    en produccion un registro publico no deberia permitir crear usuarios ADMIN.
                    """)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = RegisterRequest.class),
                    examples = @ExampleObject(value = """
                            {"nombre":"Admin Clinica","email":"admin@medicore.com","password":"Admin12345","rol":"ADMIN"}
                            """)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario registrado y autenticado",
                    content = @Content(
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(value = """
                                    {"id":1,"nombre":"Admin Clinica","email":"admin@medicore.com","rol":"ADMIN","token":"jwt-token","tokenType":"Bearer"}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email duplicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Iniciar sesion", description = "Valida credenciales y genera un token JWT para consumir endpoints privados.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = LoginRequest.class),
                    examples = @ExampleObject(value = """
                            {"email":"admin@medicore.com","password":"Admin12345"}
                            """)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto",
                    content = @Content(
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(value = """
                                    {"id":1,"nombre":"Admin Clinica","email":"admin@medicore.com","rol":"ADMIN","token":"jwt-token","tokenType":"Bearer"}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales invalidas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
