package com.docproc.export;

public class DocxExporterFactory implements ExporterFactory {
    @Override
    public String format() {
        return "docx";
    }

    @Override
    public DocumentExporter createExporter() {
        return new DocxExporter();
    }
}
