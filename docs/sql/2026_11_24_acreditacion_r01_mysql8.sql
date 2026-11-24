-- DSSM Recursos Humanos | Acreditacion R01 | MySQL 8
-- MIGRACION ADITIVA: validar respaldo y ventana de mantenimiento ANTES de ejecutar.
-- NO EJECUTAR el SQL PostgreSQL contenido en el documento original.
-- Procedimiento verifica cada columna para permitir reejecucion tras un fallo parcial.
DELIMITER $$
DROP PROCEDURE IF EXISTS acr_r01_add_column$$
CREATE PROCEDURE acr_r01_add_column(IN t VARCHAR(80),IN c VARCHAR(80),IN definition VARCHAR(255))
BEGIN
 IF NOT EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=t AND COLUMN_NAME=c) THEN
  SET @ddl=CONCAT('ALTER TABLE `',t,'` ADD COLUMN `',c,'` ',definition);
  PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;
 END IF;
END$$
DELIMITER ;

CALL acr_r01_add_column('acr_processes','year','INT');
CALL acr_r01_add_column('acr_processes','title','TEXT');
CALL acr_r01_add_column('acr_processes','legal_basis','TEXT');
CALL acr_r01_add_column('acr_processes','status','TEXT');
CALL acr_r01_add_column('acr_processes','opens_at','DATETIME(6)');
CALL acr_r01_add_column('acr_processes','closes_at','DATETIME(6)');
CALL acr_r01_add_column('acr_processes','review_starts_at','DATETIME(6)');
CALL acr_r01_add_column('acr_processes','review_deadline_at','DATETIME(6)');
CALL acr_r01_add_column('acr_processes','appeal_opens_at','DATETIME(6)');
CALL acr_r01_add_column('acr_processes','appeal_closes_at','DATETIME(6)');
CALL acr_r01_add_column('acr_processes','published_at','DATETIME(6)');
CALL acr_r01_add_column('acr_processes','created_by','INT');
CALL acr_r01_add_column('acr_processes','admissibility_days','INT');
CALL acr_r01_add_column('acr_processes','admissibility_day_type','TEXT');
CALL acr_r01_add_column('acr_processes','notice','TEXT');

CALL acr_r01_add_column('acr_applications','process_id','BIGINT');
CALL acr_r01_add_column('acr_applications','applicant_user_id','INT');
CALL acr_r01_add_column('acr_applications','tracking_code','TEXT');
CALL acr_r01_add_column('acr_applications','profession_code','TEXT');
CALL acr_r01_add_column('acr_applications','modality','TEXT');
CALL acr_r01_add_column('acr_applications','status','TEXT');
CALL acr_r01_add_column('acr_applications','eligibility_status','TEXT');
CALL acr_r01_add_column('acr_applications','final_outcome','TEXT');
CALL acr_r01_add_column('acr_applications','submitted_at','DATETIME(6)');
CALL acr_r01_add_column('acr_applications','closed_at','DATETIME(6)');
CALL acr_r01_add_column('acr_applications','receipt_reference','TEXT');
CALL acr_r01_add_column('acr_applications','draft_updated_at','DATETIME(6)');

CALL acr_r01_add_column('acr_applicant_snapshots','application_id','BIGINT');
CALL acr_r01_add_column('acr_applicant_snapshots','rut_normalized','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','full_name','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','profession','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','establishment','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','contract_type','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','hours','DECIMAL(10,2)');
CALL acr_r01_add_column('acr_applicant_snapshots','specialty','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','snapshot_json','JSON');
CALL acr_r01_add_column('acr_applicant_snapshots','captured_at','DATETIME(6)');
CALL acr_r01_add_column('acr_applicant_snapshots','first_names','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','paternal_surname','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','maternal_surname','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','birth_date','DATE');
CALL acr_r01_add_column('acr_applicant_snapshots','nationality','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','address','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','commune','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','city','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','email','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','phone','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','degree_title','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','degree_date','DATE');
CALL acr_r01_add_column('acr_applicant_snapshots','specialty_authority','TEXT');
CALL acr_r01_add_column('acr_applicant_snapshots','specialty_date','DATE');

CALL acr_r01_add_column('acr_requirements','process_id','BIGINT');
CALL acr_r01_add_column('acr_requirements','modality','TEXT');
CALL acr_r01_add_column('acr_requirements','profession_code','TEXT');
CALL acr_r01_add_column('acr_requirements','title','TEXT');
CALL acr_r01_add_column('acr_requirements','required','BOOLEAN');
CALL acr_r01_add_column('acr_requirements','order_index','INT');
CALL acr_r01_add_column('acr_requirements','valid_from','DATE');
CALL acr_r01_add_column('acr_requirements','valid_to','DATE');

CALL acr_r01_add_column('acr_documents','application_id','BIGINT');
CALL acr_r01_add_column('acr_documents','requirement_id','BIGINT');
CALL acr_r01_add_column('acr_documents','criterion_id','BIGINT');
CALL acr_r01_add_column('acr_documents','filename','TEXT');
CALL acr_r01_add_column('acr_documents','document_reference','TEXT');
CALL acr_r01_add_column('acr_documents','sha256','TEXT');
CALL acr_r01_add_column('acr_documents','uploaded_by','INT');
CALL acr_r01_add_column('acr_documents','uploaded_at','DATETIME(6)');
CALL acr_r01_add_column('acr_documents','validation_status','TEXT');
CALL acr_r01_add_column('acr_documents','validated_by','INT');
CALL acr_r01_add_column('acr_documents','title','TEXT');
CALL acr_r01_add_column('acr_documents','document_type','TEXT');
CALL acr_r01_add_column('acr_documents','period_start','DATE');
CALL acr_r01_add_column('acr_documents','period_end','DATE');
CALL acr_r01_add_column('acr_documents','issuer','TEXT');
CALL acr_r01_add_column('acr_documents','signature_checked','BOOLEAN');
CALL acr_r01_add_column('acr_documents','version_number','INT');
CALL acr_r01_add_column('acr_documents','previous_document_id','BIGINT');
CALL acr_r01_add_column('acr_documents','active','BOOLEAN');
CALL acr_r01_add_column('acr_documents','validation_reason','TEXT');
CALL acr_r01_add_column('acr_documents','validated_at','DATETIME(6)');

CALL acr_r01_add_column('acr_evaluation_criteria','process_id','BIGINT');
CALL acr_r01_add_column('acr_evaluation_criteria','parent_id','BIGINT');
CALL acr_r01_add_column('acr_evaluation_criteria','area_code','TEXT');
CALL acr_r01_add_column('acr_evaluation_criteria','factor_code','TEXT');
CALL acr_r01_add_column('acr_evaluation_criteria','title','TEXT');
CALL acr_r01_add_column('acr_evaluation_criteria','max_points','DECIMAL(10,2)');
CALL acr_r01_add_column('acr_evaluation_criteria','min_points','DECIMAL(10,2)');
CALL acr_r01_add_column('acr_evaluation_criteria','calculation_rule','TEXT');
CALL acr_r01_add_column('acr_evaluation_criteria','order_index','INT');
CALL acr_r01_add_column('acr_evaluation_criteria','profession_code','TEXT');
CALL acr_r01_add_column('acr_evaluation_criteria','modality','TEXT');
CALL acr_r01_add_column('acr_evaluation_criteria','version_number','INT');

CALL acr_r01_add_column('acr_evaluations','application_id','BIGINT');
CALL acr_r01_add_column('acr_evaluations','commission_status','TEXT');
CALL acr_r01_add_column('acr_evaluations','total_score','DECIMAL(10,2)');
CALL acr_r01_add_column('acr_evaluations','approved_by_score','BOOLEAN');
CALL acr_r01_add_column('acr_evaluations','observations','TEXT');
CALL acr_r01_add_column('acr_evaluations','finalized_at','DATETIME(6)');
CALL acr_r01_add_column('acr_evaluations','finalized_by','INT');
CALL acr_r01_add_column('acr_evaluations','version_number','INT');
CALL acr_r01_add_column('acr_evaluations','is_current','BOOLEAN');
CALL acr_r01_add_column('acr_evaluations','act_reference','TEXT');
CALL acr_r01_add_column('acr_evaluations','published_at','DATETIME(6)');
CALL acr_r01_add_column('acr_evaluations','published_by','INT');

CALL acr_r01_add_column('acr_evaluation_scores','evaluation_id','BIGINT');
CALL acr_r01_add_column('acr_evaluation_scores','criterion_id','BIGINT');
CALL acr_r01_add_column('acr_evaluation_scores','assigned_score','DECIMAL(10,2)');
CALL acr_r01_add_column('acr_evaluation_scores','rationale','TEXT');
CALL acr_r01_add_column('acr_evaluation_scores','assessed_by','INT');
CALL acr_r01_add_column('acr_evaluation_scores','assessed_at','DATETIME(6)');

CALL acr_r01_add_column('acr_commission_members','process_id','BIGINT');
CALL acr_r01_add_column('acr_commission_members','user_id','INT');
CALL acr_r01_add_column('acr_commission_members','function_code','TEXT');
CALL acr_r01_add_column('acr_commission_members','active_from','DATE');
CALL acr_r01_add_column('acr_commission_members','active_to','DATE');
CALL acr_r01_add_column('acr_commission_members','appointment_reference','TEXT');

CALL acr_r01_add_column('acr_appeals','application_id','BIGINT');
CALL acr_r01_add_column('acr_appeals','submitted_by','INT');
CALL acr_r01_add_column('acr_appeals','grounds','TEXT');
CALL acr_r01_add_column('acr_appeals','submitted_at','DATETIME(6)');
CALL acr_r01_add_column('acr_appeals','status','TEXT');
CALL acr_r01_add_column('acr_appeals','decision','TEXT');
CALL acr_r01_add_column('acr_appeals','decision_reason','TEXT');
CALL acr_r01_add_column('acr_appeals','decided_by','INT');
CALL acr_r01_add_column('acr_appeals','decided_at','DATETIME(6)');
CALL acr_r01_add_column('acr_appeals','appeal_number','TEXT');
CALL acr_r01_add_column('acr_appeals','challenged_decision','TEXT');
CALL acr_r01_add_column('acr_appeals','notified_at','DATETIME(6)');
CALL acr_r01_add_column('acr_appeals','cause','TEXT');
CALL acr_r01_add_column('acr_appeals','requested_action','TEXT');
CALL acr_r01_add_column('acr_appeals','consent','BOOLEAN');
CALL acr_r01_add_column('acr_appeals','receipt_reference','TEXT');
CALL acr_r01_add_column('acr_appeals','challenged_resolution_id','BIGINT');

CALL acr_r01_add_column('acr_resolutions','application_id','BIGINT');
CALL acr_r01_add_column('acr_resolutions','appeal_id','BIGINT');
CALL acr_r01_add_column('acr_resolutions','resolution_number','TEXT');
CALL acr_r01_add_column('acr_resolutions','resolution_type','TEXT');
CALL acr_r01_add_column('acr_resolutions','outcome','TEXT');
CALL acr_r01_add_column('acr_resolutions','issued_at','DATETIME(6)');
CALL acr_r01_add_column('acr_resolutions','issued_by','INT');
CALL acr_r01_add_column('acr_resolutions','document_reference','TEXT');
CALL acr_r01_add_column('acr_resolutions','signed_at','DATETIME(6)');
CALL acr_r01_add_column('acr_resolutions','decision_reason','TEXT');
CALL acr_r01_add_column('acr_resolutions','modified_outcome','TEXT');

CALL acr_r01_add_column('acr_certificates','application_id','BIGINT');
CALL acr_r01_add_column('acr_certificates','resolution_id','BIGINT');
CALL acr_r01_add_column('acr_certificates','certificate_number','TEXT');
CALL acr_r01_add_column('acr_certificates','verification_token','TEXT');
CALL acr_r01_add_column('acr_certificates','issued_at','DATETIME(6)');
CALL acr_r01_add_column('acr_certificates','pdf_reference','TEXT');
CALL acr_r01_add_column('acr_certificates','pdf_sha256','TEXT');
CALL acr_r01_add_column('acr_certificates','status','TEXT');
CALL acr_r01_add_column('acr_certificates','revoked_at','DATETIME(6)');
CALL acr_r01_add_column('acr_certificates','revoked_reason','TEXT');
CALL acr_r01_add_column('acr_certificates','certificate_type','TEXT');
CALL acr_r01_add_column('acr_certificates','period_start','DATE');
CALL acr_r01_add_column('acr_certificates','period_end','DATE');
CALL acr_r01_add_column('acr_certificates','holder_name','TEXT');
CALL acr_r01_add_column('acr_certificates','profession','TEXT');

CALL acr_r01_add_column('acr_notifications','application_id','BIGINT');
CALL acr_r01_add_column('acr_notifications','recipient_user_id','INT');
CALL acr_r01_add_column('acr_notifications','event_code','TEXT');
CALL acr_r01_add_column('acr_notifications','channel','TEXT');
CALL acr_r01_add_column('acr_notifications','delivery_status','TEXT');
CALL acr_r01_add_column('acr_notifications','sent_at','DATETIME(6)');
CALL acr_r01_add_column('acr_notifications','error_summary','TEXT');
CALL acr_r01_add_column('acr_notifications','process_id','BIGINT');
CALL acr_r01_add_column('acr_notifications','subject','TEXT');
CALL acr_r01_add_column('acr_notifications','content','TEXT');
CALL acr_r01_add_column('acr_notifications','scheduled_at','DATETIME(6)');

CALL acr_r01_add_column('acr_audit_events','process_id','BIGINT');
CALL acr_r01_add_column('acr_audit_events','application_id','BIGINT');
CALL acr_r01_add_column('acr_audit_events','actor_user_id','INT');
CALL acr_r01_add_column('acr_audit_events','action','TEXT');
CALL acr_r01_add_column('acr_audit_events','prior_state','TEXT');
CALL acr_r01_add_column('acr_audit_events','new_state','TEXT');
CALL acr_r01_add_column('acr_audit_events','occurred_at','DATETIME(6)');
CALL acr_r01_add_column('acr_audit_events','metadata_json','JSON');
CALL acr_r01_add_column('acr_audit_events','visible_to_applicant','BOOLEAN');
CALL acr_r01_add_column('acr_audit_events','public_observation','TEXT');

CALL acr_r01_add_column('acr_process_documents','process_id','BIGINT');
CALL acr_r01_add_column('acr_process_documents','filename','TEXT');
CALL acr_r01_add_column('acr_process_documents','sha256','TEXT');
CALL acr_r01_add_column('acr_process_documents','uploaded_by','INT');
CALL acr_r01_add_column('acr_process_documents','uploaded_at','DATETIME(6)');
CALL acr_r01_add_column('acr_process_documents','active','BOOLEAN');
CALL acr_r01_add_column('acr_process_documents','document_reference','TEXT');

CREATE TABLE IF NOT EXISTS `acr_application_jobs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `application_id` BIGINT NOT NULL,
  `position_number` INT,
  `establishment` TEXT,
  `unit_name` TEXT,
  `hours` DECIMAL(10,2),
  `job_specialty` TEXT,
  `appointment_type` TEXT,
  KEY `idx_application_jobs_parent` (`application_id`),
  CONSTRAINT `fk_r01_application_jobs_application_id` FOREIGN KEY (`application_id`) REFERENCES `acr_applications` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `acr_declarations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `application_id` BIGINT NOT NULL UNIQUE,
  `declaration_text` TEXT,
  `declared_at` DATETIME(6),
  `place` TEXT,
  `accepted` BOOLEAN,
  `signature_method` TEXT,
  `document_id` BIGINT,
  KEY `idx_declarations_parent` (`application_id`),
  CONSTRAINT `fk_r01_declarations_application_id` FOREIGN KEY (`application_id`) REFERENCES `acr_applications` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_r01_declarations_document_id` FOREIGN KEY (`document_id`) REFERENCES `acr_documents` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `acr_training` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `application_id` BIGINT NOT NULL,
  `activity_name` TEXT,
  `modality` TEXT,
  `provider` TEXT,
  `start_date` DATE,
  `end_date` DATE,
  `academic_hours` DECIMAL(10,2),
  `has_evaluation` BOOLEAN,
  `grade_result` TEXT,
  `specialization_type` TEXT,
  `document_id` BIGINT,
  `training_validation` TEXT,
  KEY `idx_training_parent` (`application_id`),
  CONSTRAINT `fk_r01_training_application_id` FOREIGN KEY (`application_id`) REFERENCES `acr_applications` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_r01_training_document_id` FOREIGN KEY (`document_id`) REFERENCES `acr_documents` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `acr_academic_evidence` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `application_id` BIGINT NOT NULL,
  `evidence_type` TEXT,
  `institution` TEXT,
  `role_name` TEXT,
  `description` TEXT,
  `period_start` DATE,
  `period_end` DATE,
  `teaching_hours` DECIMAL(10,2),
  `research_count` INT,
  `document_id` BIGINT,
  `observations` TEXT,
  KEY `idx_academic_evidence_parent` (`application_id`),
  CONSTRAINT `fk_r01_academic_evidence_application_id` FOREIGN KEY (`application_id`) REFERENCES `acr_applications` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_r01_academic_evidence_document_id` FOREIGN KEY (`document_id`) REFERENCES `acr_documents` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `acr_clinical_evidence` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `application_id` BIGINT NOT NULL,
  `activity_type` TEXT,
  `establishment` TEXT,
  `unit_name` TEXT,
  `period_start` DATE,
  `period_end` DATE,
  `hours` DECIMAL(10,2),
  `quantitative_indicator` TEXT,
  `qualitative_evaluation` TEXT,
  `certifier` TEXT,
  `document_id` BIGINT,
  `observations` TEXT,
  KEY `idx_clinical_evidence_parent` (`application_id`),
  CONSTRAINT `fk_r01_clinical_evidence_application_id` FOREIGN KEY (`application_id`) REFERENCES `acr_applications` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_r01_clinical_evidence_document_id` FOREIGN KEY (`document_id`) REFERENCES `acr_documents` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `acr_critical_evidence` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `application_id` BIGINT NOT NULL,
  `event_type` TEXT,
  `event_date` DATE,
  `establishment` TEXT,
  `description` TEXT,
  `participation` TEXT,
  `certifier` TEXT,
  `document_id` BIGINT,
  KEY `idx_critical_evidence_parent` (`application_id`),
  CONSTRAINT `fk_r01_critical_evidence_application_id` FOREIGN KEY (`application_id`) REFERENCES `acr_applications` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_r01_critical_evidence_document_id` FOREIGN KEY (`document_id`) REFERENCES `acr_documents` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `acr_org_evidence` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `application_id` BIGINT NOT NULL,
  `evidence_type` TEXT,
  `description` TEXT,
  `relevance` TEXT,
  `period_start` DATE,
  `period_end` DATE,
  `unit_name` TEXT,
  `establishment` TEXT,
  `role_name` TEXT,
  `contribution_count` INT,
  `document_id` BIGINT,
  `certifier` TEXT,
  KEY `idx_org_evidence_parent` (`application_id`),
  CONSTRAINT `fk_r01_org_evidence_application_id` FOREIGN KEY (`application_id`) REFERENCES `acr_applications` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_r01_org_evidence_document_id` FOREIGN KEY (`document_id`) REFERENCES `acr_documents` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `acr_recognitions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `application_id` BIGINT NOT NULL,
  `recognition_type` TEXT,
  `institution` TEXT,
  `recognition_date` DATE,
  `reason` TEXT,
  `resolution_number` TEXT,
  `document_id` BIGINT,
  KEY `idx_recognitions_parent` (`application_id`),
  CONSTRAINT `fk_r01_recognitions_application_id` FOREIGN KEY (`application_id`) REFERENCES `acr_applications` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_r01_recognitions_document_id` FOREIGN KEY (`document_id`) REFERENCES `acr_documents` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `acr_admissibility_reviews` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `application_id` BIGINT NOT NULL,
  `reviewer_id` INT,
  `decision` TEXT,
  `reason` TEXT,
  `reviewed_at` DATETIME(6),
  `is_current` BOOLEAN,
  KEY `idx_admissibility_reviews_parent` (`application_id`),
  CONSTRAINT `fk_r01_admissibility_reviews_application_id` FOREIGN KEY (`application_id`) REFERENCES `acr_applications` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `acr_admissibility_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `review_id` BIGINT NOT NULL,
  `requirement_id` BIGINT,
  `document_id` BIGINT,
  `result` TEXT,
  `observation` TEXT,
  KEY `idx_admissibility_items_parent` (`review_id`),
  CONSTRAINT `fk_r01_admissibility_items_review_id` FOREIGN KEY (`review_id`) REFERENCES `acr_admissibility_reviews` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_r01_admissibility_items_requirement_id` FOREIGN KEY (`requirement_id`) REFERENCES `acr_requirements` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_r01_admissibility_items_document_id` FOREIGN KEY (`document_id`) REFERENCES `acr_documents` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `acr_commission_assignments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `application_id` BIGINT NOT NULL,
  `member_id` BIGINT NOT NULL,
  `assigned_at` DATETIME(6),
  `active` BOOLEAN,
  KEY `idx_commission_assignments_parent` (`application_id`),
  CONSTRAINT `fk_r01_commission_assignments_application_id` FOREIGN KEY (`application_id`) REFERENCES `acr_applications` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_r01_commission_assignments_member_id` FOREIGN KEY (`member_id`) REFERENCES `acr_commission_members` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `acr_appeal_documents` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `appeal_id` BIGINT,
  `document_id` BIGINT,
  `purpose` TEXT,
  KEY `idx_appeal_documents_parent` (`appeal_id`),
  CONSTRAINT `fk_r01_appeal_documents_appeal_id` FOREIGN KEY (`appeal_id`) REFERENCES `acr_appeals` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_r01_appeal_documents_document_id` FOREIGN KEY (`document_id`) REFERENCES `acr_documents` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `acr_certificate_signers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `certificate_id` BIGINT NOT NULL,
  `signer_name` TEXT,
  `signer_title` TEXT,
  `signature_reference` TEXT,
  KEY `idx_certificate_signers_parent` (`certificate_id`),
  CONSTRAINT `fk_r01_certificate_signers_certificate_id` FOREIGN KEY (`certificate_id`) REFERENCES `acr_certificates` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `acr_document_files` (
 `document_id` BIGINT NOT NULL PRIMARY KEY,
 `pdf_bytes` LONGBLOB NOT NULL,
 CONSTRAINT `fk_acr_blob_doc` FOREIGN KEY (`document_id`) REFERENCES `acr_documents` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
DROP PROCEDURE IF EXISTS acr_r01_add_column;
SELECT TABLE_NAME, COUNT(*) AS columnas FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME LIKE 'acr_%' GROUP BY TABLE_NAME ORDER BY TABLE_NAME;
