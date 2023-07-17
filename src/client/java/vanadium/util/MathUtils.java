package vanadium.util;

import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Range;

import java.util.Random;
import java.util.random.RandomGenerator;

public class MathUtils {
    public static final RandomGenerator RANDOM = RandomGenerator.getDefault();
    public static final float PI2 = (float) (Math.PI * 2);
    public static final int ALPHA = 255 << 24;
    public static final float PHI = (float) (Math.PI * (3 - Math.sqrt(5)) * 0.5);

    public static int createColor(int r, int g, int b) {
        int rgb = (r << 16) | (g << 8) | b;
        return ColorConverter.rgbToArgb(rgb, ALPHA);
    }

    public static int getColorClamp(int red, int green, int blue) {
        red = Range.between(0,255).fit(red);
        green = Range.between(0,255).fit(green);
        blue = Range.between(0,255).fit(blue);
        return createColor(red, green, blue);
    }

    public static double radiansToDegrees(float measurementInRadians) {
        return Math.toDegrees(measurementInRadians);
    }

    public static double degreesToRadians(float measurementInDegress) {
        return Math.toRadians(measurementInDegress);
    }
}