package com.teletrabajo.entity.capacitacion;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="cap_certificates", uniqueConstraints=@UniqueConstraint(name="uk_cap_certificate_number",columnNames="certificate_number"))
public class CapCertificate {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,unique=true) public String verificationCode;
 @ManyToOne(fetch=FetchType.EAGER) public CapParticipant participant;
 @ManyToOne(fetch=FetchType.EAGER) public CapSignatory firstSignatory;
 @ManyToOne(fetch=FetchType.EAGER) public CapSignatory secondSignatory;
 public Instant issuedAt, revokedAt;
 @Column(name="certificate_number") public String certificateNumber;
 public String documentReference, status, pdfSha256;
 public String firstSignerName, firstSignerTitle, firstSignerPosition, firstSignerAsset, firstSignerSha256;
 public String secondSignerName, secondSignerTitle, secondSignerPosition, secondSignerAsset, secondSignerSha256;
 public String issuedBy, revokedBy;
 public Long getParticipationId(){return participant==null?null:participant.id;}
}
