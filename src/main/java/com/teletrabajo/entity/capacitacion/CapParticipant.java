package com.teletrabajo.entity.capacitacion;
import jakarta.persistence.*;
@Entity @Table(name="cap_participants")
public class CapParticipant {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
@Column(nullable=false) public String rut; @Column(nullable=false) public String fullName; public String email; public String result; @ManyToOne(fetch=FetchType.EAGER) public CapActivity activity;
}
