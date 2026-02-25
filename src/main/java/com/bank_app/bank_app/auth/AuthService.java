package com.bank_app.bank_app.auth;

import com.bank_app.bank_app.auth.dto.AuthResponse;
import com.bank_app.bank_app.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpSession;

public interface AuthService {
    AuthResponse login(LoginRequest request, HttpSession session);
    void logout(HttpSession session);
    boolean isAuthenticated(HttpSession session);
}
