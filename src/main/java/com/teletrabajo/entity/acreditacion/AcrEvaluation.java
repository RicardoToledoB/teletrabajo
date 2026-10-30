package com.teletrabajo.entity.acreditacion;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="acr_evaluations")
public class AcrEvaluation {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long applicationId;
  public String commissionStatus;
  public BigDecimal totalScore;
  public Boolean approvedByScore;
  @Column(columnDefinition="TEXT") public String observations;
  public LocalDateTime finalizedAt;
  public Integer finalizedBy;
}
