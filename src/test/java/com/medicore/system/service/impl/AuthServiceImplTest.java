package com.medicore.system.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

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

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(usuarioRepository, medicoRepository, passwordEncoder, authenticationManager, jwtService);
    }

    @Test
    void registrarDoctorConMedicoActivoAsociaUsuario() {
        Medico medico = medicoActivo("79998887");
        RegisterRequest request = request(Rol.DOCTOR);
        request.setNumeroDocumentoMedico("79998887");

        when(usuarioRepository.existsByEmail("doctor@medicore.com")).thenReturn(false);
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico);
        when(passwordEncoder.encode("Doctor12345")).thenReturn("encoded-password");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(authentication)).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario usuarioGuardado = captor.getValue();

        assertSame(medico, usuarioGuardado.getMedico());
        assertEquals(Rol.DOCTOR, usuarioGuardado.getRol());
        assertEquals("encoded-password", usuarioGuardado.getPassword());
        assertEquals("79998887", response.getMedicoId());
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void registrarDoctorSinMedicoAsociadoLanzaBusinessException() {
        RegisterRequest request = request(Rol.DOCTOR);

        when(usuarioRepository.existsByEmail("doctor@medicore.com")).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.register(request));

        assertEquals("El usuario doctor debe estar asociado a un medico.", exception.getMessage());
    }

    @Test
    void registrarDoctorConMedicoInexistenteLanzaResourceNotFoundException() {
        RegisterRequest request = request(Rol.DOCTOR);
        request.setNumeroDocumentoMedico("79998887");

        when(usuarioRepository.existsByEmail("doctor@medicore.com")).thenReturn(false);
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> authService.register(request));
    }

    @Test
    void registrarDoctorConMedicoInactivoLanzaBusinessException() {
        Medico medico = medicoActivo("79998887");
        medico.setActivo(false);
        RegisterRequest request = request(Rol.DOCTOR);
        request.setNumeroDocumentoMedico("79998887");

        when(usuarioRepository.existsByEmail("doctor@medicore.com")).thenReturn(false);
        when(medicoRepository.findByNumeroDocumento("79998887")).thenReturn(medico);

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.register(request));

        assertEquals("El medico asociado al usuario esta inactivo.", exception.getMessage());
    }

    @Test
    void registrarAdminConMedicoAsociadoLanzaBusinessException() {
        RegisterRequest request = request(Rol.ADMIN);
        request.setEmail("admin@medicore.com");
        request.setNumeroDocumentoMedico("79998887");

        when(usuarioRepository.existsByEmail("admin@medicore.com")).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.register(request));

        assertEquals("Solo los usuarios con rol DOCTOR pueden asociarse a un medico.", exception.getMessage());
    }

    @Test
    void registrarAdminSinMedicoAsociadoEsValido() {
        RegisterRequest request = request(Rol.ADMIN);
        request.setEmail("admin@medicore.com");

        when(usuarioRepository.existsByEmail("admin@medicore.com")).thenReturn(false);
        when(passwordEncoder.encode("Doctor12345")).thenReturn("encoded-password");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(authentication)).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        assertNull(captor.getValue().getMedico());
        assertEquals("ADMIN", response.getRol());
        assertNull(response.getMedicoId());
    }

    @Test
    void registrarUserSinMedicoAsociadoEsValido() {
        RegisterRequest request = request(Rol.USER);
        request.setEmail("user@medicore.com");

        when(usuarioRepository.existsByEmail("user@medicore.com")).thenReturn(false);
        when(passwordEncoder.encode("Doctor12345")).thenReturn("encoded-password");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(authentication)).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        assertNull(captor.getValue().getMedico());
        assertEquals("USER", response.getRol());
        assertNull(response.getMedicoId());
    }

    private RegisterRequest request(Rol rol) {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Doctor Medicore");
        request.setEmail("doctor@medicore.com");
        request.setPassword("Doctor12345");
        request.setRol(rol);
        return request;
    }

    private Medico medicoActivo(String numeroDocumento) {
        Medico medico = new Medico();
        medico.setNumeroDocumento(numeroDocumento);
        medico.setPrimerNombre("Carlos");
        medico.setPrimerApellido("Rojas");
        medico.setTipoDocumento("CC");
        medico.setActivo(true);
        return medico;
    }
}
