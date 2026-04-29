public class JSONExporter extends Exporter {
    private String esc(String s) { return s.replace("\\","\\\\").replace("\"","\\\""); }

    private String serEl(DocumentElement e, int d) {
        String p = "  ".repeat(d);
        if (e instanceof Paragraph) return p+"{\"type\":\"paragraph\",\"text\":\""+esc(((Paragraph)e).getText())+"\"}";
        if (e instanceof Header)    return p+"{\"type\":\"header\",\"level\":"+((Header)e).getLevel()+",\"text\":\""+esc(((Header)e).getText())+"\"}";
        if (e instanceof Footer)    return p+"{\"type\":\"footer\",\"text\":\""+esc(((Footer)e).getText())+"\"}";
        if (e instanceof Image) { Image i=(Image)e; return p+"{\"type\":\"image\",\"src\":\""+esc(i.getSrc())+"\",\"alt\":\""+esc(i.getAlt())+"\",\"width\":"+i.getWidth()+",\"height\":"+i.getHeight()+"}"; }
        if (e instanceof Table) {
            Table t=(Table)e;
            StringBuilder sb=new StringBuilder(p+"{\"type\":\"table\",\"headers\":[");
            for (int i=0;i<t.getHeaders().size();i++){if(i>0)sb.append(",");sb.append("\"").append(esc(t.getHeaders().get(i))).append("\"");}
            sb.append("],\"rows\":[");
            java.util.List<String[]> rows=t.getRows();
            for(int i=0;i<rows.size();i++){if(i>0)sb.append(",");sb.append("[");String[]row=rows.get(i);for(int j=0;j<row.length;j++){if(j>0)sb.append(",");sb.append("\"").append(esc(row[j])).append("\"");}sb.append("]");}
            return sb.append("]}").toString();
        }
        if (e instanceof Section) {
            Section s=(Section)e;
            StringBuilder sb=new StringBuilder(p+"{\n"+p+"  \"type\":\"section\",\n"+p+"  \"title\":\""+esc(s.getTitle())+"\",\n"+p+"  \"children\":[\n");
            java.util.List<DocumentElement> kids=s.getChildren();
            for(int i=0;i<kids.size();i++){sb.append(serEl(kids.get(i),d+2));if(i<kids.size()-1)sb.append(",");sb.append("\n");}
            return sb.append(p+"  ]\n"+p+"}").toString();
        }
        return p+"{}";
    }

    @Override public String exportDocument(Document doc) {
        StringBuilder sb=new StringBuilder("{\n  \"type\":\"document\",\n  \"title\":\""+esc(doc.getTitle())+"\",\n  \"metadata\":{\n");
        java.util.Map<String,String> m=doc.getMetadata();
        java.util.List<String> keys=new java.util.ArrayList<>(m.keySet());
        for(int i=0;i<keys.size();i++){sb.append("    \"").append(keys.get(i)).append("\":\"").append(esc(m.get(keys.get(i)))).append("\"");if(i<keys.size()-1)sb.append(",");sb.append("\n");}
        sb.append("  },\n  \"children\":[\n");
        java.util.List<DocumentElement> kids=doc.getChildren();
        for(int i=0;i<kids.size();i++){sb.append(serEl(kids.get(i),2));if(i<kids.size()-1)sb.append(",");sb.append("\n");}
        return sb.append("  ]\n}").toString();
    }
    @Override public String exportParagraph(Paragraph p) { return "{\"type\":\"paragraph\",\"text\":\""+esc(p.getText())+"\"}"; }
    @Override public String exportHeader(Header h)        { return "{\"type\":\"header\",\"level\":"+h.getLevel()+"}"; }
    @Override public String exportTable(Table t)          { return "{\"type\":\"table\"}"; }
    @Override public String exportImage(Image img)        { return "{\"type\":\"image\",\"src\":\""+esc(img.getSrc())+"\"}"; }
    @Override public String exportFooter(Footer f)        { return "{\"type\":\"footer\",\"text\":\""+esc(f.getText())+"\"}"; }
    @Override public String exportSection(Section s)      { return "{\"type\":\"section\",\"title\":\""+esc(s.getTitle())+"\"}"; }
}
