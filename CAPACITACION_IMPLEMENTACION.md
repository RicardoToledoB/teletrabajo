# Módulo Capacitación - primera integración backend

Se incorporan entidades JPA, repositorios y endpoints REST al proyecto original, conservando Teletrabajo y Bienestar.

## Endpoints
GET/POST /api/v1/capacitacion/actividades
GET/POST /api/v1/capacitacion/participantes
GET/POST /api/v1/capacitacion/firmantes
GET /api/v1/capacitacion/certificados
POST /api/v1/capacitacion/certificados/emitir/{participantId}
POST /api/v1/capacitacion/certificados/{id}/revocar
GET /api/v1/public/capacitacion/verificar/{code}

**Alcance:** primera integración estructural. Aún NO implementa generación de PDF, carga CSV, QR gráfico, carga segura de firmas, filtros/paginación ni auditoría completa. NO desplegar a producción sin pruebas de permisos, validaciones y revisión de datos personales. Las tablas se generan según la configuración JPA existente. La emisión actual registra un código, pero NO representa un certificado oficial firmado.
