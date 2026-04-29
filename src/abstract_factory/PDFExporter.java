public class PDFExporter extends Exporter {
    private String el(DocumentElement e) {
        if (e instanceof Header)    return exportHeader((Header) e);
        if (e instanceof Paragraph) return exportParagraph((Paragraph) e);
        if (e instanceof Table)     return exportTable((Table) e);
        if (e instanceof Image)     return exportImage((Image) e);
        if (e instanceof Footer)    return exportFooter((Footer) e);
        if (e instanceof Section)   return exportSection((Section) e);
        return "";
    }
    @Override public String exportDocument(Document doc) {
        StringBuilder sb = new StringBuilder("%PDF-1.4\nTitle: "+doc.getTitle()
            +"\nAuthor: "+doc.getMetadata().get("author")+"\n"+"-".repeat(50)+"\n");
        for (DocumentElement c : doc.getChildren()) sb.append(el(c)).append("\n");
        return sb.append("-".repeat(50)).append("\n%%EOF").toString();
    }
    @Override public String exportParagraph(Paragraph p) {
        String pre = (p.getFontStyle() != null) ? "["+p.getFontStyle()+"] " : "";
        return "  " + pre + p.getText();
    }
    @Override public String exportHeader(Header h) {
        char bar = (h.getLevel() == 1) ? '=' : '-';
        return "\n" + h.getText() + "\n" + String.valueOf(bar).repeat(h.getText().length());
    }
    @Override public String exportTable(Table t) {
        int w = t.getHeaders().stream().mapToInt(String::length).max().orElse(10);
        StringBuilder sep = new StringBuilder("+");
        for (int i = 0; i < t.getHeaders().size(); i++) sep.append("-".repeat(w+2)).append("+");
        StringBuilder sb = new StringBuilder(sep + "\n|");
        for (String h : t.getHeaders()) sb.append(String.format(" %-"+w+"s |", h));
        sb.append("\n").append(sep);
        for (String[] row : t.getRows()) {
            sb.append("\n|");
            for (int i = 0; i < t.getHeaders().size(); i++) {
                String c = (i < row.length) ? row[i] : "";
                sb.append(String.format(" %-"+w+"s |", c));
            }
        }
        return sb.append("\n").append(sep).toString();
    }
    @Override public String exportImage(Image img)  { return "  [IMAGE: "+img.getAlt()+" | "+img.getWidth()+"x"+img.getHeight()+" | "+img.getSrc()+"]"; }
    @Override public String exportFooter(Footer f)  { return "\n[FOOTER] " + f.getText(); }
    @Override public String exportSection(Section s) {
        StringBuilder sb = new StringBuilder("\n-- " + s.getTitle() + " --\n");
        for (DocumentElement c : s.getChildren()) sb.append(el(c)).append("\n");
        return sb.toString();
    }
}
