# Configuración de perfiles de base de datos

El backend quedó preparado para trabajar con tres perfiles:

- `h2`: desarrollo rápido/local en memoria.
- `mysql`: MySQL 8.x para pruebas reales, preproducción o producción.
- `mariadb`: alternativa si el motor utilizado es MariaDB.

## Perfil por defecto

Por defecto el proyecto levanta con H2:

```properties
spring.profiles.active=${SPRING_PROFILES_ACTIVE:h2}
```

## Levantar con H2

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

H2 Console:

```text
http://localhost:8080/h2-console
```

Credenciales H2:

```text
JDBC URL: jdbc:h2:mem:teletrabajo
User: sa
Password: password
```

## Levantar con MySQL

Crear base de datos:

```sql
CREATE DATABASE teletrabajo_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

Crear usuario:

```sql
CREATE USER 'teletrabajo_user'@'localhost' IDENTIFIED BY 'ClaveSeguraAqui';
GRANT ALL PRIVILEGES ON teletrabajo_db.* TO 'teletrabajo_user'@'localhost';
FLUSH PRIVILEGES;
```

Levantar:

```bash
SPRING_PROFILES_ACTIVE=mysql \
DB_HOST=localhost \
DB_PORT=3306 \
DB_NAME=teletrabajo_db \
DB_USERNAME=teletrabajo_user \
DB_PASSWORD=ClaveSeguraAqui \
./mvnw spring-boot:run
```

También puede usarse:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

si las variables de entorno ya están configuradas.

## Variables disponibles para MySQL

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `DB_HOST` | `localhost` | Host MySQL |
| `DB_PORT` | `3306` | Puerto MySQL |
| `DB_NAME` | `teletrabajo_db` | Base de datos |
| `DB_USERNAME` | `teletrabajo_user` | Usuario BD |
| `DB_PASSWORD` | vacío | Password BD |
| `JPA_DDL_AUTO` | `update` | Estrategia Hibernate |
| `SQL_INIT_MODE` | `never` | Evita ejecutar `data.sql` en MySQL |

## Migración SQL

Se incluye el script:

```text
docs/sql/2026_05_16_mysql_migracion_teletrabajo.sql
```

Este script asegura las columnas nuevas:

- `users.birth_date`
- `users.contract_date`
- `users.contract_type`
- `wellbeing_postulations.current_step`

## Endpoints relevantes

```http
POST /api/v1/wellbeing/postulations/start
GET  /api/v1/wellbeing/postulations/my-drafts
GET  /api/v1/wellbeing/postulations/my-active
GET  /api/v1/wellbeing/postulations/my/{id}
PATCH /api/v1/wellbeing/postulations/my/{id}/step
GET  /api/v1/registers_modifieds/search?administratorId=3&userId=15&registerDatetime=2026-05&page=0&size=10&sort=id,desc
```

## Nota de seguridad aplicada

Se ajustaron mapeos internos de usuarios para no devolver `password`/hash de contraseña en DTOs asociados a otros módulos.
