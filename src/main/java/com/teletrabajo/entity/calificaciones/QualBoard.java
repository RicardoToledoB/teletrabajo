package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_boards")
public class QualBoard {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long cycleId;
  public String designationRef;
  public String status;
}
