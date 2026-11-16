package com.teletrabajo.dto.capacitacion;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true)
public record CapSignatoryRequest(@NotBlank @Size(max=255) String fullName, @Size(max=255) String positionTitle,
 @Size(max=255) String signatureImagePath, Boolean active, LocalDate validFrom, LocalDate validTo,
 @Schema(description="Posicion de firma en el certificado: IZQUIERDA o DERECHA; no se admiten 1 ni 2", allowableValues={"IZQUIERDA","DERECHA"}) @Size(max=255) String signaturePosition, @Size(max=255) String signatureAsset) {}
