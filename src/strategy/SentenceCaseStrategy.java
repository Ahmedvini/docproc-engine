public class SentenceCaseStrategy implements FormattingStrategy {
    @Override public String format(String text) {
        if (text == null || text.isEmpty()) return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase();
    }
}
