-- ============================================================
-- FIX POST-CARGA DATA.SQL - SISTEMA TELETRABAJO
-- Motor: MySQL 8.x
-- Ejecutar una vez si se cargó un data.sql antiguo con claves planas.
-- La contraseña funcional seguirá siendo: 123456
-- ============================================================

USE teletrabajo_db;

START TRANSACTION;

-- BCrypt válido para contraseña temporal: 123456
UPDATE users
SET password = '$2y$10$htrouSnj416XOthn0lvmheLTr/13ho6euQ.bauA4kPJMElVRXoGau',
    updated_at = CURRENT_TIMESTAMP
WHERE password = '123456';

INSERT INTO roles (name, created_at, updated_at, deleted_at)
SELECT 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

INSERT INTO roles (name, created_at, updated_at, deleted_at)
SELECT 'ADMINISTRATIVO', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMINISTRATIVO');

INSERT INTO roles (name, created_at, updated_at, deleted_at)
SELECT 'SUPERVISOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'SUPERVISOR');

INSERT INTO roles (name, created_at, updated_at, deleted_at)
SELECT 'JEFATURA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'JEFATURA');

INSERT INTO roles (name, created_at, updated_at, deleted_at)
SELECT 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'USER');

-- Rol base para usuarios activos
INSERT INTO users_roles (user_id, role_id, created_at, updated_at, deleted_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL
FROM users u
JOIN roles r ON r.name = 'ADMINISTRATIVO'
WHERE u.deleted_at IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM users_roles ur
      WHERE ur.user_id = u.id
        AND ur.role_id = r.id
        AND ur.deleted_at IS NULL
  );

-- Ricardo/admin inicial
INSERT INTO users_roles (user_id, role_id, created_at, updated_at, deleted_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.deleted_at IS NULL
  AND (
      LOWER(u.email) = LOWER('ricardo.toledo.b@redsalud.gov.cl')
      OR LOWER(u.email) = LOWER('ricardo.toledob@gmail.com')
      OR u.username = '15582517'
      OR LOWER(u.email) = LOWER('admin@demo.com')
      OR LOWER(u.username) = LOWER('admin')
  )
  AND NOT EXISTS (
      SELECT 1 FROM users_roles ur
      WHERE ur.user_id = u.id
        AND ur.role_id = r.id
        AND ur.deleted_at IS NULL
  );

COMMIT;
