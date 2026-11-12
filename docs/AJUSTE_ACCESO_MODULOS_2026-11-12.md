# Ajuste de autorización — 12 de noviembre de 2026

## Alcance
Sobre el ZIP `gestiondepersonas(1).zip`, se retiran exclusivamente las restricciones por rol de Acreditación, Calificaciones y Capacitación. Se conserva JWT, autenticación sin sesión y reglas de propiedad de expedientes. No se modifican entidades, repositorios, propiedades de conexión, dependencias, servicios de negocio ni reglas de los otros módulos.

## Archivos Java de producción modificados
- `security/SecurityConfig.java`: declara explícitamente las tres familias de rutas como autenticadas, sin requisitos de rol. El resto de la cadena se mantiene.
- `controller/acreditacion/AcreditacionController.java`: elimina las dos restricciones `ACR_REFERENTE`. El servicio de usuarios genera autoridades con prefijo `ROLE_`, por lo que la expresión anterior tampoco coincidía con `ROLE_ACR_REFERENTE`.
- `controller/calificaciones/CalificacionesController.java`: elimina la restricción ADMIN del controlador completo (tres endpoints).
- `controller/capacitacion/CapacitacionController.java`: elimina cinco restricciones ADMIN.
- `controller/capacitacion/CapacitacionExtraController.java`: elimina la restricción ADMIN de importación CSV.

No se amplía el acceso a expedientes ajenos (`GET /acreditacion/postulaciones/{id}` sigue siendo del titular). No se implementan nuevos endpoints ni transiciones de estado. La verificación pública de certificados conserva su excepción GET.

## Diagnóstico y prueba del 401
El código confirma restricciones por rol, que normalmente producen 403 si el JWT es válido. No se dispone de los headers ni logs de las peticiones reportadas; por eso no se puede confirmar el origen del 401 observado en producción. El filtro JWT no se cambia: sigue validando firma, expiración y usuario existente. Si persiste 401, revisar el encabezado Authorization en Angular/Postman, que se envíe el access token (no el refresh token), vigencia del token y que el proxy lo preserve.

Ejemplo de prueba QA con datos sintéticos y JWT válido de un usuario no administrador:

```bash
curl -i -X POST 'https://teletrabajo.dssm.cl/api/v1/acreditacion/convocatorias' \
  -H 'Authorization: Bearer ACCESS_TOKEN_VALIDO' \
  -H 'Content-Type: application/json' \
  --data '{"year":2027,"title":"Prueba QA","legalBasis":"Prueba","opensAt":"2027-03-01T09:00:00","closesAt":"2027-03-31T17:00:00"}'
```

Usar entorno de QA, no crear datos de prueba en producción. Para participantes usar una actividad existente de QA (`activity: {"id": ID_EXISTENTE}`), RUT y datos sintéticos. Un JWT válido no garantiza éxito frente a payload inválido, restricciones de BD o reglas de negocio.

## Pruebas
Se incorpora `src/test/java/com/teletrabajo/security/ModuleAuthorizationTest.java`: cadena real de Spring Security + JWT, MockMvc, repositorios y servicios simulados. Cubre las operaciones antes restringidas con diversos roles y sin rol, 401 sin JWT o con token inválido, propiedad de postulación y verificación pública.

```bash
./mvnw -Dtest=ModuleAuthorizationTest test
./mvnw clean package
```

Estado de esta entrega: revisión estática y comparación del ZIP realizadas. **No se ejecutaron Maven ni las pruebas**: el entorno no tiene Maven instalado y el wrapper no pudo descargar la distribución. Tampoco se conectó a MySQL ni se realizó despliegue. Compilar y ejecutar en QA antes de instalar.

## Consideraciones para puesta en producción
- Validar el alcance con Gestión de Personas y Ciberseguridad: todos los usuarios autenticados podrán crear convocatorias y ciclos, registrar participantes, crear firmantes, emitir/revocar certificados e importar participantes. Esto amplía permisos sobre procesos institucionales y datos personales; no equivale a que los módulos estén listos para uso productivo.
- Los documentos originales describen módulos iniciales con funcionalidades pendientes. Acreditación crea `PROGRAMADA` por diseño y Calificaciones crea `BORRADOR`. El POST de convocatoria actual solo persiste año, título, base legal, fechas de apertura/cierre y metadatos del servidor; los demás campos del ejemplo de Patricio no se incorporaron en este ajuste de seguridad.
- Mantener la configuración de base de datos y los secretos propios del servidor. Respaldar JAR y BD y revisar la configuración de generación de esquema antes de desplegar.
