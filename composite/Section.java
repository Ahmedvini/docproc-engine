public class Section extends CompositeElement {
    private String title;
    public Section(String title) { super(); this.title = title; }
    private Section(Section src) { super(src); this.title = src.title; }

    @Override public String render(int indent) {
        StringBuilder sb = new StringBuilder(pad(indent) + "[Section: " + title + "]");
        for (DocumentElement c : children) sb.append("\n").append(c.render(indent + 1));
        return sb.toString();
    }
    @Override public void    accept(DocumentVisitor v) {
        v.visit(this);
        for (DocumentElement c : children) c.accept(v);
    }
    @Override public Section deepCopy()                { return new Section(this); }
    public String getTitle()             { return title; }
    public void   setTitle(String title) { this.title = title; }
}
