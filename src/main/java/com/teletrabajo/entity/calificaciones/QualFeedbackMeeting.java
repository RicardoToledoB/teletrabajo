package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_feedback_meetings")
public class QualFeedbackMeeting {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long subjectId;
  public LocalDateTime startsAt;
  public Boolean attended;
  public Long minutesDocId;
}
