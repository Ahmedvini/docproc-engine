import java.util.List;
public class StandardElementFactory implements ElementFactory {
    @Override public Paragraph create_paragraph(String text)            { return new Paragraph(text);     }
    @Override public Header    create_header(String text, int level)     { return new Header(text, level); }
    @Override public Image     create_image(String src, String alt)      { return new Image(src, alt);     }
    @Override public Table     create_table(List<String> headers)        { return new Table(headers);      }
    @Override public Footer    create_footer(String text)                { return new Footer(text);        }
    @Override public Section   create_section(String title)              { return new Section(title);      }
}
