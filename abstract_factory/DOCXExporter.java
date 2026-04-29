public class DOCXExporter extends Exporter {
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
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<w:document>\n<w:body>\n");
        for (DocumentElement c : doc.getChildren()) sb.append(el(c)).append("\n");
        return sb.append("</w:body>\n</w:document>").toString();
    }
    @Override public String exportParagraph(Paragraph p) {
        String fa = (p.getFontStyle() != null) ? " w:font=\""+p.getFontStyle().family+"\" w:sz=\""+(p.getFontStyle().size*2)+"\"" : "";
        return "  <w:p"+fa+"><w:r><w:t>"+p.getText()+"</w:t></w:r></w:p>";
    }
    @Override public String exportHeader(Header h) {
        return "  <w:p><w:pPr><w:pStyle w:val=\"Heading"+h.getLevel()+"\"/></w:pPr><w:r><w:t>"+h.getText()+"</w:t></w:r></w:p>";
    }
    @Override public String exportTable(Table t) {
        StringBuilder sb = new StringBuilder("  <w:tbl><w:tr>");
        for (String h : t.getHeaders()) sb.append("<w:tc><w:p><w:r><w:t>").append(h).append("</w:t></w:r></w:p></w:tc>");
        sb.append("</w:tr>");
        for (String[] row : t.getRows()) {
            sb.append("<w:tr>");
            for (String c : row) sb.append("<w:tc><w:p><w:r><w:t>").append(c).append("</w:t></w:r></w:p></w:tc>");
            sb.append("</w:tr>");
        }
        return sb.append("</w:tbl>").toString();
    }
    @Override public String exportImage(Image img)  { return "  <w:drawing><wp:inline><a:blip r:embed=\""+img.getSrc()+"\"/><wp:docPr descr=\""+img.getAlt()+"\"/></wp:inline></w:drawing>"; }
    @Override public String exportFooter(Footer f)  { return "  <w:footer><w:r><w:t>"+f.getText()+"</w:t></w:r></w:footer>"; }
    @Override public String exportSection(Section s) {
        StringBuilder sb = new StringBuilder("  <w:sectPr><w:title>"+s.getTitle()+"</w:title></w:sectPr>\n");
        for (DocumentElement c : s.getChildren()) sb.append(el(c)).append("\n");
        return sb.toString();
    }
}
