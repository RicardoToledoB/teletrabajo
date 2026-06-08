# Entrega final backend Teletrabajo - MySQL 8

Esta versión queda preparada para operar en servidor con MySQL 8.x y mantener H2 para desarrollo rápido.

## 1. Perfiles disponibles

- `h2`: desarrollo local rápido, base en memoria.
- `mysql`: ambiente real recomendado para servidor MySQL 8.x.
- `mariadb`: disponible si se requiere compatibilidad con MariaDB.

Arranque recomendado en servidor:

```bash
cp .env.example .env
nano .env
./mvnw clean package -DskipTests
./restart.sh
```

## 2. Variables principales en `.env`

```bash
SPRING_PROFILES_ACTIVE=mysql
SERVER_PORT=8080
DB_HOST=localhost
DB_PORT=3306
DB_NAME=teletrabajo_db
DB_USERNAME=teletrabajo_user
DB_PASSWORD=CAMBIAR_CLAVE_SEGURA_SERVIDOR
JWT_SECRET=CAMBIAR_POR_SECRETO_LARGO_DE_256_BITS_O_MAS
JPA_DDL_AUTO=update
SQL_INIT_MODE=never
DATA_SEEDER_ENABLED=false
```

## 3. Correcciones aplicadas

### Compatibilidad MySQL 8

Se renombró la tabla lógica `groups` a `work_groups`, debido a que `groups` genera conflicto en MySQL 8 al crear llaves foráneas.

El endpoint REST no cambia:

```http
/api/v1/groups
```

### Corrección `users_roles`

Se corrigió el `@SQLDelete` de `UserRoleEntity`, que antes apuntaba incorrectamente a `users_groups`.

### Contraseñas BCrypt

La creación normal de usuarios, recuperación de contraseña, cambio de contraseña y usuarios demo del `DataSeeder` usan `PasswordEncoder`/BCrypt.

Además, las respuestas DTO no deben exponer `password`; se deja en `null`.

### Seeder controlado

El `DataSeeder` queda apagado por defecto en MySQL:

```properties
app.data-seeder.enabled=false
```

Solo se activa manualmente si se define:

```bash
DATA_SEEDER_ENABLED=true
```

## 4. Scripts disponibles

```bash
./start.sh
./stop.sh
./restart.sh
./deploy.sh
```

Flujo normal:

```bash
git pull
./mvnw clean package -DskipTests
./restart.sh
```

O directamente:

```bash
./deploy.sh
```

## 5. Verificación posterior al despliegue

```bash
tail -n 150 logs/teletrabajo-back.log
```

Debe aparecer una línea similar a:

```text
Started TeletrabajoApplication
```

Validar en MySQL:

```sql
SHOW TABLES;
DESCRIBE users;
DESCRIBE work_groups;
DESCRIBE users_groups;
DESCRIBE users_roles;
DESCRIBE wellbeing_postulations;
```

Validar contraseñas no encriptadas:

```sql
SELECT id, email, password
FROM users
WHERE password IS NOT NULL
  AND password NOT LIKE '$2a$%'
  AND password NOT LIKE '$2b$%'
  AND password NOT LIKE '$2y$%';
```

Si la consulta no devuelve filas, las contraseñas están correctamente almacenadas como BCrypt.
