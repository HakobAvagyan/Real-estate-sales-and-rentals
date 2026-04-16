package org.example.rest.api;

import lombok.RequiredArgsConstructor;
import org.example.dto.user.UserRegisterDto;
import org.example.exception.ErrorCode;
import org.example.model.enums.Role;
import org.example.security.jwt.JwtService;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest body) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(body.email(), body.password()));
            String token = jwtService.createAccessToken((UserDetails) auth.getPrincipal());
            return ResponseEntity.ok(Map.of("accessToken", token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDto body) {
        if (body.getEmail() == null || body.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }
        if (body.getPassword() == null || body.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
        }
        if (userService.findByEmail(body.getEmail().trim()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ErrorCode.USER_ALREADY_REGISTERED.format(body.getEmail())));
        }
        body.setEmail(body.getEmail().trim());
        body.setBlocked(true);
        if (body.getRole() == null) {
            body.setRole(Role.USER);
        }
        UserRegisterDto saved = userService.save(body, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Registered. Verify your email with the code sent to your inbox.",
                "email", saved.getEmail()
        ));
    }


    public record LoginRequest(String email, String password) {
    }
}
