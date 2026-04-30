package com.medicore.system.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @Column(name = "numero_documento", length = 30)
    @NotBlank
    private String numeroDocumento;

    @Column(name = "primer_nombre", nullable = false, length = 80)
    @NotBlank
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 80)
    private String segundoNombre;

    @Column(name = "primer_apellido", nullable = false, length = 80)
    @NotBlank
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 80)
    private String segundoApellido;

    @Column(name = "tipo_documento", nullable = false, length = 20)
    @NotBlank
    private String tipoDocumento;

    @Column(name = "fecha_expedicion_documento")
    @PastOrPresent
    private LocalDate fechaExpedicionDoc;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "paciente")
    private List<Cita> citas = new ArrayList<>();

    public Paciente() {
    }

    public Paciente(String numeroDocumento, String primerNombre, String segundoNombre, String primerApellido,
            String segundoApellido, String tipoDocumento, LocalDate fechaExpedicionDoc) {
        this.numeroDocumento = numeroDocumento;
        this.primerNombre = primerNombre;
        this.segundoNombre = segundoNombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.tipoDocumento = tipoDocumento;
        this.fechaExpedicionDoc = fechaExpedicionDoc;
    }

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

    public List<Cita> getCitas() {
        return citas;
    }

    public void setCitas(List<Cita> citas) {
        this.citas = citas;
    }
}
