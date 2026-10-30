package com.teletrabajo.entity.acreditacion;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="acr_commission_members")
public class AcrCommissionMember {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long processId;
  public Integer userId;
  public String functionCode;
  public LocalDate activeFrom;
  public LocalDate activeTo;
  public String appointmentReference;
}
