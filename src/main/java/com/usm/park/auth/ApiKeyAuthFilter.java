package com.usm.park.auth;

import com.usm.park.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyAuthFilter(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String apiKeyValue = request.getHeader("X-API-KEY");
        if (apiKeyValue == null) {
            apiKeyValue = request.getHeader("Authorization");
            if (apiKeyValue != null && apiKeyValue.startsWith("ApiKey ")) {
                apiKeyValue = apiKeyValue.substring(7);
            }
        }
        if (apiKeyValue == null || apiKeyValue.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing API Key");
            return;
        }
        boolean valid = apiKeyRepository.findByKeyValueAndActiveTrue(apiKeyValue).isPresent();
        if (!valid) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or inactive API Key");
            return;
        }
        var auth = new UsernamePasswordAuthenticationToken("api-client", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/h2-console")
            || path.startsWith("/error")
            || path.startsWith("/v3/api-docs")
            || path.startsWith("/swagger-ui");
    }
}
