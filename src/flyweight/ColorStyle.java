public final class ColorStyle {
    public final int r, g, b;
    ColorStyle(int r, int g, int b) { this.r=r; this.g=g; this.b=b; }
    public String getHex() { return String.format("#%02X%02X%02X", r, g, b); }
    @Override public String toString() { return "Color(" + getHex() + ")"; }
}
