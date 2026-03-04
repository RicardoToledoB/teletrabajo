package com.teletrabajo.security.dto;

import com.teletrabajo.dto.RoleDTO;

import java.util.List;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresIn,
        List<RoleDTO> roles,
        ProfileDTO profile,
        String refreshToken
) {}