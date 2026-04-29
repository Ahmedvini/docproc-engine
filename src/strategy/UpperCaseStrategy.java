public class UpperCaseStrategy implements FormattingStrategy {
    @Override public String format(String text) { return text.toUpperCase(); }
}
