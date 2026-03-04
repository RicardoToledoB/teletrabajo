package com.teletrabajo.dto;

import com.teletrabajo.entity.RegisterEntity;
import com.teletrabajo.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterModifiedDTO {

    private Integer id;
    private RegisterDTO register;
    private UserDTO administrator;
    private UserDTO user;
    private String observations;
    private String register_datetime;
    private String state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
