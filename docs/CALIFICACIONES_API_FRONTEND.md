# Calificaciones del Personal — API inicial para frontend

## Alcance y precauciones
Primera integración técnica en el backend de Gestión de Personas. Conserva los módulos existentes. **No está lista para producción**: falta revisión normativa, roles funcionales, migraciones controladas, seguridad por expediente, auditoría, documentos, validaciones y pruebas de regresión.

### Base
`/api/v1/calificaciones` — requiere JWT y rol `ADMIN` (exclusivamente para pruebas sintéticas autorizadas).

| Método | Ruta | Estado |
|---|---|---|
| GET | `/ciclos` | Implementado; ADMIN |
| GET | `/ciclos/{id}` | Implementado; ADMIN |
| POST | `/ciclos` | Implementado; crea únicamente BORRADOR; ADMIN |

Ejemplo POST `/api/v1/calificaciones/ciclos`:
```json
{"year":2027,"startAt":"2027-01-01T00:00:00","endAt":"2027-12-31T23:59:00","rulesVersion":"PENDIENTE-VALIDACION","legalBasis":"Pendiente de revisión jurídica"}
```
Los campos `id`, `status` y `createdAt` son establecidos por servidor; no se permite activar ni cerrar ciclos mediante esta API.

### Modelo de datos
Se incorporan 21 entidades `Qual*` y repositorios JPA correspondientes a las 21 tablas `qual_*` descritas en la propuesta de 02/11/2026. **Las clases son un modelo inicial, no sustituyen la migración SQL ni validan la estructura de la BD real**. Relaciones de usuario se mantienen como IDs hasta cotejar el esquema institucional.

### Pendiente, NO conectar como API real
Universo, hoja de vida, anotaciones, informes, puntajes, precalificación, retroalimentación, observaciones, juntas, acuerdos, notificaciones, apelaciones, resoluciones, documentos, reportes y auditoría. El frontend puede diseñar sus 18 pantallas, pero debe deshabilitar operaciones sin contrato real y no simular persistencia ni decisiones.

### Despliegue
No reiniciar servicio productivo sin respaldar JAR y MySQL y validar migraciones: el entorno actual usa `JPA_DDL_AUTO=update`, lo que puede alterar el esquema al iniciar.
