-- 06_add_birth_date_family_members.sql
-- Agrega fecha de nacimiento al grupo familiar sin afectar datos existentes.
-- Es seguro para producción: la columna es NULL y no modifica registros existentes.

SET @db_name := DATABASE();

SET @exists_birth_date := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = @db_name
    AND TABLE_NAME = 'wellbeing_family_members'
    AND COLUMN_NAME = 'birth_date'
);

SET @sql_birth_date := IF(
  @exists_birth_date = 0,
  'ALTER TABLE wellbeing_family_members ADD COLUMN birth_date DATE NULL AFTER last_names',
  'SELECT "La columna birth_date ya existe en wellbeing_family_members" AS message'
);

PREPARE stmt_birth_date FROM @sql_birth_date;
EXECUTE stmt_birth_date;
DEALLOCATE PREPARE stmt_birth_date;
