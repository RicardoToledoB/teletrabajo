-- ============================================================
-- Fix MySQL 8 - tabla groups es palabra reservada/problemática
-- Sistema Teletrabajo
-- ============================================================
-- Objetivo:
-- Renombrar la tabla groups a work_groups para evitar error SQL:
-- "near 'groups (id)'" al crear claves foráneas con Hibernate/MySQL.
--
-- Ejecutar solo si la tabla groups fue creada previamente.
-- En una base nueva, Hibernate creará directamente work_groups.
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- Si existe tabla antigua y aún no existe la nueva, renombrar.
RENAME TABLE `groups` TO work_groups;

SET FOREIGN_KEY_CHECKS = 1;
