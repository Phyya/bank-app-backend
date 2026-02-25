package com.bank_app.bank_app.auth;

import com.bank_app.bank_app.auth.dto.AuthResponse;
import com.bank_app.bank_app.auth.dto.LoginRequest;
import com.bank_app.bank_app.auth.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {
        AuthResponse response = authService.login(request, session);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        if (!authService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not authenticated"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("accountNumber", session.getAttribute(AuthServiceImpl.SESSION_ACCOUNT_NUMBER));
        response.put("accountName", session.getAttribute(AuthServiceImpl.SESSION_ACCOUNT_NAME));

        return ResponseEntity.ok(response);
    }
}
