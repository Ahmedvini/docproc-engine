package com.docproc.export;

import com.docproc.model.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DocxExporter implements DocumentExporter {
    @Override
    public void export(Document document, Path outputFile) throws IOException {
        Files.createDirectories(outputFile.getParent());
        Files.writeString(outputFile, "DOCX-EXPORT\n" + document.render());
    }
}
