package com.teletrabajo.security.controller;

import com.teletrabajo.dto.RoleDTO;
import com.teletrabajo.entity.UserEntity;
import com.teletrabajo.repository.UserRepository;
import com.teletrabajo.repository.UserRoleRepository;
import com.teletrabajo.security.JwtService;
import com.teletrabajo.security.dto.AuthRequest;
import com.teletrabajo.security.dto.AuthResponse;
import com.teletrabajo.security.dto.ProfileDTO;
import com.teletrabajo.security.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    private final RefreshTokenService refreshTokenService;


    // =======================================================
    // LOGIN
    // =======================================================
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        UserDetails principal = userDetailsService.loadUserByUsername(req.email());
        UserEntity u = userRepository.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new UsernameNotFoundException("Email no encontrado"));

        // ROLES con ID
        List<RoleDTO> roles = userRoleRepository.findRolesByUserIdFull(u.getId())
                .stream()
                .map(r -> new RoleDTO(
                        r.getId(),
                        r.getName(),
                        r.getCreatedAt(),
                        r.getUpdatedAt(),
                        r.getDeletedAt()
                ))
                .toList();



        // FULLNAME
        String fullName = String.join(" ",
                safe(u.getFirstName()), safe(u.getSecondName()),
                safe(u.getFirstLastName()), safe(u.getSecondLastName())
        ).replaceAll("\\s+", " ").trim();

        // PROFILE con ID
        ProfileDTO profile = new ProfileDTO(
                u.getId(),
                u.getEmail(),
                u.getUsername(),
                fullName
        );

        // CLAIMS → aquí va SOLO TEXTO, no DTO
        Map<String, Object> claims = Map.of(
                "roles", roles.stream().map(RoleDTO::getName).toList(),   // <-- CORREGIDO
                "username", u.getUsername(),
                "email", u.getEmail(),
                "fullName", fullName
        );

        String token = jwtService.generateToken(principal, claims);
        String refreshToken = refreshTokenService.generateRefreshToken(u.getEmail());

        AuthResponse body = new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                roles,

                profile,
                refreshToken
        );

        return ResponseEntity.ok(body);
    }



    // =======================================================
    // REFRESH TOKEN
    // =======================================================
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        if (refreshToken == null || !refreshTokenService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(401).body(null);
        }

        String email = refreshTokenService.extractUsername(refreshToken);
        UserDetails user = userDetailsService.loadUserByUsername(email);
        UserEntity u = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email no encontrado"));

        // ROLES
        List<RoleDTO> roles = userRoleRepository.findRolesByUserIdFull(u.getId())
                .stream()
                .map(r -> new RoleDTO(
                        r.getId(),
                        r.getName(),
                        r.getCreatedAt(),
                        r.getUpdatedAt(),
                        r.getDeletedAt()
                ))
                .toList();



        // FULLNAME
        String fullName = String.join(" ",
                safe(u.getFirstName()), safe(u.getSecondName()),
                safe(u.getFirstLastName()), safe(u.getSecondLastName())
        ).replaceAll("\\s+", " ").trim();

        // PROFILE
        ProfileDTO profile = new ProfileDTO(
                u.getId(),
                u.getEmail(),
                u.getUsername(),
                fullName
        );

        // CLAIMS → SOLO NOMBRES
        Map<String, Object> claims = Map.of(
                "roles", roles.stream().map(RoleDTO::getName).toList(), // <-- CORREGIDO
                "username", u.getUsername(),
                "email", u.getEmail()
        );

        String newAccessToken = jwtService.generateToken(user, claims);
        String newRefreshToken = refreshTokenService.generateRefreshToken(email);

        return ResponseEntity.ok(new AuthResponse(
                newAccessToken,
                "Bearer",
                jwtService.getExpirationMs(),
                roles,
                profile,
                newRefreshToken
        ));
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

}
