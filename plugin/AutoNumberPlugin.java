public class AutoNumberPlugin implements Plugin {
    @Override public String getName()    { return "AutoNumberer"; }
    @Override public String getVersion() { return "1.0.0"; }
    @Override public void onLoad(PluginManager pm) { System.out.println("  [Plugin] Loaded  : " + getName() + " v" + getVersion()); }
    @Override public void onUnload()               { System.out.println("  [Plugin] Unloaded: " + getName()); }
    public void numberHeaders(Document doc) {
        int[] counters = new int[3];
        walk(doc, counters);
        System.out.println("  [AutoNumberPlugin] Headers numbered in '" + doc.getTitle() + "'");
    }
    private void walk(DocumentElement el, int[] c) {
        if (el instanceof Header) {
            Header h = (Header) el;
            int lvl = Math.min(h.getLevel(), 3) - 1;
            c[lvl]++;
            for (int i = lvl+1; i < c.length; i++) c[i] = 0;
            StringBuilder pre = new StringBuilder();
            for (int i = 0; i <= lvl; i++) { if (i>0) pre.append("."); pre.append(c[i]); }
            if (!h.getText().startsWith(pre.toString())) h.setText(pre + "  " + h.getText());
        }
        if (el instanceof CompositeElement)
            for (DocumentElement child : ((CompositeElement) el).getChildren()) walk(child, c);
    }
}
