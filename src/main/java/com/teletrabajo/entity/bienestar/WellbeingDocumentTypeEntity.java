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
@Table(name="wellbeing_document_types", uniqueConstraints=@UniqueConstraint(name="uk_wb_document_type_code", columnNames="code"))
@AllArgsConstructor @NoArgsConstructor @Data @Builder
@SQLDelete(sql = "UPDATE wellbeing_document_types SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class WellbeingDocumentTypeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=80) private String code;
    @Column(nullable=false, length=180) private String name;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private BienestarEnums.DocumentGroup documentGroup;
    @Column(nullable=false) private Boolean required;
    @Column(length=255) private String helpText;
    @Column(length=120) private String allowedExtensions;
    private Integer maxSizeMb;
    private Boolean active;
    @Column(name="created_at") private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    @Column(name="deleted_at") private LocalDateTime deletedAt;
    @PrePersist public void prePersist(){ createdAt = LocalDateTime.now(); if(active==null) active=true; if(required==null) required=false; }
    @PreUpdate public void preUpdate(){ updatedAt = LocalDateTime.now(); }
}
