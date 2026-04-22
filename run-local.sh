#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CLASS_DIR="$ROOT_DIR/classes"
FLASK_DIR="$ROOT_DIR/flask_ui"
JAVA_PORT="${JAVA_PORT:-8080}"
FLASK_PORT="${FLASK_PORT:-5000}"

if ! command -v javac >/dev/null 2>&1; then
  echo "javac is required but not installed"
  exit 1
fi

if ! command -v java >/dev/null 2>&1; then
  echo "java is required but not installed"
  exit 1
fi

if ! command -v python3 >/dev/null 2>&1; then
  echo "python3 is required but not installed"
  exit 1
fi

mkdir -p "$CLASS_DIR"
SOURCE_LIST="$ROOT_DIR/.java_sources.tmp"
find "$ROOT_DIR/src/main/java" -name "*.java" > "$SOURCE_LIST"
javac --release 17 -d "$CLASS_DIR" @"$SOURCE_LIST"
rm -f "$SOURCE_LIST"

if [[ ! -d "$FLASK_DIR/.venv" ]]; then
  python3 -m venv "$FLASK_DIR/.venv"
fi

"$FLASK_DIR/.venv/bin/pip" install -r "$FLASK_DIR/requirements.txt" >/dev/null

cleanup() {
  if [[ -n "${FLASK_PID:-}" ]]; then
    kill "$FLASK_PID" 2>/dev/null || true
  fi
  if [[ -n "${JAVA_PID:-}" ]]; then
    kill "$JAVA_PID" 2>/dev/null || true
  fi
}
trap cleanup EXIT INT TERM

PORT="$JAVA_PORT" java --add-modules jdk.httpserver -cp "$CLASS_DIR" com.docproc.app.Main --server &
JAVA_PID=$!

DOCPROC_ENGINE_URL="http://127.0.0.1:${JAVA_PORT}" PORT="$FLASK_PORT" "$FLASK_DIR/.venv/bin/python" "$FLASK_DIR/app.py" &
FLASK_PID=$!

echo "Java engine: http://127.0.0.1:${JAVA_PORT}"
echo "Flask UI:    http://127.0.0.1:${FLASK_PORT}"

echo "Press Ctrl+C to stop both services"
wait "$FLASK_PID"
