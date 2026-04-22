package com.docproc.model;

import com.docproc.visitor.DocumentVisitor;

public class Section extends DocumentComponent {
    private String heading;

    public Section(String heading) {
        this.heading = heading;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    @Override
    public String getType() {
        return "Section";
    }

    @Override
    public String render() {
        StringBuilder builder = new StringBuilder();
        builder.append("## ").append(heading).append("\n");
        for (DocumentComponent child : getChildren()) {
            builder.append(child.render()).append("\n");
        }
        return builder.toString();
    }

    @Override
    public DocumentComponent deepCopy() {
        Section copy = new Section(heading);
        copy.setStyle(getStyle());
        for (DocumentComponent child : getChildren()) {
            copy.add(child.deepCopy());
        }
        return copy;
    }

    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visitSection(this);
        for (DocumentComponent child : getChildren()) {
            child.accept(visitor);
        }
    }
}
