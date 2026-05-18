package com.teletrabajo.service.impl;


import com.teletrabajo.dto.EmailRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(EmailRequestDTO dto) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(dto.getTo());
        message.setSubject(dto.getSubject());
        message.setText(dto.getMessage());

        mailSender.send(message);
    }
}