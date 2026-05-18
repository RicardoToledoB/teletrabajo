# Módulo Bienestar - Endpoints seguros `/my`

Esta versión agrega endpoints seguros para que el frontend use rutas con `/my` y el backend valide que la postulación o recurso pertenezca al usuario autenticado por JWT.

## Endpoints agregados

### Afiliado

```http
PUT /api/v1/wellbeing/postulations/my/{id}/affiliate
```

### Integrantes familiares

```http
POST   /api/v1/wellbeing/postulations/my/{id}/family-members
PUT    /api/v1/wellbeing/postulations/my/family-members/{familyMemberId}
DELETE /api/v1/wellbeing/postulations/my/family-members/{familyMemberId}
```

### Beneficiario

```http
PUT /api/v1/wellbeing/postulations/my/{id}/beneficiary
```

### Información académica

```http
PUT /api/v1/wellbeing/postulations/my/{id}/academic-info
PUT /api/v1/wellbeing/postulations/my/{id}/academic-verification
```

### Ingresos y gastos

```http
POST   /api/v1/wellbeing/postulations/my/{id}/incomes
DELETE /api/v1/wellbeing/postulations/my/incomes/{incomeId}
PUT    /api/v1/wellbeing/postulations/my/{id}/fixed-expenses
POST   /api/v1/wellbeing/postulations/my/{id}/other-expenses
DELETE /api/v1/wellbeing/postulations/my/expenses/{expenseId}
```

### Salud

```http
POST   /api/v1/wellbeing/postulations/my/{id}/health-records
DELETE /api/v1/wellbeing/postulations/my/health-records/{recordId}
```

### Vivienda

```http
PUT /api/v1/wellbeing/postulations/my/{id}/housing
```

### Documentos

```http
POST   /api/v1/wellbeing/postulations/my/{id}/documents
DELETE /api/v1/wellbeing/postulations/my/documents/{documentId}
```

### Envío de postulación

```http
POST /api/v1/wellbeing/postulations/my/{id}/submit
```

## Regla de frontend

Para el flujo de usuario autenticado, preferir siempre `/my`:

```text
GET  /my-drafts
GET  /my/{id}
GET  /my/{id}/summary
PUT  /my/{id}/affiliate
POST /my/{id}/family-members
PATCH /my/{id}/step
```

Si se recibe 401:

1. Verificar que se envíe `Authorization: Bearer TOKEN`.
2. Renovar token con login.
3. Confirmar que el usuario tenga rol permitido.

Si se recibe error de postulación eliminada o no encontrada:

1. Confirmar que `deleted_at IS NULL`.
2. Confirmar que la postulación pertenezca al usuario autenticado.
3. Confirmar que esté en `DRAFT` u `OBSERVED`.
