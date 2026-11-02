package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_scores")
public class QualScore {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long reportId;
  public Long ruleId;
  public BigDecimal score;
  @Column(columnDefinition="TEXT") public String rationale;
}
