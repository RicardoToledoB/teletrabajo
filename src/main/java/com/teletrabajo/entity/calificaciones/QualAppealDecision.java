package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_appeal_decisions")
public class QualAppealDecision {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long appealId;
  public Integer decidedBy;
  @Column(columnDefinition="TEXT") public String decision;
  public LocalDateTime decidedAt;
}
