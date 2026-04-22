package com.docproc.core;

import com.docproc.export.DocxExporterFactory;
import com.docproc.export.ExporterFactory;
import com.docproc.export.HtmlExporterFactory;
import com.docproc.export.JsonExporterFactory;
import com.docproc.export.PdfExporterFactory;
import com.docproc.export.XmlExporterFactory;
import com.docproc.model.Document;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ExportManager {
    private static final ExportManager INSTANCE = new ExportManager();
    private final Map<String, ExporterFactory> factories = new ConcurrentHashMap<>();

    private ExportManager() {
        registerFactory(new PdfExporterFactory());
        registerFactory(new HtmlExporterFactory());
        registerFactory(new DocxExporterFactory());
        registerFactory(new JsonExporterFactory());
        registerFactory(new XmlExporterFactory());
    }

    public static ExportManager getInstance() {
        return INSTANCE;
    }

    public void registerFactory(ExporterFactory factory) {
        factories.put(factory.format().toLowerCase(), factory);
    }

    public void export(String format, Document document, Path outputFile) {
        ExporterFactory factory = factories.get(format.toLowerCase());
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }
        try {
            factory.createExporter().export(document, outputFile);
        } catch (IOException e) {
            throw new IllegalStateException("Export failed for format: " + format, e);
        }
    }
}
