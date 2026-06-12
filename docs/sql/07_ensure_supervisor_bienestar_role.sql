-- Crea el rol SUPERVISOR_BIENESTAR si no existe.
-- No asigna usuarios automáticamente. Asignar manualmente en users_roles si corresponde.
INSERT INTO roles (name, created_at)
SELECT 'SUPERVISOR_BIENESTAR', CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE UPPER(name) = 'SUPERVISOR_BIENESTAR' AND deleted_at IS NULL
);
