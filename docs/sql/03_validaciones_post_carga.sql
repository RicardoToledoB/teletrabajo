USE teletrabajo_db;

SELECT 'users' AS tabla, COUNT(*) AS total FROM users
UNION ALL SELECT 'roles', COUNT(*) FROM roles
UNION ALL SELECT 'users_roles', COUNT(*) FROM users_roles
UNION ALL SELECT 'stablishments', COUNT(*) FROM stablishments
UNION ALL SELECT 'wellbeing_document_types', COUNT(*) FROM wellbeing_document_types
UNION ALL SELECT 'wellbeing_postulations', COUNT(*) FROM wellbeing_postulations;

-- Debe devolver 0 filas.
SELECT id, email, username, password
FROM users
WHERE password IS NOT NULL
  AND password NOT LIKE '$2a$%'
  AND password NOT LIKE '$2b$%'
  AND password NOT LIKE '$2y$%';

-- Validar usuario administrador de Ricardo.
SELECT u.id, u.email, u.username, r.name AS role_name
FROM users u
JOIN users_roles ur ON ur.user_id = u.id AND ur.deleted_at IS NULL
JOIN roles r ON r.id = ur.role_id AND r.deleted_at IS NULL
WHERE LOWER(u.email) = LOWER('ricardo.toledo.b@redsalud.gov.cl')
   OR LOWER(u.email) = LOWER('ricardo.toledob@gmail.com')
   OR u.username = '15582517'
ORDER BY r.name;

-- Detectar borradores eliminados lógicamente.
SELECT id, code, status, user_id, deleted_at, created_at, updated_at
FROM wellbeing_postulations
WHERE deleted_at IS NOT NULL
ORDER BY deleted_at DESC;
