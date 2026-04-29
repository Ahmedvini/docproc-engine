public class Header extends DocumentElement {
    private String text;
    private int    level;
    public Header(String text, int level) { super(); this.text = text; this.level = level; }
    private Header(Header src) { super(src); this.text = src.text; this.level = src.level; }

    @Override public String render(int indent) { return pad(indent) + "<h" + level + "> " + text; }
    @Override public void   accept(DocumentVisitor v) { v.visit(this); }
    @Override public Header deepCopy()                { return new Header(this); }
    public String getText()            { return text;  }
    public int    getLevel()           { return level; }
    public void   setText(String text) { this.text = text; }
}
