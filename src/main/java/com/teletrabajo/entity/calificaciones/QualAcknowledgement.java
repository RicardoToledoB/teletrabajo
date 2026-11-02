package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_acknowledgements")
public class QualAcknowledgement {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long subjectId;
  public Long notificationId;
  public LocalDateTime receivedAt;
  @Column(columnDefinition="TEXT") public String observation;
}
