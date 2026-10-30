package com.teletrabajo.entity.acreditacion;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="acr_resolutions")
public class AcrResolution {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long applicationId;
  public Long appealId;
  public String resolutionNumber;
  public String resolutionType;
  public String outcome;
  public LocalDateTime issuedAt;
  public Integer issuedBy;
  public String documentReference;
  public LocalDateTime signedAt;
}
