public class InsertTextCommand implements Command {
    private final Paragraph paragraph;
    private final String text;
    private final int position;
    private final String snapshot;

    public InsertTextCommand(Paragraph paragraph, String text) { this(paragraph, text, -1); }
    public InsertTextCommand(Paragraph paragraph, String text, int position) {
        this.paragraph = paragraph; this.text = text;
        this.position  = position;  this.snapshot = paragraph.getText();
    }
    @Override public Paragraph execute() {
        String t = paragraph.getText();
        paragraph.setText(position == -1 ? t + text : t.substring(0, position) + text + t.substring(position));
        return paragraph;
    }
    @Override public void   undo()           { paragraph.setText(snapshot); }
    @Override public String getDescription() { String t = text.length()>20 ? text.substring(0,20)+"..." : text; return "InsertText('"+t+"')"; }
}
