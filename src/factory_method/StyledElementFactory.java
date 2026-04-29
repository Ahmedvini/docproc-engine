import java.util.List;
public class StyledElementFactory implements ElementFactory {
    private final FontStyle  defaultFont;
    private final ColorStyle defaultColor;

    public StyledElementFactory(FontStyle font, ColorStyle color) {
        this.defaultFont = font; this.defaultColor = color;
    }
    private <T extends DocumentElement> T styled(T el) { el.setStyle(defaultFont, defaultColor); return el; }

    @Override public Paragraph create_paragraph(String text)            { return styled(new Paragraph(text));     }
    @Override public Header    create_header(String text, int level)     { return styled(new Header(text, level)); }
    @Override public Image     create_image(String src, String alt)      { return new Image(src, alt);             }
    @Override public Table     create_table(List<String> headers)        { return new Table(headers);              }
    @Override public Footer    create_footer(String text)                { return styled(new Footer(text));        }
    @Override public Section   create_section(String title)              { return new Section(title);              }
}
