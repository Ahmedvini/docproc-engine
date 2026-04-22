package com.docproc.command;

import com.docproc.core.DocumentManager;
import com.docproc.style.TextStyle;

public class FormatChangeCommand implements Command {
    private final DocumentManager manager;
    private final String componentId;
    private final TextStyle newStyle;
    private TextStyle oldStyle;

    public FormatChangeCommand(DocumentManager manager, String componentId, TextStyle newStyle) {
        this.manager = manager;
        this.componentId = componentId;
        this.newStyle = newStyle;
    }

    @Override
    public void execute() {
        oldStyle = manager.getComponentStyle(componentId);
        manager.setComponentStyle(componentId, newStyle);
    }

    @Override
    public void undo() {
        manager.setComponentStyle(componentId, oldStyle);
    }

    @Override
    public String name() {
        return "FormatChange";
    }
}
