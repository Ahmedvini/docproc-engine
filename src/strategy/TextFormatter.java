public class TextFormatter {
    private FormattingStrategy strategy;
    public TextFormatter()                              { this.strategy = new PlainTextStrategy(); }
    public TextFormatter(FormattingStrategy strategy)   { this.strategy = strategy; }
    public void   setStrategy(FormattingStrategy s)     { this.strategy = s; }
    public String format(String text)                   { return strategy.format(text); }
}
