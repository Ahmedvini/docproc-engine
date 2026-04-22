package com.docproc.app;

import com.docproc.builder.DocumentBuilder;
import com.docproc.collab.CollaborationSession;
import com.docproc.command.DeleteTextCommand;
import com.docproc.command.FormatChangeCommand;
import com.docproc.command.InsertTextCommand;
import com.docproc.core.DocumentManager;
import com.docproc.core.ExportManager;
import com.docproc.core.PluginManager;
import com.docproc.factory.DefaultDocumentElementFactory;
import com.docproc.gui.SmartDocumentEditorFrame;
import com.docproc.hosting.DocumentHttpServer;
import com.docproc.model.Document;
import com.docproc.model.Paragraph;
import com.docproc.observer.AutoSaveObserver;
import com.docproc.observer.LivePreviewObserver;
import com.docproc.strategy.UpperCaseFormattingStrategy;
import com.docproc.style.StyleFlyweightFactory;
import com.docproc.style.TextStyle;
import com.docproc.versioning.VersionControlService;
import com.docproc.visitor.SpellCheckVisitor;
import com.docproc.visitor.WordCountVisitor;

import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        boolean guiMode = hasArg(args, "--gui");
        boolean serverMode = hasArg(args, "--server");

        TextStyle normal = StyleFlyweightFactory.getInstance().getStyle("Georgia", 12, "black", false, false);
        TextStyle emphasized = StyleFlyweightFactory.getInstance().getStyle("Georgia", 12, "darkblue", true, false);

        Document document = new DocumentBuilder(new DefaultDocumentElementFactory())
            .start("Smart Document Editor")
            .addHeader("Sample Header")
            .addSection("Introduction")
            .addParagraph("this is a simple demo", normal)
            .addImage("images/diagram.png", "system architecture", normal)
            .addTable(List.of(List.of("Pattern", "Used"), List.of("Singleton", "Yes")), normal)
            .addFooter("Sample Footer")
            .build();

        DocumentManager manager = DocumentManager.getInstance();
        manager.setCurrentDocument(document);
        manager.addObserver(new LivePreviewObserver());
        manager.addObserver(new AutoSaveObserver(manager::getCurrentDocument, Path.of("autosave/latest.txt")));

        Paragraph paragraph = findFirstParagraph(document);
        manager.execute(new InsertTextCommand(manager, paragraph.getId(), " with plugin support"));
        manager.execute(new DeleteTextCommand(manager, paragraph.getId(), 8));
        manager.execute(new FormatChangeCommand(manager, paragraph.getId(), emphasized));
        manager.undo();
        manager.redo();

        new UpperCaseFormattingStrategy().apply(paragraph);

        ExportManager exports = ExportManager.getInstance();
        exports.export("pdf", document, Path.of("exports/document.pdf.txt"));
        exports.export("html", document, Path.of("exports/document.html"));
        exports.export("docx", document, Path.of("exports/document.docx.txt"));
        exports.export("json", document, Path.of("exports/document.json"));
        exports.export("xml", document, Path.of("exports/document.xml"));

        PluginManager pluginManager = PluginManager.getInstance();
        pluginManager.loadFromServiceLoader();
        pluginManager.execute("word-frequency", document);

        WordCountVisitor wordCounter = new WordCountVisitor();
        document.accept(wordCounter);
        System.out.println("Word count: " + wordCounter.getCount());

        SpellCheckVisitor spellChecker = new SpellCheckVisitor();
        document.accept(spellChecker);
        System.out.println("Unknown words: " + spellChecker.getUnknownWords());

        CollaborationSession collaboration = new CollaborationSession(manager);
        collaboration.simulate(List.of(
            new CollaborationSession.EditorAction("Alice", new InsertTextCommand(manager, paragraph.getId(), " [A]")),
            new CollaborationSession.EditorAction("Bob", new InsertTextCommand(manager, paragraph.getId(), " [B]"))
        ));

        VersionControlService vcs = new VersionControlService();
        vcs.commit(document, "Initial commit");
        manager.execute(new InsertTextCommand(manager, paragraph.getId(), " v2"));
        vcs.commit(document, "Added v2 tag");
        Document restored = vcs.checkout(1);
        System.out.println("Restored version title: " + restored.getTitle());

        if (guiMode) {
            SmartDocumentEditorFrame.launch();
            return;
        }

        if (serverMode) {
            int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
            new DocumentHttpServer(manager).start(port);
        }
    }

    private static boolean hasArg(String[] args, String target) {
        for (String arg : args) {
            if (target.equals(arg)) {
                return true;
            }
        }
        return false;
    }

    private static Paragraph findFirstParagraph(Document document) {
        for (var child : document.getChildren()) {
            if (child instanceof Paragraph paragraph) {
                return paragraph;
            }
            for (var nested : child.getChildren()) {
                if (nested instanceof Paragraph paragraph) {
                    return paragraph;
                }
            }
        }
        throw new IllegalStateException("No paragraph found");
    }
}
