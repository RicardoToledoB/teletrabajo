# Guía QA rápida — expediente R01 (sin ejecución automática)

La base URL es `/api/v1/acreditacion`; siempre se exige `Authorization: Bearer <JWT>`. Todas las pruebas deben usar una convocatoria QA publicada, dentro del plazo, de un usuario de pruebas.

1. `POST /convocatorias/{id}/postulaciones`: crear borrador con profesión y modalidad de la convocatoria.
2. `PUT /postulaciones/{id}/ficha`: enviar JSON con nombres, datos de contacto, establecimiento y profesión; `rutNormalized` y `userId` son controlados por backend. `GET /postulaciones/{id}/ficha` verifica la lectura.
3. `POST /postulaciones/{id}/cargos`: JSON `{ "positionNumber": 1, "establishment": "Establecimiento QA", "hours": 22 }`. Máximo dos cargos.
4. `POST /postulaciones/{id}/documentos`: Body `form-data`, clave `file` (PDF), `requirementId` opcional. `GET /postulaciones/{id}/documentos` muestra SHA-256 e ID; `GET .../{documentId}/pdf` descarga el PDF.
5. `PUT /postulaciones/{id}/declaracion`: `{"declarationText":"Texto autorizado para QA","accepted":true,"signatureMethod":"DOCUMENTO_ADJUNTO","documentId":<ID_PDF>}`. No sustituye certificación de validez de firma.
6. Añadir antecedentes con POST de la sección pertinente y validar GET, PUT, DELETE durante borrador.
7. `POST /postulaciones/{id}/presentar`: valida mínimos, congela ficha y antecedentes y conserva folio. Repetir el POST debe devolver el mismo folio y comprobante sin registrar otra presentación (idempotencia). Probar cambios a ficha, cargos y documentos después: 409.
8. Consultar `GET /postulaciones/{id}/trayectoria`. Token de otro usuario debe fallar al acceder al expediente.

Los endpoints de convocatoria y el GET/POST de firmantes pertenecen a módulos diferentes. `POST /convocatorias/{id}/documentos` sube **bases**; `POST /postulaciones/{id}/documentos` sube **respaldo del postulante**. Son colecciones y permisos diferentes.
