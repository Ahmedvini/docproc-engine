public class Image extends DocumentElement {
    private String src, alt;
    private int    width, height;

    public Image(String src, String alt, int width, int height) {
        super(); this.src=src; this.alt=alt; this.width=width; this.height=height;
    }
    public Image(String src, String alt) { this(src, alt, 800, 600); }
    private Image(Image o) { super(o); this.src=o.src; this.alt=o.alt; this.width=o.width; this.height=o.height; }

    @Override public String render(int indent) {
        return pad(indent)+"<img src='"+src+"' alt='"+alt+"' "+width+"x"+height+">";
    }
    @Override public void  accept(DocumentVisitor v) { v.visit(this); }
    @Override public Image deepCopy()                { return new Image(this); }
    public String getSrc()    { return src;    }
    public String getAlt()    { return alt;    }
    public int    getWidth()  { return width;  }
    public int    getHeight() { return height; }
}
