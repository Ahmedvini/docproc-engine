import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ExportManager {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final Map<String, ExporterFactory> factories = new LinkedHashMap<>();
    private final List<String>                 log       = new ArrayList<>();
    private ExportManager() {
        factories.put("html",  new HTMLExporterFactory());
        factories.put("pdf",   new PDFExporterFactory());
        factories.put("docx",  new DOCXExporterFactory());
        factories.put("json",  new JSONExporterFactory());
    }

    private static final class Holder {
        static final ExportManager INSTANCE = new ExportManager();
    }
    public static ExportManager getInstance() { return Holder.INSTANCE; }

    public void register(String format, ExporterFactory factory) {
        factories.put(format.toLowerCase(), factory);
        System.out.println("  [ExportMgr] Registered new format: '" + format + "'");
    }
    public String export(Document doc, String format) {
        String key = format.toLowerCase();
        ExporterFactory fac = factories.get(key);
        if (fac == null) throw new IllegalArgumentException("Unknown format: " + format + ". Available: " + formats());
        String result = fac.create().exportDocument(doc);
        String entry  = "[" + LocalDateTime.now().format(FMT) + "] "
            + String.format("%-38s", doc.getTitle()) + "-> " + key.toUpperCase() + " (" + result.length() + " chars)";
        log.add(entry);
        System.out.println("  [ExportMgr] " + entry.trim());
        return result;
    }
    public List<String> formats() { return new ArrayList<>(factories.keySet()); }
    public List<String> getLog()  { return List.copyOf(log); }
}
