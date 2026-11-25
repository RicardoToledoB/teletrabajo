-- R03: esquema aislado, no modifica tablas de Bienestar/RDA/Teletrabajo.
-- Ejecutar tras respaldo y revisión, UNA VEZ, antes de desplegar R03.
CREATE TABLE IF NOT EXISTS acr_support_catalog_records (
  id BIGINT NOT NULL AUTO_INCREMENT,
  catalog VARCHAR(80) NOT NULL,
  code VARCHAR(100) NULL,
  name VARCHAR(300) NOT NULL,
  parent_id BIGINT NULL,
  country_id BIGINT NULL,
  commune_id BIGINT NULL,
  city VARCHAR(200) NULL,
  active TINYINT(1) NOT NULL DEFAULT 1,
  review_status VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
  source VARCHAR(300) NULL,
  created_by INT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_acr_catalog_code (catalog,code),
  INDEX ix_acr_catalog_active_name (catalog,active,name),
  INDEX ix_acr_catalog_parent (catalog,parent_id),
  INDEX ix_acr_catalog_country (catalog,country_id),
  CONSTRAINT fk_acr_catalog_parent FOREIGN KEY (parent_id) REFERENCES acr_support_catalog_records(id),
  CONSTRAINT fk_acr_catalog_country FOREIGN KEY (country_id) REFERENCES acr_support_catalog_records(id),
  CONSTRAINT fk_acr_catalog_commune FOREIGN KEY (commune_id) REFERENCES acr_support_catalog_records(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
-- SIN cargar catálogos oficiales ni asignar códigos provisionales.
