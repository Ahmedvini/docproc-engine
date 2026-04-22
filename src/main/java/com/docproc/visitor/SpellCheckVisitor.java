package com.docproc.visitor;

import com.docproc.model.Document;
import com.docproc.model.Footer;
import com.docproc.model.Header;
import com.docproc.model.ImageElement;
import com.docproc.model.Paragraph;
import com.docproc.model.Section;
import com.docproc.model.TableElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpellCheckVisitor implements DocumentVisitor {
    private static final Set<String> DICTIONARY = Set.of(
        "smart", "document", "editor", "section", "table", "image", "header", "footer", "text",
        "this", "is", "a", "simple", "demo", "plugin", "format", "export", "java", "system"
    );

    private final List<String> unknownWords = new ArrayList<>();

    public List<String> getUnknownWords() {
        return new ArrayList<>(new HashSet<>(unknownWords));
    }

    @Override
    public void visitDocument(Document document) {
    }

    @Override
    public void visitSection(Section section) {
        scan(section.getHeading());
    }

    @Override
    public void visitParagraph(Paragraph paragraph) {
        scan(paragraph.getText());
    }

    @Override
    public void visitImage(ImageElement image) {
        scan(image.getCaption());
    }

    @Override
    public void visitTable(TableElement table) {
        table.getRows().forEach(row -> row.forEach(this::scan));
    }

    @Override
    public void visitHeader(Header header) {
        scan(header.getText());
    }

    @Override
    public void visitFooter(Footer footer) {
        scan(footer.getText());
    }

    private void scan(String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        String[] words = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").split("\\s+");
        for (String word : words) {
            if (!word.isBlank() && !DICTIONARY.contains(word)) {
                unknownWords.add(word);
            }
        }
    }
}
