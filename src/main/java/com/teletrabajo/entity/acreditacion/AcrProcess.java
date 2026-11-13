package com.teletrabajo.entity.acreditacion;
import jakarta.persistence.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
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
  @ElementCollection(fetch=FetchType.EAGER)
  @CollectionTable(name="acr_process_professions", joinColumns=@JoinColumn(name="process_id"), foreignKey=@ForeignKey(name="fk_acr_prof_process"))
  @OrderColumn(name="list_index") @Column(name="profession",length=255,nullable=false)
  public List<String> professions = new ArrayList<>();
  @ElementCollection(fetch=FetchType.EAGER)
  @CollectionTable(name="acr_process_modalities", joinColumns=@JoinColumn(name="process_id"), foreignKey=@ForeignKey(name="fk_acr_mod_process"))
  @OrderColumn(name="list_index") @Column(name="modality",length=255,nullable=false)
  public List<String> modalities = new ArrayList<>();
  public Integer admissibilityDays;
  public String admissibilityDayType;
  @Column(columnDefinition="TEXT") public String notice;
}
