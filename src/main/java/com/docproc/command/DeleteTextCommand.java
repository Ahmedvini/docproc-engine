package com.docproc.command;

import com.docproc.core.DocumentManager;

public class DeleteTextCommand implements Command {
    private final DocumentManager manager;
    private final String paragraphId;
    private final int count;
    private String previousText;

    public DeleteTextCommand(DocumentManager manager, String paragraphId, int count) {
        this.manager = manager;
        this.paragraphId = paragraphId;
        this.count = count;
    }

    @Override
    public void execute() {
        previousText = manager.getParagraphText(paragraphId);
        int from = Math.max(0, previousText.length() - count);
        manager.setParagraphText(paragraphId, previousText.substring(0, from));
    }

    @Override
    public void undo() {
        manager.setParagraphText(paragraphId, previousText);
    }

    @Override
    public String name() {
        return "DeleteText";
    }
}
