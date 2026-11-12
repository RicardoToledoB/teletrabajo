# Acreditación Ley Médica — Entrega inicial backend / Angular

## Estado de esta entrega

**Fase A/B parcial, NO PRODUCTIVA.** Se incorporan 14 entidades y repositorios JPA aislados bajo prefijo `acr_`, y un controlador inicial. No se han implementado todavía las 15 pantallas ni el ciclo completo de acreditación. No se ha ejecutado Maven ni probado contra MySQL en este entorno.

**No reemplazar el backend de producción ni reiniciar el servicio con este ZIP** hasta verificar migraciones, respaldos, seguridad y pruebas de regresión.

## Rutas implementadas

Base autenticada: `/api/v1/acreditacion`

| Método | Ruta | Descripción | Seguridad |
|---|---|---|---|
| GET | `/convocatorias` | Lista convocatorias | JWT |
| POST | `/convocatorias` | Crea convocatoria PROGRAMADA | JWT (cualquier rol) |
| GET | `/convocatorias/{id}/requisitos` | Requisitos de una convocatoria | JWT |
| POST | `/convocatorias/{id}/requisitos` | Agrega requisito mientras PROGRAMADA | JWT (cualquier rol) |
| GET | `/mis-postulaciones` | Expedientes del usuario autenticado | JWT |
| POST | `/convocatorias/{id}/postulaciones` | Crea borrador si convocatoria ABIERTA y en plazo | JWT |
| GET | `/postulaciones/{id}` | Consulta solo del titular | JWT + propiedad |

### POST /convocatorias
```json
{"year":2027,"title":"Acreditación Ley Médica 2027","legalBasis":"Bases vigentes","opensAt":"2027-03-01T00:00:00","closesAt":"2027-03-31T23:59:00"}
```
### POST /convocatorias/{id}/postulaciones
```json
{"professionCode":"MEDICO_CIRUJANO","modality":"OBLIGACION"}
```
Modalidades iniciales: `OBLIGACION`, `EXCELENCIA`. Los códigos de profesión y los requisitos deben normalizarse contra catálogos autorizados antes de producción.

## Seguridad y pendientes críticos

- Los endpoints del módulo aceptan cualquier usuario autenticado con JWT, sin exigir `ACR_REFERENTE`. Se conserva la propiedad de las postulaciones y las validaciones de negocio.
- No existen aún endpoints de apertura/cierre de convocatorias; la creación de borradores requiere estado ABIERTA y por tanto queda bloqueada hasta desarrollar la transición autorizada.
- Faltan: datos y snapshot al presentar; anexos PDF y hash; admisibilidad; Comisión, puntajes y acta; apelaciones; decisión del Director; resoluciones; certificados, firma y QR; auditoría de cambios completa; reportes; notificaciones; pruebas automatizadas.
- No hay endpoint público nuevo, ni exposición de datos personales.
- Las 14 entidades son **modelo preliminar**; antes de aplicar en MySQL se deben revisar claves foráneas, índices, unicidad, catálogos, versiones, y migraciones SQL explícitas.
- La configuración existente usa `JPA_DDL_AUTO=update` en producción; NO arrancar el JAR con estas entidades contra producción antes de preparar y revisar el SQL.
- Se mantuvieron los archivos previos de Teletrabajo, Bienestar y Capacitación sin modificación intencional; se agregaron solo archivos en `acreditacion/` y esta documentación.

## Mapeo a propuesta

La propuesta contiene 15 vistas y 14 tablas (`acr_processes`, `acr_applications`, `acr_applicant_snapshots`, `acr_requirements`, `acr_documents`, `acr_evaluation_criteria`, `acr_evaluations`, `acr_evaluation_scores`, `acr_commission_members`, `acr_appeals`, `acr_resolutions`, `acr_certificates`, `acr_notifications`, `acr_audit_events`). Esta entrega define las entidades de las 14 tablas pero implementa solo los 7 endpoints indicados.
