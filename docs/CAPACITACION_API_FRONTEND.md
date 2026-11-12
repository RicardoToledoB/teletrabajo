# API Certificados de Capacitación — versión de integración

Base: `/api/v1/capacitacion`. Requiere JWT excepto consulta pública de verificación. Lecturas y escrituras admiten cualquier usuario autenticado, sin exigir rol específico.

## Endpoints

- `GET /actividades`, `POST /actividades`
- `GET /participantes`, `POST /participantes`
- `GET /firmantes`, `POST /firmantes`
- `GET /certificados`, `GET /certificados/{id}`
- `POST /certificados/emitir/{participantId}`
- `POST /certificados/{id}/revocar`
- `GET /certificados/{id}/pdf` → `application/pdf`, descarga
- `GET /certificados/{id}/qr` → `image/png`
- `POST /actividades/{activityId}/importar-participantes` → `multipart/form-data` campo `file` (CSV UTF-8)
- Público: `GET /api/v1/public/capacitacion/verificar/{code}`

## CSV

Cabecera exacta `rut,nombre,email,resultado`. Ejemplo:

```csv
rut,nombre,email,resultado
12345678-9,Persona Ejemplo,ejemplo@redsalud.gob.cl,APROBADO
```

El CSV inicial no soporta comas entrecomilladas, columnas adicionales ni deduplicación. Límite 5 MB. Respuesta: `batchId`, `totalRows`, `successfulRows`, `failedRows`, `errors`.

## PDF y QR

PDF básico sin firmas: NO es un certificado oficialmente firmado. QR representa URL de verificación; configurar `capacitacion.verification-base-url` con la URL pública real del backend. No exponer documentos ni datos sensibles sin autorización y revisión de roles.

## Limitaciones y validación pendiente

Pendiente: DTO de todas las respuestas CRUD, paginación, validación RUT, idempotencia de emisión, firmas autorizadas, auditoría y pruebas integrales. No desplegar directamente a producción. Compilar con `./mvnw clean package` y probar en QA antes de entregar como API estable.
