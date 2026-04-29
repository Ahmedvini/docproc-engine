public class WordCountPlugin implements Plugin {
    @Override public String getName()    { return "WordCounter"; }
    @Override public String getVersion() { return "1.0.0"; }
    @Override public void onLoad(PluginManager pm) { System.out.println("  [Plugin] Loaded  : " + getName() + " v" + getVersion()); }
    @Override public void onUnload()               { System.out.println("  [Plugin] Unloaded: " + getName()); }
    public String count(Document doc) { WordCountVisitor v = new WordCountVisitor(); doc.accept(v); return v.report(); }
}
