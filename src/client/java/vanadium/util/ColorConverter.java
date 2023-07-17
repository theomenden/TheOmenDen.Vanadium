package vanadium.util;

import vanadium.models.VanadiumColor;

public final class ColorConverter {
    private static final int WHITE_RGB = 0xFFFFFF;
    private static final int WHITE_ARGB = 0xFFFFFFFF;
    private static final float[] WHITE_HSL = {0f, 0f, 1f};
    private static final String WHITE_HEX = "#FFFFFF";

    private static final float saturationRatio = 1/255.0f;

    private ColorConverter() {
    }

    public static float[] rgbToHsl(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        float redNormalized = red * saturationRatio;
        float greenNormalized = green * saturationRatio;
        float blueNormalized = blue * saturationRatio;

        float max = Math.max(redNormalized, Math.max(greenNormalized, blueNormalized));
        float min = Math.min(redNormalized, Math.min(greenNormalized, blueNormalized));

        float hue = 0f;
        float saturation = 0f;

        float lightness = (max + min) / 2f;

        if (max == min) {
            return new float[] {hue, saturation, lightness};
        }

        float delta = max - min;
        saturation = lightness > 0.5f ? delta / (2f - max - min) : delta / (max + min);

        if(max == redNormalized) {
            hue = (greenNormalized - blueNormalized) / delta;
        }
        else if(max == greenNormalized) {
            hue = 2f + (blueNormalized - redNormalized) / delta;
        }
        else {
            hue = 4f + (redNormalized - greenNormalized) / delta;
        }

        hue /= 6f;

        return new float[] {hue, saturation, lightness};
    }

    public static int hslToRgb(float hue, float saturation, float lightness) {
        if (saturation == 0f) {
            int value = Math.round(lightness * 255f);
            return value << 16 | (value << 8) | value;
        }

        float q = lightness < 0.5f ? lightness * (1f + saturation) : lightness + saturation - lightness * saturation;
        float p = 2f * lightness - q;
        float r = hueToRgb(p, q, hue + 1f / 3f);
        float g = hueToRgb(p, q, hue);
        float b = hueToRgb(p, q, hue - 1f / 3f);
        int red = Math.round(r * 255f);
        int green = Math.round(g * 255f);
        int blue = Math.round(b * 255f);
        return red << 16 | green << 8 | blue;
    }

    public static String rgbToHex(int rgb) {
        return String.format("#%06X", rgb & 0xFFFFFF);
    }

    public static int hexToRgb(String hex) {
        if(hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        return Integer.parseInt(hex, 16);
    }

    public static int rgbToArgb(int rgb, int alpha) {
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }

    public static int argbToRgb(int argb) {
        return argb & 0xFFFFFF;
    }

    public static int getWhiteRgb() {
        return WHITE_RGB;
    }

    public static int getWhiteArgb() {
        return WHITE_ARGB;
    }

    public static float[] getWhiteHsl() {
        return WHITE_HSL;
    }

    public static String getWhiteHex() {
        return WHITE_HEX;
    }

    private static float hueToRgb(float p, float q, float t) {
        if(t < 0f) t += 1f;
        if(t > 1f) t -= 1f;
        if(t < 1f / 6f) return p + (q - p) * 6f * t;
        if(t < 1f / 2f) return q;
        if(t < 2f / 3f) return p + (q - p) * (2f / 3f - t) * 6f;
        return p;
    }

}
