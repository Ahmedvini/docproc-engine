package com.docproc.visitor;

import com.docproc.model.Document;
import com.docproc.model.Footer;
import com.docproc.model.Header;
import com.docproc.model.ImageElement;
import com.docproc.model.Paragraph;
import com.docproc.model.Section;
import com.docproc.model.TableElement;

public class WordCountVisitor implements DocumentVisitor {
    private int count;

    public int getCount() {
        return count;
    }

    @Override
    public void visitDocument(Document document) {
    }

    @Override
    public void visitSection(Section section) {
        count += words(section.getHeading());
    }

    @Override
    public void visitParagraph(Paragraph paragraph) {
        count += words(paragraph.getText());
    }

    @Override
    public void visitImage(ImageElement image) {
        count += words(image.getCaption());
    }

    @Override
    public void visitTable(TableElement table) {
        table.getRows().forEach(row -> row.forEach(cell -> count += words(cell)));
    }

    @Override
    public void visitHeader(Header header) {
        count += words(header.getText());
    }

    @Override
    public void visitFooter(Footer footer) {
        count += words(footer.getText());
    }

    private int words(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }
}
