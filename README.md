# Smart Document Editor (Plugin-Based, Extensible)

This project implements a modular Smart Document Editor in Java with all requested patterns and bonus tasks.

## Build and Run

```bash
mvn test
mvn exec:java
mvn exec:java -Dexec.args="--gui"
mvn exec:java -Dexec.args="--server"
```

Generated files:

- `exports/` (PDF/HTML/DOCX/JSON/XML exports)
- `autosave/latest.txt` (observer auto-save)

## Included Patterns

- Singleton: `DocumentManager`, `PluginManager`, `ExportManager`
- Factory Method: `DocumentElementFactory`
- Abstract Factory: exporter factories (`PdfExporterFactory`, etc.)
- Builder: `DocumentBuilder`
- Prototype: `DocumentComponent.deepCopy()`
- Flyweight: `StyleFlyweightFactory`
- Composite: `DocumentComponent` tree (`Document -> Section -> Elements`)
- Command: `InsertTextCommand`, `DeleteTextCommand`, `FormatChangeCommand`
- Strategy: formatting strategies + exporter selection by format
- Observer (bonus): `AutoSaveObserver`, `LivePreviewObserver`
- Visitor (bonus): `WordCountVisitor`, `SpellCheckVisitor`

## Bonus Tasks

- GUI (Swing): `SmartDocumentEditorFrame`
- JSON/XML export: implemented in export module
- Plugin system: `PluginManager` + ServiceLoader plugin (`WordFrequencyPlugin`)
- Real-time collaboration simulation: `CollaborationSession`
- Version control system: `VersionControlService`

See detailed UML and design notes in `docs/Documentation.md`.

## Hosting

This project now includes a built-in HTTP server mode and Docker deployment files.

### Local hosting

```bash
mvn -DskipTests exec:java -Dexec.args="--server"
```

Server endpoints:

- `GET /health`
- `GET /document`
- `GET /word-count`
- `GET /spell-check`

Default port is `8080`. In cloud platforms, the app reads the `PORT` environment variable automatically.

### Docker

```bash
docker build -t docproc-engine .
docker run -p 8080:8080 docproc-engine
```

### Render

1. Push this repository to GitHub.
2. Create a new Render Web Service from the repo.
3. Render auto-detects `render.yaml` and Docker setup.
4. Deploy and open your service URL.
