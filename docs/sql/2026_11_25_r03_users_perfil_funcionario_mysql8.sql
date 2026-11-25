-- Gestión de las Personas R03: ampliación aditiva y repetible de users
-- Requiere tabla acr_support_catalog_records del módulo R03, aplicada antes de usar las referencias.
-- NO ejecuta UPDATE ni modifica registros previos.
DELIMITER $$
DROP PROCEDURE IF EXISTS r03_add_users_profile_columns $$
CREATE PROCEDURE r03_add_users_profile_columns()
BEGIN
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='establishment_id') THEN
    ALTER TABLE `users` ADD COLUMN `establishment_id` BIGINT NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='unit_id') THEN
    ALTER TABLE `users` ADD COLUMN `unit_id` BIGINT NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='profession_id') THEN
    ALTER TABLE `users` ADD COLUMN `profession_id` BIGINT NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='position') THEN
    ALTER TABLE `users` ADD COLUMN `position` VARCHAR(250) NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='staff_category') THEN
    ALTER TABLE `users` ADD COLUMN `staff_category` VARCHAR(100) NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='grade') THEN
    ALTER TABLE `users` ADD COLUMN `grade` VARCHAR(20) NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='contract_hours') THEN
    ALTER TABLE `users` ADD COLUMN `contract_hours` DECIMAL(5,2) NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='applicable_law') THEN
    ALTER TABLE `users` ADD COLUMN `applicable_law` VARCHAR(50) NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='institutional_entry_date') THEN
    ALTER TABLE `users` ADD COLUMN `institutional_entry_date` DATE NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='employment_status') THEN
    ALTER TABLE `users` ADD COLUMN `employment_status` VARCHAR(50) NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='nationality_id') THEN
    ALTER TABLE `users` ADD COLUMN `nationality_id` BIGINT NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='professional_title_id') THEN
    ALTER TABLE `users` ADD COLUMN `professional_title_id` BIGINT NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='phone') THEN
    ALTER TABLE `users` ADD COLUMN `phone` VARCHAR(30) NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='personal_email') THEN
    ALTER TABLE `users` ADD COLUMN `personal_email` VARCHAR(254) NULL;
  END IF;
END $$
CALL r03_add_users_profile_columns() $$
DROP PROCEDURE r03_add_users_profile_columns $$
DELIMITER ;
-- Las referencias se validan mediante el servicio contra el catálogo y tipo BIGINT.
-- No creamos claves foráneas hasta cerrar la decisión de fuente maestra de establecimientos/unidades.
