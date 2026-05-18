#!/usr/bin/env bash
set -euo pipefail

# ============================================================
# restart.sh - Sistema Teletrabajo Backend
# Reinicia el backend Spring Boot.
# ============================================================

APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

"$APP_DIR/stop.sh"
sleep 2
"$APP_DIR/start.sh"
