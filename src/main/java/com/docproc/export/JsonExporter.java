package com.docproc.export;

import com.docproc.model.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonExporter implements DocumentExporter {
    @Override
    public void export(Document document, Path outputFile) throws IOException {
        String json = "{\n  \"title\": \"" + escape(document.getTitle()) + "\",\n  \"content\": \"" +
            escape(document.render()).replace("\\n", "\\\\n") + "\"\n}";
        Files.createDirectories(outputFile.getParent());
        Files.writeString(outputFile, json);
    }

    private String escape(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
