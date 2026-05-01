package com.medicore.system.dto.response;

public class AuthResponse {

    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private String token;
    private String tokenType = "Bearer";

    public AuthResponse(Long id, String nombre, String email, String rol, String token) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.token = token;
    }

    public AuthResponse(String token, String email, String rol) {
        this(null, null, email, rol, token);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
