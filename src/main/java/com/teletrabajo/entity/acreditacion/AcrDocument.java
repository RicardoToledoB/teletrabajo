package com.teletrabajo.entity.acreditacion;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="acr_documents")
public class AcrDocument {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long applicationId;
  public Long requirementId;
  public Long criterionId;
  public String filename;
  public String documentReference;
  public String sha256;
  public Integer uploadedBy;
  public LocalDateTime uploadedAt;
  public String validationStatus;
  public Integer validatedBy;
}
