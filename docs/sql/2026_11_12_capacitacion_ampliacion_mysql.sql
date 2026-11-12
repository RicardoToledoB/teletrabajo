-- MySQL 8. Ejecutar UNA vez, ANTES de desplegar esta version, sobre el esquema confirmado.
-- Precondicion: estructura de 33 columnas reportada el 12/11/2026; respaldar primero.
-- No elimina tablas, no modifica filas existentes; DDL MySQL hace commit implicito.
USE teletrabajo_db;
ALTER TABLE cap_activities
 ADD COLUMN modality VARCHAR(255) NULL,
 ADD COLUMN execution_number INT NULL,
 ADD UNIQUE KEY uk_cap_activity_execution (code,execution_number);
ALTER TABLE cap_participants
 ADD COLUMN user_id INT NULL,
 ADD COLUMN attendance_percent DECIMAL(5,2) NULL,
 ADD COLUMN grade DECIMAL(5,2) NULL,
 ADD COLUMN status VARCHAR(255) NULL;
ALTER TABLE cap_signatories
 ADD COLUMN valid_from DATE NULL,
 ADD COLUMN valid_to DATE NULL,
 ADD COLUMN signature_position VARCHAR(255) NULL;
ALTER TABLE cap_certificates
 ADD COLUMN certificate_number VARCHAR(255) NULL,
 ADD COLUMN document_reference VARCHAR(255) NULL,
 ADD COLUMN status VARCHAR(255) NULL,
 ADD COLUMN pdf_sha256 VARCHAR(255) NULL,
 ADD COLUMN first_signer_name VARCHAR(255) NULL,
 ADD COLUMN first_signer_title VARCHAR(255) NULL,
 ADD COLUMN first_signer_position VARCHAR(255) NULL,
 ADD COLUMN first_signer_asset VARCHAR(255) NULL,
 ADD COLUMN first_signer_sha256 VARCHAR(255) NULL,
 ADD COLUMN second_signer_name VARCHAR(255) NULL,
 ADD COLUMN second_signer_title VARCHAR(255) NULL,
 ADD COLUMN second_signer_position VARCHAR(255) NULL,
 ADD COLUMN second_signer_asset VARCHAR(255) NULL,
 ADD COLUMN second_signer_sha256 VARCHAR(255) NULL,
 ADD COLUMN issued_by VARCHAR(255) NULL,
 ADD COLUMN revoked_by VARCHAR(255) NULL,
 ADD UNIQUE KEY uk_cap_certificate_number (certificate_number);
CREATE TABLE cap_certificate_documents (
 certificate_id BIGINT NOT NULL PRIMARY KEY,
 pdf_bytes LONGBLOB NOT NULL,
 CONSTRAINT fk_cap_document_certificate FOREIGN KEY (certificate_id) REFERENCES cap_certificates(id)
) ENGINE=InnoDB;
-- Comprobaciones; los cinco registros de cada tabla deben mantenerse.
SELECT COUNT(*) AS actividades FROM cap_activities;
SELECT COUNT(*) AS participantes FROM cap_participants;
SELECT TABLE_NAME,COLUMN_NAME,COLUMN_TYPE FROM information_schema.COLUMNS
 WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME LIKE 'cap_%' ORDER BY TABLE_NAME,ORDINAL_POSITION;
