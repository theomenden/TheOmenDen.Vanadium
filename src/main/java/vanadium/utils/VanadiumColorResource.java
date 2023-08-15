package vanadium.utils;

import vanadium.models.records.VanadiumColor;

public class VanadiumColorResource {
    private VanadiumColorResource() {}

    public static VanadiumColor resolveColorFromRgb(int rgb) {
        int fullAlphaRgb = ColorConverter.rgbToArgb(rgb, 1);
        return new VanadiumColor(fullAlphaRgb);
    }

    public static VanadiumColor resolveColorFromHex(String hex) {
        int hexRgb = ColorConverter.hexToRgb(hex);
        int fullAlphaRgb = ColorConverter.rgbToArgb(hexRgb, 1);
        return new VanadiumColor(fullAlphaRgb);
    }

    public static VanadiumColor resolveColorFromHex(String hex, int alpha) {
        int hexRgb = ColorConverter.hexToRgb(hex);
        int rgba = ColorConverter.rgbToArgb(hexRgb, alpha);
        return new VanadiumColor(rgba);
    }

    public static String resolveVanadiumColorToHex(VanadiumColor color) {
        return ColorConverter.rgbToHex(color.rgb());
    }
}
