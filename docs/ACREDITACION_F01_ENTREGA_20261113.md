# Acreditación Ley Médica — F01, entrega incremental 13/11/2026

Base: gestiondepersonas_capacitacion_integral.zip. No se cambia Capacitación, Calificaciones, JWT, usuarios o configuraciones de producción. No se han ejecutado pruebas aquí (Maven no disponible; wrapper no puede descargarlo). No es un módulo F01–F22 completo. Requiere QA antes de despliegue oficial.

## Cambios y compatibilidad
- Las 14 tablas acr_ existentes se mantienen. SQL manual aditivo: tres columnas nuevas en acr_processes y tres tablas auxiliares (profesiones, modalidades y PDF de bases/anexos). No volver a ejecutar la migración de Capacitación. No ejecutar el SQL dos veces; DDL MySQL no es transaccional.
- Los siete endpoints existentes conservan método, ruta y HTTP 200. POST de convocatoria acepta DTO AcrProcessRequest; respuesta conserva los campos existentes y añade professions, modalities, admissibilityDays, admissibilityDayType y notice. Si no se indica status, se conserva PROGRAMADA; si se indica BORRADOR se respeta. Se persisten todas las fechas.
- Fecha/hora local de America/Punta_Arenas, sin offset. Se acepta YYYY-MM-DD: inicio 00:00:00, fin 23:59:59.999999; o ISO local YYYY-MM-DDTHH:mm:ss. No se calcula todavía el calendario de días hábiles ni feriados.
- PUT reemplaza los campos editables completos; enviar todos los valores que se quieran conservar. Identidad creadora y publicación son determinadas por servidor. No se editan convocatorias publicadas ni sus requisitos/bases.
- F01 permite opciones de profesión/modalidad múltiples. La publicación admite las modalidades ya implementadas OBLIGACION y EXCELENCIA (también textos con tildes). No se considera Voluntaria un sinónimo legal sin confirmar. Al crear un expediente, professionCode debe coincidir con un valor habilitado de professions (sin distinción de mayúsculas/tildes). Validar con Patricio el catálogo definitivo antes de publicar; no se comprueba aquí la elegibilidad laboral real del postulante.
- Todas las rutas requieren JWT. Cualquier rol puede crear convocatoria; creador u operador habilitado edita sus borradores y requisitos. Las actuaciones oficiales de publicar/abrir/cerrar exigen cuenta configurada, sin depender del rol: `acreditacion.official-operators=correo.institucional@redsalud.gob.cl` en configuración de producción. Vacío = acciones oficiales bloqueadas (403). No usar cuentas de ejemplo en producción. La propiedad del expediente sigue protegida.
- Publicar exige título, año, normativa, fechas, profesiones, modalidades y plazo/tipo de admisibilidad; no se exige PDF todavía hasta confirmar regla institucional. Si la publicación ocurre antes de la apertura, estado PROGRAMADA y publishedAt informado; si dentro de plazo, ABIERTA. La apertura de una convocatoria programada es una acción manual durante el plazo. Cierre manual después de vencer, también si nunca se abrió. No hay tarea automática, reapertura ni cancelación. Las postulaciones fuera de plazo se bloquean incluso sin cierre manual. Confirmar estas reglas con el referente.
- PDF bases/anexos se sube mediante multipart, máximo 10 MB, PDFBox valida formato no cifrado; se almacena original y SHA-256 en BD. Descarga autenticada; borrador solo creador/operador; publicado cualquier usuario autenticado. Retiro lógico antes de publicar; no se elimina el original. Falta integrar escaneo antivirus institucional y límites operacionales de MySQL (`max_allowed_packet` suficiente) antes de uso real.

## Rutas nuevas (prefijo /api/v1/acreditacion)
| Método | Ruta | Contrato |
|---|---|---|
| GET | /convocatorias/{id} | AcrProcess |
| PUT | /convocatorias/{id} | AcrProcessRequest -> AcrProcess |
| POST | /convocatorias/{id}/publicar | sin body -> AcrProcess |
| POST | /convocatorias/{id}/abrir | sin body -> AcrProcess |
| POST | /convocatorias/{id}/cerrar | sin body -> AcrProcess |
| PUT | /convocatorias/{id}/requisitos/{requirementId} | AcrRequirement -> AcrRequirement |
| GET | /convocatorias/{id}/documentos | AcrProcessDocumentDto[] |
| POST | /convocatorias/{id}/documentos | multipart file -> AcrProcessDocumentDto |
| GET | /convocatorias/{id}/documentos/{documentId}/pdf | application/pdf |
| POST | /convocatorias/{id}/documentos/{documentId}/retirar | sin body -> AcrProcessDocumentDto |

JSON ejemplo POST/PUT:
```json
{"year":2027,"title":"Convocatoria QA","legalBasis":"Bases QA","professions":["MEDICO_CIRUJANO","CIRUJANO_DENTISTA"],"modalities":["OBLIGACION","EXCELENCIA"],"opensAt":"2027-03-01T09:00:00","closesAt":"2027-03-31T17:00:00","admissibilityDays":5,"admissibilityDayType":"habiles","reviewStartsAt":"2027-04-01","reviewDeadlineAt":"2027-04-10","appealOpensAt":"2027-04-11","appealClosesAt":"2027-04-18","notice":"Aviso QA","status":"BORRADOR"}
```

## Pruebas y despliegue
1. Respaldo del proyecto/JAR/configuración y `mysqldump --single-transaction` de teletrabajo_db. Guardar fuera del directorio web con permisos restringidos. Descomprimir código conservando configuración productiva; no copiar secretos al ZIP de distribución.
2. `./mvnw -Dtest=ModuleAuthorizationTest,CapacitacionEmissionTest,AcrF01PersistenceTest test` (H2 y mocks, no MySQL productivo). `./mvnw clean package`.
3. Durante ventana controlada, detener servicio, aplicar `docs/sql/2026_11_13_acreditacion_f01_mysql.sql` UNA vez y verificar columnas/tablas. No aplicar si ya existen las columnas: revisar primero. Mantener perfil mysql, credenciales, JWT, configuración de Capacitación. Autorizar operadores solo cuando se valide institucionalmente.
4. `sudo systemctl start teletrabajo`; comprobar logs de arranque y probar Swagger con token real. Antes de uso oficial, QA en base no productiva: crear F01, recuperar con GET, editar y recuperar tras reinicio, verificar PDF/hash, comprobar 401 sin JWT, 403 para no operador, 409 fuera de estado/plazo, validar transición y postulación. No crear convocatoria oficial en QA.
5. Si falla, volver al JAR/configuración respaldados; la ampliación aditiva puede conservarse. No ejecutar DROP ni restaurar toda la BD sin evaluar cambios realizados desde el respaldo.

## Pendientes del informe AG-GP-ACR-SWAGGER-001
F02–F22: snapshot y expediente completo, anexos del postulante, presentación/subsanación, admisibilidad, comisiones, criterios y reglas de puntuación, actas, resultados, apelaciones, resoluciones, certificados, notificaciones y reportes. Los siete contratos originales no equivalen a ciclo completo. Se necesita propuesta rectora del 30/10/2026 y catálogo/reglas/autoridades aprobadas para completar esas etapas. No se ha inventado normativa ni cálculo de puntajes.
