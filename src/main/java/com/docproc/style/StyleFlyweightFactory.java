package com.docproc.style;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class StyleFlyweightFactory {
    private static final StyleFlyweightFactory INSTANCE = new StyleFlyweightFactory();
    private final Map<String, TextStyle> styles = new ConcurrentHashMap<>();

    private StyleFlyweightFactory() {
    }

    public static StyleFlyweightFactory getInstance() {
        return INSTANCE;
    }

    public TextStyle getStyle(String fontFamily, int fontSize, String color, boolean bold, boolean italic) {
        String key = fontFamily + "|" + fontSize + "|" + color + "|" + bold + "|" + italic;
        return styles.computeIfAbsent(key,
            k -> new TextStyle(fontFamily, fontSize, color, bold, italic));
    }

    public int cacheSize() {
        return styles.size();
    }
}
