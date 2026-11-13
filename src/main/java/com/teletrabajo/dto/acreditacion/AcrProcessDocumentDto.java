package com.teletrabajo.dto.acreditacion;
import java.time.LocalDateTime;
public record AcrProcessDocumentDto(Long id,Long processId,String filename,String sha256,Integer uploadedBy,LocalDateTime uploadedAt,Boolean active) {}
