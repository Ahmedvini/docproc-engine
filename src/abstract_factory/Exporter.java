public abstract class Exporter {
    public abstract String exportDocument(Document doc);
    public abstract String exportParagraph(Paragraph p);
    public abstract String exportHeader(Header h);
    public abstract String exportTable(Table t);
    public abstract String exportImage(Image img);
    public abstract String exportFooter(Footer f);
    public abstract String exportSection(Section s);
}
