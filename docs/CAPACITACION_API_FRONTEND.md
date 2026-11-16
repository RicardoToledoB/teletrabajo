# Certificados de Capacitación — ampliación 12/11/2026

Base `/api/v1/capacitacion`, JWT obligatorio. Se conservan rutas y nombres previos.
**Cambio de seguridad deliberado:** actividades, participantes, lecturas e importaciones admiten todos los roles autenticados. Alta/edición de firmantes y emisión/revocación requieren habilitación explícita y una lista de cuentas institucionales autorizadas (independiente del rol). No habilitar con cuentas generales. No afecta Acreditación, Calificaciones ni otros módulos.

## Actividades
GET/POST `/actividades`; PUT `/actividades/{id}` (reemplazo completo; enviar todos los campos).
POST ejemplo:
```json
{"title":"Curso QA","code":"CURSO-QA","provider":"DSSM","startDate":"2026-11-12","endDate":"2026-11-13","hours":8,"active":true,"modality":"VIRTUAL","executionNumber":1}
```
Modalidades PRESENCIAL, VIRTUAL, MIXTA, A DISTANCIA (o null). `code` + `executionNumber` único cuando ambos existen. Los registros anteriores mantienen nulos los campos nuevos.

## Participantes
GET/POST `/participantes`; PUT `/participantes/{id}` (reemplazo completo).
```json
{"rut":"RUT_DE_PRUEBA","fullName":"Persona QA","email":"qa@example.test","result":"APROBADO","activity":{"id":1},"userId":null,"attendancePercent":95.0,"grade":6.5,"status":"COMPLETADO"}
```
`activityId` también aceptado, debe coincidir si se envía `activity.id`; usar una actividad existente. `userId` opcional, ID de users; no se serializa el objeto usuario. `attendancePercent` 0..100, dos decimales. `grade` numérico no negativo, hasta dos decimales (la escala académica se debe definir institucionalmente). `status`: INSCRITO, EN_CURSO, COMPLETADO, RETIRADO; `result` conserva el resultado académico, independiente del estado. La importación CSV anterior sigue disponible con cuatro columnas; campos nuevos nulos. No se inventan datos históricos.

## Firmantes
GET/POST `/firmantes`; PUT `/firmantes/{id}` (reemplazo completo).
```json
{"fullName":"Firmante QA","positionTitle":"Cargo QA","signatureImagePath":"izquierda.png","signaturePosition":"IZQUIERDA","validFrom":"2026-11-01","validTo":null,"active":true}
```
`signatureAsset` es alias de `signatureImagePath` y se admite también en el request. No se aceptan URLs, rutas absolutas, ni traversal. PNG existentes suministrados por operador de servidor o cargados por API en directorio privado; máximo 2 MB y 2000x2000. Si ambos campos de referencia son nulos/omitidos en PUT, se conserva la firma existente. La fecha inicial y el cargo son obligatorios para la selección al emitir; fecha final nula = vigencia indefinida. Exactamente un firmante vigente por IZQUIERDA y DERECHA. Las imágenes de firma **no son FEA**.

## Carga y reemplazo de firma (16/11/2026)
`POST /firmantes/{id}/firma`, JWT, multipart/form-data, parte `file` (PNG, JPG/JPEG). El operador debe estar en `capacitacion.official-operators` y `capacitacion.official-issuance-enabled=true`. El archivo se valida por contenido, se decodifica y se convierte a PNG sin metadatos originales, con máximo 2.000.000 bytes de entrada y salida y dimensiones hasta 2000×2000. Se genera un nombre único `firma-<uuid>.png` y se escribe en `capacitacion.signature-directory` (privado, fuera del repositorio). Se actualiza `signatureImagePath` del mismo ID; `signatureAsset` es su alias. HTTP 200 devuelve el firmante completo, con las dos referencias iguales. GET `/firmantes` devuelve la referencia actual, no una URL pública ni los bytes. No se expone una descarga de la imagen por API. El reemplazo conserva los archivos anteriores y no modifica PDF históricos. Si falla la transacción DB después de escribir el archivo, puede quedar un archivo huérfano; no se borra automáticamente para evitar afectar referencias históricas.

Errores: 400 imagen inválida, tamaño/dimensiones o falta de `file`; 401 JWT inválido/ausente; 403 operador no autorizado; 404 firmante inexistente; 409 emisión oficial no habilitada; 500 error de almacenamiento. El límite multipart global puede producir un error antes del controlador si se excede. No enviar una ruta local del navegador como referencia de firma. Para un nuevo firmante, POST JSON sin imagen, luego POST multipart con su ID. Para editar datos sin cambiar la firma, omitir ambas referencias o enviar la referencia vigente.

Esta actualización no requiere ALTER TABLE. Respaldar también el directorio de firmas junto con MySQL. Confirmar permisos de lectura/escritura de la cuenta real del servicio antes del despliegue. Pruebas nuevas: modalidad POST/PUT, contrato multipart/GET, persistencia H2, reemplazo JPG/PNG, formatos/tamaño/dimensiones, autorización y firmante inexistente. Ejecutar `./mvnw clean package` en el servidor. Pruebas agregadas aún no ejecutadas al momento de empaquetar (Maven no disponible en entorno de preparación).

## Certificados
- GET `/certificados`, GET `/certificados/{id}`.
- POST `/certificados/emitir/{participantId}`: requiere participante aprobado, actividad asociada, estado COMPLETADO o nulo para registros anteriores; dos firmantes vigentes y sus PNG. Si ya existe un certificado no revocado, devuelve el mismo (bloqueo transaccional del participante). Si fue revocado, permite nueva emisión con nuevo número.
- POST `/certificados/{id}/revocar`: registra operador y fecha, conserva PDF original.
- GET `/certificados/{id}/pdf`: devuelve bytes originales, comprueba SHA-256; conserva la descarga autenticada aunque revocado. No genera PDF retroactivo para un certificado sin documento almacenado.
- GET `/certificados/{id}/qr`: QR PNG hacia verificación pública.
- GET `/api/v1/public/capacitacion/verificar/{code}`: devuelve valid, code, issuedAt, status, certificateNumber, pdfSha256 (sin RUT ni nombres). `valid` requiere no revocado y documento registrado; para comprobar bytes comparar SHA-256 del PDF.

Respuesta de certificado conserva `participant`, `firstSignatory`, `secondSignatory`, `issuedAt`, `revokedAt`, `verificationCode`. Añade `participationId` (alias de participant.id), `certificateNumber` (CAP-año-ID), `status`, `documentReference`, `pdfSha256`, `issuedBy`, `revokedBy` y snapshots `firstSignerName/Title/Position/Asset/Sha256`, `secondSignerName/Title/Position/Asset/Sha256`. El PDF congelado contiene imágenes y texto de ambos firmantes; el hash de cada imagen se conserva. Las relaciones de participante y firmantes siguen siendo referencias vivas, no usar esas referencias para representar el contenido histórico; usar snapshots y PDF. El documento se guarda en cap_certificate_documents, no se expone el BLOB en JSON. QR incluido en PDF. No es firma electrónica avanzada ni sello de tiempo externo.

## Despliegue y verificación
1. Respaldar `teletrabajo_db` (incluidos certificados y BLOB) y configuración actual. Revisar que sea la estructura reportada con 5 actividades, 5 participantes y 0 certificados/firmantes. Ejecutar `docs/sql/2026_11_12_capacitacion_ampliacion_mysql.sql` una sola vez antes del arranque nuevo. DDL hace commit implícito; si falla, revisar qué ALTER ya se aplicó y continuar solo los pendientes. No ejecutar el script completo por segunda vez ni eliminar tablas. Conservar `spring.jpa.hibernate.ddl-auto=update` y perfil mysql actuales; jamás `create-drop` en producción.
2. Compilar `./mvnw -Dtest=ModuleAuthorizationTest,CapacitacionEmissionTest test`, después `./mvnw clean package`. Verificar Swagger `/v3/api-docs` y arranque. La prueba de emisión usa PNG ficticios y repositorios simulados; no sustituye prueba MySQL real.
3. Reiniciar servicio y verificar logs y los conteos. Las operaciones nuevas de documentos oficiales están deshabilitadas de forma predeterminada. Solo habilitar tras aprobación institucional, en el properties de producción efectivamente cargado (o configuración externa correspondiente):
```properties
capacitacion.official-issuance-enabled=true
capacitacion.official-operators=cuenta.institucional.autorizada@example.test
capacitacion.signature-directory=/var/lib/teletrabajo/capacitacion/firmas
capacitacion.verification-base-url=https://teletrabajo.dssm.cl/api/v1/public/capacitacion/verificar
```
Reemplazar la cuenta ficticia con las cuentas autorizadas reales (separadas por coma; mismo nombre de autenticación JWT). Directorio no público, lectura y escritura para cuenta del servicio; administración reservada a operadores autorizados. No incluir imágenes reales en el ZIP ni en logs. Usar QA con imágenes ficticias y operadores de prueba antes de habilitar producción. Para emitir oficialmente aprobar el formato, uso de imágenes, permisos y requisitos de aprobación/asistencia/nota; estos últimos no tienen umbral institucional definido y no se imponen valores arbitrarios.
4. Validar que un usuario autenticado pueda leer/crear actividades y participantes, que operadores no autorizados no emitan/revoquen, que se emita con dos firmantes y se pueda verificar QR, PDF y SHA-256, que cambios de firmantes no cambien el PDF y que revocación cambie la verificación. Probar error 404 con actividad inexistente y 400 con asistencia fuera de rango.

## Limitaciones
No se incorpora FEA, diseño de plantilla aprobado, auditoría integral de altas/ediciones ni auditoría integral de carga de imágenes; la carga exige operador autorizado. CSV no tiene soporte de comillas/columnas nuevas. Los campos nuevos son opcionales para registros previos. Antes de producción revisar permisos de lecturas y edición de información personal, actualmente abiertos a todos los usuarios autenticados por requerimiento previo. La autenticación JWT y el diagnóstico 401 se conservan.
