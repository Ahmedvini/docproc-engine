package com.docproc;

import com.docproc.builder.DocumentBuilder;
import com.docproc.command.InsertTextCommand;
import com.docproc.core.DocumentManager;
import com.docproc.factory.DefaultDocumentElementFactory;
import com.docproc.model.Document;
import com.docproc.model.Paragraph;
import com.docproc.style.StyleFlyweightFactory;
import com.docproc.style.TextStyle;
import com.docproc.visitor.WordCountVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DocumentSystemTest {
    @Test
    void flyweightReusesSameStyleInstance() {
        StyleFlyweightFactory factory = StyleFlyweightFactory.getInstance();
        TextStyle one = factory.getStyle("Georgia", 12, "black", false, false);
        TextStyle two = factory.getStyle("Georgia", 12, "black", false, false);
        assertSame(one, two);
    }

    @Test
    void commandUndoRedoWorks() {
        TextStyle style = StyleFlyweightFactory.getInstance().getStyle("Georgia", 12, "black", false, false);
        Document document = new DocumentBuilder(new DefaultDocumentElementFactory())
            .start("Test")
            .addSection("S1")
            .addParagraph("hello", style)
            .build();

        Paragraph paragraph = findParagraph(document);
        DocumentManager manager = DocumentManager.getInstance();
        manager.setCurrentDocument(document);

        manager.execute(new InsertTextCommand(manager, paragraph.getId(), " world"));
        assertEquals("hello world", paragraph.getText());

        manager.undo();
        assertEquals("hello", paragraph.getText());

        manager.redo();
        assertEquals("hello world", paragraph.getText());
    }

    @Test
    void visitorCountsWords() {
        TextStyle style = StyleFlyweightFactory.getInstance().getStyle("Georgia", 12, "black", false, false);
        Document document = new DocumentBuilder(new DefaultDocumentElementFactory())
            .start("Test")
            .addSection("Intro")
            .addParagraph("one two three", style)
            .addTable(List.of(List.of("four", "five")), style)
            .build();

        WordCountVisitor visitor = new WordCountVisitor();
        document.accept(visitor);
        assertEquals(7, visitor.getCount());
    }

    private Paragraph findParagraph(Document document) {
        return (Paragraph) document.getChildren().get(0).getChildren().get(0);
    }
}
