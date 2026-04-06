package org.example.rest.chat;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.chat.StompUserPrincipalProvider;
import org.example.security.jwt.JwtService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RestStompPrincipalProvider implements StompUserPrincipalProvider {

    private final ObjectProvider<JwtService> jwtService;

    @Override
    public Optional<Principal> resolvePrincipal(HttpServletRequest request) {
        JwtService jwt = jwtService.getIfAvailable();
        if (jwt == null) {
            return Optional.empty();
        }
        String token = request.getParameter("access_token");
        if (token == null || token.isBlank()) {
            token = request.getParameter("token");
        }
        if (token != null && !token.isBlank() && jwt.isValid(token.trim())) {
            return Optional.of(jwt.parseAuthentication(token.trim()));
        }
        return Optional.empty();
    }
}
