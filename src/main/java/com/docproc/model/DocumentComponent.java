package com.docproc.model;

import com.docproc.style.TextStyle;
import com.docproc.visitor.DocumentVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class DocumentComponent {
    private final String id;
    private final List<DocumentComponent> children = new ArrayList<>();
    private TextStyle style;

    protected DocumentComponent() {
        this.id = UUID.randomUUID().toString();
    }

    protected DocumentComponent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public TextStyle getStyle() {
        return style;
    }

    public void setStyle(TextStyle style) {
        this.style = style;
    }

    public void add(DocumentComponent component) {
        children.add(component);
    }

    public void remove(DocumentComponent component) {
        children.remove(component);
    }

    public List<DocumentComponent> getChildren() {
        return Collections.unmodifiableList(children);
    }

    protected List<DocumentComponent> mutableChildren() {
        return children;
    }

    public DocumentComponent findById(String targetId) {
        if (id.equals(targetId)) {
            return this;
        }
        for (DocumentComponent child : children) {
            DocumentComponent match = child.findById(targetId);
            if (match != null) {
                return match;
            }
        }
        return null;
    }

    public abstract String getType();

    public abstract String render();

    public abstract DocumentComponent deepCopy();

    public abstract void accept(DocumentVisitor visitor);
}
