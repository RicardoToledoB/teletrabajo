# Correcciones de compatibilidad MySQL 8 - Backend Teletrabajo

## 1. Problema detectado

En MySQL 8 el nombre de tabla `groups` genera conflicto al crear claves foráneas. El backend fallaba al iniciar con un error similar a:

```text
SQLSyntaxErrorException: You have an error in your SQL syntax ... near 'groups (id)'
HikariPool-1 - Shutdown initiated
```

Esto ocurría porque H2 permite nombres que MySQL trata como reservados o problemáticos.

## 2. Corrección aplicada

Se cambió la tabla asociada a `GroupEntity`:

```java
@Table(name="work_groups")
@SQLDelete(sql = "UPDATE work_groups SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
```

También se actualizaron las consultas nativas de `GroupRepository` para usar `work_groups`.

El endpoint REST se mantiene igual:

```http
/api/v1/groups
```

Por lo tanto, el frontend no necesita cambiar rutas.

## 3. Corrección adicional

Se corrigió `UserRoleEntity`, que tenía el `@SQLDelete` apuntando erróneamente a `users_groups` en vez de `users_roles`.

## 4. Si la base está recién creada

No ejecutar nada manual. Hibernate creará `work_groups` automáticamente.

## 5. Si ya existe la tabla antigua `groups`

Ejecutar:

```sql
SET FOREIGN_KEY_CHECKS = 0;
RENAME TABLE `groups` TO work_groups;
SET FOREIGN_KEY_CHECKS = 1;
```

Script incluido:

```text
docs/sql/2026_05_16_mysql_fix_reserved_groups.sql
```

## 6. Seeder de datos demo

El `DataSeeder` ahora es condicional. En MySQL/producción queda desactivado por defecto para no crear usuarios demo automáticamente.

Para activarlo solo en una carga inicial controlada:

```bash
export DATA_SEEDER_ENABLED=true
./restart.sh
```

Luego volver a dejarlo desactivado:

```bash
export DATA_SEEDER_ENABLED=false
./restart.sh
```

## 7. Archivo `.env`

`start.sh` ahora carga automáticamente variables desde `.env` si el archivo existe en la raíz del proyecto. Este archivo queda ignorado por Git.

Ejemplo:

```bash
SPRING_PROFILES_ACTIVE=mysql
DB_HOST=localhost
DB_PORT=3306
DB_NAME=teletrabajo_db
DB_USERNAME=teletrabajo_user
DB_PASSWORD=CAMBIAR_CLAVE
JWT_SECRET=CAMBIAR_SECRETO_LARGO
```
