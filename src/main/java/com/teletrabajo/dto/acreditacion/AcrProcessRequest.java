package com.teletrabajo.dto.acreditacion;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** Fecha local de Magallanes; las fechas sin hora se interpretan como inicio/fin del día. */
public record AcrProcessRequest(
        Integer year, String title, String legalBasis,
        List<String> professions, List<String> modalities,
        @Schema(example="2027-03-01T09:00:00", description="Fecha y hora local de Magallanes (ISO); también YYYY-MM-DD") String opensAt,
        @Schema(example="2027-03-31T17:00:00") String closesAt,
        Integer admissibilityDays,
        @Schema(allowableValues={"habiles","corridos"}) String admissibilityDayType,
        String reviewStartsAt, String reviewDeadlineAt,
        String appealOpensAt, String appealClosesAt,
        String notice,
        @Schema(allowableValues={"BORRADOR","PROGRAMADA"}, description="Solo para creación; por compatibilidad, omitido = PROGRAMADA. Publicar/abrir/cerrar son acciones separadas.") String status) {}
