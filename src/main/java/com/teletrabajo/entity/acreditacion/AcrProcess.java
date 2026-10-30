package com.teletrabajo.entity.acreditacion;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="acr_processes")
public class AcrProcess {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Integer year;
  public String title;
  @Column(columnDefinition="TEXT") public String legalBasis;
  public String status;
  public LocalDateTime opensAt;
  public LocalDateTime closesAt;
  public LocalDateTime reviewStartsAt;
  public LocalDateTime reviewDeadlineAt;
  public LocalDateTime appealOpensAt;
  public LocalDateTime appealClosesAt;
  public LocalDateTime publishedAt;
  public Integer createdBy;
}
