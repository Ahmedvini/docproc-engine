package com.docproc.export;

public class XmlExporterFactory implements ExporterFactory {
    @Override
    public String format() {
        return "xml";
    }

    @Override
    public DocumentExporter createExporter() {
        return new XmlExporter();
    }
}
