package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_subjects")
public class QualSubject {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long cycleId;
  public Integer userId;
  public Integer supervisorUserId;
  public String eligibilityStatus;
  @Column(columnDefinition="TEXT") public String exclusionReason;
}
