-- MySQL 8, aplicar UNA vez tras respaldo y antes del arranque del nuevo JAR.
-- Solo amplía Acreditacion; no elimina ni modifica filas existentes.
ALTER TABLE acr_processes
 ADD COLUMN admissibility_days INT NULL,
 ADD COLUMN admissibility_day_type VARCHAR(255) NULL,
 ADD COLUMN notice TEXT NULL;
CREATE TABLE acr_process_professions (
 process_id BIGINT NOT NULL,
 list_index INT NOT NULL,
 profession VARCHAR(255) NOT NULL,
 PRIMARY KEY(process_id,list_index),
 CONSTRAINT fk_acr_prof_process FOREIGN KEY(process_id) REFERENCES acr_processes(id)
) ENGINE=InnoDB;
CREATE TABLE acr_process_modalities (
 process_id BIGINT NOT NULL,
 list_index INT NOT NULL,
 modality VARCHAR(255) NOT NULL,
 PRIMARY KEY(process_id,list_index),
 CONSTRAINT fk_acr_mod_process FOREIGN KEY(process_id) REFERENCES acr_processes(id)
) ENGINE=InnoDB;
CREATE TABLE acr_process_documents (
 id BIGINT NOT NULL AUTO_INCREMENT,
 process_id BIGINT NOT NULL,
 filename VARCHAR(255) NOT NULL,
 sha256 VARCHAR(64) NOT NULL,
 uploaded_by INT NULL,
 uploaded_at DATETIME(6) NULL,
 active BIT(1) NOT NULL,
 pdf_bytes LONGBLOB NOT NULL,
 PRIMARY KEY(id),
 INDEX idx_acr_process_documents(process_id),
 CONSTRAINT fk_acr_doc_process FOREIGN KEY(process_id) REFERENCES acr_processes(id)
) ENGINE=InnoDB;
SHOW COLUMNS FROM acr_processes;
SHOW TABLES LIKE 'acr_process%';
