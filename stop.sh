#!/usr/bin/env bash
set -euo pipefail

# ============================================================
# stop.sh - Sistema Teletrabajo Backend
# Detiene el backend Spring Boot usando archivo PID.
# ============================================================

APP_NAME="${APP_NAME:-teletrabajo-back}"
APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PID_FILE="${PID_FILE:-$APP_DIR/$APP_NAME.pid}"
TIMEOUT_SECONDS="${TIMEOUT_SECONDS:-30}"

if [[ ! -f "$PID_FILE" ]]; then
  echo "[$APP_NAME] No existe archivo PID. La aplicación podría no estar en ejecución."
  exit 0
fi

PID="$(cat "$PID_FILE")"

if ! kill -0 "$PID" 2>/dev/null; then
  echo "[$APP_NAME] El proceso PID $PID no existe. Limpiando PID file."
  rm -f "$PID_FILE"
  exit 0
fi

echo "[$APP_NAME] Deteniendo aplicación. PID: $PID"
kill "$PID"

for i in $(seq 1 "$TIMEOUT_SECONDS"); do
  if ! kill -0 "$PID" 2>/dev/null; then
    echo "[$APP_NAME] Aplicación detenida correctamente."
    rm -f "$PID_FILE"
    exit 0
  fi
  sleep 1
done

echo "[$APP_NAME] No respondió a SIGTERM en $TIMEOUT_SECONDS segundos. Forzando cierre..."
kill -9 "$PID" 2>/dev/null || true
rm -f "$PID_FILE"
echo "[$APP_NAME] Aplicación detenida forzadamente."
