package com.teletrabajo.security.dto;

public record ProfileDTO(
        Integer id,
        String email,
        String username,
        String fullName
) {}