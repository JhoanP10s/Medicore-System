package com.medicore.system.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medicore.system.dto.request.LoginRequest;
import com.medicore.system.dto.request.RegisterRequest;
import com.medicore.system.dto.response.AuthResponse;
import com.medicore.system.exception.BusinessException;
import com.medicore.system.exception.ResourceNotFoundException;
import com.medicore.system.model.entity.Medico;
import com.medicore.system.model.entity.Rol;
import com.medicore.system.model.entity.Usuario;
import com.medicore.system.repository.MedicoRepository;
import com.medicore.system.repository.UsuarioRepository;
import com.medicore.system.security.JwtService;
import com.medicore.system.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final MedicoRepository medicoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UsuarioRepository usuarioRepository,
            MedicoRepository medicoRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.medicoRepository = medicoRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());

        if (usuarioRepository.existsByEmail(email)) {
            throw new BusinessException("Ya existe un usuario registrado con este email.");
        }

        Medico medico = resolverMedicoParaRegistro(request);

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre().trim());
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        usuario.setActivo(true);
        usuario.setMedico(medico);
        usuarioRepository.save(usuario);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        String token = jwtService.generateToken(authentication);

        return toAuthResponse(usuario, token);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        String token = jwtService.generateToken(authentication);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Credenciales invalidas."));

        return toAuthResponse(usuario, token);
    }

    private Medico resolverMedicoParaRegistro(RegisterRequest request) {
        if (Rol.DOCTOR.equals(request.getRol())) {
            String numeroDocumentoMedico = request.getNumeroDocumentoMedico();
            if (numeroDocumentoMedico == null || numeroDocumentoMedico.isBlank()) {
                throw new BusinessException("El usuario doctor debe estar asociado a un medico.");
            }
            Medico medico = medicoRepository.findByNumeroDocumento(numeroDocumentoMedico);
            if (medico == null) {
                throw new ResourceNotFoundException("No existe medico registrado");
            }
            if (Boolean.FALSE.equals(medico.getActivo())) {
                throw new BusinessException("El medico asociado al usuario esta inactivo.");
            }
            return medico;
        }

        if (request.getNumeroDocumentoMedico() != null && !request.getNumeroDocumentoMedico().isBlank()) {
            throw new BusinessException("Solo los usuarios con rol DOCTOR pueden asociarse a un medico.");
        }
        return null;
    }

    private AuthResponse toAuthResponse(Usuario usuario, String token) {
        String medicoId = usuario.getMedico() != null ? usuario.getMedico().getNumeroDocumento() : null;
        return new AuthResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().name(),
                medicoId,
                token);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
