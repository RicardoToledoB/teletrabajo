package com.teletrabajo.entity.acreditacion;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="acr_audit_events")
public class AcrAuditEvent {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long processId;
  public Long applicationId;
  public Integer actorUserId;
  public String action;
  public String priorState;
  public String newState;
  public LocalDateTime occurredAt;
  @Column(columnDefinition="TEXT") public String metadataJson;
}
