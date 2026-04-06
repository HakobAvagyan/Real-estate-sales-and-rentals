package org.example.app.chat;

import jakarta.servlet.http.HttpServletRequest;
import org.example.chat.StompUserPrincipalProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

@Component
public class AppStompPrincipalProvider implements StompUserPrincipalProvider {

    @Override
    public Optional<Principal> resolvePrincipal(HttpServletRequest request) {
        Principal p = request.getUserPrincipal();
        if (p == null) {
            return Optional.empty();
        }
        if (p instanceof org.springframework.security.core.Authentication authentication) {
            return Optional.of(authentication);
        }
        return Optional.of(new UsernamePasswordAuthenticationToken(
                p.getName(), null, Collections.emptyList()));
    }
}
