package com.teletrabajo.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Integer id;
    private String firstName;
    private String secondName;
    private String firstLastName;
    private String secondLastName;
    private String full_name;
    private String email;
    private String username;
    private String password;
    private String rut;
    private String birth_date;
    private String contract_date;
    private String contract_type;
    @com.fasterxml.jackson.annotation.JsonAlias("establishment_id")
    private Long establishmentId;
    @com.fasterxml.jackson.annotation.JsonAlias("unit_id")
    private Long unitId;
    @com.fasterxml.jackson.annotation.JsonAlias("profession_id")
    private Long professionId;
    @com.fasterxml.jackson.annotation.JsonAlias("position")
    private String position;
    @com.fasterxml.jackson.annotation.JsonAlias("staff_category")
    private String staffCategory;
    @com.fasterxml.jackson.annotation.JsonAlias("grade")
    private String grade;
    @com.fasterxml.jackson.annotation.JsonAlias("contract_hours")
    private java.math.BigDecimal contractHours;
    @com.fasterxml.jackson.annotation.JsonAlias("applicable_law")
    private String applicableLaw;
    @com.fasterxml.jackson.annotation.JsonAlias("institutional_entry_date")
    private java.time.LocalDate institutionalEntryDate;
    @com.fasterxml.jackson.annotation.JsonAlias("employment_status")
    private String employmentStatus;
    @com.fasterxml.jackson.annotation.JsonAlias("nationality_id")
    private Long nationalityId;
    @com.fasterxml.jackson.annotation.JsonAlias("professional_title_id")
    private Long professionalTitleId;
    @com.fasterxml.jackson.annotation.JsonAlias("phone")
    private String phone;
    @com.fasterxml.jackson.annotation.JsonAlias("personal_email")
    private String personalEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
