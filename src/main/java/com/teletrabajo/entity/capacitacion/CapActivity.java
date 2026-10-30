package com.teletrabajo.entity.capacitacion;
import jakarta.persistence.*;
@Entity @Table(name="cap_activities")
public class CapActivity {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
@Column(nullable=false) public String title; public String code; public String provider; public java.time.LocalDate startDate; public java.time.LocalDate endDate; public Integer hours; public Boolean active = true;
}
