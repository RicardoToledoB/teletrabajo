package com.teletrabajo.entity.acreditacion;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="acr_requirements")
public class AcrRequirement {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long processId;
  public String modality;
  public String professionCode;
  public String requirementKey;
  public String title;
  public Boolean required;
  public Integer orderIndex;
  public LocalDate validFrom;
  public LocalDate validTo;
}
