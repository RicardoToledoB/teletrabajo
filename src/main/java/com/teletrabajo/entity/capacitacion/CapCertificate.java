package com.teletrabajo.entity.capacitacion;
import jakarta.persistence.*;
@Entity @Table(name="cap_certificates")
public class CapCertificate {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
@Column(nullable=false,unique=true) public String verificationCode; @ManyToOne(fetch=FetchType.EAGER) public CapParticipant participant; @ManyToOne(fetch=FetchType.EAGER) public CapSignatory firstSignatory; @ManyToOne(fetch=FetchType.EAGER) public CapSignatory secondSignatory; public java.time.Instant issuedAt; public java.time.Instant revokedAt;
}
