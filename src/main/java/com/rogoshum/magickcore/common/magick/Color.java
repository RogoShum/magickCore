package com.rogoshum.magickcore.common.magick;

import java.util.Objects;

public class Color {
    public final static Color ORIGIN_COLOR = Color.create(1, 1, 1);
    public final static Color YELLOW_COLOR = Color.create(1, 1, 0);
    public final static Color BLUE_COLOR = Color.create(0, 0, 1);
    public final static Color RED_COLOR = Color.create(1, 0, 0);
    public final static Color GREEN_COLOR = Color.create(0, 1, 0);
    public final static Color BLACK_COLOR = Color.create(0, 0, 0);
    public final static Color GREY_COLOR = Color.create(0, 0, 0);
    public final static Color BROWN_COLOR = Color.create(0.4f, 0.3f, 0);
    private final float r;
    private final float g;
    private final float b;
    private final int decimal;

    private Color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.decimal = ((0xFF) << 24) |
                (((int)(r * 255) & 0xFF) << 16) |
                (((int)(g * 255) & 0xFF) << 8)  |
                (((int)(b * 255) & 0xFF));
    }

    private Color(int decimal) {
        this.r = ((decimal >> 16) & 0xFF) / 255f;
        this.g = ((decimal >> 8) & 0xFF) / 255f;
        this.b = ((decimal) & 0xFF) / 255f;
        this.decimal = decimal;
    }

    public static Color create(float r, float g, float b) {
        return new Color(r, g, b);
    }

    public static Color create(int decimal) {
        return new Color(decimal);
    }

    public float r() {
        return r;
    }

    public float g() {
        return g;
    }

    public float b() {
        return b;
    }
    public int getDecimalColor() {
        return decimal;
    }

    @Override
    public String toString() {
        return "Color{" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color) o;
        return Float.compare(color.r, r) == 0 && Float.compare(color.g, g) == 0 && Float.compare(color.b, b) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b);
    }
}
