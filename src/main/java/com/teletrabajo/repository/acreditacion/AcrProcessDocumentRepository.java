package com.teletrabajo.repository.acreditacion;
import com.teletrabajo.entity.acreditacion.AcrProcessDocument;
import com.teletrabajo.dto.acreditacion.AcrProcessDocumentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
public interface AcrProcessDocumentRepository extends JpaRepository<AcrProcessDocument,Long> {
 @Query("select new com.teletrabajo.dto.acreditacion.AcrProcessDocumentDto(d.id,d.processId,d.filename,d.sha256,d.uploadedBy,d.uploadedAt,d.active) from AcrProcessDocument d where d.processId=:id and d.active=true order by d.id")
 List<AcrProcessDocumentDto> listActive(@Param("id") Long id);
 Optional<AcrProcessDocument> findByIdAndProcessIdAndActiveTrue(Long id,Long processId);
}
