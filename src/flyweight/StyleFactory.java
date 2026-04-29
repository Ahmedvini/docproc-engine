import java.util.HashMap;
import java.util.Map;

public final class StyleFactory {
    private static final Map<String, FontStyle>  fonts  = new HashMap<>();
    private static final Map<String, ColorStyle> colors = new HashMap<>();
    private StyleFactory() {}

    public static FontStyle getFont(String family, int size, boolean bold, boolean italic) {
        String key = family + ":" + size + ":" + bold + ":" + italic;
        if (!fonts.containsKey(key)) {
            fonts.put(key, new FontStyle(family, size, bold, italic));
            System.out.println("  [Flyweight] Created  : " + fonts.get(key));
        } else {
            System.out.println("  [Flyweight] Reused   : " + fonts.get(key));
        }
        return fonts.get(key);
    }

    public static FontStyle getFont(String family, int size) {
        return getFont(family, size, false, false);
    }

    public static ColorStyle getColor(int r, int g, int b) {
        String key = r + ":" + g + ":" + b;
        colors.computeIfAbsent(key, k -> new ColorStyle(r, g, b));
        return colors.get(key);
    }

    public static String poolInfo() {
        return fonts.size() + " font(s), " + colors.size() + " color(s)";
    }
}
