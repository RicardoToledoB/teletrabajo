package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_import_batches")
public class QualImportBatch {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public String sourceHash;
  public String status;
  public Integer createdBy;
  public LocalDateTime createdAt;
}
