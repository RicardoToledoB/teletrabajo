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
@Table(name="wellbeing_family_incomes", indexes={@Index(name="idx_wb_income_postulation", columnList="postulation_id")})
@AllArgsConstructor @NoArgsConstructor @Data @Builder
@SQLDelete(sql = "UPDATE wellbeing_family_incomes SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class WellbeingFamilyIncomeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="postulation_id", nullable=false) private WellbeingPostulationEntity postulation;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="family_member_id") private WellbeingFamilyMemberEntity familyMember;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="contract_type_id") private ContractTypeEntity incomeType;
    @Column(precision=14, scale=2, nullable=false) private BigDecimal amount;
    @Column(name="created_at") private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    @Column(name="deleted_at") private LocalDateTime deletedAt;
    @PrePersist public void prePersist(){ createdAt = LocalDateTime.now(); if(amount==null) amount=BigDecimal.ZERO; }
    @PreUpdate public void preUpdate(){ updatedAt = LocalDateTime.now(); }
}
