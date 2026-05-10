package com.medicore.system.dto.response;

public class DashboardResponse {
    private String tipo;
    private AdminDashboardResponse admin;
    private DoctorDashboardResponse doctor;
    private UserDashboardResponse user;

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public AdminDashboardResponse getAdmin() { return admin; }
    public void setAdmin(AdminDashboardResponse admin) { this.admin = admin; }
    public DoctorDashboardResponse getDoctor() { return doctor; }
    public void setDoctor(DoctorDashboardResponse doctor) { this.doctor = doctor; }
    public UserDashboardResponse getUser() { return user; }
    public void setUser(UserDashboardResponse user) { this.user = user; }
}
