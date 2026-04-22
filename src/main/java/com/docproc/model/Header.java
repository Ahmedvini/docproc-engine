package com.docproc.model;

import com.docproc.visitor.DocumentVisitor;

public class Header extends DocumentComponent {
    private String text;

    public Header(String text) {
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
        return "Header";
    }

    @Override
    public String render() {
        return "[HEADER] " + text + "\n";
    }

    @Override
    public DocumentComponent deepCopy() {
        Header copy = new Header(text);
        copy.setStyle(getStyle());
        return copy;
    }

    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visitHeader(this);
    }
}
