public class WordCountVisitor implements DocumentVisitor {
    private int words=0, chars=0, paragraphs=0, sections=0, images=0;

    private void count(String text) {
        if (text == null || text.isBlank()) return;
        words += text.trim().split("\\s+").length;
        chars += text.length();
    }
    @Override public void visit(Document  d) { count(d.getTitle()); }
    @Override public void visit(Section   s) { sections++; count(s.getTitle()); }
    @Override public void visit(Paragraph p) { paragraphs++; count(p.getText()); }
    @Override public void visit(Header    h) { count(h.getText()); }
    @Override public void visit(Footer    f) { count(f.getText()); }
    @Override public void visit(Image   img) { images++; }
    @Override public void visit(Table     t) { for (String[] row : t.getRows()) for (String c : row) count(c); }

    public String report() {
        return "  +-- Word Count Report ----------------\n"
             + "  |  Words       : " + words      + "\n"
             + "  |  Characters  : " + chars      + "\n"
             + "  |  Paragraphs  : " + paragraphs + "\n"
             + "  |  Sections    : " + sections   + "\n"
             + "  |  Images      : " + images     + "\n"
             + "  +-------------------------------------";
    }
}
