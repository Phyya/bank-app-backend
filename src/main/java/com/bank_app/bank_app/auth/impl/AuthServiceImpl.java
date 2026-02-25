package com.bank_app.bank_app.auth.impl;

import com.bank_app.bank_app.account.Account;
import com.bank_app.bank_app.account.AccountRepository;
import com.bank_app.bank_app.auth.AuthService;
import com.bank_app.bank_app.auth.dto.AuthResponse;
import com.bank_app.bank_app.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    // Session attribute keys
    public static final String SESSION_ACCOUNT_NUMBER = "accountNumber";
    public static final String SESSION_ACCOUNT_NAME = "accountName";

    public AuthServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse login(LoginRequest request, HttpSession session) {
        // Find account by account number
        Long accountNumber;
        try {
            accountNumber = Long.parseLong(request.getAccountNumber());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid account number format");
        }

        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new RuntimeException("Invalid account number or password"));

        // Verify password using BCrypt
        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new RuntimeException("Invalid account number or password");
        }

        // Store account info in session
        session.setAttribute(SESSION_ACCOUNT_NUMBER, account.getAccountNumber());
        session.setAttribute(SESSION_ACCOUNT_NAME, account.getAccountName());

        return new AuthResponse(
                "Login successful",
                account.getAccountName(),
                account.getAccountNumber(),
                session.getId()
        );
    }

    @Override
    public void logout(HttpSession session) {
        session.invalidate();
    }

    @Override
    public boolean isAuthenticated(HttpSession session) {
        return session.getAttribute(SESSION_ACCOUNT_NUMBER) != null;
    }
}
