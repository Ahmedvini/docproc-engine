package com.docproc.export;

import com.docproc.model.Document;

import java.io.IOException;
import java.nio.file.Path;

public interface DocumentExporter {
    void export(Document document, Path outputFile) throws IOException;
}
