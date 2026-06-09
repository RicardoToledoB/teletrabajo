package com.teletrabajo.entity.bienestar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.teletrabajo.entity.*;


@Entity
@Table(name="wellbeing_postulation_documents", indexes={@Index(name="idx_wb_doc_postulation", columnList="postulation_id")})
@AllArgsConstructor @NoArgsConstructor @Data @Builder
@SQLDelete(sql = "UPDATE wellbeing_postulation_documents SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class WellbeingPostulationDocumentEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="postulation_id", nullable=false) private WellbeingPostulationEntity postulation;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="document_type_id", nullable=false) private WellbeingDocumentTypeEntity documentType;
    @Column(length=220) private String originalFilename;
    @Column(length=500) private String storagePath;
    @Column(length=120) private String contentType;
    private Long sizeBytes;
    @Column(length=80) private String checksum;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="uploaded_by_user_id") private UserEntity uploadedBy;
    @Column(name="uploaded_at") private LocalDateTime uploadedAt;
    @Column(name="created_at") private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    @Column(name="deleted_at") private LocalDateTime deletedAt;
    @PrePersist public void prePersist(){ LocalDateTime now=LocalDateTime.now(); createdAt=now; if(uploadedAt==null) uploadedAt=now; }
    @PreUpdate public void preUpdate(){ updatedAt = LocalDateTime.now(); }
}
