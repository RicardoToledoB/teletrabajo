package com.teletrabajo.entity.calificaciones;
import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
@Entity
@Table(name="qual_board_sessions")
public class QualBoardSession {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public Long boardId;
  public LocalDateTime heldAt;
  public Boolean quorumMet;
  public Long minutesDocId;
}
