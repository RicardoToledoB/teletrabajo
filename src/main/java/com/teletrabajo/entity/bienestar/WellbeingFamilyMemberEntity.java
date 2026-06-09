package com.teletrabajo.entity.bienestar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.teletrabajo.entity.*;


@Entity
@Table(name="wellbeing_family_members", indexes={@Index(name="idx_wb_family_postulation", columnList="postulation_id")})
@AllArgsConstructor @NoArgsConstructor @Data @Builder
@SQLDelete(sql = "UPDATE wellbeing_family_members SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class WellbeingFamilyMemberEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="postulation_id", nullable=false) private WellbeingPostulationEntity postulation;
    @Column(length=20) private String rut;
    @Column(length=120) private String names;
    @Column(length=120) private String lastNames;
    @Column(name="birth_date") private LocalDate birthDate;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="prevition_id") private PrevitionEntity prevition;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="contract_type_id") private ContractTypeEntity incomeType;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="parent_type_id") private ParentTypeEntity parentType;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="civil_state_id") private CivilStateEntity civilState;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="activity_id") private ActivityEntity activity;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="work_place_id") private WorkPlaceEntity workPlace;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="study_id") private StudyEntity studyLevel;
    @Column(length=120) private String studyPlace;
    @Column(name="others_workplaces", length=255) private String othersWorkplaces;
    @Column(name="others_activities", length=255) private String othersActivities;
    @Column(name="is_student") private Boolean student;
    @Column(precision=14, scale=2) private BigDecimal monthlyIncome;
    @Column(name="created_at") private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    @Column(name="deleted_at") private LocalDateTime deletedAt;
    @PrePersist public void prePersist(){ createdAt = LocalDateTime.now(); if(monthlyIncome==null) monthlyIncome=BigDecimal.ZERO; }
    @PreUpdate public void preUpdate(){ updatedAt = LocalDateTime.now(); }
}
