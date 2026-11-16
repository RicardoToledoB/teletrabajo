# Corrección Capacitación — 16/11/2026

Base: proyecto completo F01 Git H2 corregido (13/11). No requiere migración SQL. Mantener configuración MySQL, JWT, Acreditación y operadores existentes. La modalidad A DISTANCIA se almacena tal cual, sin convertirla a VIRTUAL. PUT actividad sigue siendo reemplazo completo. El nuevo POST multipart de firma devuelve el firmante, con signatureImagePath y signatureAsset idénticos. Ver CAPACITACION_API_FRONTEND.md.

## Despliegue
- Integrar el proyecto completo en el repositorio existente conservando .git y configuración local. Revisar git diff; no añadir secretos ni imágenes al Git.
- `./mvnw clean package` (14 pruebas esperadas si no hay otros cambios). No desplegar si falla.
- Identificar usuario de systemd con `systemctl show teletrabajo -p User -p Group`. Crear o ajustar el directorio privado configurado para que esa cuenta pueda escribir; no usar permisos 777. Conservar y respaldar archivos existentes. No publicar el directorio por Apache.
- Conservar configuración de operadores oficiales; no habilitar emisión para usuarios generales. Reiniciar y verificar logs; probar con imágenes ficticias y usuario autorizado antes de usar firmas reales.
- Los archivos de firma se mantienen fuera del JAR. Respaldar directorio junto con MySQL. Sin migración nueva; no repetir migraciones previas.

Validación local: revisión estática de cambios y ZIP. Maven wrapper no pudo descargar Maven; pruebas pendientes de ejecución en servidor.
