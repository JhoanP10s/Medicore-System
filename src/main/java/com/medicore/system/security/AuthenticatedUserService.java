package com.medicore.system.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medicore.system.model.entity.Cita;
import com.medicore.system.model.entity.HistoriaClinica;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.model.entity.Rol;
import com.medicore.system.model.entity.Usuario;
import com.medicore.system.repository.UsuarioRepository;

@Service
public class AuthenticatedUserService {

    private final UsuarioRepository usuarioRepository;

    public AuthenticatedUserService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new AccessDeniedException("No hay usuario autenticado.");
        }
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("No se encontro el usuario autenticado."));
    }

    public boolean isAdmin() {
        return hasAuthority("ROLE_ADMIN");
    }

    public boolean isDoctor() {
        return hasAuthority("ROLE_DOCTOR");
    }

    public boolean isUser() {
        return hasAuthority("ROLE_USER");
    }

    @Transactional(readOnly = true)
    public String getCurrentDoctorDocumentoOrThrow() {
        Usuario usuario = getCurrentUser();
        if (!Rol.DOCTOR.equals(usuario.getRol())) {
            throw new AccessDeniedException("El usuario autenticado no tiene rol doctor.");
        }
        Medico medico = usuario.getMedico();
        if (medico == null) {
            throw new AccessDeniedException("El usuario doctor no tiene un medico asociado.");
        }
        if (Boolean.FALSE.equals(medico.getActivo())) {
            throw new AccessDeniedException("El medico asociado al usuario esta inactivo.");
        }
        return medico.getNumeroDocumento();
    }

    @Transactional(readOnly = true)
    public void validateDoctorOwnsMedico(String medicoDocumento) {
        if (isAdmin()) {
            return;
        }
        if (!isDoctor()) {
            return;
        }
        String currentDoctorDocumento = getCurrentDoctorDocumentoOrThrow();
        if (medicoDocumento == null || !currentDoctorDocumento.equals(medicoDocumento)) {
            throw new AccessDeniedException("No tienes permisos para acceder a recursos de otro medico.");
        }
    }

    @Transactional(readOnly = true)
    public void validateDoctorOwnsCita(Cita cita) {
        if (isAdmin() || !isDoctor()) {
            return;
        }
        if (cita == null || cita.getMedico() == null) {
            throw new AccessDeniedException("No se pudo validar la propiedad de la cita.");
        }
        validateDoctorOwnsMedico(cita.getMedico().getNumeroDocumento());
    }

    @Transactional(readOnly = true)
    public void validateDoctorOwnsHistoriaClinica(HistoriaClinica historiaClinica) {
        if (isAdmin() || !isDoctor()) {
            return;
        }
        if (historiaClinica == null || historiaClinica.getMedico() == null) {
            throw new AccessDeniedException("No se pudo validar la propiedad de la historia clinica.");
        }
        validateDoctorOwnsMedico(historiaClinica.getMedico().getNumeroDocumento());
    }

    private boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> authority.equals(grantedAuthority.getAuthority()));
    }
}
