package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_personnel_snapshots")
public class QualPersonnelSnapshot {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long subjectId;
  @Column(columnDefinition="TEXT") public String snapshotJson;
  public LocalDateTime capturedAt;
}
