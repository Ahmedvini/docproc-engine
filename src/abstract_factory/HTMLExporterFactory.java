public class HTMLExporterFactory implements ExporterFactory {
    @Override public Exporter create()        { return new HTMLExporter(); }
    @Override public String   fileExtension() { return ".html"; }
    @Override public String   mimeType()      { return "text/html"; }
}
