package com.docproc.plugin;

import com.docproc.model.Document;

public interface Plugin {
    String name();

    void initialize(PluginContext context);

    void execute(Document document);
}
