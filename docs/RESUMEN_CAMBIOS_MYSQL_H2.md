# Resumen de ajustes realizados - Perfiles H2 / MySQL

## Objetivo

El backend quedó preparado para ejecutarse tanto con H2 como con MySQL 8.x mediante perfiles de Spring Boot.

## Archivos agregados/modificados

### Configuración

- `src/main/resources/application.properties`
- `src/main/resources/application-h2.properties`
- `src/main/resources/application-mysql.properties`
- `src/main/resources/application-mariadb.properties`

### Dependencias

Se agregó el driver oficial de MySQL en `pom.xml`:

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

Se mantiene también el driver MariaDB para compatibilidad alternativa.

### Seeder

Se ajustó `DataSeeder` para que sea idempotente. Esto evita errores por duplicidad de usuarios demo o roles al reiniciar el backend usando una base persistente como MySQL.

### Seguridad DTO

Se reemplazaron mapeos que devolvían `password(entity.getPassword())` por `password(null)` en servicios que exponen datos de usuario indirectamente.

## Perfil H2

Pensado para desarrollo rápido local.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

## Perfil MySQL

Pensado para pruebas reales, preproducción o producción.

```bash
SPRING_PROFILES_ACTIVE=mysql \
DB_HOST=localhost \
DB_PORT=3306 \
DB_NAME=teletrabajo_db \
DB_USERNAME=teletrabajo_user \
DB_PASSWORD=ClaveSeguraAqui \
./mvnw spring-boot:run
```

## Script SQL incluido

```text
docs/sql/2026_05_16_mysql_migracion_teletrabajo.sql
```

Incluye:

- creación opcional de la base `teletrabajo_db`;
- columnas nuevas en `users`;
- columna `current_step` en `wellbeing_postulations`;
- índices recomendados para búsquedas nuevas.

## Recomendación de producción

Para producción formal, evitar dejar credenciales en archivos `.properties`. Usar variables de entorno:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`

## Nota

No se ejecutó compilación Maven en este entorno porque Maven Wrapper requiere descarga desde internet. El `pom.xml` fue validado como XML correcto y los cambios fueron aplicados sobre la estructura existente del proyecto.
