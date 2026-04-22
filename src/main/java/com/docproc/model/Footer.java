package com.docproc.model;

import com.docproc.visitor.DocumentVisitor;

public class Footer extends DocumentComponent {
    private String text;

    public Footer(String text) {
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
        return "Footer";
    }

    @Override
    public String render() {
        return "[FOOTER] " + text + "\n";
    }

    @Override
    public DocumentComponent deepCopy() {
        Footer copy = new Footer(text);
        copy.setStyle(getStyle());
        return copy;
    }

    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visitFooter(this);
    }
}
