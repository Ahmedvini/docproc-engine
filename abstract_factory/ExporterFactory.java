public interface ExporterFactory {
    Exporter create();
    String   fileExtension();
    String   mimeType();
}
