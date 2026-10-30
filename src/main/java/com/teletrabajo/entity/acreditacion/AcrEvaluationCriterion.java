package com.teletrabajo.entity.acreditacion;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="acr_evaluation_criteria")
public class AcrEvaluationCriterion {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long processId;
  public Long parentId;
  public String areaCode;
  public String factorCode;
  public String title;
  public BigDecimal maxPoints;
  public BigDecimal minPoints;
  public String calculationRule;
  public Integer orderIndex;
}
