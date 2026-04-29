import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class PluginManager {
    private final Map<String, Plugin>            plugins = new HashMap<>();
    private final Map<String, List<Consumer<?>>> hooks   = new HashMap<>();
    private PluginManager() {}

    private static final class Holder {
        static final PluginManager INSTANCE = new PluginManager();
    }
    public static PluginManager getInstance() { return Holder.INSTANCE; }

    public void load(Plugin plugin)  { plugin.onLoad(this); plugins.put(plugin.getName(), plugin); }
    public void unload(String name)  { Plugin p = plugins.remove(name); if (p != null) p.onUnload(); }

    @SuppressWarnings("unchecked")
    public <T> void registerHook(String event, Consumer<T> cb) {
        hooks.computeIfAbsent(event, k -> new ArrayList<>()).add(cb);
    }
    @SuppressWarnings({"unchecked","rawtypes"})
    public <T> void fire(String event, T data) {
        for (Consumer cb : hooks.getOrDefault(event, new ArrayList<>())) cb.accept(data);
    }
    public Plugin       get(String name)      { return plugins.get(name); }
    public List<String> listPlugins()         { return new ArrayList<>(plugins.keySet()); }
    public boolean      isLoaded(String name) { return plugins.containsKey(name); }
}
