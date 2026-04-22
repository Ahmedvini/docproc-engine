package com.docproc.factory;

import com.docproc.model.Footer;
import com.docproc.model.Header;
import com.docproc.model.ImageElement;
import com.docproc.model.Paragraph;
import com.docproc.model.TableElement;

import java.util.List;

public interface DocumentElementFactory {
    Paragraph createParagraph(String text);

    ImageElement createImage(String path, String caption);

    TableElement createTable(List<List<String>> rows);

    Header createHeader(String text);

    Footer createFooter(String text);
}
