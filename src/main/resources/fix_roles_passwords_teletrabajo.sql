-- ============================================================
-- FIX INICIAL POST-CARGA DATA.SQL - SISTEMA TELETRABAJO
-- Motor: MySQL 8.x
-- Objetivo:
--   1) Convertir claves planas '123456' a BCrypt.
--   2) Crear roles mínimos del sistema.
--   3) Asignar ADMINISTRATIVO a todos los usuarios activos.
--   4) Asignar ADMIN al usuario de Ricardo Toledo.
--   5) Entregar consultas de validación.
--
-- IMPORTANTE:
--   - Ejecutar UNA VEZ después de cargar data.sql.
--   - La contraseña funcional seguirá siendo: 123456
--   - Pero quedará almacenada en formato BCrypt.
-- ============================================================

USE teletrabajo_db;

START TRANSACTION;

-- ============================================================
-- 1. Corregir contraseñas planas '123456' a BCrypt
-- Hash BCrypt generado para la contraseña: 123456
-- Prefix $2y$ compatible con BCrypt/Spring Security.
-- ============================================================

UPDATE users
SET password = '$2y$10$htrouSnj416XOthn0lvmheLTr/13ho6euQ.bauA4kPJMElVRXoGau',
    updated_at = CURRENT_TIMESTAMP
WHERE password = '123456';


-- ============================================================
-- 2. Crear roles mínimos si no existen
-- El backend normaliza internamente ROLE_ cuando corresponde,
-- por eso los nombres se guardan como ADMIN, ADMINISTRATIVO, etc.
-- ============================================================

INSERT INTO roles (name, created_at, updated_at, deleted_at)
SELECT 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'ADMIN'
);

INSERT INTO roles (name, created_at, updated_at, deleted_at)
SELECT 'ADMINISTRATIVO', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'ADMINISTRATIVO'
);

INSERT INTO roles (name, created_at, updated_at, deleted_at)
SELECT 'SUPERVISOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'SUPERVISOR'
);

INSERT INTO roles (name, created_at, updated_at, deleted_at)
SELECT 'JEFATURA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'JEFATURA'
);

INSERT INTO roles (name, created_at, updated_at, deleted_at)
SELECT 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'USER'
);


-- ============================================================
-- 3. Asignar rol ADMINISTRATIVO a todos los usuarios activos
-- Evita duplicados usando NOT EXISTS.
-- ============================================================

INSERT INTO users_roles (user_id, role_id, created_at, updated_at, deleted_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL
FROM users u
JOIN roles r ON r.name = 'ADMINISTRATIVO'
WHERE u.deleted_at IS NULL
  AND NOT EXISTS (
      SELECT 1
      FROM users_roles ur
      WHERE ur.user_id = u.id
        AND ur.role_id = r.id
        AND ur.deleted_at IS NULL
  );


-- ============================================================
-- 4. Asignar rol ADMIN a Ricardo Toledo
-- Se usa email institucional y username/RUT sin DV como respaldo.
-- Ajusta o agrega correos si necesitas otros administradores.
-- ============================================================

INSERT INTO users_roles (user_id, role_id, created_at, updated_at, deleted_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.deleted_at IS NULL
  AND (
      LOWER(u.email) = LOWER('ricardo.toledo.b@redsalud.gov.cl')
      OR LOWER(u.email) = LOWER('ricardo.toledob@gmail.com')
      OR u.username = '15582517'
  )
  AND NOT EXISTS (
      SELECT 1
      FROM users_roles ur
      WHERE ur.user_id = u.id
        AND ur.role_id = r.id
        AND ur.deleted_at IS NULL
  );


-- ============================================================
-- 5. Opcional: asignar ADMIN a usuarios demo si existen
-- No afecta si no existen.
-- ============================================================

INSERT INTO users_roles (user_id, role_id, created_at, updated_at, deleted_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.deleted_at IS NULL
  AND (
      LOWER(u.email) = LOWER('admin@demo.com')
      OR LOWER(u.username) = LOWER('admin')
  )
  AND NOT EXISTS (
      SELECT 1
      FROM users_roles ur
      WHERE ur.user_id = u.id
        AND ur.role_id = r.id
        AND ur.deleted_at IS NULL
  );

COMMIT;


-- ============================================================
-- VALIDACIONES
-- ============================================================

-- Totales principales
SELECT 'users' AS tabla, COUNT(*) AS total FROM users
UNION ALL
SELECT 'roles', COUNT(*) FROM roles
UNION ALL
SELECT 'users_roles', COUNT(*) FROM users_roles
UNION ALL
SELECT 'stablishments', COUNT(*) FROM stablishments
UNION ALL
SELECT 'wellbeing_document_types', COUNT(*) FROM wellbeing_document_types;

-- Debe devolver 0 filas. Si devuelve filas, todavía hay claves sin BCrypt.
SELECT id, email, username, password
FROM users
WHERE password IS NOT NULL
  AND password NOT LIKE '$2a$%'
  AND password NOT LIKE '$2b$%'
  AND password NOT LIKE '$2y$%';

-- Validar roles de Ricardo
SELECT u.id, u.email, u.username, r.name AS role_name
FROM users u
JOIN users_roles ur ON ur.user_id = u.id AND ur.deleted_at IS NULL
JOIN roles r ON r.id = ur.role_id AND r.deleted_at IS NULL
WHERE LOWER(u.email) = LOWER('ricardo.toledo.b@redsalud.gov.cl')
   OR LOWER(u.email) = LOWER('ricardo.toledob@gmail.com')
   OR u.username = '15582517'
ORDER BY r.name;
