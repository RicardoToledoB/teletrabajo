# Despliegue producción - Teletrabajo backend

## Variables recomendadas

Crear `.env` desde `.env.example` y mantenerlo fuera de Git.

Valores críticos:

```bash
SPRING_PROFILES_ACTIVE=mysql
SQL_INIT_MODE=never
DATA_SEEDER_ENABLED=false
JPA_DDL_AUTO=update
DB_HOST=localhost
DB_NAME=teletrabajo_db
DB_USERNAME=teletrabajo_user
DB_PASSWORD='CLAVE_REAL'
JWT_SECRET='SECRETO_LARGO_REAL'
```

## Flujo normal de despliegue

```bash
cd /var/www/html/teletrabajo
git pull
./mvnw clean package -DskipTests
./restart.sh
tail -n 120 logs/teletrabajo-back.log
```

Debe aparecer:

```text
Started TeletrabajoApplication
The following 1 profile is active: "mysql"
jdbc:mysql://localhost:3306/teletrabajo_db
```

## Carga inicial limpia

Solo si se recrea la base:

```bash
sudo mysql -u root -p
```

```sql
DROP DATABASE IF EXISTS teletrabajo_db;
CREATE DATABASE teletrabajo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON teletrabajo_db.* TO 'teletrabajo_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

Luego:

```bash
./restart.sh
sudo mysql -u root -p teletrabajo_db < src/main/resources/data.sql
sudo mysql -u root -p teletrabajo_db < docs/sql/03_validaciones_post_carga.sql
```

El `data.sql` de esta versión ya trae passwords BCrypt para la clave temporal `123456` y carga roles/asignaciones mínimas.

## Corrección soft delete bienestar

Si una postulación tiene `deleted_at` informado, Hibernate la oculta por `@SQLRestriction("deleted_at IS NULL")`.

Esta versión agrega:

```http
POST /api/v1/wellbeing/postulations/{id}/restore
POST /api/v1/wellbeing/postulations/my/{id}/restore
PUT  /api/v1/wellbeing/postulations/my/{id}/affiliate
```

Además, las operaciones de edición entregan un error claro cuando el registro está eliminado lógicamente.
