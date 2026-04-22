package com.docproc.core;

import com.docproc.model.Document;
import com.docproc.plugin.Plugin;
import com.docproc.plugin.PluginContext;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public final class PluginManager {
    private static final PluginManager INSTANCE = new PluginManager();
    private final Map<String, Plugin> plugins = new ConcurrentHashMap<>();

    private PluginManager() {
    }

    public static PluginManager getInstance() {
        return INSTANCE;
    }

    public void register(Plugin plugin) {
        plugin.initialize(new PluginContext(DocumentManager.getInstance(), ExportManager.getInstance()));
        plugins.put(plugin.name().toLowerCase(), plugin);
    }

    public void loadFromServiceLoader() {
        ServiceLoader<Plugin> loader = ServiceLoader.load(Plugin.class);
        for (Plugin plugin : loader) {
            register(plugin);
        }
    }

    public void execute(String name, Document document) {
        Plugin plugin = plugins.get(name.toLowerCase());
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin not found: " + name);
        }
        plugin.execute(document);
    }
}
