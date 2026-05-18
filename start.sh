#!/usr/bin/env bash
set -euo pipefail

# ============================================================
# start.sh - Sistema Teletrabajo Backend
# Ejecuta el backend Spring Boot usando perfil MySQL por defecto.
# Compatible con despliegue por JAR en servidor Linux.
# ============================================================

APP_NAME="${APP_NAME:-teletrabajo-back}"
APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Cargar variables desde .env si existe.
# El archivo debe tener formato KEY=VALUE y NO debe subirse a Git.
if [[ -f "$APP_DIR/.env" ]]; then
  set -a
  # shellcheck disable=SC1091
  source "$APP_DIR/.env"
  set +a
fi

APP_PROFILE="${SPRING_PROFILES_ACTIVE:-mysql}"
APP_PORT="${SERVER_PORT:-8080}"
PID_FILE="${PID_FILE:-$APP_DIR/$APP_NAME.pid}"
LOG_DIR="${LOG_DIR:-$APP_DIR/logs}"
LOG_FILE="${LOG_FILE:-$LOG_DIR/$APP_NAME.log}"
JAVA_OPTS="${JAVA_OPTS:--Xms256m -Xmx1024m}"

# Base de datos MySQL por defecto. Sobrescribir con variables de entorno en servidor si corresponde.
export SPRING_PROFILES_ACTIVE="$APP_PROFILE"
export SERVER_PORT="$APP_PORT"
export DB_HOST="${DB_HOST:-localhost}"
export DB_PORT="${DB_PORT:-3306}"
export DB_NAME="${DB_NAME:-teletrabajo_db}"
export DB_USERNAME="${DB_USERNAME:-teletrabajo_user}"
export DB_PASSWORD="${DB_PASSWORD:-}"
export JPA_DDL_AUTO="${JPA_DDL_AUTO:-update}"
export SQL_INIT_MODE="${SQL_INIT_MODE:-never}"

mkdir -p "$LOG_DIR"

if [[ -f "$PID_FILE" ]] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
  echo "[$APP_NAME] Ya se encuentra en ejecución. PID: $(cat "$PID_FILE")"
  exit 0
fi

JAR_FILE="${JAR_FILE:-}"
if [[ -z "$JAR_FILE" ]]; then
  JAR_FILE="$(find "$APP_DIR/target" -maxdepth 1 -type f -name '*.jar' ! -name '*sources.jar' ! -name '*javadoc.jar' | head -n 1 || true)"
fi

if [[ -z "$JAR_FILE" || ! -f "$JAR_FILE" ]]; then
  echo "[$APP_NAME] No se encontró archivo JAR en $APP_DIR/target."
  echo "Compila primero con: ./mvnw clean package -DskipTests"
  exit 1
fi

if [[ -z "$DB_PASSWORD" ]]; then
  echo "[$APP_NAME] ADVERTENCIA: DB_PASSWORD está vacío. Define la clave antes de iniciar en servidor."
fi

echo "[$APP_NAME] Iniciando aplicación..."
echo "[$APP_NAME] Perfil: $SPRING_PROFILES_ACTIVE"
echo "[$APP_NAME] Puerto: $SERVER_PORT"
echo "[$APP_NAME] BD: $DB_HOST:$DB_PORT/$DB_NAME"
echo "[$APP_NAME] JAR: $JAR_FILE"
echo "[$APP_NAME] Log: $LOG_FILE"

nohup java $JAVA_OPTS -jar "$JAR_FILE" > "$LOG_FILE" 2>&1 &
echo $! > "$PID_FILE"

sleep 2
if kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
  echo "[$APP_NAME] Aplicación iniciada correctamente. PID: $(cat "$PID_FILE")"
else
  echo "[$APP_NAME] La aplicación no quedó en ejecución. Revisa el log: $LOG_FILE"
  rm -f "$PID_FILE"
  exit 1
fi
