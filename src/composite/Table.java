import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Table extends CompositeElement {
    private final List<String>   headers;
    private final List<String[]> rows;

    public Table(List<String> headers) { super(); this.headers = new ArrayList<>(headers); this.rows = new ArrayList<>(); }
    private Table(Table src) {
        super(src);
        this.headers = new ArrayList<>(src.headers);
        this.rows    = new ArrayList<>();
        for (String[] r : src.rows) this.rows.add(Arrays.copyOf(r, r.length));
    }

    public Table addRow(String... cells) { rows.add(cells); return this; }

    @Override public String render(int indent) {
        if (headers.isEmpty()) return pad(indent) + "<table/>";
        int colW = headers.stream().mapToInt(String::length).max().orElse(8) + 2;
        String bar = "+".repeat(0);
        StringBuilder sep = new StringBuilder(pad(indent) + "+");
        for (int i = 0; i < headers.size(); i++) sep.append("-".repeat(colW+2)).append("+");
        StringBuilder sb = new StringBuilder(pad(indent) + "<table>\n" + sep + "\n" + pad(indent) + "|");
        for (String h : headers) sb.append(String.format(" %-"+colW+"s |", h));
        sb.append("\n").append(sep);
        for (String[] row : rows) {
            sb.append("\n").append(pad(indent)).append("|");
            for (int i = 0; i < headers.size(); i++) {
                String c = (i < row.length) ? row[i] : "";
                sb.append(String.format(" %-"+colW+"s |", c));
            }
        }
        return sb.append("\n").append(sep).toString();
    }
    @Override public void  accept(DocumentVisitor v) { v.visit(this); }
    @Override public Table deepCopy()                { return new Table(this); }
    public List<String>   getHeaders() { return List.copyOf(headers); }
    public List<String[]> getRows()    { return List.copyOf(rows); }
}
