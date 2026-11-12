package com.teletrabajo.entity.capacitacion;
import jakarta.persistence.*;
@Entity @Table(name="cap_certificate_documents")
public class CapCertificateDocument {
 @Id public Long certificateId;
 @Lob @Column(nullable=false, columnDefinition="LONGBLOB") public byte[] pdfBytes;
}
