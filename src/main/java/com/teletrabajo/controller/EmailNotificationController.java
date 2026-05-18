package com.teletrabajo.controller;


import com.teletrabajo.dto.EmailRequestDTO;
import com.teletrabajo.service.impl.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/notifications")
@PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR','JEFATURA')")
public class EmailNotificationController {

    @Autowired
    private EmailNotificationService service;

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO dto) {
        service.sendEmail(dto);
        return ResponseEntity.ok("Correo enviado correctamente");
    }
}