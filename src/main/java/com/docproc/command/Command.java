package com.docproc.command;

public interface Command {
    void execute();

    void undo();

    String name();
}
