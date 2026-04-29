import java.util.UUID;

public abstract class DocumentElement {
    protected String     elementId;
    protected FontStyle  fontStyle;
    protected ColorStyle colorStyle;

    protected DocumentElement() {
        this.elementId = UUID.randomUUID().toString().substring(0, 8);
    }
    protected DocumentElement(DocumentElement src) {
        this.elementId  = UUID.randomUUID().toString().substring(0, 8);
        this.fontStyle  = src.fontStyle;
        this.colorStyle = src.colorStyle;
    }

    public abstract String          render(int indent);
    public abstract void            accept(DocumentVisitor visitor);
    public abstract DocumentElement deepCopy();

    public void      setStyle(FontStyle font, ColorStyle color) {
        if (font  != null) this.fontStyle  = font;
        if (color != null) this.colorStyle = color;
    }
    public String     getElementId()  { return elementId;  }
    public FontStyle  getFontStyle()  { return fontStyle;  }
    public ColorStyle getColorStyle() { return colorStyle; }

    protected String pad(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent * 2; i++) sb.append(' ');
        return sb.toString();
    }
}
