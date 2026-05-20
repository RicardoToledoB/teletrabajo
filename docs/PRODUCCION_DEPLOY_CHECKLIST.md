# Checklist despliegue producción - Teletrabajo Backend

## 1. Variables de entorno

Crear `.env` desde `.env.example`:

```bash
cp .env.example .env
nano .env
chmod 600 .env
```

Valores mínimos:

```bash
SPRING_PROFILES_ACTIVE=mysql
SERVER_PORT=8080
DB_HOST=localhost
DB_PORT=3306
DB_NAME=teletrabajo_db
DB_USERNAME=teletrabajo_user
DB_PASSWORD='CLAVE_REAL'
JWT_SECRET='SECRETO_LARGO_SEGURO'
JPA_DDL_AUTO=update
SQL_INIT_MODE=never
DATA_SEEDER_ENABLED=false
```

## 2. Compilar

```bash
./mvnw clean package -DskipTests
```

Debe terminar con `BUILD SUCCESS`.

## 3. Reiniciar

```bash
./restart.sh
```

Validar log:

```bash
tail -n 120 logs/teletrabajo-back.log
```

Debe aparecer `Started TeletrabajoApplication`.

## 4. Base de datos limpia opcional

Solo si se va a recrear desde cero:

```sql
DROP DATABASE teletrabajo_db;
CREATE DATABASE teletrabajo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON teletrabajo_db.* TO 'teletrabajo_user'@'localhost';
FLUSH PRIVILEGES;
```

Luego levantar backend una vez para crear tablas y ejecutar:

```bash
sudo mysql -u root -p teletrabajo_db < src/main/resources/data.sql
sudo mysql -u root -p teletrabajo_db < docs/sql/03_validaciones_post_carga.sql
```

## 5. Validación de passwords

```sql
SELECT id, email, username, password
FROM users
WHERE password IS NOT NULL
  AND password NOT LIKE '$2a$%'
  AND password NOT LIKE '$2b$%'
  AND password NOT LIKE '$2y$%';
```

El resultado esperado es `Empty set`.

## 6. Endpoints /my agregados

- `PUT /api/v1/wellbeing/postulations/my/{id}/affiliate`
- `POST /api/v1/wellbeing/postulations/my/{id}/family-members`
- `PUT /api/v1/wellbeing/postulations/my/family-members/{familyMemberId}`
- `DELETE /api/v1/wellbeing/postulations/my/family-members/{familyMemberId}`
- `PUT /api/v1/wellbeing/postulations/my/{id}/beneficiary`
- `PUT /api/v1/wellbeing/postulations/my/{id}/academic-info`
- `PUT /api/v1/wellbeing/postulations/my/{id}/academic-verification`
- `POST /api/v1/wellbeing/postulations/my/{id}/incomes`
- `DELETE /api/v1/wellbeing/postulations/my/incomes/{incomeId}`
- `PUT /api/v1/wellbeing/postulations/my/{id}/fixed-expenses`
- `POST /api/v1/wellbeing/postulations/my/{id}/other-expenses`
- `DELETE /api/v1/wellbeing/postulations/my/expenses/{expenseId}`
- `POST /api/v1/wellbeing/postulations/my/{id}/health-records`
- `DELETE /api/v1/wellbeing/postulations/my/health-records/{recordId}`
- `PUT /api/v1/wellbeing/postulations/my/{id}/housing`
- `POST /api/v1/wellbeing/postulations/my/{id}/documents`
- `DELETE /api/v1/wellbeing/postulations/my/documents/{documentId}`
- `GET /api/v1/wellbeing/postulations/my/{id}/summary`
- `POST /api/v1/wellbeing/postulations/my/{id}/submit`
- `POST /api/v1/wellbeing/postulations/my/{id}/restore`
