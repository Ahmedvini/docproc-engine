package com.docproc.export;

import com.docproc.model.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HtmlExporter implements DocumentExporter {
    @Override
    public void export(Document document, Path outputFile) throws IOException {
        String html = "<html><body><pre>" + escape(document.render()) + "</pre></body></html>";
        Files.createDirectories(outputFile.getParent());
        Files.writeString(outputFile, html);
    }

    private String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
