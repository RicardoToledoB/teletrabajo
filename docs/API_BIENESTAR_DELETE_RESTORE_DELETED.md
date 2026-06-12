# APIs Bienestar - Eliminación lógica, restauración y eliminados

Todas las APIs requieren JWT:

```http
Authorization: Bearer TOKEN
```

Roles habilitados por el controller:

- ADMIN
- ADMINISTRATIVO
- SUPERVISOR
- JEFATURA
- SUPERVISOR_BIENESTAR

## Eliminar lógicamente una postulación administrativa/general

```http
DELETE /api/v1/wellbeing/postulations/{id}
```

Ejemplo:

```http
DELETE https://teletrabajo.dssm.cl/api/v1/wellbeing/postulations/15
```

Respuesta esperada:

```http
204 No Content
```

Efecto: completa `deleted_at` en `wellbeing_postulations`. No borra físicamente el registro.

## Restaurar una postulación eliminada

```http
POST /api/v1/wellbeing/postulations/{id}/restore
```

Ejemplo:

```http
POST https://teletrabajo.dssm.cl/api/v1/wellbeing/postulations/15/restore
```

Respuesta esperada:

```http
204 No Content
```

Efecto: deja `deleted_at = NULL`.

## Ver postulaciones eliminadas

```http
GET /api/v1/wellbeing/postulations/deleted
```

Ejemplo:

```http
GET https://teletrabajo.dssm.cl/api/v1/wellbeing/postulations/deleted
```

Filtros opcionales:

```http
GET /api/v1/wellbeing/postulations/deleted?userId=10&periodYear=2026&status=DRAFT
```

Respuesta esperada:

```json
[
  {
    "id": 15,
    "code": "AES-2026-00015",
    "periodYear": 2026,
    "userId": 10,
    "userRut": "12345678-9",
    "userFullName": "Nombre Apellido",
    "status": "DRAFT",
    "currentStep": 5,
    "createdAt": "2026-06-08T10:00:00",
    "updatedAt": "2026-06-08T11:00:00",
    "deletedAt": "2026-06-08T18:30:00"
  }
]
```

