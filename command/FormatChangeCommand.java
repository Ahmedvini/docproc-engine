public class FormatChangeCommand implements Command {
    private final DocumentElement element;
    private final FontStyle  newFont,  oldFont;
    private final ColorStyle newColor, oldColor;

    public FormatChangeCommand(DocumentElement element, FontStyle newFont, ColorStyle newColor) {
        this.element  = element;
        this.newFont  = newFont;  this.oldFont  = element.getFontStyle();
        this.newColor = newColor; this.oldColor = element.getColorStyle();
    }
    @Override public DocumentElement execute() { element.setStyle(newFont, newColor); return element; }
    @Override public void            undo()    { element.setStyle(oldFont, oldColor); }
    @Override public String getDescription()   { return "FormatChange(font="+newFont+", color="+newColor+")"; }
}
