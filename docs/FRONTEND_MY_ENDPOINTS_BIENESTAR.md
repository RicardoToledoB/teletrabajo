# Módulo Bienestar - Endpoints /my para Frontend

## Reglas generales

Todas las peticiones deben enviar JWT:

```http
Authorization: Bearer TOKEN
Content-Type: application/json
```

El frontend debe usar preferentemente endpoints `/my`, porque el backend valida que la postulación pertenezca al usuario autenticado.

Base URL:

```http
/api/v1/wellbeing/postulations
```

## Flujo recomendado

1. Al entrar al módulo, consultar borradores:

```http
GET /api/v1/wellbeing/postulations/my-drafts
```

2. Si existe borrador, usar el `id` retornado.
3. Si no existe borrador, crear una postulación:

```http
POST /api/v1/wellbeing/postulations/start
```

Body actual:

```json
{
  "userId": 228,
  "periodYear": 2026
}
```

4. Guardar el `postulationId` activo.
5. Usar siempre ese mismo ID en los pasos siguientes.
6. Si el usuario elimina/descarta un borrador, limpiar el ID guardado en memoria/localStorage.

## Endpoints principales

### Guardar paso actual

```http
PATCH /api/v1/wellbeing/postulations/my/{id}/step
```

```json
{
  "currentStep": 4
}
```

### Afiliado

```http
PUT /api/v1/wellbeing/postulations/my/{id}/affiliate
```

```json
{
  "rut": "11799136-9",
  "names": "PATRICIO IVAN",
  "lastNames": "JARA GARCES",
  "phone": "991981830",
  "email": "patricio.jara@redsalud.gob.cl",
  "address": "TU DIRECCION",
  "birthDate": "1993-10-11",
  "sex": "M",
  "affiliateType": "ACTIVO",
  "stablishmentId": 1,
  "affiliateDate": "2022-01-03"
}
```

### Familiares

Crear:

```http
POST /api/v1/wellbeing/postulations/my/{id}/family-members
```

Actualizar:

```http
PUT /api/v1/wellbeing/postulations/my/family-members/{familyMemberId}
```

Eliminar:

```http
DELETE /api/v1/wellbeing/postulations/my/family-members/{familyMemberId}
```

### Beneficiario

```http
PUT /api/v1/wellbeing/postulations/my/{id}/beneficiary
```

```json
{
  "beneficiaryType": "AFFILIATE",
  "familyMemberId": null
}
```

### Información académica

```http
PUT /api/v1/wellbeing/postulations/my/{id}/academic-info
```

### Verificación académica

```http
PUT /api/v1/wellbeing/postulations/my/{id}/academic-verification
```

### Ingresos

```http
POST /api/v1/wellbeing/postulations/my/{id}/incomes
DELETE /api/v1/wellbeing/postulations/my/incomes/{incomeId}
```

### Gastos

```http
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

### Resumen

```http
GET /api/v1/wellbeing/postulations/my/{id}/summary
```

### Enviar postulación

```http
POST /api/v1/wellbeing/postulations/my/{id}/submit
```

### Restaurar postulación eliminada

```http
POST /api/v1/wellbeing/postulations/my/{id}/restore
```

## Regla crítica sobre deleted_at

Si una postulación tiene `deleted_at` con fecha, el backend la considera eliminada lógicamente y no permitirá modificarla. El frontend no debe seguir editando IDs eliminados. Debe limpiar `postulationId` y volver a consultar `/my-drafts`.


## Step 2 - Hogar monoparental

Campo agregado a la postulación: `isSingleParentHome`.

Endpoint:

```http
PATCH /api/v1/wellbeing/postulations/my/{id}/family-group
```

Body:

```json
{
  "isSingleParentHome": true
}
```

Este dato indica si el grupo familiar corresponde a un hogar monoparental y se usará posteriormente para evaluación social/puntaje.
