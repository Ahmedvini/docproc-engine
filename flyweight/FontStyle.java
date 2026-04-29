public final class FontStyle {
    public final String  family;
    public final int     size;
    public final boolean bold;
    public final boolean italic;

    FontStyle(String family, int size, boolean bold, boolean italic) {
        this.family = family; this.size = size;
        this.bold = bold;     this.italic = italic;
    }

    public String toCss() {
        String s = "font-family:" + family + ";font-size:" + size + "px";
        if (bold)   s += ";font-weight:bold";
        if (italic) s += ";font-style:italic";
        return s;
    }

    @Override public String toString() {
        String f = (bold ? "B" : "") + (italic ? "I" : "");
        return "Font(" + family + "," + size + (f.isEmpty() ? "" : "," + f) + ")";
    }
}
