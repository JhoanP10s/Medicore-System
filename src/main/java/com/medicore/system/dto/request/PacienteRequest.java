package com.medicore.system.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PacienteRequest {

    @Schema(description = "Numero de documento del paciente", example = "1020304050")
    @NotBlank(message = "El numero de documento es obligatorio")
    @Size(max = 30, message = "El numero de documento no puede superar 30 caracteres")
    private String numeroDocumento;

    @Schema(description = "Primer nombre del paciente", example = "Laura")
    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(max = 80, message = "El primer nombre no puede superar 80 caracteres")
    private String primerNombre;

    @Schema(description = "Segundo nombre del paciente", example = "Marcela")
    @Size(max = 80, message = "El segundo nombre no puede superar 80 caracteres")
    private String segundoNombre;

    @Schema(description = "Primer apellido del paciente", example = "Gomez")
    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 80, message = "El primer apellido no puede superar 80 caracteres")
    private String primerApellido;

    @Schema(description = "Segundo apellido del paciente", example = "Perez")
    @Size(max = 80, message = "El segundo apellido no puede superar 80 caracteres")
    private String segundoApellido;

    @Schema(description = "Tipo de documento", example = "CC")
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Size(max = 20, message = "El tipo de documento no puede superar 20 caracteres")
    private String tipoDocumento;

    @Schema(description = "Fecha de expedicion del documento", example = "2020-05-10")
    @PastOrPresent(message = "La fecha de expedicion no puede ser futura")
    private LocalDate fechaExpedicionDoc;

    @Schema(description = "Correo electronico del paciente", example = "laura.gomez@example.com")
    @Email(message = "El email debe tener un formato valido")
    @Size(max = 120, message = "El email no puede superar 120 caracteres")
    private String email;

    @Schema(description = "Telefono de contacto del paciente", example = "+573001112233")
    @Pattern(regexp = "^$|^[+]?[0-9]{7,15}$", message = "El telefono debe contener entre 7 y 15 digitos y puede iniciar con +")
    private String telefono;

    @Schema(description = "Indica si el paciente esta activo", example = "true")
    private Boolean activo;

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getPrimerNombre() {
        return primerNombre;
    }

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public LocalDate getFechaExpedicionDoc() {
        return fechaExpedicionDoc;
    }

    public void setFechaExpedicionDoc(LocalDate fechaExpedicionDoc) {
        this.fechaExpedicionDoc = fechaExpedicionDoc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
