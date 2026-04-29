public interface DocumentVisitor {
    void visit(Document  document);
    void visit(Section   section);
    void visit(Paragraph paragraph);
    void visit(Header    header);
    void visit(Footer    footer);
    void visit(Image     image);
    void visit(Table     table);
}
