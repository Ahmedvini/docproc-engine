public class DeleteTextCommand implements Command {
    private final Paragraph paragraph;
    private final int start, end;
    private final String snapshot;

    public DeleteTextCommand(Paragraph paragraph, int start, int end) {
        this.paragraph = paragraph; this.start = start; this.end = end; this.snapshot = paragraph.getText();
    }
    @Override public Paragraph execute() {
        String t = paragraph.getText();
        paragraph.setText(t.substring(0, start) + t.substring(Math.min(end, t.length())));
        return paragraph;
    }
    @Override public void   undo()           { paragraph.setText(snapshot); }
    @Override public String getDescription() { return "DeleteText(["+start+":"+end+"])"; }
}
