public class MarkdownStripStrategy implements FormattingStrategy {
    @Override public String format(String text) {
        for (String m : new String[]{"**", "__", "*", "_", "~~", "`"})
            text = text.replace(m, "");
        return text;
    }
}
