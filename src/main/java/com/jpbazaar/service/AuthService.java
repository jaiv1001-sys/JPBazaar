package com.jpbazaar.service;

import com.jpbazaar.dto.request.LoginRequest;
import com.jpbazaar.dto.request.RegisterRequest;
import com.jpbazaar.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
