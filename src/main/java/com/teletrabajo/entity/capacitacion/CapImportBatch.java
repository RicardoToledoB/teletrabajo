package com.teletrabajo.entity.capacitacion;
import jakarta.persistence.*;
@Entity @Table(name="cap_import_batches")
public class CapImportBatch {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
public String fileName; public Integer totalRows; public Integer successfulRows; public Integer failedRows; public java.time.Instant importedAt; public String importedBy;
}
