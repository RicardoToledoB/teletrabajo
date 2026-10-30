package com.teletrabajo.entity.acreditacion;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="acr_applicant_snapshots")
public class AcrApplicantSnapshot {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long applicationId;
  public String rutNormalized;
  public String fullName;
  public String profession;
  public String establishment;
  public String contractType;
  public Integer hours;
  public String specialty;
  @Column(columnDefinition="TEXT") public String snapshotJson;
  public LocalDateTime capturedAt;
}
