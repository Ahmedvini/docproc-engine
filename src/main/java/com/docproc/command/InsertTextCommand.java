package com.docproc.command;

import com.docproc.core.DocumentManager;

public class InsertTextCommand implements Command {
    private final DocumentManager manager;
    private final String paragraphId;
    private final String insertion;
    private String previousText;

    public InsertTextCommand(DocumentManager manager, String paragraphId, String insertion) {
        this.manager = manager;
        this.paragraphId = paragraphId;
        this.insertion = insertion;
    }

    @Override
    public void execute() {
        previousText = manager.getParagraphText(paragraphId);
        manager.setParagraphText(paragraphId, previousText + insertion);
    }

    @Override
    public void undo() {
        manager.setParagraphText(paragraphId, previousText);
    }

    @Override
    public String name() {
        return "InsertText";
    }
}
