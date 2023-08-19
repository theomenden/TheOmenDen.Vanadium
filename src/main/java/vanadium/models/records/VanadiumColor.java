package vanadium.models.records;

import org.apache.commons.lang3.Range;

public record VanadiumColor(int rgb) {

    public VanadiumColor(int r, int g, int b) {
        this(r << 16 | g << 8 | b);
    }

    public VanadiumColor(int a, int r, int g, int b) {
        this(a << 24 | r << 16 | g << 8 | b);
    }

    public VanadiumColor(float h, float s, float l) {
        this(hslToRgb(h, s, l));
    }

    public VanadiumColor(String hex) {
        this(Integer.parseInt(hex, 16));
    }

    private static int hslToRgb(float h, float s, float l) {
        float r, g, b;
        if (s == 0) {
            r = g = b = l; // achromatic
        } else {
            float q = l < 0.5? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = hueToRgb(p, q, h + 1f / 3);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1f / 3);
        }

        return Math.round((r * 255)) << 16 | Math.round((g * 255)) << 8 | Math.round((b * 255));
    }

    private static float hueToRgb(float p, float q, float t) {
        if(t < 0f) t+= 1f;
        if(t > 1f) t-= 1f;
        if(t < 1/6f) return p + (q - p) * 6f * t;
        if(t < 1/2f) return q;
        if(t < 2/3f) return p + (q - p) * (2f/3f - t) * 6f;
        return p;
    }

    public VanadiumColor adjustBrightness(float factor) {
        int r = Range.between(0,255).fit((int)(((rgb >> 16) & 0xFF)* factor));
        int g = Range.between(0,255).fit((int)(((rgb >> 8) & 0xFF)* factor));
        int b = Range
                .between(0,255).fit((int)((rgb & 0xFF)* factor));

        return new VanadiumColor((rgb & 0xFF000000) | (r << 16) | (g << 8) | b);
    }
}
