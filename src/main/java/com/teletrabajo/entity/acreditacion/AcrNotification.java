package com.teletrabajo.entity.acreditacion;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="acr_notifications")
public class AcrNotification {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long applicationId;
  public Integer recipientUserId;
  public String eventCode;
  public String channel;
  public String deliveryStatus;
  public LocalDateTime sentAt;
  public String errorSummary;
}
