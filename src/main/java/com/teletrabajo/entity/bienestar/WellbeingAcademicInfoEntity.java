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
@Table(name="wellbeing_academic_infos")
@AllArgsConstructor @NoArgsConstructor @Data @Builder
@SQLDelete(sql = "UPDATE wellbeing_academic_infos SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class WellbeingAcademicInfoEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name="postulation_id", nullable=false, unique=true) private WellbeingPostulationEntity postulation;
    @Column(length=180) private String institution;
    @Column(length=180) private String career;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="study_id") private StudyEntity studyLevel;
    @Column(length=40) private String currentSemester;
    private Integer careerDurationSemesters;
    @Column(name="studies_in_region") private Boolean studiesInRegion;
    @Column(name="had_previous_benefit") private Boolean hadPreviousBenefit;
    @Column(name="created_at") private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    @Column(name="deleted_at") private LocalDateTime deletedAt;
    @PrePersist public void prePersist(){ createdAt = LocalDateTime.now(); }
    @PreUpdate public void preUpdate(){ updatedAt = LocalDateTime.now(); }
}
