package com.teletrabajo.entity.capacitacion;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity @Table(name="cap_signatories")
public class CapSignatory {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String fullName;
 public String positionTitle, signatureImagePath, signaturePosition;
 public LocalDate validFrom, validTo;
 public Boolean active = true;
 public String getSignatureAsset(){return signatureImagePath;}
}
