package com.teletrabajo.dto.capacitacion;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true)
public record CapSignatoryRequest(@NotBlank @Size(max=255) String fullName, @Size(max=255) String positionTitle,
 @Size(max=255) String signatureImagePath, Boolean active, LocalDate validFrom, LocalDate validTo,
 @Size(max=255) String signaturePosition, @Size(max=255) String signatureAsset) {}
