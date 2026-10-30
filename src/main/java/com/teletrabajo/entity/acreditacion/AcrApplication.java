package com.teletrabajo.entity.acreditacion;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="acr_applications")
public class AcrApplication {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long processId;
  public Integer applicantUserId;
  @Column(unique=true) public String trackingCode;
  public String professionCode;
  public String modality;
  public String status;
  public String eligibilityStatus;
  public String finalOutcome;
  public LocalDateTime submittedAt;
  public LocalDateTime closedAt;
}
