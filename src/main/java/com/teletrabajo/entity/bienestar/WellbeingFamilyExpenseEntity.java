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
@Table(name="wellbeing_family_expenses", indexes={@Index(name="idx_wb_exp_postulation", columnList="postulation_id")})
@AllArgsConstructor @NoArgsConstructor @Data @Builder
@SQLDelete(sql = "UPDATE wellbeing_family_expenses SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class WellbeingFamilyExpenseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="postulation_id", nullable=false) private WellbeingPostulationEntity postulation;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private BienestarEnums.ExpenseCategory category;
    @Column(length=80) private String code;
    @Column(length=180) private String name;
    @Column(length=255) private String description;
    @Column(precision=14, scale=2, nullable=false) private BigDecimal amount;
    @Column(name="created_at") private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    @Column(name="deleted_at") private LocalDateTime deletedAt;
    @PrePersist public void prePersist(){ createdAt = LocalDateTime.now(); if(amount==null) amount=BigDecimal.ZERO; }
    @PreUpdate public void preUpdate(){ updatedAt = LocalDateTime.now(); }
}
