package com.docproc.builder;

import com.docproc.factory.DocumentElementFactory;
import com.docproc.model.Document;
import com.docproc.model.DocumentComponent;
import com.docproc.model.Section;
import com.docproc.style.TextStyle;

import java.util.List;

public class DocumentBuilder {
    private final DocumentElementFactory factory;
    private Document document;
    private Section currentSection;

    public DocumentBuilder(DocumentElementFactory factory) {
        this.factory = factory;
    }

    public DocumentBuilder start(String title) {
        this.document = new Document(title);
        this.currentSection = null;
        return this;
    }

    public DocumentBuilder addHeader(String text) {
        document.add(factory.createHeader(text));
        return this;
    }

    public DocumentBuilder addFooter(String text) {
        document.add(factory.createFooter(text));
        return this;
    }

    public DocumentBuilder addSection(String heading) {
        currentSection = new Section(heading);
        document.add(currentSection);
        return this;
    }

    public DocumentBuilder addParagraph(String text, TextStyle style) {
        DocumentComponent paragraph = factory.createParagraph(text);
        paragraph.setStyle(style);
        addToCurrentLevel(paragraph);
        return this;
    }

    public DocumentBuilder addImage(String path, String caption, TextStyle style) {
        DocumentComponent image = factory.createImage(path, caption);
        image.setStyle(style);
        addToCurrentLevel(image);
        return this;
    }

    public DocumentBuilder addTable(List<List<String>> rows, TextStyle style) {
        DocumentComponent table = factory.createTable(rows);
        table.setStyle(style);
        addToCurrentLevel(table);
        return this;
    }

    public Document build() {
        if (document == null) {
            throw new IllegalStateException("Call start() before build()");
        }
        return document;
    }

    private void addToCurrentLevel(DocumentComponent component) {
        if (currentSection != null) {
            currentSection.add(component);
        } else {
            document.add(component);
        }
    }
}
