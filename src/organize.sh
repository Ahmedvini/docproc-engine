#!/bin/bash
# organize.sh — organizes the project into a clean folder structure
# Run this from the folder containing all .java files, run.sh, and the PDFs
# Usage: bash organize.sh

set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo ""
echo "============================================================"
echo "  Document Processing System — Project Organizer"
echo "============================================================"
echo "  Working in: $SCRIPT_DIR"
echo ""

# ── Create folder structure ───────────────────────────────────────────────────
echo "  [1/5] Creating folder structure..."

mkdir -p src/flyweight
mkdir -p src/visitor
mkdir -p src/composite
mkdir -p src/factory_method
mkdir -p src/abstract_factory
mkdir -p src/builder
mkdir -p src/strategy
mkdir -p src/observer
mkdir -p src/command
mkdir -p src/singleton
mkdir -p src/plugin
mkdir -p src/main
mkdir -p docs
mkdir -p bin
mkdir -p out

echo "         src/flyweight        — FontStyle, ColorStyle, StyleFactory"
echo "         src/visitor          — DocumentVisitor interface + implementations"
echo "         src/composite        — Document tree (Element, Section, Document …)"
echo "         src/factory_method   — ElementFactory hierarchy"
echo "         src/abstract_factory — Exporter + ExporterFactory families"
echo "         src/builder          — DocumentBuilder"
echo "         src/strategy         — FormattingStrategy + TextFormatter"
echo "         src/observer         — EventBus + observer implementations"
echo "         src/command          — Command interface + history"
echo "         src/singleton        — Manager singletons"
echo "         src/plugin           — Plugin interface + implementations"
echo "         src/main             — Entry points (Main, GUI, Collaboration)"
echo "         docs/                — PDF deliverables"
echo "         bin/                 — Compiled .class files"
echo "         out/                 — Exported documents"

# ── Move source files ─────────────────────────────────────────────────────────
echo ""
echo "  [2/5] Moving source files..."

# ① Flyweight
for f in FontStyle.java ColorStyle.java StyleFactory.java; do
  [ -f "$f" ] && mv "$f" src/flyweight/ && echo "         -> src/flyweight/$f"
done

# ② Visitor
for f in DocumentVisitor.java WordCountVisitor.java SpellCheckVisitor.java; do
  [ -f "$f" ] && mv "$f" src/visitor/ && echo "         -> src/visitor/$f"
done

# ③ Composite + Prototype
for f in DocumentElement.java CompositeElement.java \
         Paragraph.java Header.java Footer.java Image.java \
         Table.java Section.java Document.java; do
  [ -f "$f" ] && mv "$f" src/composite/ && echo "         -> src/composite/$f"
done

# ④ Factory Method
for f in ElementFactory.java StandardElementFactory.java StyledElementFactory.java; do
  [ -f "$f" ] && mv "$f" src/factory_method/ && echo "         -> src/factory_method/$f"
done

# ⑤ Abstract Factory
for f in Exporter.java \
         HTMLExporter.java PDFExporter.java DOCXExporter.java JSONExporter.java \
         ExporterFactory.java \
         HTMLExporterFactory.java PDFExporterFactory.java \
         DOCXExporterFactory.java JSONExporterFactory.java; do
  [ -f "$f" ] && mv "$f" src/abstract_factory/ && echo "         -> src/abstract_factory/$f"
done

# ⑥ Builder
for f in DocumentBuilder.java; do
  [ -f "$f" ] && mv "$f" src/builder/ && echo "         -> src/builder/$f"
done

# ⑦ Strategy
for f in FormattingStrategy.java PlainTextStrategy.java UpperCaseStrategy.java \
         TitleCaseStrategy.java SentenceCaseStrategy.java \
         MarkdownStripStrategy.java TextFormatter.java; do
  [ -f "$f" ] && mv "$f" src/strategy/ && echo "         -> src/strategy/$f"
done

# ⑧ Observer
for f in DocumentObserver.java DocumentEventBus.java \
         AutoSaveObserver.java LivePreviewObserver.java \
         VersionControlObserver.java; do
  [ -f "$f" ] && mv "$f" src/observer/ && echo "         -> src/observer/$f"
done

# ⑨ Command
for f in Command.java InsertTextCommand.java DeleteTextCommand.java \
         FormatChangeCommand.java AddElementCommand.java \
         CommandHistory.java; do
  [ -f "$f" ] && mv "$f" src/command/ && echo "         -> src/command/$f"
done

# ⑩ Singleton
for f in DocumentManager.java PluginManager.java ExportManager.java; do
  [ -f "$f" ] && mv "$f" src/singleton/ && echo "         -> src/singleton/$f"
done

# ⑪ Plugin
for f in Plugin.java SpellCheckPlugin.java WordCountPlugin.java \
         AutoNumberPlugin.java; do
  [ -f "$f" ] && mv "$f" src/plugin/ && echo "         -> src/plugin/$f"
done

# Entry points
for f in Main.java DocumentEditorGUI.java CollaborationSimulation.java; do
  [ -f "$f" ] && mv "$f" src/main/ && echo "         -> src/main/$f"
done

# Docs
for f in Documentation.pdf UML_Diagram.pdf; do
  [ -f "$f" ] && mv "$f" docs/ && echo "         -> docs/$f"
done

# ── Compile from organized structure ──────────────────────────────────────────
echo ""
echo "  [3/5] Compiling all sources..."

find src -name "*.java" > sources.txt
javac -d bin @sources.txt 2>&1

if [ $? -ne 0 ]; then
  echo ""
  echo "  ERROR: Compilation failed. Check errors above."
  rm -f sources.txt
  exit 1
fi

rm -f sources.txt
echo "         Compilation successful."
echo "         Class files -> bin/"

# ── Update run.sh ─────────────────────────────────────────────────────────────
echo ""
echo "  [4/5] Writing run.sh..."

cat > run.sh << 'RUNEOF'
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
RUNEOF

chmod +x run.sh
echo "         run.sh updated."

# ── Print final tree ──────────────────────────────────────────────────────────
echo ""
echo "  [5/5] Final project structure:"
echo ""

if command -v tree &>/dev/null; then
  tree -I "bin|*.class" --dirsfirst
else
  find . -not -path "./bin/*" -not -name "*.class" | sort | \
  awk '{
    n = split($0, a, "/")
    indent = ""
    for (i=2; i<n; i++) indent = indent "    "
    print "  " indent (n>1 ? "|-- " : "") a[n]
  }'
fi

echo ""
echo "============================================================"
echo "  Done! To run the project:"
echo ""
echo "    bash run.sh           # 11-pattern demo"
echo "    bash run.sh gui       # Swing GUI"
echo "    bash run.sh collab    # Collaboration simulation"
echo "============================================================"
echo ""
