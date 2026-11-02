package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_board_decisions")
public class QualBoardDecision {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long sessionId;
  public Long subjectId;
  public BigDecimal finalScore;
  public String listCode;
  @Column(columnDefinition="TEXT") public String grounds;
}
