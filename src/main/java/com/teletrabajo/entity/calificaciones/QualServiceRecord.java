package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_service_records")
public class QualServiceRecord {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long subjectId;
  public String entryType;
  public LocalDateTime occurredAt;
  public Long docId;
}
