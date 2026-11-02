package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_cycle_rules")
public class QualCycleRule {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long cycleId;
  public String code;
  public BigDecimal minScore;
  public BigDecimal maxScore;
  public BigDecimal weight;
  @Column(columnDefinition="TEXT") public String formula;
}
