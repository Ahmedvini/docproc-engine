package com.docproc.observer;

public interface DocumentObserver {
    void onDocumentChanged(DocumentEvent event);
}
