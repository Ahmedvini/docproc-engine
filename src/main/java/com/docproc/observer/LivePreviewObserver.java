package com.docproc.observer;

public class LivePreviewObserver implements DocumentObserver {
    @Override
    public void onDocumentChanged(DocumentEvent event) {
        System.out.println("[LivePreview] " + event.action() + " at " + event.timestamp());
    }
}
