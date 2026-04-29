# Plugin-Based Extensible Document Processing System

> A full demonstration of **11 Gang-of-Four design patterns** implemented in Java 21,  
> featuring a Swing GUI, real-time collaboration simulation, and multi-format export.

**Author:** Ahmed Elsheikh  
**University:** Egypt-Japan University of Science and Technology (E-JUST)  
**Course:** Software Design Patterns  
**Language:** Java 21 (no external libraries)

---

## Table of Contents

1. [Quick Start](#quick-start)
2. [Project Structure](#project-structure)
3. [Design Patterns](#design-patterns)
4. [Running the Project](#running-the-project)
5. [Features](#features)
6. [Pattern Interactions](#pattern-interactions)
7. [Extending the System](#extending-the-system)
8. [Deliverables](#deliverables)

---

## Quick Start

```bash
# 1. Organize files and compile
bash organize.sh

# 2. Run the 11-pattern demo
bash run.sh

# 3. Launch the Swing GUI
bash run.sh gui

# 4. Run the collaboration simulation
bash run.sh collab
```

**Requirements:** JDK 11 or higher. No external libraries needed.

---

## Project Structure

```
project/
│
├── src/
│   ├── flyweight/              ② Flyweight Pattern
│   │   ├── FontStyle.java          Immutable shared font descriptor
│   │   ├── ColorStyle.java         Immutable shared colour descriptor
│   │   └── StyleFactory.java       Pool — guarantees one object per unique style
│   │
│   ├── visitor/                ⑪ Visitor Pattern (Bonus)
│   │   ├── DocumentVisitor.java    Visitor interface (7 visit() overloads)
│   │   ├── WordCountVisitor.java   Counts words, chars, paragraphs, images
│   │   └── SpellCheckVisitor.java  Flags 10 common misspellings
│   │
│   ├── composite/              ⑦ Composite + ⑥ Prototype
│   │   ├── DocumentElement.java    Abstract component (deepCopy = Prototype)
│   │   ├── CompositeElement.java   Abstract composite node (holds children)
│   │   ├── Document.java           Root composite node
│   │   ├── Section.java            Composite — named section
│   │   ├── Table.java              Composite — data table with rows
│   │   ├── Paragraph.java          Leaf — text paragraph
│   │   ├── Header.java             Leaf — heading (level 1–3)
│   │   ├── Footer.java             Leaf — page footer
│   │   └── Image.java              Leaf — embedded image
│   │
│   ├── factory_method/         ③ Factory Method Pattern
│   │   ├── ElementFactory.java         Abstract creator interface
│   │   ├── StandardElementFactory.java Plain unstyled elements
│   │   └── StyledElementFactory.java   Pre-styled elements (uses Flyweight)
│   │
│   ├── abstract_factory/       ④ Abstract Factory Pattern
│   │   ├── Exporter.java               Abstract product
│   │   ├── HTMLExporter.java           Concrete product — HTML output
│   │   ├── PDFExporter.java            Concrete product — PDF text output
│   │   ├── DOCXExporter.java           Concrete product — OOXML output
│   │   ├── JSONExporter.java           Concrete product — JSON output
│   │   ├── ExporterFactory.java        Abstract factory interface
│   │   ├── HTMLExporterFactory.java    Concrete factory
│   │   ├── PDFExporterFactory.java     Concrete factory
│   │   ├── DOCXExporterFactory.java    Concrete factory
│   │   └── JSONExporterFactory.java    Concrete factory
│   │
│   ├── builder/                ⑤ Builder Pattern
│   │   └── DocumentBuilder.java    Fluent builder — integrates Factory, Strategy,
│   │                               Command, and Observer in every add*() call
│   │
│   ├── strategy/               ⑨ Strategy Pattern
│   │   ├── FormattingStrategy.java     Strategy interface
│   │   ├── PlainTextStrategy.java      Identity transform
│   │   ├── UpperCaseStrategy.java      text.toUpperCase()
│   │   ├── TitleCaseStrategy.java      Title Case
│   │   ├── SentenceCaseStrategy.java   Sentence case
│   │   ├── MarkdownStripStrategy.java  Removes **, __, *, _, ~~, `
│   │   └── TextFormatter.java          Context — delegates to active strategy
│   │
│   ├── observer/               ⑩ Observer Pattern (Bonus)
│   │   ├── DocumentObserver.java       Observer interface
│   │   ├── DocumentEventBus.java       Subject — maintains & notifies observers
│   │   ├── AutoSaveObserver.java       Logs auto-save triggers
│   │   ├── LivePreviewObserver.java    Logs preview refresh events
│   │   └── VersionControlObserver.java Creates timestamped snapshots
│   │
│   ├── command/                ⑧ Command Pattern
│   │   ├── Command.java                Command interface
│   │   ├── InsertTextCommand.java      Insert text at position or end
│   │   ├── DeleteTextCommand.java      Delete text range [start, end)
│   │   ├── FormatChangeCommand.java    Change font / colour on any element
│   │   ├── AddElementCommand.java      Add child to composite node
│   │   └── CommandHistory.java         Dual-stack undo/redo manager
│   │
│   ├── singleton/              ① Singleton Pattern
│   │   ├── DocumentManager.java    Document registry + event bus access
│   │   ├── PluginManager.java      Plugin registry + hook system
│   │   └── ExportManager.java      Export format registry + audit log
│   │
│   ├── plugin/                 Plugin System
│   │   ├── Plugin.java             Plugin interface (onLoad, onUnload)
│   │   ├── SpellCheckPlugin.java   Wraps SpellCheckVisitor; hooks before_export
│   │   ├── WordCountPlugin.java    Wraps WordCountVisitor
│   │   └── AutoNumberPlugin.java   Walks Composite tree, numbers headers
│   │
│   └── main/                   Entry Points
│       ├── Main.java                   11-pattern demo (console)
│       ├── DocumentEditorGUI.java      Swing GUI
│       └── CollaborationSimulation.java Real-time collaboration demo
│
├── docs/
│   ├── Documentation.pdf       8-page technical documentation
│   └── UML_Diagram.pdf         2-page A3 UML class diagram
│
├── bin/                        Compiled .class files (auto-generated)
├── out/                        Exported documents
├── organize.sh                 One-time file organizer
├── run.sh                      Compile + launch script
└── README.md                   This file
```

---

## Design Patterns

### ① Singleton — `src/singleton/`

| Class | Responsibility |
|---|---|
| `DocumentManager` | Central registry for all open documents; owns the event bus |
| `PluginManager` | Plugin lifecycle management and hook/event system |
| `ExportManager` | Export format registry and audit log |

**Implementation:** Bill-Pugh Initialization-on-Demand Holder — thread-safe, lazy, zero synchronisation overhead.

```java
private static final class Holder {
    static final DocumentManager INSTANCE = new DocumentManager();
}
public static DocumentManager getInstance() { return Holder.INSTANCE; }
```

**Verified at runtime:**
```
DocumentManager singleton: true
PluginManager   singleton: true
ExportManager   singleton: true
```

---

### ② Flyweight — `src/flyweight/`

`FontStyle` and `ColorStyle` are immutable value objects (intrinsic state).  
`StyleFactory` is a pool keyed by `(family, size, bold, italic)` / `(r, g, b)`.

```java
FontStyle f1 = StyleFactory.getFont("Arial", 12);
FontStyle f2 = StyleFactory.getFont("Arial", 12);
System.out.println(f1 == f2);  // true — same object
```

A 1000-paragraph document using Arial 12pt holds **one** `FontStyle` in memory instead of 1000.

---

### ③ Factory Method — `src/factory_method/`

`ElementFactory` declares the interface. Two concrete factories override it:

- `StandardElementFactory` — plain, unstyled elements.
- `StyledElementFactory` — applies Flyweight `FontStyle`/`ColorStyle` automatically.

```java
ElementFactory factory = new StyledElementFactory(
    StyleFactory.getFont("Arial", 12),
    StyleFactory.getColor(15, 52, 96)
);
Paragraph p = factory.create_paragraph("Hello");  // pre-styled
```

---

### ④ Abstract Factory — `src/abstract_factory/`

Each format has its own factory producing a matching `Exporter`:

```
ExporterFactory (interface)
├── HTMLExporterFactory  →  HTMLExporter
├── PDFExporterFactory   →  PDFExporter
├── DOCXExporterFactory  →  DOCXExporter
└── JSONExporterFactory  →  JSONExporter
```

Adding a new format requires **zero changes** to existing code:

```java
ExportManager.getInstance().register("xml", new XmlExporterFactory());
```

---

### ⑤ Builder — `src/builder/`

Fluent API that composes Factory Method + Strategy + Command + Observer in every call:

```java
Document doc = DocumentManager.getInstance().newBuilder()
    .newDocument("Annual Report 2025", "Ahmed Elsheikh")
    .addSection("Executive Summary")
    .addParagraph("Key findings for 2025...")
    .addTable(List.of("Quarter", "Revenue", "Growth"),
              List.of(new String[]{"Q1", "12.4M", "+8%"}))
    .addImage("chart.png", "Q1 Chart", 1200, 500)
    .addFooter("Confidential — Page {PAGE}")
    .build();
```

---

### ⑥ Prototype — `src/composite/`

Every `DocumentElement` subclass overrides `deepCopy()`:

```java
Table original = ...;           // 4-row table
Table clone    = original.deepCopy();
clone.addRow("Q5", "20M", "+10%");

System.out.println(original.getRows().size());  // 4 — unchanged
System.out.println(clone.getRows().size());     // 5
System.out.println(original.getElementId().equals(clone.getElementId())); // false
```

Flyweight style references are **not** duplicated (they are intrinsic/shared).

---

### ⑦ Composite — `src/composite/`

```
Document  (root composite)
└── Section  (composite)
    ├── Header     (leaf)
    ├── Paragraph  (leaf)
    ├── Image      (leaf)
    └── Table      (composite — stores rows)
└── Footer   (leaf)
```

`render(indent)` and `accept(visitor)` work identically on any node — leaf or composite.

---

### ⑧ Command — `src/command/`

Every mutation is a `Command` object with `execute()` and `undo()`:

| Command | execute() | undo() |
|---|---|---|
| `InsertTextCommand` | Inserts text at position | Restores snapshot |
| `DeleteTextCommand` | Removes text range | Restores snapshot |
| `FormatChangeCommand` | Sets font/colour | Restores old style |
| `AddElementCommand` | Adds child to composite | Removes child |

`CommandHistory` uses two `ArrayDeque` stacks — unlimited undo/redo:

```
[Cmd] + execute : Add(Section)
[Cmd] + execute : InsertText(' [AMENDED]')
[Cmd] < undone  : InsertText(' [AMENDED]')    ← undo
[Cmd] > redone  : InsertText(' [AMENDED]')    ← redo
```

---

### ⑨ Strategy — `src/strategy/`

Five interchangeable formatting algorithms, swappable at runtime:

| Strategy | Result of `format("hello world")` |
|---|---|
| `PlainTextStrategy` | `hello world` |
| `UpperCaseStrategy` | `HELLO WORLD` |
| `TitleCaseStrategy` | `Hello World` |
| `SentenceCaseStrategy` | `Hello world` |
| `MarkdownStripStrategy` | strips `**`, `_`, `~~`, `` ` `` |

```java
TextFormatter fmt = new TextFormatter();
fmt.setStrategy(new TitleCaseStrategy());
String result = fmt.format("hello world");  // "Hello World"
```

---

### ⑩ Observer *(Bonus)* — `src/observer/`

`DocumentEventBus` (subject) notifies all subscribers on every structural change:

| Event | Fired by |
|---|---|
| `document_created` | `DocumentBuilder.newDocument()` |
| `section_added` | `DocumentBuilder.addSection()` |
| `text_added` | `DocumentBuilder.addParagraph()` |
| `document_saved` | `DocumentManager.save()` |
| `collab_edit` | `CollaborationSimulation` |

14 version snapshots were created in a single demo run.

---

### ⑪ Visitor *(Bonus)* — `src/visitor/`

Analytics traverse the entire Composite tree without modifying any element class:

```
+-- Word Count Report ----------------
|  Words       : 77
|  Characters  : 532
|  Paragraphs  : 5
|  Sections    : 4
|  Images      : 2
+-------------------------------------

!! Spell Check: 6 issue(s) found:
    'goverment'    -> 'government'
    'accomodation' -> 'accommodation'
    'beleive'      -> 'believe'
    ...
```

---

## Running the Project

### Option A — Script (recommended)

```bash
bash run.sh           # 11-pattern console demo
bash run.sh gui       # Swing GUI
bash run.sh collab    # Collaboration simulation
```

### Option B — Manual

```bash
# Compile all source files recursively
find src -name "*.java" > sources.txt
javac -d bin @sources.txt

# Run
java -cp bin Main                     # Console demo
java -cp bin DocumentEditorGUI        # Swing GUI
java -cp bin CollaborationSimulation  # Collaboration
```

---

## Features

### Console Demo (`Main.java`)

Exercises all 11 patterns in sequence with clearly labelled output sections:
- Singleton identity verification
- Flyweight pool creation vs reuse
- Factory Method styled vs plain comparison
- Composite tree full render
- Prototype deep copy confirmation
- Command undo/redo walkthrough
- Strategy algorithm comparison
- Abstract Factory multi-format export (HTML, PDF, DOCX, JSON)
- Visitor word count + spell check
- Observer event log with 14 version snapshots
- Plugin load + execution

### Swing GUI (`DocumentEditorGUI.java`)

| UI Element | Pattern Demonstrated |
|---|---|
| JTree (left panel) | ⑦ Composite — mirrors Document tree live |
| Toolbar add buttons | ⑤ Builder — step-by-step assembly |
| Undo / Redo buttons | ⑧ Command — dual-stack history |
| Format combo box | ⑨ Strategy — swap algorithm at runtime |
| Clone button | ⑥ Prototype — deepCopy() with new ID |
| Export menu items | ④ Abstract Factory — 4 formats |
| Event log panel | ⑩ Observer — live green-on-dark feed |
| Status bar | ② Flyweight — pool size shown live |
| Plugin menu | Plugin system — Word Count, Spell Check |

### Collaboration Simulation (`CollaborationSimulation.java`)

Simulates **Alice, Bob, and Carol** editing the same document concurrently:

- **Phase 1** — Alice creates initial structure (sections, headers, paragraphs)
- **Phase 2** — Bob and Carol add content concurrently
- **Phase 3** — Conflict: Alice and Bob edit the same paragraph simultaneously
  - Detected and resolved with **last-write-wins** policy
- **Phase 4** — Bob performs undo/redo; Carol adds conclusion
- **Phase 5** — Alice finalises with footer; JSON export of merged document

**Output:**
```
Alice    :  6 operations  |  Activity log: 14 entries
Bob      :  3 operations  |  Activity log: 16 entries
Carol    :  4 operations  |  Activity log: 14 entries
TOTAL    : 13 total operations
Snapshots recorded: 15
```

---

## Pattern Interactions

The real power of this system comes from patterns composing naturally:

```
DocumentBuilder.addParagraph(text)
  │
  ├── TextFormatter.format(text)          ← ⑨ Strategy
  ├── ElementFactory.create_paragraph()   ← ③ Factory Method
  │     └── StyleFactory.getFont(...)     ← ② Flyweight
  ├── CommandHistory.execute(AddCmd)      ← ⑧ Command
  └── DocumentEventBus.notify("text_added") ← ⑩ Observer
        ├── AutoSaveObserver.onEvent()
        ├── LivePreviewObserver.onEvent()
        └── VersionControlObserver.onEvent()

ExportManager.export(doc, "html")
  └── HTMLExporterFactory.create()        ← ④ Abstract Factory
        └── HTMLExporter.exportDocument()
              └── doc.accept(exporter)    ← ⑦ Composite traversal

doc.accept(new WordCountVisitor())         ← ⑪ Visitor
  └── visits every node in Composite tree
        without modifying any element class
```

---

## Extending the System

| Goal | How |
|---|---|
| **New export format** | Subclass `Exporter` + `ExporterFactory`; call `ExportManager.getInstance().register("xml", new XmlFactory())` |
| **New element type** | Subclass `DocumentElement` (leaf) or `CompositeElement`; add `visit()` overload to `DocumentVisitor` |
| **New formatting rule** | Subclass `FormattingStrategy`; pass to `builder.withFormatting(new MyStrategy())` |
| **New analytics** | Subclass `DocumentVisitor`; call `doc.accept(new MyVisitor())` |
| **New plugin** | Implement `Plugin`; call `PluginManager.getInstance().load(new MyPlugin())` |
| **Persistence** | Implement `SaveCommand`; use `JSONExporter`; hook into `AutoSaveObserver` |
| **Real GUI editor** | Bind `DocumentBuilder` to Swing MVC; use `JTree` backed by the Composite structure |
| **Network collaboration** | Subscribe a `WebSocketObserver` to `DocumentEventBus`; broadcast events to remote peers |

---

## Deliverables

| Item | File | Status |
|---|---|---|
| Full Java implementation | `src/**/*.java` (57 files) | ✅ |
| Console demo | `src/main/Main.java` | ✅ |
| Swing GUI | `src/main/DocumentEditorGUI.java` | ✅ (Bonus) |
| Collaboration simulation | `src/main/CollaborationSimulation.java` | ✅ (Bonus) |
| Plugin system | `src/plugin/` | ✅ (Bonus) |
| JSON/DOCX/HTML/PDF export | `src/abstract_factory/` | ✅ (Bonus) |
| Version control simulation | `VersionControlObserver` | ✅ (Bonus) |
| UML class diagram | `docs/UML_Diagram.pdf` | ✅ (2-page A3) |
| Technical documentation | `docs/Documentation.pdf` | ✅ (8 pages) |
| Build script | `run.sh` | ✅ |
| Organizer script | `organize.sh` | ✅ |

---

## Pattern Summary Table

| # | Pattern | Category | Key Classes | Lines |
|---|---|---|---|---|
| ① | Singleton | Creational | `DocumentManager`, `PluginManager`, `ExportManager` | ~120 |
| ② | Flyweight | Structural | `StyleFactory`, `FontStyle`, `ColorStyle` | ~80 |
| ③ | Factory Method | Creational | `ElementFactory`, `StandardElementFactory`, `StyledElementFactory` | ~90 |
| ④ | Abstract Factory | Creational | `ExporterFactory` + 4 factories + 4 exporters | ~350 |
| ⑤ | Builder | Creational | `DocumentBuilder` | ~110 |
| ⑥ | Prototype | Creational | `deepCopy()` on all 9 element classes | ~30 |
| ⑦ | Composite | Structural | `DocumentElement`, `CompositeElement`, 7 concrete nodes | ~250 |
| ⑧ | Command | Behavioural | `Command`, 4 commands, `CommandHistory` | ~180 |
| ⑨ | Strategy | Behavioural | `FormattingStrategy`, 5 strategies, `TextFormatter` | ~100 |
| ⑩ | Observer | Behavioural | `DocumentEventBus`, 3 observers | ~110 |
| ⑪ | Visitor | Behavioural | `DocumentVisitor`, `WordCountVisitor`, `SpellCheckVisitor` | ~130 |
