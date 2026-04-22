package com.docproc.model;

import com.docproc.visitor.DocumentVisitor;

public class Document extends DocumentComponent {
    private String title;

    public Document(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getType() {
        return "Document";
    }

    @Override
    public String render() {
        StringBuilder builder = new StringBuilder();
        builder.append("# ").append(title).append("\n\n");
        for (DocumentComponent child : getChildren()) {
            builder.append(child.render()).append("\n");
        }
        return builder.toString();
    }

    @Override
    public DocumentComponent deepCopy() {
        Document copy = new Document(title);
        copy.setStyle(getStyle());
        for (DocumentComponent child : getChildren()) {
            copy.add(child.deepCopy());
        }
        return copy;
    }

    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visitDocument(this);
        for (DocumentComponent child : getChildren()) {
            child.accept(visitor);
        }
    }
}
