# R02 — Acceso transversal temporal para integración

La versión conserva JWT obligatorio para `/api/v1/acreditacion/**`, `/api/v1/capacitacion/**` y `/api/v1/calificaciones/**`; no usa `permitAll()` anónimo. Calificaciones y las operaciones comunes ya aceptaban cualquier perfil autenticado. Los bloqueos adicionales de operadores se controlan como sigue:

- `ACREDITACION_INTEGRATION_OPEN_ACCESS_ENABLED=true`: permite que cualquier **usuario autenticado e identificado en la base** supere la verificación interna de operador en los procedimientos administrativos de Acreditación. No altera `official(auth)` para lectura de expedientes y borradores ajenos ni las reglas de titularidad. Los límites por estado, plazo, asignación de comisión y validación de negocio continúan. `acreditacion.official-decisions-enabled=false` **sigue impidiendo** resoluciones, publicación de resultados y actos oficiales cuando no se han autorizado sus reglas.
- `CAPACITACION_INTEGRATION_OPEN_ACCESS_ENABLED=true`: permite a cualquier usuario autenticado gestionar y descargar imágenes de firmantes, sin aparecer en la lista `capacitacion.official-operators`. **No habilita emisión/revocación de certificados**; estas siguen exigiendo tanto `capacitacion.official-issuance-enabled=true` como pertenencia a la lista de operadores autorizados.
- Los flags nuevos vienen `false` por defecto. Su uso en producción solo corresponde a un período explícito de QA supervisada; implica que usuarios ordinarios podrán hacer cambios administrativos reales sobre convocatorias, criterios, firmantes y otras entidades. Lo aconsejable es activarlos exclusivamente en una instancia de prueba, con datos ficticios.

## Variables de entorno (en `.env` existente, NO incluido en este ZIP)

```dotenv
ACREDITACION_INTEGRATION_OPEN_ACCESS_ENABLED=true
CAPACITACION_INTEGRATION_OPEN_ACCESS_ENABLED=true
```

Después de desplegar un JAR probado, reiniciar la instancia de **pruebas** para aplicar el cambio; no basta con añadir las variables al archivo. Para cerrar la ventana de QA, usar `false` y reiniciar. No activar `ACREDITACION_OFFICIAL_DECISIONS_ENABLED` ni `CAPACITACION_OFFICIAL_ISSUANCE_ENABLED` durante integración visual.

## Alcance y respuestas

- Sin JWT: 401 en rutas protegidas.
- Con JWT válido, usuario reconocido: acceso sin comprobación de rol a la administración que usan los indicadores de integración.
- Expediente ajeno, borrador ajeno o apelación que solo puede presentar su titular: restricciones preservadas (403/404).
- Operación oficial sin reglas aprobadas: 409, aunque esté activo el acceso transversal.
- Validación incorrecta, plazo vencido, expediente no presentado o evaluador no asignado: restricciones funcionales preservadas.
- Este cambio no añade migraciones de base de datos y no altera los módulos previos de Bienestar y Teletrabajo.

## Verificación mínima

1. `./mvnw clean package`, confirmar todas las pruebas y cobertura del contrato R01.
2. Iniciar en `teletrabajo_r01_test` con `JPA_DDL_AUTO=validate`, cuenta MySQL propia y puerto alternativo.
3. Comprobar dos JWT de perfiles distintos para administración normal; uno sin token (401); acceso ajeno denegado; emisión oficial bloqueada.
4. Verificar `GET /api/v1/capacitacion/firmantes/{id}/firma` y un CRUD de convocatoria no oficial.
5. Apagar los flags y comprobar la restitución de los controles de operador antes de producción definitiva.
