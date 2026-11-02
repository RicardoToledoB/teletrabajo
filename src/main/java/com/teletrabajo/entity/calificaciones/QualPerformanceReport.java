package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_performance_reports")
public class QualPerformanceReport {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long subjectId;
  public Integer evaluatorId;
  public String periodKey;
  public Integer version;
  public String status;
}
