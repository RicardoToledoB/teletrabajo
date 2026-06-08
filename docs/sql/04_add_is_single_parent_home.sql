-- Agrega campo de hogar monoparental a postulaciones de bienestar.
-- Ejecutar una vez en producción si Hibernate no aplica ddl-auto update automáticamente.

ALTER TABLE wellbeing_postulations
  ADD COLUMN is_single_parent_home BOOLEAN NULL DEFAULT FALSE AFTER current_step;

UPDATE wellbeing_postulations
SET is_single_parent_home = FALSE
WHERE is_single_parent_home IS NULL;

SELECT id, is_single_parent_home
FROM wellbeing_postulations
ORDER BY id DESC
LIMIT 10;
