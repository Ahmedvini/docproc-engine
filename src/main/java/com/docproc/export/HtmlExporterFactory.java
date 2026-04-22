package com.docproc.export;

public class HtmlExporterFactory implements ExporterFactory {
    @Override
    public String format() {
        return "html";
    }

    @Override
    public DocumentExporter createExporter() {
        return new HtmlExporter();
    }
}
