package com.docproc.model;

import com.docproc.visitor.DocumentVisitor;

public class Paragraph extends DocumentComponent {
    private String text;

    public Paragraph(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getType() {
        return "Paragraph";
    }

    @Override
    public String render() {
        return text + "\n";
    }

    @Override
    public DocumentComponent deepCopy() {
        Paragraph copy = new Paragraph(text);
        copy.setStyle(getStyle());
        return copy;
    }

    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visitParagraph(this);
    }
}
