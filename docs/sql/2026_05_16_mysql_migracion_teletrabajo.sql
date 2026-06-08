-- ============================================================
-- Migracion MySQL 8.x - Sistema Teletrabajo
-- Usuarios / Bienestar / Registros Modificados
-- ============================================================
-- Recomendacion:
-- 1) Ejecutar primero en ambiente de pruebas.
-- 2) Respaldar la base de datos antes de producción.
-- 3) Script preparado para evitar error si columnas/indices ya existen.
-- ============================================================

CREATE DATABASE IF NOT EXISTS teletrabajo_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE teletrabajo_db;

-- ============================================================
-- Columnas nuevas en users
-- ============================================================

SET @sql := (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE users ADD COLUMN birth_date VARCHAR(20) NULL',
    'SELECT ''users.birth_date ya existe'''
  )
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'users'
    AND column_name = 'birth_date'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE users ADD COLUMN contract_date VARCHAR(20) NULL',
    'SELECT ''users.contract_date ya existe'''
  )
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'users'
    AND column_name = 'contract_date'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE users ADD COLUMN contract_type VARCHAR(50) NULL',
    'SELECT ''users.contract_type ya existe'''
  )
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'users'
    AND column_name = 'contract_type'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- Columna current_step en wellbeing_postulations
-- ============================================================

SET @sql := (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE wellbeing_postulations ADD COLUMN current_step INT NULL DEFAULT 1',
    'SELECT ''wellbeing_postulations.current_step ya existe'''
  )
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'wellbeing_postulations'
    AND column_name = 'current_step'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

UPDATE wellbeing_postulations
SET current_step = 1
WHERE current_step IS NULL;

-- ============================================================
-- Indices recomendados para las consultas nuevas
-- ============================================================

SET @sql := (
  SELECT IF(
    COUNT(*) = 0,
    'CREATE INDEX idx_registers_modifieds_admin_user_datetime ON registers_modifieds (administrator_id, user_id, register_datetime)',
    'SELECT ''idx_registers_modifieds_admin_user_datetime ya existe'''
  )
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'registers_modifieds'
    AND index_name = 'idx_registers_modifieds_admin_user_datetime'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    COUNT(*) = 0,
    'CREATE INDEX idx_wb_post_user_status_deleted ON wellbeing_postulations (user_id, status, deleted_at)',
    'SELECT ''idx_wb_post_user_status_deleted ya existe'''
  )
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'wellbeing_postulations'
    AND index_name = 'idx_wb_post_user_status_deleted'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
