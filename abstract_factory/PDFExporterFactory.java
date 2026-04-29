public class PDFExporterFactory implements ExporterFactory {
    @Override public Exporter create()        { return new PDFExporter(); }
    @Override public String   fileExtension() { return ".pdf"; }
    @Override public String   mimeType()      { return "application/pdf"; }
}
