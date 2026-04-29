public class Paragraph extends DocumentElement {
    private String text;
    public Paragraph(String text) { super(); this.text = text; }
    private Paragraph(Paragraph src) { super(src); this.text = src.text; }

    @Override public String render(int indent) {
        String style = (fontStyle != null) ? " [" + fontStyle + "]" : "";
        return pad(indent) + "<p" + style + "> " + text;
    }
    @Override public void      accept(DocumentVisitor v) { v.visit(this); }
    @Override public Paragraph deepCopy()                { return new Paragraph(this); }
    public String getText()              { return text; }
    public void   setText(String text)   { this.text = text; }
}
