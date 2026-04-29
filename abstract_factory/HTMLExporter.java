public class HTMLExporter extends Exporter {
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
        StringBuilder body = new StringBuilder();
        for (DocumentElement c : doc.getChildren()) body.append(el(c)).append("\n");
        return "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <meta charset=\"UTF-8\">\n  <title>"
            + doc.getTitle() + "</title>\n</head>\n<body>\n" + body + "</body>\n</html>";
    }
    @Override public String exportParagraph(Paragraph p) {
        String s = (p.getFontStyle() != null) ? " style=\"" + p.getFontStyle().toCss() + "\"" : "";
        return "<p" + s + ">" + p.getText() + "</p>";
    }
    @Override public String exportHeader(Header h) { return "<h"+h.getLevel()+">"+h.getText()+"</h"+h.getLevel()+">"; }
    @Override public String exportTable(Table t) {
        StringBuilder sb = new StringBuilder("<table>\n<thead><tr>");
        for (String h : t.getHeaders()) sb.append("<th>").append(h).append("</th>");
        sb.append("</tr></thead>\n<tbody>");
        for (String[] row : t.getRows()) {
            sb.append("<tr>");
            for (String c : row) sb.append("<td>").append(c).append("</td>");
            sb.append("</tr>");
        }
        return sb.append("</tbody>\n</table>").toString();
    }
    @Override public String exportImage(Image img) {
        return "<img src=\""+img.getSrc()+"\" alt=\""+img.getAlt()+"\" width=\""+img.getWidth()+"\" height=\""+img.getHeight()+"\"/>";
    }
    @Override public String exportFooter(Footer f)  { return "<footer>" + f.getText() + "</footer>"; }
    @Override public String exportSection(Section s) {
        StringBuilder sb = new StringBuilder("<section>\n<h2>" + s.getTitle() + "</h2>\n");
        for (DocumentElement c : s.getChildren()) sb.append(el(c)).append("\n");
        return sb.append("</section>").toString();
    }
}
