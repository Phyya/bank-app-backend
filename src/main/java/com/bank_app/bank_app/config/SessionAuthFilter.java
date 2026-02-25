package com.bank_app.bank_app.config;

import com.bank_app.bank_app.auth.impl.AuthServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SessionAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Allow public paths
        if (isPublicPath(path, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check if user is authenticated
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(AuthServiceImpl.SESSION_ACCOUNT_NUMBER) == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized. Please login first.\"}");
            return;
        }

        // User is authenticated, continue
        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path, String method) {
        // Auth endpoints
        if (path.startsWith("/auth/login") || path.startsWith("/auth/logout")) {
            return true;
        }

        // /accounts/me requires authentication
        if (path.startsWith("/accounts/me")) {
            return false;
        }

        // All other /accounts paths are public
        if (path.startsWith("/accounts")) {
            return true;
        }

        return false;
    }
}
