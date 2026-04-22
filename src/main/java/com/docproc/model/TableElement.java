package com.docproc.model;

import com.docproc.visitor.DocumentVisitor;

import java.util.ArrayList;
import java.util.List;

public class TableElement extends DocumentComponent {
    private final List<List<String>> rows;

    public TableElement(List<List<String>> rows) {
        this.rows = new ArrayList<>();
        for (List<String> row : rows) {
            this.rows.add(new ArrayList<>(row));
        }
    }

    public List<List<String>> getRows() {
        List<List<String>> copy = new ArrayList<>();
        for (List<String> row : rows) {
            copy.add(new ArrayList<>(row));
        }
        return copy;
    }

    @Override
    public String getType() {
        return "Table";
    }

    @Override
    public String render() {
        StringBuilder builder = new StringBuilder();
        for (List<String> row : rows) {
            builder.append("| ");
            for (String col : row) {
                builder.append(col).append(" | ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public DocumentComponent deepCopy() {
        TableElement copy = new TableElement(rows);
        copy.setStyle(getStyle());
        return copy;
    }

    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visitTable(this);
    }
}
