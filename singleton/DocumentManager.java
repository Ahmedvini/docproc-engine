import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DocumentManager {
    private final Map<String, Document> registry = new LinkedHashMap<>();
    private final DocumentEventBus      eventBus = new DocumentEventBus();
    private DocumentManager() {}

    private static final class Holder {
        static final DocumentManager INSTANCE = new DocumentManager();
    }
    public static DocumentManager getInstance() { return Holder.INSTANCE; }

    public DocumentBuilder newBuilder()                         { return new DocumentBuilder().withEventBus(eventBus); }
    public DocumentBuilder newBuilder(ElementFactory factory)   { return new DocumentBuilder().withFactory(factory).withEventBus(eventBus); }

    public String save(Document doc) {
        registry.put(doc.getElementId(), doc);
        eventBus.notify("document_saved", doc.getTitle());
        return doc.getElementId();
    }
    public Document          get(String id)   { return registry.get(id); }
    public int               count()          { return registry.size();  }
    public DocumentEventBus  getEventBus()    { return eventBus;         }
    public List<String>      list() {
        List<String> out = new ArrayList<>();
        for (Map.Entry<String, Document> e : registry.entrySet())
            out.add(e.getKey() + ": " + e.getValue().getTitle());
        return out;
    }
}
