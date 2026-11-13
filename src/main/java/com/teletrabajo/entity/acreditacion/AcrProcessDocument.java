package com.teletrabajo.entity.acreditacion;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="acr_process_documents")
public class AcrProcessDocument {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public Long processId;
 @Column(nullable=false) public String filename;
 @Column(nullable=false,length=64) public String sha256;
 public Integer uploadedBy;
 public LocalDateTime uploadedAt;
 @Column(nullable=false) public Boolean active=true;
 @Lob @Column(nullable=false,columnDefinition="LONGBLOB") public byte[] pdfBytes;
}
