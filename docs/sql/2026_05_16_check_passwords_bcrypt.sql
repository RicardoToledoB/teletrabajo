-- Verificación de usuarios con contraseñas posiblemente no encriptadas en BCrypt.
-- Si esta consulta devuelve filas, esos usuarios deben tener su contraseña restablecida.

SELECT id, email, username, password
FROM users
WHERE password IS NOT NULL
  AND password NOT LIKE '$2a$%'
  AND password NOT LIKE '$2b$%'
  AND password NOT LIKE '$2y$%';
