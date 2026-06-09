USE teletrabajo_db;

-- Script idempotente para agregar campos libres en integrantes familiares.
-- No toca usuarios, mantenedores ni postulaciones existentes.

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'wellbeing_family_members'
    AND COLUMN_NAME = 'others_workplaces'
);

SET @ddl := IF(
  @col_exists = 0,
  'ALTER TABLE wellbeing_family_members ADD COLUMN others_workplaces VARCHAR(255) NULL AFTER study_place',
  'SELECT ''La columna others_workplaces ya existe'' AS mensaje'
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'wellbeing_family_members'
    AND COLUMN_NAME = 'others_activities'
);

SET @ddl := IF(
  @col_exists = 0,
  'ALTER TABLE wellbeing_family_members ADD COLUMN others_activities VARCHAR(255) NULL AFTER others_workplaces',
  'SELECT ''La columna others_activities ya existe'' AS mensaje'
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT
  COLUMN_NAME,
  COLUMN_TYPE,
  IS_NULLABLE
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'wellbeing_family_members'
  AND COLUMN_NAME IN ('others_workplaces', 'others_activities')
ORDER BY ORDINAL_POSITION;
