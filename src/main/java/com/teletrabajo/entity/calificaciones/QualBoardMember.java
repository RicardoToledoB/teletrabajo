package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_board_members")
public class QualBoardMember {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long boardId;
  public Integer userId;
  public String function;
  public LocalDate validFrom;
  public Boolean recused;
}
