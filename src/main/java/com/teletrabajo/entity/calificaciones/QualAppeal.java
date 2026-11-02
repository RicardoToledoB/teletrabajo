package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_appeals")
public class QualAppeal {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long subjectId;
  public Long contestedDecisionId;
  public LocalDateTime submittedAt;
  public String status;
}
