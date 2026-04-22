package com.docproc.export;

public class JsonExporterFactory implements ExporterFactory {
    @Override
    public String format() {
        return "json";
    }

    @Override
    public DocumentExporter createExporter() {
        return new JsonExporter();
    }
}
