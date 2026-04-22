package com.docproc.style;

import java.util.Objects;

public final class TextStyle {
    private final String fontFamily;
    private final int fontSize;
    private final String color;
    private final boolean bold;
    private final boolean italic;

    public TextStyle(String fontFamily, int fontSize, String color, boolean bold, boolean italic) {
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.color = color;
        this.bold = bold;
        this.italic = italic;
    }

    public String fontFamily() {
        return fontFamily;
    }

    public int fontSize() {
        return fontSize;
    }

    public String color() {
        return color;
    }

    public boolean bold() {
        return bold;
    }

    public boolean italic() {
        return italic;
    }

    @Override
    public String toString() {
        return "TextStyle{" +
            "font='" + fontFamily + '\'' +
            ", size=" + fontSize +
            ", color='" + color + '\'' +
            ", bold=" + bold +
            ", italic=" + italic +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TextStyle style)) {
            return false;
        }
        return fontSize == style.fontSize
            && bold == style.bold
            && italic == style.italic
            && Objects.equals(fontFamily, style.fontFamily)
            && Objects.equals(color, style.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fontFamily, fontSize, color, bold, italic);
    }
}
