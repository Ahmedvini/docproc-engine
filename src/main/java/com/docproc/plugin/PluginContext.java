package com.docproc.plugin;

import com.docproc.core.DocumentManager;
import com.docproc.core.ExportManager;

public record PluginContext(DocumentManager documentManager, ExportManager exportManager) {
}
