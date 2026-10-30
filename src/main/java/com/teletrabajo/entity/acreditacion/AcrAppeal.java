package com.teletrabajo.entity.acreditacion;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="acr_appeals")
public class AcrAppeal {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long applicationId;
  public Integer submittedBy;
  @Column(columnDefinition="TEXT") public String grounds;
  public LocalDateTime submittedAt;
  public String status;
  public String decision;
  @Column(columnDefinition="TEXT") public String decisionReason;
  public Integer decidedBy;
  public LocalDateTime decidedAt;
}
