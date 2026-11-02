package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_notifications")
public class QualNotification {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long subjectId;
  public String eventCode;
  public String status;
  public LocalDateTime sentAt;
  public LocalDateTime deliveredAt;
}
