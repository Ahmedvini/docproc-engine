#!/bin/bash
# run.sh — compile and launch the Document Processing System
# Usage:
#   bash run.sh            -> runs Main (11-pattern demo)
#   bash run.sh gui        -> runs Swing GUI
#   bash run.sh collab     -> runs Collaboration Simulation

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

MODE="${1:-main}"

echo ""
echo "============================================================"
echo "  Document Processing System"
echo "============================================================"

# Compile
echo "  Compiling..."
rm -rf bin && mkdir -p bin
find src -name "*.java" > .sources.tmp
javac -d bin @.sources.tmp
if [ $? -ne 0 ]; then
  echo "  Compilation FAILED."
  rm -f .sources.tmp; exit 1
fi
rm -f .sources.tmp
echo "  Compiled OK."
echo ""

case "$MODE" in
  gui)
    echo "  Launching Swing GUI..."
    java -cp bin DocumentEditorGUI
    ;;
  collab)
    echo "  Running Collaboration Simulation..."
    java -cp bin CollaborationSimulation
    ;;
  *)
    echo "  Running 11-Pattern Demo..."
    java -cp bin Main
    ;;
esac
