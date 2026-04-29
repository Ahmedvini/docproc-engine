import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellCheckVisitor implements DocumentVisitor {
    private static final Map<String, String> CORRECTIONS = new HashMap<>();
    static {
        CORRECTIONS.put("teh","the"); CORRECTIONS.put("recieve","receive");
        CORRECTIONS.put("occured","occurred"); CORRECTIONS.put("seperate","separate");
        CORRECTIONS.put("definately","definitely"); CORRECTIONS.put("accomodation","accommodation");
        CORRECTIONS.put("goverment","government"); CORRECTIONS.put("untill","until");
        CORRECTIONS.put("wierd","weird"); CORRECTIONS.put("beleive","believe");
    }

    public static class Issue {
        public final String word, suggestion, context;
        Issue(String w, String s, String c) { word=w; suggestion=s; context=c; }
    }
    private final List<Issue> issues = new ArrayList<>();

    private void check(String text, String ctx) {
        if (text == null) return;
        for (String raw : text.split("\\s+")) {
            String word = raw.replaceAll("[.,!?;:()]","").toLowerCase();
            if (CORRECTIONS.containsKey(word)) issues.add(new Issue(word, CORRECTIONS.get(word), ctx));
        }
    }
    @Override public void visit(Document  d) { check(d.getTitle(), "Title"); }
    @Override public void visit(Section   s) { check(s.getTitle(), "Section '"+s.getTitle()+"'"); }
    @Override public void visit(Paragraph p) { check(p.getText(),  "Paragraph"); }
    @Override public void visit(Header    h) { check(h.getText(),  "H"+h.getLevel()); }
    @Override public void visit(Footer    f) { check(f.getText(),  "Footer"); }
    @Override public void visit(Image   img) { check(img.getAlt(), "Image alt"); }
    @Override public void visit(Table     t) { for (String[] row : t.getRows()) for (String c : row) check(c, "Table cell"); }

    public String report() {
        if (issues.isEmpty()) return "  OK Spell Check: No issues found.";
        StringBuilder sb = new StringBuilder("  !! Spell Check: " + issues.size() + " issue(s) found:\n");
        for (Issue i : issues) sb.append("    '").append(i.word).append("' -> '").append(i.suggestion).append("'   in ").append(i.context).append("\n");
        return sb.toString().trim();
    }
    public List<Issue> getIssues() { return List.copyOf(issues); }
}
