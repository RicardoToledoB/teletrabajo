#!/usr/bin/env bash
set -euo pipefail

APP_NAME="${APP_NAME:-teletrabajo-back}"
APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$APP_DIR"

echo "[$APP_NAME] Actualizando repositorio..."
git pull

echo "[$APP_NAME] Compilando backend..."
./mvnw clean package -DskipTests

echo "[$APP_NAME] Reiniciando backend..."
./restart.sh

echo "[$APP_NAME] Últimos logs:"
tail -n 100 "${LOG_FILE:-$APP_DIR/logs/$APP_NAME.log}"
