public class JSONExporterFactory implements ExporterFactory {
    @Override public Exporter create()        { return new JSONExporter(); }
    @Override public String   fileExtension() { return ".json"; }
    @Override public String   mimeType()      { return "application/json"; }
}
