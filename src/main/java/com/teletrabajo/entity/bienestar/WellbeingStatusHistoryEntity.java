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
@Table(name="wellbeing_status_histories", indexes={@Index(name="idx_wb_status_postulation", columnList="postulation_id")})
@AllArgsConstructor @NoArgsConstructor @Data @Builder
public class WellbeingStatusHistoryEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="postulation_id", nullable=false) private WellbeingPostulationEntity postulation;
    @Enumerated(EnumType.STRING) @Column(name="old_status", length=30) private BienestarEnums.PostulationStatus oldStatus;
    @Enumerated(EnumType.STRING) @Column(name="new_status", nullable=false, length=30) private BienestarEnums.PostulationStatus newStatus;
    @Column(columnDefinition="TEXT") private String observation;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="changed_by_user_id") private UserEntity changedBy;
    @Column(name="created_at") private LocalDateTime createdAt;
    @PrePersist public void prePersist(){ createdAt = LocalDateTime.now(); }
}
