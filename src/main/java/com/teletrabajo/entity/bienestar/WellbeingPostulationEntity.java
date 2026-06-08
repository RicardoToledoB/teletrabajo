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
@Table(name="wellbeing_postulations",
       uniqueConstraints = @UniqueConstraint(name="uk_wellbeing_postulation_code", columnNames="code"),
       indexes = {
           @Index(name="idx_wb_post_user_year", columnList="user_id,period_year"),
           @Index(name="idx_wb_post_status", columnList="status")
       })
@AllArgsConstructor @NoArgsConstructor @Data @Builder
@SQLDelete(sql = "UPDATE wellbeing_postulations SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class WellbeingPostulationEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=30)
    private String code;

    @Column(name="period_year", nullable=false)
    private Integer periodYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="stablishment_id")
    private StablishmentEntity stablishment;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=30)
    private BienestarEnums.PostulationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name="beneficiary_type", length=30)
    private BienestarEnums.BeneficiaryType beneficiaryType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="beneficiary_family_member_id")
    private WellbeingFamilyMemberEntity beneficiaryFamilyMember;

    @Column(length=20) private String affiliateRut;
    @Column(length=120) private String affiliateNames;
    @Column(length=120) private String affiliateLastNames;
    @Column(length=30) private String affiliatePhone;
    @Column(length=160) private String affiliateEmail;
    @Column(length=255) private String affiliateAddress;
    @Column(length=20) private String affiliateBirthDate;
    @Column(length=30) private String affiliateSex;
    @Column(length=30) private String affiliateType;
    @Column(length=20) private String affiliateDate;

    @Column(precision=14, scale=2) private BigDecimal totalFamilyIncome;
    @Column(precision=14, scale=2) private BigDecimal totalBasicExpenses;
    @Column(precision=14, scale=2) private BigDecimal totalEducationExpenses;
    @Column(precision=14, scale=2) private BigDecimal totalOtherExpenses;
    @Column(precision=14, scale=2) private BigDecimal totalHealthExpenses;
    @Column(precision=14, scale=2) private BigDecimal totalFamilyExpenses;

    @Column(name="submitted_at") private LocalDateTime submittedAt;
    @Column(name="created_at") private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    @Column(name="deleted_at") private LocalDateTime deletedAt;

    @Column(name="current_step")
    private Integer currentStep;

    /**
     * Indica si el grupo familiar del postulante corresponde a un hogar monoparental.
     * Se informa en Step 2: Grupo Familiar y se usa para evaluación social/puntaje.
     */
    @Column(name = "is_single_parent_home")
    private Boolean isSingleParentHome;

    @PrePersist
    public void prePersist(){


        LocalDateTime now = LocalDateTime.now();
        if (currentStep == null) currentStep = 1;
        if (isSingleParentHome == null) isSingleParentHome = false;
        if (createdAt == null) createdAt = now;
        if (status == null) status = BienestarEnums.PostulationStatus.DRAFT;
        if (totalFamilyIncome == null) totalFamilyIncome = BigDecimal.ZERO;
        if (totalBasicExpenses == null) totalBasicExpenses = BigDecimal.ZERO;
        if (totalEducationExpenses == null) totalEducationExpenses = BigDecimal.ZERO;
        if (totalOtherExpenses == null) totalOtherExpenses = BigDecimal.ZERO;
        if (totalHealthExpenses == null) totalHealthExpenses = BigDecimal.ZERO;
        if (totalFamilyExpenses == null) totalFamilyExpenses = BigDecimal.ZERO;
    }
    @PreUpdate public void preUpdate(){ updatedAt = LocalDateTime.now(); }
}
