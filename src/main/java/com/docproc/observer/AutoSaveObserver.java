package com.docproc.observer;

import com.docproc.model.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AutoSaveObserver implements DocumentObserver {
    private final DocumentSupplier supplier;
    private final Path output;

    public AutoSaveObserver(DocumentSupplier supplier, Path output) {
        this.supplier = supplier;
        this.output = output;
    }

    @Override
    public void onDocumentChanged(DocumentEvent event) {
        Document document = supplier.get();
        if (document == null) {
            return;
        }
        try {
            Files.createDirectories(output.getParent());
            Files.writeString(output, document.render());
        } catch (IOException e) {
            throw new IllegalStateException("Auto-save failed", e);
        }
    }

    @FunctionalInterface
    public interface DocumentSupplier {
        Document get();
    }
}
