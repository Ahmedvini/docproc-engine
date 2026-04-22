package com.docproc.export;

public class PdfExporterFactory implements ExporterFactory {
    @Override
    public String format() {
        return "pdf";
    }

    @Override
    public DocumentExporter createExporter() {
        return new PdfExporter();
    }
}
