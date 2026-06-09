-- ==========================================================
-- Módulo Bienestar - Postulación Apoyo Estudios Superiores
-- Motor objetivo: MariaDB / MySQL
-- Criterio: no modifica tablas existentes. Solo crea tablas nuevas.
-- Integra con tablas existentes: users, stablishments, parents_types,
-- previtions, contracts_types, civil_states, activities, works_places,
-- studies, types_housings, types_properties.
-- ==========================================================

CREATE TABLE IF NOT EXISTS wellbeing_postulations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(30) NOT NULL,
  period_year INT NOT NULL,
  user_id INT NOT NULL,
  stablishment_id INT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
  beneficiary_type VARCHAR(30) NULL,
  beneficiary_family_member_id BIGINT NULL,
  affiliate_rut VARCHAR(20) NULL,
  affiliate_names VARCHAR(120) NULL,
  affiliate_last_names VARCHAR(120) NULL,
  affiliate_phone VARCHAR(30) NULL,
  affiliate_email VARCHAR(160) NULL,
  affiliate_address VARCHAR(255) NULL,
  affiliate_birth_date VARCHAR(20) NULL,
  affiliate_sex VARCHAR(30) NULL,
  affiliate_type VARCHAR(30) NULL,
  affiliate_date VARCHAR(20) NULL,
  total_family_income DECIMAL(14,2) DEFAULT 0,
  total_basic_expenses DECIMAL(14,2) DEFAULT 0,
  total_education_expenses DECIMAL(14,2) DEFAULT 0,
  total_other_expenses DECIMAL(14,2) DEFAULT 0,
  total_health_expenses DECIMAL(14,2) DEFAULT 0,
  total_family_expenses DECIMAL(14,2) DEFAULT 0,
  submitted_at DATETIME NULL,
  created_at DATETIME NULL,
  updated_at DATETIME NULL,
  deleted_at DATETIME NULL,
  CONSTRAINT uk_wellbeing_postulation_code UNIQUE (code),
  CONSTRAINT fk_wb_post_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_wb_post_stablishment FOREIGN KEY (stablishment_id) REFERENCES stablishments(id),
  INDEX idx_wb_post_user_year (user_id, period_year),
  INDEX idx_wb_post_status (status)
);

CREATE TABLE IF NOT EXISTS wellbeing_family_members (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  postulation_id BIGINT NOT NULL,
  rut VARCHAR(20) NULL,
  names VARCHAR(120) NULL,
  last_names VARCHAR(120) NULL,
  birth_date DATE NULL,
  prevition_id INT NULL,
  contract_type_id INT NULL,
  parent_type_id INT NULL,
  civil_state_id INT NULL,
  activity_id INT NULL,
  work_place_id INT NULL,
  study_id INT NULL,
  study_place VARCHAR(120) NULL,
  is_student TINYINT(1) NULL,
  monthly_income DECIMAL(14,2) DEFAULT 0,
  created_at DATETIME NULL,
  updated_at DATETIME NULL,
  deleted_at DATETIME NULL,
  CONSTRAINT fk_wb_fam_post FOREIGN KEY (postulation_id) REFERENCES wellbeing_postulations(id),
  CONSTRAINT fk_wb_fam_prevition FOREIGN KEY (prevition_id) REFERENCES previtions(id),
  CONSTRAINT fk_wb_fam_income_type FOREIGN KEY (contract_type_id) REFERENCES contracts_types(id),
  CONSTRAINT fk_wb_fam_parent FOREIGN KEY (parent_type_id) REFERENCES parents_types(id),
  CONSTRAINT fk_wb_fam_civil FOREIGN KEY (civil_state_id) REFERENCES civil_states(id),
  CONSTRAINT fk_wb_fam_activity FOREIGN KEY (activity_id) REFERENCES activities(id),
  CONSTRAINT fk_wb_fam_work_place FOREIGN KEY (work_place_id) REFERENCES works_places(id),
  CONSTRAINT fk_wb_fam_study FOREIGN KEY (study_id) REFERENCES studies(id),
  INDEX idx_wb_family_postulation (postulation_id)
);

ALTER TABLE wellbeing_postulations
  ADD CONSTRAINT fk_wb_post_beneficiary_family
  FOREIGN KEY (beneficiary_family_member_id) REFERENCES wellbeing_family_members(id);

CREATE TABLE IF NOT EXISTS wellbeing_academic_infos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  postulation_id BIGINT NOT NULL UNIQUE,
  institution VARCHAR(180) NULL,
  career VARCHAR(180) NULL,
  study_id INT NULL,
  current_semester VARCHAR(40) NULL,
  career_duration_semesters INT NULL,
  studies_in_region TINYINT(1) NULL,
  had_previous_benefit TINYINT(1) NULL,
  created_at DATETIME NULL,
  updated_at DATETIME NULL,
  deleted_at DATETIME NULL,
  CONSTRAINT fk_wb_acad_post FOREIGN KEY (postulation_id) REFERENCES wellbeing_postulations(id),
  CONSTRAINT fk_wb_acad_study FOREIGN KEY (study_id) REFERENCES studies(id)
);

CREATE TABLE IF NOT EXISTS wellbeing_academic_verifications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  postulation_id BIGINT NOT NULL UNIQUE,
  academic_situation VARCHAR(80) NULL,
  grade_average DECIMAL(4,2) NULL,
  approval_percentage DECIMAL(5,2) NULL,
  created_at DATETIME NULL,
  updated_at DATETIME NULL,
  deleted_at DATETIME NULL,
  CONSTRAINT fk_wb_acad_ver_post FOREIGN KEY (postulation_id) REFERENCES wellbeing_postulations(id)
);

CREATE TABLE IF NOT EXISTS wellbeing_family_incomes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  postulation_id BIGINT NOT NULL,
  family_member_id BIGINT NULL,
  contract_type_id INT NULL,
  amount DECIMAL(14,2) NOT NULL DEFAULT 0,
  created_at DATETIME NULL,
  updated_at DATETIME NULL,
  deleted_at DATETIME NULL,
  CONSTRAINT fk_wb_inc_post FOREIGN KEY (postulation_id) REFERENCES wellbeing_postulations(id),
  CONSTRAINT fk_wb_inc_family FOREIGN KEY (family_member_id) REFERENCES wellbeing_family_members(id),
  CONSTRAINT fk_wb_inc_type FOREIGN KEY (contract_type_id) REFERENCES contracts_types(id),
  INDEX idx_wb_income_postulation (postulation_id)
);

CREATE TABLE IF NOT EXISTS wellbeing_family_expenses (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  postulation_id BIGINT NOT NULL,
  category VARCHAR(30) NOT NULL,
  code VARCHAR(80) NULL,
  name VARCHAR(180) NULL,
  description VARCHAR(255) NULL,
  amount DECIMAL(14,2) NOT NULL DEFAULT 0,
  created_at DATETIME NULL,
  updated_at DATETIME NULL,
  deleted_at DATETIME NULL,
  CONSTRAINT fk_wb_exp_post FOREIGN KEY (postulation_id) REFERENCES wellbeing_postulations(id),
  INDEX idx_wb_exp_postulation (postulation_id)
);

CREATE TABLE IF NOT EXISTS wellbeing_health_records (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  postulation_id BIGINT NOT NULL,
  family_member_id BIGINT NULL,
  person_name VARCHAR(160) NULL,
  pathology VARCHAR(180) NULL,
  monthly_expense DECIMAL(14,2) NOT NULL DEFAULT 0,
  created_at DATETIME NULL,
  updated_at DATETIME NULL,
  deleted_at DATETIME NULL,
  CONSTRAINT fk_wb_health_post FOREIGN KEY (postulation_id) REFERENCES wellbeing_postulations(id),
  CONSTRAINT fk_wb_health_family FOREIGN KEY (family_member_id) REFERENCES wellbeing_family_members(id),
  INDEX idx_wb_health_postulation (postulation_id)
);

CREATE TABLE IF NOT EXISTS wellbeing_housings (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  postulation_id BIGINT NOT NULL UNIQUE,
  type_housing_id INT NULL,
  type_property_id INT NULL,
  housing_background TEXT NULL,
  other_background TEXT NULL,
  created_at DATETIME NULL,
  updated_at DATETIME NULL,
  deleted_at DATETIME NULL,
  CONSTRAINT fk_wb_housing_post FOREIGN KEY (postulation_id) REFERENCES wellbeing_postulations(id),
  CONSTRAINT fk_wb_housing_type FOREIGN KEY (type_housing_id) REFERENCES types_housings(id),
  CONSTRAINT fk_wb_housing_property FOREIGN KEY (type_property_id) REFERENCES types_properties(id)
);

CREATE TABLE IF NOT EXISTS wellbeing_document_types (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(80) NOT NULL,
  name VARCHAR(180) NOT NULL,
  document_group VARCHAR(30) NOT NULL,
  required TINYINT(1) NOT NULL DEFAULT 0,
  help_text VARCHAR(255) NULL,
  allowed_extensions VARCHAR(120) NULL,
  max_size_mb INT NULL,
  active TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME NULL,
  updated_at DATETIME NULL,
  deleted_at DATETIME NULL,
  CONSTRAINT uk_wb_document_type_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS wellbeing_postulation_documents (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  postulation_id BIGINT NOT NULL,
  document_type_id BIGINT NOT NULL,
  original_filename VARCHAR(220) NULL,
  storage_path VARCHAR(500) NULL,
  content_type VARCHAR(120) NULL,
  size_bytes BIGINT NULL,
  checksum VARCHAR(80) NULL,
  uploaded_by_user_id INT NULL,
  uploaded_at DATETIME NULL,
  created_at DATETIME NULL,
  updated_at DATETIME NULL,
  deleted_at DATETIME NULL,
  CONSTRAINT fk_wb_doc_post FOREIGN KEY (postulation_id) REFERENCES wellbeing_postulations(id),
  CONSTRAINT fk_wb_doc_type FOREIGN KEY (document_type_id) REFERENCES wellbeing_document_types(id),
  CONSTRAINT fk_wb_doc_user FOREIGN KEY (uploaded_by_user_id) REFERENCES users(id),
  INDEX idx_wb_doc_postulation (postulation_id)
);

CREATE TABLE IF NOT EXISTS wellbeing_status_histories (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  postulation_id BIGINT NOT NULL,
  old_status VARCHAR(30) NULL,
  new_status VARCHAR(30) NOT NULL,
  observation TEXT NULL,
  changed_by_user_id INT NULL,
  created_at DATETIME NULL,
  CONSTRAINT fk_wb_status_post FOREIGN KEY (postulation_id) REFERENCES wellbeing_postulations(id),
  CONSTRAINT fk_wb_status_user FOREIGN KEY (changed_by_user_id) REFERENCES users(id),
  INDEX idx_wb_status_postulation (postulation_id)
);
