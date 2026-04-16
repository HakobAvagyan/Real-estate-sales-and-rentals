package org.example.chat.config;

import org.example.chat.StompUserPrincipalProvider;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class ChatHandshakeHandler extends DefaultHandshakeHandler {

    private final StompUserPrincipalProvider principalProvider;

    public ChatHandshakeHandler(StompUserPrincipalProvider principalProvider) {
        this.principalProvider = principalProvider;
    }

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            return principalProvider.resolvePrincipal(servletRequest.getServletRequest()).orElse(null);
        }
        return null;
    }
}
