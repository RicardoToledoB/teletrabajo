package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_audit_events")
public class QualAuditEvent {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Integer actorUserId;
  public String action;
  public String targetRef;
  public LocalDateTime occurredAt;
}
