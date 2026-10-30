package com.teletrabajo.entity.capacitacion;
import jakarta.persistence.*;
@Entity @Table(name="cap_signatories")
public class CapSignatory {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
@Column(nullable=false) public String fullName; public String positionTitle; public String signatureImagePath; public Boolean active = true;
}
