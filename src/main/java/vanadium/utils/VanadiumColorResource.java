package vanadium.utils;

import vanadium.models.records.VanadiumColor;

public class VanadiumColorResource {
    private VanadiumColorResource() {}

    public static String resolveVanadiumColorToHex(VanadiumColor color) {
        return ColorConverter.rgbToHex(color.rgb());
    }
}
