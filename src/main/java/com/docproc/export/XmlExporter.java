package com.docproc.export;

import com.docproc.model.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class XmlExporter implements DocumentExporter {
    @Override
    public void export(Document document, Path outputFile) throws IOException {
        String xml = "<document><title>" + escape(document.getTitle()) + "</title><content><![CDATA[" +
            document.render() + "]]></content></document>";
        Files.createDirectories(outputFile.getParent());
        Files.writeString(outputFile, xml);
    }

    private String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
