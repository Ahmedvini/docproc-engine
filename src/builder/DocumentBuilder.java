import java.util.List;

public class DocumentBuilder {
    private ElementFactory   factory   = new StandardElementFactory();
    private TextFormatter    formatter = new TextFormatter();
    private CommandHistory   history   = new CommandHistory();
    private DocumentEventBus eventBus  = null;
    private Document         document;
    private Section          currentSection;

    public DocumentBuilder withFactory(ElementFactory f)    { this.factory = f;   return this; }
    public DocumentBuilder withEventBus(DocumentEventBus b) { this.eventBus = b;  return this; }
    public DocumentBuilder withFormatting(FormattingStrategy s) { formatter.setStrategy(s); return this; }

    public DocumentBuilder newDocument(String title, String author) {
        document = new Document(title); document.setMeta("author", author);
        currentSection = null; notify("document_created", title); return this;
    }
    public DocumentBuilder newDocument(String title) { return newDocument(title, "Unknown"); }

    public DocumentBuilder addSection(String title) {
        Section sec = factory.create_section(title);
        history.execute(new AddElementCommand(document, sec));
        currentSection = sec; notify("section_added", title); return this;
    }
    public DocumentBuilder addHeader(String text, int level) {
        Header h = factory.create_header(formatter.format(text), level);
        history.execute(new AddElementCommand(target(), h)); return this;
    }
    public DocumentBuilder addHeader(String text) { return addHeader(text, 1); }

    public DocumentBuilder addParagraph(String text) {
        Paragraph p = factory.create_paragraph(formatter.format(text));
        history.execute(new AddElementCommand(target(), p));
        notify("text_added", text.substring(0, Math.min(40, text.length()))); return this;
    }
    public DocumentBuilder addImage(String src, String alt, int w, int h) {
        history.execute(new AddElementCommand(target(), new Image(src, alt, w, h))); return this;
    }
    public DocumentBuilder addImage(String src, String alt) { return addImage(src, alt, 800, 600); }

    public DocumentBuilder addTable(List<String> headers, List<String[]> rows) {
        Table tbl = factory.create_table(headers);
        if (rows != null) for (String[] r : rows) tbl.addRow(r);
        history.execute(new AddElementCommand(target(), tbl)); return this;
    }
    public DocumentBuilder addTable(List<String> headers) { return addTable(headers, null); }

    public DocumentBuilder addFooter(String text) {
        history.execute(new AddElementCommand(document, factory.create_footer(text))); return this;
    }
    public DocumentBuilder styleLastElement(FontStyle font, ColorStyle color) {
        List<DocumentElement> kids = target().getChildren();
        if (!kids.isEmpty()) history.execute(new FormatChangeCommand(kids.get(kids.size()-1), font, color));
        return this;
    }
    public DocumentBuilder undo() { history.undo(); return this; }
    public DocumentBuilder redo() { history.redo(); return this; }
    public List<String>    commandLog() { return history.getSummary(); }

    public Document build() {
        if (document == null) throw new IllegalStateException("Call newDocument() before build().");
        Document doc = document; document = null; currentSection = null; return doc;
    }
    private CompositeElement target() { return (currentSection != null) ? currentSection : document; }
    private void notify(String event, Object data) { if (eventBus != null) eventBus.notify(event, data); }
}
