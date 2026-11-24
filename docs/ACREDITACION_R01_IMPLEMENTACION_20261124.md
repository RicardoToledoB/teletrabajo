# Gestión de las Personas DSSM — Acreditación Ley Médica R01

**Entrega de código en revisión. NO desplegar directamente en producción sin compilar, realizar QA y validar el SQL MySQL 8 en un clon de la BD.**

## Fuentes y delimitación

Base: `AG-SIRUS-ACR-ARQUITECTURA-R01.zip`, conservado bajo `docs/referencia-acreditacion-r01/` para trazabilidad. Documento original: 28 tablas/316 campos, 191 controles, F01–F22 y 67 operaciones REST propuestas. **El OpenAPI del documento es una propuesta, no un contrato validado por la DSSM**. Su `ACR-SCHEMA.sql` es PostgreSQL y termina en ROLLBACK; **NO ejecutarlo en MySQL**. El archivo `docs/sql/2026_11_24_acreditacion_r01_mysql8.sql` es una migración aditiva preparada para la arquitectura existente MySQL 8, pero requiere inspección y ensayo con el esquema real previo a producción.

## Qué existe en código de esta entrega

Se conservó el proyecto completo del backend `teletrabajo` (Gestión de las Personas), incluidos Bienestar, Teletrabajo, Calificaciones, Capacitación, descarga de imágenes de firmas, JWT y Acreditación F01.

Se incorporó `AcreditacionExpedienteR01Controller` para el expediente del titular autenticado:

- `GET/PUT /api/v1/acreditacion/postulaciones/{id}/ficha`: ficha del solicitante (RUT y usuario obtenidos del sistema, sin confiar en IDs del JSON). La ficha se guarda como snapshot JSON, que se congela al presentar.
- `GET/PUT /api/v1/acreditacion/postulaciones/{id}/declaracion`: texto y declaración; se exige documento firmado adjunto para el envío, hasta que Jurídica confirme mecanismo de firma simple en plataforma.
- `GET/POST /api/v1/acreditacion/postulaciones/{id}/{seccion}` y `PUT/DELETE /api/v1/acreditacion/postulaciones/{id}/{seccion}/{itemId}` para secciones: `cargos`, `capacitaciones`, `antecedentes-academicos`, `antecedentes-clinicos`, `situaciones-criticas`, `antecedentes-organizacionales`, `reconocimientos`.
- `GET/POST /api/v1/acreditacion/postulaciones/{id}/documentos`: metadatos y carga multipart PDF, `file` obligatorio; opciones `requirementId` y `criterionId`, comprobando pertenencia al proceso; límite 10.000.000 bytes, lectura PDF, SHA-256, BLOB en BD, sin rutas externas.
- `GET /api/v1/acreditacion/postulaciones/{id}/documentos/{documentId}/pdf` descarga autenticada, con comprobación SHA-256. `POST .../retirar` marca documento inactivo solo durante borrador (no borra bytes).
- `POST /api/v1/acreditacion/postulaciones/{id}/presentar` bloquea fila de postulación y valida periodo, ficha, 1–2 cargos, declaración firmada y respaldos de requisitos obligatorios aplicables. Registra estado `PRESENTADA`, fecha y auditoría. Reintentos sobre un expediente ya `PRESENTADA` devuelven el mismo folio y la fecha original, sin volver a registrar la presentación. El comprobante **no significa aprobación**.
- `GET /api/v1/acreditacion/postulaciones/{id}/trayectoria` devuelve acciones acotadas, sin `metadata_json` ni información interna.
- `PUT /api/v1/acreditacion/postulaciones/{id}` actualiza profesión/modalidad de borrador solo cuando son opciones habilitadas en F01.
- `GET /api/v1/acreditacion/convocatorias/{id}/postulaciones` requiere operador oficial configurado. La lectura de borradores de convocatoria y requisitos fue acotada a creador/operador; convocatorias publicadas continúan consultables con JWT.

Todas las operaciones de expediente usan JWT, validan pertenencia por `applicant_user_id` obtenido del principal, y prohíben cambios tras presentar. Los nombres de tabla y campo de la API de anexos se seleccionan de una lista fija, no directamente desde entradas libres; las consultas usan parámetros de JDBC. El control de visibilidad no equivale a una política completa de perfiles de comisiones o director: esa matriz sigue pendiente.

## Esquema

La migración aditiva `docs/sql/2026_11_24_acreditacion_r01_mysql8.sql` añade campos faltantes a las tablas existentes según R01, sin cambiar los tipos de columnas ya existentes, y crea las trece tablas adicionales del modelo propuesto, más `acr_document_files` para los PDF de postulaciones. Conserva F01 `acr_process_professions` y `acr_process_modalities` como representación normalizada de las listas del modelo R01. **No reemplaza el esquema productivo completo, ni aporta todas las claves foráneas compuestas del informe R01.** Se necesita una auditoría relacional antes de certificar conformidad.

## No operativo en esta entrega / NO declarar terminado

**F14–F22: admisibilidad, comisión, ponderaciones/puntajes, publicación de resultados, apelaciones, resolución del Director, emisión/revocación de certificados, notificaciones y reportes oficiales.** Se agregan sus tablas y columnas modelo, pero **no se habilitan endpoints transaccionales oficiales que pudieran aprobar, calificar, firmar o certificar con reglas no aprobadas**. Pendiente de autorización de perfiles, bases, rúbricas, legalidad de firma, evaluación de criterios y revisión de privacidad. El `ACR-OPENAPI.json` propuesto **no se publica automáticamente** como si fuera Swagger real.

F02–F13: hay una primera implementación operativa en código sujeta a pruebas, no cobertura completa de sus 191 controles y todas las validaciones dependientes de bases. El snapshot de datos `captured_at` congela la ficha pero todavía falta versionar cambios sobre antecedentes con procedimiento formal de subsanación. La eliminación física de filas de anexos en estado borrador conserva una acción de auditoría, pero no un histórico completo del contenido eliminado.

## Verificación obligatoria antes de liberar

1. Respaldar el repositorio y base de datos existente. Conservar la carpeta `.git` y la configuración `.env`; **no subir secretos a Git**.
2. Revisar y ejecutar el SQL en una copia restaurada de `teletrabajo_db`. No ejecutar el SQL PostgreSQL del ZIP original. Verificar estructura, índices y relaciones contra `information_schema`; chequear específicamente `acr_document_files` y permisos del usuario MySQL de la aplicación.
3. En entorno QA, `./mvnw clean package` y pruebas con JWT de dos usuarios distintos; comprobar 403/404 frente a expedientes de terceros, 409 después de presentar, rechazo de PDF inválido, archivos mayores al límite, fechas fuera de plazo y reintentos concurrentes de presentación. Confirmar funcionalidades anteriores de Capacitación, Calificaciones, Bienestar y Teletrabajo.
4. Validar con Angular los JSON por formulario y la persistencia tras F5; probar `multipart/form-data` sin fijar `Content-Type` manualmente.
5. Configurar operadores oficiales con `ACREDITACION_OFFICIAL_OPERATORS` **solo tras aprobar autorizaciones**. El backend anterior exigía `acreditacion.official-operators` para publicar/abrir/cerrar, y esta entrega lo conserva.
6. Solo tras pruebas y conformidad institucional planificar un despliegue, con rollback del JAR **y** restauración de BD desde respaldo si hiciera falta. No revertir tablas a ciegas.

## Observaciones técnicas pendientes

- Implementar trazabilidad completa de ediciones y versiones de documentos/cargos, y control optimista de concurrencia del borrador. No crear FEA/visados o resoluciones de manera ficticia.
- Configurar registro estructurado de errores HTTP (sin tokens/datos sensibles). Conciliar restricciones y tipos con el esquema físico de `teletrabajo_db` antes de ejecutar migraciones.
- El nombre de la aplicación Java permanece `teletrabajo` para no romper systemd, dominio, JAR ni despliegue existente. Funcionalmente pertenece a **Gestión de las Personas / Recursos Humanos**.
