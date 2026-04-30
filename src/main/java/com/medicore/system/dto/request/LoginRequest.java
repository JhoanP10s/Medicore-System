package com.medicore.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @Schema(description = "Correo electronico del usuario", example = "admin@medicore.com")
    @Email(message = "El email debe tener un formato valido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @Schema(description = "Contrasena del usuario", example = "Admin12345")
    @NotBlank(message = "La contrasena es obligatoria")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
