package com.medicore.system.dto.request;

import com.medicore.system.model.entity.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @Schema(description = "Nombre completo del usuario", example = "Admin Clinica")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @Schema(description = "Correo electronico usado para iniciar sesion", example = "admin@medicore.com")
    @Email(message = "El email debe tener un formato valido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @Schema(description = "Contrasena del usuario", example = "Admin12345")
    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 8, max = 80, message = "La contrasena debe tener entre 8 y 80 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "La contrasena debe tener al menos una mayuscula, una minuscula y un numero")
    private String password;

    @Schema(description = "Rol del usuario. En produccion no debe permitirse crear ADMIN desde registro publico.", example = "ADMIN")
    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

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

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
