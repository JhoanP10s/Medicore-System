package com.medicore.system.service;

import com.medicore.system.dto.request.LoginRequest;
import com.medicore.system.dto.request.RegisterRequest;
import com.medicore.system.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
