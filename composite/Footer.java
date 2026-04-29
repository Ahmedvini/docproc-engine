public class Footer extends DocumentElement {
    private String text;
    public Footer(String text) { super(); this.text = text; }
    private Footer(Footer src) { super(src); this.text = src.text; }

    @Override public String render(int indent) { return pad(indent) + "<footer> " + text; }
    @Override public void   accept(DocumentVisitor v) { v.visit(this); }
    @Override public Footer deepCopy()                { return new Footer(this); }
    public String getText()            { return text; }
    public void   setText(String text) { this.text = text; }
}
