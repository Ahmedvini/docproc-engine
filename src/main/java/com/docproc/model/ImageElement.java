package com.docproc.model;

import com.docproc.visitor.DocumentVisitor;

public class ImageElement extends DocumentComponent {
    private final String path;
    private final String caption;

    public ImageElement(String path, String caption) {
        this.path = path;
        this.caption = caption;
    }

    public String getPath() {
        return path;
    }

    public String getCaption() {
        return caption;
    }

    @Override
    public String getType() {
        return "Image";
    }

    @Override
    public String render() {
        return "![" + caption + "](" + path + ")\n";
    }

    @Override
    public DocumentComponent deepCopy() {
        ImageElement copy = new ImageElement(path, caption);
        copy.setStyle(getStyle());
        return copy;
    }

    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visitImage(this);
    }
}
