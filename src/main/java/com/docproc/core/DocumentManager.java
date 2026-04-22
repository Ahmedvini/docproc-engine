package com.docproc.core;

import com.docproc.command.Command;
import com.docproc.command.CommandManager;
import com.docproc.model.Document;
import com.docproc.model.DocumentComponent;
import com.docproc.model.Paragraph;
import com.docproc.observer.DocumentEvent;
import com.docproc.observer.DocumentObserver;
import com.docproc.style.TextStyle;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DocumentManager {
    private static final DocumentManager INSTANCE = new DocumentManager();

    private final CommandManager commandManager = new CommandManager();
    private final List<DocumentObserver> observers = new CopyOnWriteArrayList<>();
    private Document currentDocument;

    private DocumentManager() {
    }

    public static DocumentManager getInstance() {
        return INSTANCE;
    }

    public Document getCurrentDocument() {
        return currentDocument;
    }

    public void setCurrentDocument(Document currentDocument) {
        this.currentDocument = currentDocument;
        notifyObservers("DocumentManager", "set-document");
    }

    public void addObserver(DocumentObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(DocumentObserver observer) {
        observers.remove(observer);
    }

    public void execute(Command command) {
        commandManager.execute(command);
        notifyObservers("Command", command.name());
    }

    public void undo() {
        commandManager.undo();
        notifyObservers("Command", "undo");
    }

    public void redo() {
        commandManager.redo();
        notifyObservers("Command", "redo");
    }

    public String getParagraphText(String paragraphId) {
        DocumentComponent component = requireComponent(paragraphId);
        if (!(component instanceof Paragraph paragraph)) {
            throw new IllegalArgumentException("Component is not a paragraph: " + paragraphId);
        }
        return paragraph.getText();
    }

    public void setParagraphText(String paragraphId, String text) {
        DocumentComponent component = requireComponent(paragraphId);
        if (!(component instanceof Paragraph paragraph)) {
            throw new IllegalArgumentException("Component is not a paragraph: " + paragraphId);
        }
        paragraph.setText(text);
    }

    public TextStyle getComponentStyle(String componentId) {
        return requireComponent(componentId).getStyle();
    }

    public void setComponentStyle(String componentId, TextStyle style) {
        requireComponent(componentId).setStyle(style);
    }

    public DocumentComponent findComponent(String componentId) {
        if (currentDocument == null) {
            return null;
        }
        return currentDocument.findById(componentId);
    }

    private DocumentComponent requireComponent(String componentId) {
        DocumentComponent component = findComponent(componentId);
        if (component == null) {
            throw new IllegalArgumentException("Component not found: " + componentId);
        }
        return component;
    }

    private void notifyObservers(String source, String action) {
        DocumentEvent event = new DocumentEvent(source, action, Instant.now());
        for (DocumentObserver observer : observers) {
            observer.onDocumentChanged(event);
        }
    }
}
