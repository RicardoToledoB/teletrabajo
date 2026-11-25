# Acreditación Ley Médica — Integración R02 / 25-11-2026

## Alcance del ZIP

Este ZIP contiene el **backend completo** de Gestión de las Personas (Java 17, Spring Boot 4.0.3). Se preservan los módulos existentes y se añade `AcreditacionActuacionesR02Controller` para las operaciones ausentes del contrato de referencia `ACR-OPENAPI-OBJETIVO-R01.json`.

La versión R01 instalada previamente creó 31 tablas `acr_%` en MySQL 8. R02 usa esas tablas existentes: **no trae ni exige ejecutar una segunda migración SQL**.

### Nuevas rutas

- `GET/POST /api/v1/acreditacion/convocatorias/{id}/criterios`
- `GET/POST /api/v1/acreditacion/convocatorias/{id}/comision`
- `GET/POST /api/v1/acreditacion/convocatorias/{id}/notificaciones`
- `GET/POST /api/v1/acreditacion/postulaciones/{id}/admisibilidad`
- `GET/POST /api/v1/acreditacion/postulaciones/{id}/asignaciones`
- `GET/POST /api/v1/acreditacion/postulaciones/{id}/evaluaciones`
- `GET/POST /api/v1/acreditacion/postulaciones/{id}/apelaciones`
- `GET/POST /api/v1/acreditacion/postulaciones/{id}/resoluciones`
- `GET/POST /api/v1/acreditacion/postulaciones/{id}/certificados`
- `POST /api/v1/acreditacion/postulaciones/{id}/admisibilidad/{reviewId}/items`
- `POST /api/v1/acreditacion/postulaciones/{id}/evaluaciones/{evaluationId}/puntajes`
- `POST /api/v1/acreditacion/postulaciones/{id}/evaluaciones/{evaluationId}/cerrar`
- `POST /api/v1/acreditacion/postulaciones/{id}/resultados/publicar`
- `POST /api/v1/acreditacion/postulaciones/{id}/apelaciones/{appealId}/presentar`
- `POST /api/v1/acreditacion/postulaciones/{id}/apelaciones/{appealId}/resolver`
- `POST /api/v1/acreditacion/postulaciones/{id}/certificados/{certificateId}/revocar`
- `GET /api/v1/acreditacion/verificacion/{token}` (respuesta mínima; pública)
- `GET /api/v1/acreditacion/convocatorias/{id}/reportes` (totales agregados)

La cobertura de rutas debe comprobarse con Swagger real (`/v3/api-docs`), sin confundir presencia de una ruta con flujo de negocio completamente operativo. Los módulos F01-F13 existentes usan rutas parametrizadas y siguen integrados.

## Seguridad

- Operaciones de convocatoria/comisión/evaluación exigen JWT y operador configurado en `acreditacion.official-operators` (identidad exacta de Spring Security).
- Expedientes y documentos son legibles por el titular y operadores institucionales autorizados; **solo el titular** puede modificar su borrador. Decisiones internas requieren operador institucional.
- `GET /verificacion/{token}` publica **solo** número, estado y fechas del certificado vigente o revocado; los borradores no son públicamente verificables.
- `acreditacion.official-decisions-enabled=false` por defecto: **bloquea** admisibilidad decisoria, publicación de resultados, resolución de apelaciones, resoluciones y actos certificados hasta la autorización institucional. Activar solo después de validar normativa, segregación de funciones y documentos firmados.
- Las notificaciones se registran como **PENDIENTE**: no se presentan como correos enviados y no existe despacho automático en este controlador.
- Los registros de certificados que puedan prepararse se almacenan como **PENDIENTE_VALIDACION**: esta API no emite una certificación válida automáticamente. No hay validación criptográfica de firma de resolución ni emisión de PDF en esta entrega.
- El cierre de evaluación suma puntajes; **no inventa una fórmula o umbral de aprobación**. La validez de su publicación requiere reglas aprobadas.

## Validación antes de desplegar

1. Ejecutar `./mvnw clean package` y verificar todas las pruebas, incluida la carga del contexto Spring.
2. Comparar los endpoints en `http://127.0.0.1:8081/v3/api-docs` con `ACR-OPENAPI-OBJETIVO-R01.json`, verificando parámetros y `requestBody`. Algunas operaciones utilizan `Map<String,Object>` para validar campos con lista permitida, por lo que la especificación Swagger autogenerada puede ser menos estricta que el contrato original.
3. Realizar pruebas en MySQL copia de producción (`teletrabajo_r01_test`) con usuario de escritura **dedicado exclusivamente a esa base**, nunca reutilizar el acceso de escritura de producción en pruebas.
4. Probar autorizaciones 401/403, titularidad, estado de convocatoria, datos erróneos, referencias cruzadas, plazos y estados de expediente.
5. Mantener `acreditacion.official-decisions-enabled=false` hasta contar con validación jurídica/funcional.
6. Respaldar la BD, desplegar de manera controlada y confirmar pruebas de humo de los demás módulos.

## Limitaciones pendientes para cierre institucional de F14–F22

La arquitectura R01 provee un modelo de referencia, **no** fórmulas oficiales completas ni facultades verificadas. Requieren validación e implementación posterior: matriz definitiva de puntuación por modalidad y profesión, reglamento de admisibilidad, asignación autorizada de comisiones, firma electrónica de resoluciones, efectos de apelación, generación y firma de PDF de certificados, registro de notificaciones con comprobante de entrega, revocación con acto válido y generación de reportes institucionales definitivos.

No se debe comunicar a frontend que los 84 contratos equivalen a 84 procesos end-to-end aprobados. La entrega suma soporte técnico de rutas, controles y persistencia, pero requiere QA y aprobación para actos oficiales.

## Estado de verificación de este artefacto

En el entorno de preparación se verificó la integridad del ZIP, estructura de clases y mapeo estático de rutas. **No se pudo ejecutar Maven**, porque su wrapper no pudo descargar Apache Maven 3.9.12. La compilación en el servidor de ensayo es obligatoria antes de la publicación.
