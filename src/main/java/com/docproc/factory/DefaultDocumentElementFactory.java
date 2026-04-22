package com.docproc.factory;

import com.docproc.model.Footer;
import com.docproc.model.Header;
import com.docproc.model.ImageElement;
import com.docproc.model.Paragraph;
import com.docproc.model.TableElement;

import java.util.List;

public class DefaultDocumentElementFactory implements DocumentElementFactory {
    @Override
    public Paragraph createParagraph(String text) {
        return new Paragraph(text);
    }

    @Override
    public ImageElement createImage(String path, String caption) {
        return new ImageElement(path, caption);
    }

    @Override
    public TableElement createTable(List<List<String>> rows) {
        return new TableElement(rows);
    }

    @Override
    public Header createHeader(String text) {
        return new Header(text);
    }

    @Override
    public Footer createFooter(String text) {
        return new Footer(text);
    }
}
