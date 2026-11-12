package com.teletrabajo.dto.capacitacion;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true)
public record CapActivityRequest(@NotBlank @Size(max=255) String title, @Size(max=255) String code,
 @Size(max=255) String provider, LocalDate startDate, LocalDate endDate, @PositiveOrZero Integer hours,
 Boolean active, @Size(max=255) String modality, @Positive Integer executionNumber) {}
