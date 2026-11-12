package com.teletrabajo.entity.capacitacion;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="cap_participants")
public class CapParticipant {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String rut;
 @Column(nullable=false) public String fullName;
 public String email, result, status;
 public Integer userId;
 @Column(precision=5,scale=2) public BigDecimal attendancePercent;
 @Column(precision=5,scale=2) public BigDecimal grade;
 @ManyToOne(fetch=FetchType.EAGER) public CapActivity activity;
}
