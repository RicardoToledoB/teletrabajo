package com.teletrabajo.entity.acreditacion;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="acr_certificates")
public class AcrCertificate {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long applicationId;
  public Long resolutionId;
  @Column(unique=true) public String certificateNumber;
  @Column(unique=true) public String verificationToken;
  public LocalDateTime issuedAt;
  public String pdfReference;
  public String pdfSha256;
  public String status;
  public LocalDateTime revokedAt;
  public String revokedReason;
}
