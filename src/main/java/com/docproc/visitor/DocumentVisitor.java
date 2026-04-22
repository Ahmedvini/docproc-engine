package com.docproc.visitor;

import com.docproc.model.Document;
import com.docproc.model.Footer;
import com.docproc.model.Header;
import com.docproc.model.ImageElement;
import com.docproc.model.Paragraph;
import com.docproc.model.Section;
import com.docproc.model.TableElement;

public interface DocumentVisitor {
    void visitDocument(Document document);

    void visitSection(Section section);

    void visitParagraph(Paragraph paragraph);

    void visitImage(ImageElement image);

    void visitTable(TableElement table);

    void visitHeader(Header header);

    void visitFooter(Footer footer);
}
