package vanadium;

public record VanadiumConfig(boolean ShouldClearSky,
                             boolean ShouldClearVoid,
                             boolean ShouldBlendSkyLights,
                             boolean ShouldFlickerBlockLights,
                             double BlockLightIntensityExponent) {

    public static double ScaledToRelativeBlockLightIntensity(double relativeBlockLightIntensityExponent) {
        return Math.log(2) * 0.25 * relativeBlockLightIntensityExponent;
    }

    static final VanadiumConfig DefaultConfiguration = new VanadiumConfig(false, false, true, true, -13.0);
}
