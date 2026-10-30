package com.teletrabajo.entity.acreditacion;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="acr_evaluation_scores")
public class AcrEvaluationScore {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long evaluationId;
  public Long criterionId;
  public BigDecimal assignedScore;
  @Column(columnDefinition="TEXT") public String rationale;
  public Integer assessedBy;
  public LocalDateTime assessedAt;
}
