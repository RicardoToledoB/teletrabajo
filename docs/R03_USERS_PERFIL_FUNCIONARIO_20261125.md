# R03 – ampliación de users para mantenedor e importación de funcionarios

Integrado sobre el mismo proyecto R03 parcial (derivado de R02). No es un servicio independiente.

## Alcance
- Se agregaron 14 propiedades opcionales de perfil laboral y personal en `UserEntity`, `UserDTO` y mapeo de `UserServiceImpl`.
- Se mantienen los endpoints preexistentes `GET /api/v1/users`, `GET /api/v1/users/all`, `GET /api/v1/users/{id}`, `GET /api/v1/users/search`, `POST /api/v1/users`, `PUT /api/v1/users/{id}` y variantes paginadas.
- JSON utiliza camelCase (`establishmentId`, `institutionalEntryDate`, etc.); como entrada también acepta nombres de columna snake_case mediante `@JsonAlias`. Swagger publica camelCase.
- Se conserva el ID `Integer` propio de users. Los cinco identificadores de catálogos son `Long` (BIGINT). No se ha añadido ninguna relación que obligue a cambiar IDs anteriores.
- Los campos nuevos aceptan NULL en SQL. Al editar mediante PUT, el valor NULL o ausente NO sobrescribe datos nuevos ya guardados, para compatibilidad con formularios anteriores. Por lo tanto, el PUT actual no permite borrar deliberadamente uno de esos nuevos valores; requerirá un contrato PATCH futuro.
- La validación de referencias para valores no nulos reutiliza el catálogo R03 `acr_support_catalog_records` y exige catálogos vigentes y tipo correcto.
- Las operaciones de usuarios conservan sus anotaciones originales de autorización por rol; el acceso transversal de Acreditación no concede permiso de administración de usuarios.
- La importación masiva actual de funcionarios no forma parte del código del módulo `users` revisado: el contrato de incorporación individual está preparado, pero un importador externo deberá enviar estas propiedades; no afirmar que la importación masiva está probada.

## Instalación en laboratorio, NO en producción todavía
1. Respaldar la base de pruebas.
2. Aplicar primero `docs/sql/2026_11_25_r03_catalogos_apoyo_mysql8.sql` y luego `docs/sql/2026_11_25_r03_users_perfil_funcionario_mysql8.sql` (migración idempotente).
3. Levantar contra el esquema de prueba con `JPA_DDL_AUTO=validate`.
4. Compilar (`./mvnw clean test package`) y probar creación, lectura, búsqueda, actualización, 400 por referencia inválida, y preservación de atributos omitidos.
5. Confirmar que el front y el importador aceptan la semántica de edición sin borrado de campos nuevos.

## Riesgos y pendientes
- Verificar si `establishment_id` y `unit_id` deben usar tablas institucionales preexistentes en vez de `acr_support_catalog_records` antes de declarar una fuente maestra definitiva.
- No aplicar esta versión directamente a producción sin verificar referencias, migración y pruebas automatizadas.
- Esta entrega no implementa un importador masivo ni las cinco operaciones PDF aún pendientes de R03.
- Sin compilación Java verificada en este entorno.
