import java.util.List;
public interface ElementFactory {
    Paragraph create_paragraph(String text);
    Header    create_header(String text, int level);
    Image     create_image(String src, String alt);
    Table     create_table(List<String> headers);
    Footer    create_footer(String text);
    Section   create_section(String title);
}
