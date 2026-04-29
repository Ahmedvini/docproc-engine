import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class Document extends CompositeElement {
    private String              title;
    private Map<String, String> metadata;

    public Document(String title) {
        super();
        this.title = title;
        this.metadata = new LinkedHashMap<>();
        metadata.put("created", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        metadata.put("author",  "Unknown");
        metadata.put("version", "1.0");
    }
    private Document(Document src) {
        super(src);
        this.title    = src.title;
        this.metadata = new LinkedHashMap<>(src.metadata);
    }

    @Override public String render(int indent) {
        String bar = "=".repeat(54);
        StringBuilder sb = new StringBuilder(bar + "\n  DOCUMENT : " + title + "\n  Author   : "
            + metadata.get("author") + "\n  Created  : " + metadata.get("created") + "\n" + bar);
        for (DocumentElement c : children) sb.append("\n").append(c.render(indent));
        return sb.append("\n").append(bar).toString();
    }
    @Override public void     accept(DocumentVisitor v) {
        v.visit(this);
        for (DocumentElement c : children) c.accept(v);
    }
    @Override public Document deepCopy()                { return new Document(this); }
    public String              getTitle()                        { return title; }
    public void                setTitle(String t)               { this.title = t; }
    public Map<String, String> getMetadata()                     { return metadata; }
    public void                setMeta(String key, String value) { metadata.put(key, value); }
}
