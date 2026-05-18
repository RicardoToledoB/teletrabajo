# Scripts de servidor - Sistema Teletrabajo Backend

Se incorporaron tres scripts para operar el backend Spring Boot en servidor Linux:

```bash
start.sh
stop.sh
restart.sh
```

## 1. Compilar proyecto

Antes de iniciar con los scripts, compilar el backend:

```bash
./mvnw clean package -DskipTests
```

El JAR quedará en:

```text
target/teletrabajo-0.0.1-SNAPSHOT.jar
```

## 2. Configurar variables de entorno

El script usa perfil `mysql` por defecto y lee variables de entorno.

Variables principales:

```bash
export SPRING_PROFILES_ACTIVE=mysql
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=teletrabajo_db
export DB_USERNAME=teletrabajo_user
export DB_PASSWORD='CLAVE_SEGURA_SERVIDOR'
export JWT_SECRET='SECRETO_LARGO_Y_SEGURO'
```

También se incluye un archivo de referencia:

```text
.env.example
```

No subir claves reales a GitHub.

## 3. Iniciar backend

```bash
./start.sh
```

Por defecto:

- Perfil: `mysql`
- Puerto: `8080`
- Base de datos: `localhost:3306/teletrabajo_db`
- Logs: `logs/teletrabajo-back.log`
- PID: `teletrabajo-back.pid`

## 4. Detener backend

```bash
./stop.sh
```

El script intenta detener de forma controlada con `SIGTERM`. Si no responde, fuerza el cierre con `SIGKILL`.

## 5. Reiniciar backend

```bash
./restart.sh
```

## 6. Ver logs

```bash
tail -f logs/teletrabajo-back.log
```

## 7. Ejemplo completo de inicio en servidor

```bash
export SPRING_PROFILES_ACTIVE=mysql
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=teletrabajo_db
export DB_USERNAME=teletrabajo_user
export DB_PASSWORD='ClaveSeguraServidorAqui'
export JWT_SECRET='CambiarPorUnSecretoMuyLargoSeguroDeProduccion'

./start.sh
```

## 8. Notas

- Los scripts están pensados para despliegue por JAR.
- Si se desea usar otro JAR, se puede definir:

```bash
export JAR_FILE=/ruta/al/backend.jar
./start.sh
```

- Si se desea otro puerto:

```bash
export SERVER_PORT=8081
./start.sh
```

- Para producción formal se recomienda posteriormente crear un servicio `systemd`.
