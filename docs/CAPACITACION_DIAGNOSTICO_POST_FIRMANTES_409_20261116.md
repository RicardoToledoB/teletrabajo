# Incidencia POST /api/v1/capacitacion/firmantes — 409 (16/11/2026)

## Causa en el código anterior

`CapacitacionController.saveSignatory()` llamaba a `CapacitacionService.requireOfficialOperator()` antes de validar el JSON o persistir. La misma validación exigía `capacitacion.official-issuance-enabled=true` y, cuando estaba desactivada (valor predeterminado), lanzaba HTTP 409 `Emision oficial no habilitada`. Por eso todos los cuerpos POST podían fallar con 409 mientras GET `/firmantes` seguía devolviendo `[]` (lee `findAll()`, sin filtros). En el código anterior **no existe** validación de unicidad de nombre ni posición en el POST; la selección de exactamente un firmante vigente por lado se realiza al emitir. Verificar índices manuales adicionales mediante `SHOW INDEX FROM cap_signatories` si existieran en producción.

## Corrección acotada, compatible con Angular

- CRUD de firmantes y subida de imágenes usan `requireSignatoryOperator()`, que comprueba JWT y coincidencia exacta del principal con una cuenta incluida en `capacitacion.official-operators`. Si no está en la lista retorna 403, no 409.
- Emisión y revocación mantienen `requireOfficialOperator()`, que exige, además, `capacitacion.official-issuance-enabled=true` (409 si deshabilitado).
- GET `/firmantes` conserva `findAll()` y expone activos/inactivos/históricos. No hay filtros ocultos.
- `signaturePosition` mantiene el contrato **IZQUIERDA/DERECHA**; las cadenas `"1"` y `"2"` no tienen equivalencia institucional definida, por lo que retornan 400 y se debe ajustar el request en Postman/Angular.
- Alta/edición conserva los datos y PDF históricos; no se introducen índices únicos ni se ejecutan migraciones SQL.

## Configuración y verificación

En el `application-<perfil>.properties` activo (o configuración efectiva equivalente), establecer una cuenta institucional autorizada real para gestionar firmantes:

```properties
capacitacion.official-operators=correo.institucional.autorizado@redsalud.gob.cl
# No se requiere habilitar emisiones para dar de alta firmantes.
capacitacion.official-issuance-enabled=false
```

Si la emisión institucional ya fue aprobada y está habilitada en producción, **conservar el valor vigente** de `capacitacion.official-issuance-enabled`; no cambiarlo por efecto de esta corrección. El correo mostrado es ilustrativo, no una cuenta verificada.

Para verificar registros e índices, sin modificar la BD:

```sql
SELECT COUNT(*) AS firmantes FROM cap_signatories;
SELECT id,full_name,position_title,signature_position,active,valid_from,valid_to FROM cap_signatories ORDER BY id;
SHOW INDEX FROM cap_signatories;
```

Ejemplo del POST (con JWT de un operador configurado):

```json
{"fullName":"Firmante QA","positionTitle":"Cargo QA","signaturePosition":"IZQUIERDA","active":true,"validFrom":"2026-11-01","validTo":null}
```

Pruebas nuevas en `CapSignatoryAdministrationTest`: permite POST/GET/PUT con emisión deshabilitada; conserva firmantes anteriores y sus ID; mantiene 409 solo al emitir, 403 para otra cuenta y 400 para la posición `"1"`. Ejecute `./mvnw clean package` y valide en Postman **antes** de habilitar emisión.

**Nota de alcance:** la causa real del HTTP 409 en producción se confirmará al revisar la propiedad `capacitacion.official-issuance-enabled` efectiva y el log del error. No se ha consultado la base de datos de producción desde este entorno.
