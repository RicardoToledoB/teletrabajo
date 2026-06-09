# Campos nuevos en integrantes familiares

Se agregaron dos campos opcionales para texto libre cuando el usuario seleccione una opción tipo "Otro" en Step 2 — Grupo Familiar.

## Campos nuevos

```json
{
  "othersWorkplaces": "Lugar de trabajo no registrado en mantenedor",
  "othersActivities": "Actividad no registrada en mantenedor"
}
```

## Endpoint

Crear integrante familiar:

```http
POST /api/v1/wellbeing/postulations/my/{id}/family-members
```

Actualizar integrante familiar:

```http
PUT /api/v1/wellbeing/postulations/my/family-members/{familyMemberId}
```

## Ejemplo de payload

```json
{
  "rut": "17238732-2",
  "names": "VIVIANA PAOLA",
  "lastNames": "BIANCHI CARCAMO",
  "previtionId": 1,
  "incomeTypeId": 1,
  "parentTypeId": 1,
  "civilStateId": 1,
  "activityId": 99,
  "workPlaceId": 99,
  "studyLevelId": 2,
  "studyPlace": "",
  "othersWorkplaces": "Lugar de trabajo no registrado",
  "othersActivities": "Actividad no clasificada",
  "student": false,
  "monthlyIncome": 0
}
```

## Respuesta / summary

El endpoint `GET /api/v1/wellbeing/postulations/my/{id}/summary` devuelve estos campos dentro de cada `familyMember`:

```json
{
  "othersWorkplaces": "Lugar de trabajo no registrado",
  "othersActivities": "Actividad no clasificada"
}
```

## Base de datos

Campos agregados a `wellbeing_family_members`:

```sql
others_workplaces VARCHAR(255) NULL
others_activities VARCHAR(255) NULL
```
