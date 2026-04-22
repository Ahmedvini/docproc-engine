package com.docproc.export;

public interface ExporterFactory {
    String format();

    DocumentExporter createExporter();
}
