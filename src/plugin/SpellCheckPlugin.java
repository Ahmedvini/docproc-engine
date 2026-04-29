public class SpellCheckPlugin implements Plugin {
    @Override public String getName()    { return "SpellChecker"; }
    @Override public String getVersion() { return "1.0.0"; }
    @Override public void onLoad(PluginManager pm) {
        pm.registerHook("before_export", (Document doc) ->
            System.out.println("  [SpellCheckPlugin] Running pre-export spell check..."));
        System.out.println("  [Plugin] Loaded  : " + getName() + " v" + getVersion());
    }
    @Override public void onUnload() { System.out.println("  [Plugin] Unloaded: " + getName()); }
    public String check(Document doc) { SpellCheckVisitor v = new SpellCheckVisitor(); doc.accept(v); return v.report(); }
}
