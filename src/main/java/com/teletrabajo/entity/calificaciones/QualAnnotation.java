package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_annotations")
public class QualAnnotation {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long subjectId;
  public String annotationType;
  @Column(columnDefinition="TEXT") public String grounds;
  public Integer authorId;
  public LocalDateTime occurredAt;
}
