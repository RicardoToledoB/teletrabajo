package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_cycles")
public class QualCycle {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Integer year;
  public LocalDateTime startAt;
  public LocalDateTime endAt;
  public String rulesVersion;
  public String status;
  @Column(columnDefinition="TEXT") public String legalBasis;
  public Integer createdBy;
  public LocalDateTime createdAt;
}
