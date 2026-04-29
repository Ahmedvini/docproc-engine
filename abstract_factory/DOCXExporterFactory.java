public class DOCXExporterFactory implements ExporterFactory {
    @Override public Exporter create()        { return new DOCXExporter(); }
    @Override public String   fileExtension() { return ".docx"; }
    @Override public String   mimeType()      { return "application/vnd.openxmlformats-officedocument.wordprocessingml.document"; }
}
