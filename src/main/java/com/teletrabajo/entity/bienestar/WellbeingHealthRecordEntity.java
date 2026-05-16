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
@Table(name="wellbeing_health_records", indexes={@Index(name="idx_wb_health_postulation", columnList="postulation_id")})
@AllArgsConstructor @NoArgsConstructor @Data @Builder
@SQLDelete(sql = "UPDATE wellbeing_health_records SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class WellbeingHealthRecordEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="postulation_id", nullable=false) private WellbeingPostulationEntity postulation;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="family_member_id") private WellbeingFamilyMemberEntity familyMember;
    @Column(length=160) private String personName;
    @Column(length=180) private String pathology;
    @Column(precision=14, scale=2, nullable=false) private BigDecimal monthlyExpense;
    @Column(name="created_at") private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    @Column(name="deleted_at") private LocalDateTime deletedAt;
    @PrePersist public void prePersist(){ createdAt = LocalDateTime.now(); if(monthlyExpense==null) monthlyExpense=BigDecimal.ZERO; }
    @PreUpdate public void preUpdate(){ updatedAt = LocalDateTime.now(); }
}
