package org.example.chat;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.Optional;

public interface StompUserPrincipalProvider {

    Optional<Principal> resolvePrincipal(HttpServletRequest request);
}
