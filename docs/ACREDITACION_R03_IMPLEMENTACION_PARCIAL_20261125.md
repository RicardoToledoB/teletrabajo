# Gestión de las Personas DSSM — continuación R03 (entrega incremental)

## Antecedentes y alcance

Se parte **del mismo código fuente del backend R02** `gestion_de_personas_backend_R02_acceso_transversal_FIX_TEST_20261125.zip`, sin crear aplicación separada ni alterar las 281 clases Java preexistentes. Se usa el paquete de requerimientos `AG-SIRUS-BACKEND-CONSOLIDADO-R02-20261125-164724.zip`.

**ATENCIÓN: entrega de desarrollo parcial, NO release aprobada para producción.** El entorno de preparación no dispone de Maven ni dependencias descargables, por lo que no fue posible ejecutar `./mvnw test` ni validar con MySQL 8. **No desplegar hasta compilar y probar en un servidor/entorno QA.**

### Matriz de las 100 operaciones

- 84 operaciones: ya publicadas en R02; se preservan sin modificar sus implementaciones. Hay contratos `Map<String,Object>` cuyo OpenAPI sigue siendo genérico; no se certifican aquí.
- 7 operaciones comunes: implementadas como `CatalogosApoyoController`, bajo `/api/v1/catalogos-apoyo`.
- 4 de las 9 candidatas ACR: implementadas como `AcreditacionConsultasR03Controller`: GET admisibilidad-items, GET puntajes, GET/POST respaldos de apelación. El POST recibe `documentId` de un documento ya cargado en la misma postulación: NO ejecuta carga multipart.
- 5 candidatas pendientes: PDFs de comprobante de presentación, comprobante de apelación, certificado, resolución, acta. Requieren contrato de generación/fuente documental **verificable** y autorización de emisión; no se simulan archivos ni se crean documentos oficiales falsos.

El archivo `docs/ACREDITACION_R03_MATRIZ_100.csv` identifica cada fila con el estado técnico y la ausencia de pruebas Postman.

## Catálogos

Doce códigos admitidos: `profesiones`, `nacionalidades`, `paises`, `establecimientos`, `unidades-servicios`, `calidades-juridicas`, `titulos-profesionales`, `especialidades`, `autoridades-certificadoras`, `universidades`, `regiones`, `comunas`.

Se usa **una sola tabla aislada** `acr_support_catalog_records`; no se modifican establecimientos ni otras fuentes de datos existentes. Modelo con `id` servidor, `catalog`, `code`, `name`, `parentId`, `countryId`, `communeId`, `city`, `active`, `reviewStatus`, `source`, marcas de tiempo y creador. Existe validación de nombres y tipos, referencias de padre entre catálogos, unicidad de códigos y desactivación lógica. Se evita borrado físico. La respuesta usa `camelCase` y los elementos DB son independientes de RDA-SM.

**Nota importante sobre la carga inicial:** las consultas GET funcionan, pero los catálogos inicialmente estarán vacíos. No se fabrican regiones, comunas, IDs oficiales ni listas de profesiones. `regiones` y `comunas` se consideran controladas y las escrituras por API están bloqueadas hasta contar con carga oficial auditada. La API de lectura devuelve como máximo 1000 elementos por solicitud; validar paginación y catálogo de comunas con frontend antes de puesta en servicio.

### Permisos

- GET: JWT autenticado (todas las identidades existentes autorizadas a acceder al módulo).
- Escrituras de mantenedores: **solo** emails en `CATALOGOS_APOYO_ADMINISTRATORS`, lista configurable y vacía por defecto. No se amplía automáticamente a todas las cuentas por la opción temporal de Acreditación.
- GET de expedientes: titular u operador oficial, conforme al modelo R02. Las consultas de admisibilidad y puntajes usan el control `requireOfficial()` del backend existente, afectado por el interruptor de acceso transversal de pruebas: **revisar este interruptor antes de exponer datos sensibles en producción**.
- POST de documentos de apelación: sólo el titular, apelación en `BORRADOR`, respaldo activo perteneciente al expediente; crea una vinculación, no un archivo.

## Migración requerida ANTES de desplegar

Script: `docs/sql/2026_11_25_r03_catalogos_apoyo_mysql8.sql`. Crea solamente `acr_support_catalog_records`; incluye índices y claves propias. No incluye datos de producción. Respaldar la BD, revisar `SHOW CREATE TABLE`, ejecutar en QA, comparar conteos y permisos antes de producción. No repetir R01.

## Integración / ejemplos

```http
GET /api/v1/catalogos-apoyo
Authorization: Bearer <JWT>

GET /api/v1/catalogos-apoyo/comunas/registros?parentId=25
Authorization: Bearer <JWT>

POST /api/v1/catalogos-apoyo/profesiones/registros
Authorization: Bearer <JWT_DE_ADMINISTRADOR>
Content-Type: application/json

{"name":"Nombre por validar","code":null,"source":"Registro interno autorizado"}

POST /api/v1/acreditacion/postulaciones/123/apelaciones/456/documentos
Authorization: Bearer <JWT_DEL_TITULAR>
Content-Type: application/json

{"documentId":789,"purpose":"Respaldo de apelación"}
```

Los identificadores numéricos en la documentación son solo ilustrativos, no corresponden a datos reales.

## Verificaciones obligatorias antes de considerar R03 terminada

1. En un ambiente aislado: `./mvnw clean test` con Java 17; corregir cualquier error previo a desplegar.
2. Restaurar respaldo en MySQL 8 de QA, ejecutar script SQL, usar `JPA_DDL_AUTO=validate`, arrancar en puerto distinto.
3. Verificar `/api-docs`: 84 operaciones antiguas + 11 incorporadas = 95, **si todas aparecen correctamente**, sin duplicidades.
4. Pruebas de permisos: JWT ausente (401), usuario común leyendo catálogos (200), usuario común intentando editar (403), administrador configurado creando (201), usuario distinto sin acceso a expediente ajeno (404).
5. Probar ambos flujos de apelación con datos de QA, validando bloqueo de documentos cruzados e inmutabilidad después de presentar.
6. Pruebas regresivas de **Beneficio Postulación, Gestión de Demanda, Teletrabajo, Bienestar, Capacitación y Calificaciones**.
7. Resolver contrato ficha personal/profesional `PUT` reportado HTTP 400 con JSON exacto de prueba, sin inferir campos. Completar DTO/JSON genéricos mediante una siguiente iteración antes de declarar 100/100 funcional.
8. Evaluar seguridad de `ACREDITACION_INTEGRATION_OPEN_ACCESS_ENABLED=true`: habilita acciones privilegiadas a cualquier JWT autenticado para integración. Usarlo solo en entorno de pruebas o ventana explícitamente autorizada.

## Estado de entrega

Se incorpora **código fuente completo como continuación R02**, migración R03 y matriz de cumplimiento. **No se declara compilación, test, autorización operativa ni despliegue exitoso** de esta entrega. Las cinco operaciones PDF y la documentación exhaustiva de 84 cuerpos/respuestas permanecen pendientes.
