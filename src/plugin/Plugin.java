public interface Plugin {
    String getName();
    String getVersion();
    void   onLoad(PluginManager pm);
    void   onUnload();
}
