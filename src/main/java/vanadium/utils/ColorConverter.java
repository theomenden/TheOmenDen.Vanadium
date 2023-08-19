package vanadium.utils;

import org.apache.commons.lang3.Range;
import org.joml.Vector3f;
import vanadium.models.records.VanadiumColor;

import java.util.stream.IntStream;

public final class ColorConverter {
    private static final int WHITE_RGB = 0xFFFFFF;
    private static final int WHITE_ARGB = 0xFFFFFFFF;
    private static final float[] WHITE_HSL = {0f, 0f, 1f};
    private static final String WHITE_HEX = "#FFFFFF";

    public static final float[] sRGBLut = new float[256];

    static {
        IntStream
                .range(0, 256)
                .forEach(i -> {
                    float color = byteToNormalizedFloat(i);
                    sRGBLut[i] = convertSRGBFloatToLinearAsFloat(color);
                });
    }

    private static int[] fp32_to_srgb8_tab4 = {
            0x0073000d, 0x007a000d, 0x0080000d, 0x0087000d, 0x008d000d, 0x0094000d, 0x009a000d, 0x00a1000d,
            0x00a7001a, 0x00b4001a, 0x00c1001a, 0x00ce001a, 0x00da001a, 0x00e7001a, 0x00f4001a, 0x0101001a,
            0x010e0033, 0x01280033, 0x01410033, 0x015b0033, 0x01750033, 0x018f0033, 0x01a80033, 0x01c20033,
            0x01dc0067, 0x020f0067, 0x02430067, 0x02760067, 0x02aa0067, 0x02dd0067, 0x03110067, 0x03440067,
            0x037800ce, 0x03df00ce, 0x044600ce, 0x04ad00ce, 0x051400ce, 0x057b00c5, 0x05dd00bc, 0x063b00b5,
            0x06970158, 0x07420142, 0x07e30130, 0x087b0120, 0x090b0112, 0x09940106, 0x0a1700fc, 0x0a9500f2,
            0x0b0f01cb, 0x0bf401ae, 0x0ccb0195, 0x0d950180, 0x0e56016e, 0x0f0d015e, 0x0fbc0150, 0x10630143,
            0x11070264, 0x1238023e, 0x1357021d, 0x14660201, 0x156601e9, 0x165a01d3, 0x174401c0, 0x182401af,
            0x18fe0331, 0x1a9602fe, 0x1c1502d2, 0x1d7e02ad, 0x1ed4028d, 0x201a0270, 0x21520256, 0x227d0240,
            0x239f0443, 0x25c003fe, 0x27bf03c4, 0x29a10392, 0x2b6a0367, 0x2d1d0341, 0x2ebe031f, 0x304d0300,
            0x31d105b0, 0x34a80555, 0x37520507, 0x39d504c5, 0x3c37048b, 0x3e7c0458, 0x40a8042a, 0x42bd0401,
            0x44c20798, 0x488e071e, 0x4c1c06b6, 0x4f76065d, 0x52a50610, 0x55ac05cc, 0x5892058f, 0x5b590559,
            0x5e0c0a23, 0x631c0980, 0x67db08f6, 0x6c55087f, 0x70940818, 0x74a007bd, 0x787d076c, 0x7c330723
    };


    private ColorConverter() {
    }


    public static int createColor(int r, int g, int b) {
        int rgb = (r << 16) | (g << 8) | b;
        return rgbToArgb(rgb, MathUtils.ALPHA);
    }

    public static int getColorClamp(int red, int green, int blue) {
        red = Range
                .between(0,255).fit(red);
        green = Range.between(0,255).fit(green);
        blue = Range.between(0,255).fit(blue);
        return createColor(red, green, blue);
    }

    public static float byteToNormalizedFloat(int color) {
        return (float)(0xFF & color) * MathUtils.INV_255;
    }

    public static byte convertNormalizedFloatToByte(float color) {
        return (byte)Math.round(color * 255.0f);
    }

    public static int convertFloatArrayToIntColor(float[] values) {
        int red = (int) (values[0] *255);
        int green = (int) (values[1] *255);
        int blue = (int) (values[2] *255);

        return (red <<16) | (green <<8) | blue;
    }

    public static float convertSRGBFloatToLinearAsFloat(float color) {
        float clamped = Range.between(0.0f, 1.0f).fit(color);

        if(clamped <= 0.0404482362771082f) {
            return color * MathUtils.INV_12_92;
        }
        return (float)Math.pow((clamped + 0.055f) * MathUtils.INV_1_055, 2.4f);
    }

    public static float convertLinearFloatToSRGBAsFloat(float color) {
        float clamped = Range.between(0.0f, 1.0f).fit(color);

        if(clamped <= 0.003130668442500063f) {
            return clamped * 12.92f;
        }
        return 1.055f * (float)Math.pow(clamped, MathUtils.INV_2_4) - 0.055f;
    }

    public static float convertSRGBByteToLinearFloat(int color) {
        return sRGBLut[0xFF & color];
    }

    public static void convertSRGBToOkLabsInPlace(int color, float[] destination, int index){
        float r = pivotRgbToLinear(((color >> 16) & 0xFF) * MathUtils.INV_255);
        float g = pivotRgbToLinear(((color >> 8) & 0xFF) * MathUtils.INV_255);
        float b = pivotRgbToLinear((color & 0xFF) * MathUtils.INV_255);

        // Convert to linear sRGB
        float l = (0.4122214708f * r) + (0.5363325363f * g) + (0.0514459929f * b);
        float m = (0.2119034982f * r) + (0.6806995451f * g) + (0.1073969566f * b);
        float s = (0.0883024619f * r) + (0.2817188376f * g) + (0.6299787005f * b);

        // Nonlinear compression
        l = pivotLinearToRgb(l);
        m = pivotLinearToRgb(m);
        s = pivotLinearToRgb(s);

        destination[0] = ((0.2104542553f * l) + (0.793617785f * m)) - (0.0040720468f * s);
        destination[1] = ((1.9779984951f * l) - (2.428592205f * m)) + (0.4505937099f * s);
        destination[2] = ((0.0259040371f * l) + (0.7827717662f * m)) - (0.808675766f * s);


    }

    public static float[] convertSrgbToOkLabAsFloatArray(int srgb) {
        float r = pivotRgbToLinear(((srgb >> 16) & 0xFF) * MathUtils.INV_255);
        float g = pivotRgbToLinear(((srgb >> 8) & 0xFF) * MathUtils.INV_255);
        float b = pivotRgbToLinear((srgb & 0xFF) * MathUtils.INV_255);

        // Convert to linear sRGB
        float l = (0.4122214708f * r) + (0.5363325363f * g) + (0.0514459929f * b);
        float m = (0.2119034982f * r) + (0.6806995451f * g) + (0.1073969566f * b);
        float s = (0.0883024619f * r) + (0.2817188376f * g) + (0.6299787005f * b);

        // Nonlinear compression
        l = pivotLinearToRgb(l);
        m = pivotLinearToRgb(m);
        s = pivotLinearToRgb(s);

        float[] lab = new float[3];
        lab[0] = ((0.2104542553f * l) + (0.793617785f * m)) - (0.0040720468f * s);
        lab[1] = ((1.9779984951f * l) - (2.428592205f * m)) + (0.4505937099f * s);
        lab[2] = ((0.0259040371f * l) + (0.7827717662f * m)) - (0.808675766f * s);

        return lab;
    }

    public static byte[] convertOkLabToSrgbAsByteArray(float l, float a, float b) {
        float r, g, bFloat;

        // Nonlinear compression
        float lLin = (l + 0.3963377774f * a + 0.2158037573f * b);
        float mLin = (l - 0.1055613458f * a - 0.0638541728f * b);
        float sLin = (l - 0.0894841775f * a - 1.291485548f * b);

        // Convert to linear sRGB
        r = pivotLinearToRgb(4.0767416621f * lLin - 3.3077115913f * mLin + 0.2309699292f * sLin);
        g = pivotLinearToRgb(-1.2684380046f * lLin + 2.6097574011f * mLin - 0.3413193965f * sLin);
        bFloat = pivotLinearToRgb(-0.0041960863f * lLin - 0.7034186147f * mLin + 1.707614701f * sLin);

        // Clamp to valid sRGB range [0, 255]
        int rByte = Range.between(0,255).fit(Math.round(r * 255.0f));
        int gByte = Range.between(0,255).fit(Math.round(g * 255.0f));
        int bByte = Range.between(0,255).fit(Math.round(bFloat * 255.0f));

        return new byte[]{(byte) rByte, (byte) gByte, (byte) bByte};
    }

    public static int convertOKLabsTosRGBAInt(float L, float a, float b)
    {
        float l_ = L + 0.3963377774f * a + 0.2158037573f * b;
        float m_ = L - 0.1055613458f * a - 0.0638541728f * b;
        float s_ = L - 0.0894841775f * a - 1.2914855480f * b;

        float l = (float)Math.pow(l_, 3);
        float m = (float)Math.pow(m_, 3);
        float s = (float)Math.pow(s_, 3);

        float rResult =  4.0767416621f * l - 3.3077115913f * m + 0.2309699292f * s;
        float gResult = -1.2684380046f * l + 2.6097574011f * m - 0.3413193965f * s;
        float bResult = -0.0041960863f * l - 0.7034186147f * m + 1.7076147010f * s;

        int rByte = linearFloatTosRGBAsByte(rResult);
        int gByte = linearFloatTosRGBAsByte(gResult);
        int bByte = linearFloatTosRGBAsByte(bResult);

        VanadiumColor color = new VanadiumColor(rByte, gByte, bByte);

        return rgbToArgb(color.rgb(), 1);
    }

    public static void convertOKLabsTosRGBAInPlace(float L, float a, float b, int[] destination, int index)
    {
        float l_ = L + 0.3963377774f * a + 0.2158037573f * b;
        float m_ = L - 0.1055613458f * a - 0.0638541728f * b;
        float s_ = L - 0.0894841775f * a - 1.2914855480f * b;

        float l = (float)Math.pow(l_, 3);
        float m = (float)Math.pow(m_, 3);
        float s = (float)Math.pow(s_, 3);

        float rResult =  4.0767416621f * l - 3.3077115913f * m + 0.2309699292f * s;
        float gResult = -1.2684380046f * l + 2.6097574011f * m - 0.3413193965f * s;
        float bResult = -0.0041960863f * l - 0.7034186147f * m + 1.7076147010f * s;

        int rByte = linearFloatTosRGBAsByte(rResult);
        int gByte = linearFloatTosRGBAsByte(gResult);
        int bByte = linearFloatTosRGBAsByte(bResult);

        VanadiumColor color = new VanadiumColor(rByte, gByte, bByte);

        int colorAsIntWithFullAlpha = rgbToArgb(color.rgb(), 1);

        destination[index] = colorAsIntWithFullAlpha;
    }

    public static byte linearFloatTosRGBAsByte(float value) {
        final int closeToOne = 0x3f7fffff;
        final int minimumValue = 114 << 23;
        final int valueAsInt = Float.floatToIntBits(value);

        int valueAsBits = Range.between(minimumValue, closeToOne).fit(valueAsInt);
        int tab = fp32_to_srgb8_tab4[(valueAsBits - minimumValue) >>> 20];
        int bias = (tab >>> 16) << 9;
        int scale = tab & 0xffff;
        int t = (valueAsBits >>> 12) & 0xff;
        return (byte) ((bias + scale * t) >>> 16);
    }

    public static float[] rgbToHsl(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        float redNormalized = red * MathUtils.INV_255;
        float greenNormalized = green * MathUtils.INV_255;
        float blueNormalized = blue * MathUtils.INV_255;

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

    public static int hexToRgbU(String hex) {
        if(hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        return Integer.parseUnsignedInt(hex, 16);
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

    public static Vector3f createColorVector(int rgb) {
        float red = ((rgb >> 16) & 0xff) * MathUtils.INV_255;
        float green = ((rgb >> 8) & 0xff) * MathUtils.INV_255;
        float blue = (rgb & 0xff) * MathUtils.INV_255;

        return new Vector3f(red, green,blue);
    }

    public static float[] createColorFloatArray(int srgb) {
        return new float[] {
        (srgb >> 16 & 0xff) * MathUtils.INV_255,
        (srgb >> 8 & 0xff) * MathUtils.INV_255,
        (srgb & 0xff) * MathUtils.INV_255
        };
    }

    private static float hueToRgb(float p, float q, float t) {
        if(t < 0f) t += 1f;
        if(t > 1f) t -= 1f;
        if(t < 1f / 6f) return p + (q - p) * 6f * t;
        if(t < 1f / 2f) return q;
        if(t < 2f / 3f) return p + (q - p) * (2f / 3f - t) * 6f;
        return p;
    }

    private static float pivotRgbToLinear(float value) {
        if(value <= 0.04045f) {
            return value * MathUtils.INV_12_92;
        }
        return (float) (0.879_415 * Math.pow((value + 0.055), 2.4));
    }

    private static float pivotLinearToRgb(float value) {
        if(value <= 0.0031308f) {
            return value * 12.92f;
        }
        return (float)(1.055 * Math.pow(value, MathUtils.INV_2_4) - 0.055);
    }
}
