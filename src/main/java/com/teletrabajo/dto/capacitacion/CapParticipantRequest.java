package com.teletrabajo.dto.capacitacion;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true)
public record CapParticipantRequest(@NotBlank @Size(max=255) String rut, @NotBlank @Size(max=255) String fullName,
 @Size(max=255) String email, @Size(max=255) String result, ActivityRef activity, @Positive Long activityId,
 @Positive Integer userId, @DecimalMin("0") @DecimalMax("100") @Digits(integer=3,fraction=2) BigDecimal attendancePercent,
 @DecimalMin("0") @Digits(integer=3,fraction=2) BigDecimal grade, @Size(max=255) String status) {
 @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true)
 public record ActivityRef(Long id) {}
}
